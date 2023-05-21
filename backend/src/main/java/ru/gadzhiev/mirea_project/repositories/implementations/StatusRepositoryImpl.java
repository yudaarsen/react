package ru.gadzhiev.mirea_project.repositories.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.gadzhiev.mirea_project.models.Status;
import ru.gadzhiev.mirea_project.repositories.interfaces.StatusRepository;
import ru.gadzhiev.mirea_project.utils.RepositoryUtils;

import java.sql.Types;
import java.util.List;

/**
 * Реализация репозитория для работы со статусами обращения
 */
@Repository
public class StatusRepositoryImpl implements StatusRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final int initialStatus;

    private final RowMapper<Status> statusRowMapperWithNextStatuses = (rs, rowNum) -> {
        Status status = new Status();
        status.setCode(rs.getInt("code"));
        status.setName(RepositoryUtils.getStringResult(rs, "name"));
        status.setLang(RepositoryUtils.getStringResult(rs, "lang"));
        status.setNextStatuses(getNextStatuses(status.getCode(), status.getLang()));
        return status;
    };

    private final RowMapper<Status> statusRowMapper = (rs, rowNum) -> {
        Status status = new Status();
        status.setCode(rs.getInt("code"));
        status.setName(RepositoryUtils.getStringResult(rs, "name"));
        status.setLang(RepositoryUtils.getStringResult(rs, "lang"));
        return status;
    };

    public StatusRepositoryImpl(@Autowired JdbcTemplate jdbcTemplate) {
        initialStatus = jdbcTemplate.queryForObject("SELECT code FROM status WHERE initial = TRUE LIMIT 1", Integer.class);
    }

    @Override
    public int getInitialStatus() {
        return initialStatus;
    }

    @Override
    public Status getStatus(int code, String lang) {
        return jdbcTemplate.queryForObject("SELECT status.code AS code, low, high, lang, name FROM status " +
                        "INNER JOIN statust ON status.code = statust.code " +
                        "WHERE status.code = ? AND lang = ?",
                new Object[] { code, lang },
                new int[] { Types.INTEGER, Types.CHAR },
                statusRowMapperWithNextStatuses
        );
    }

    private List<Status> getNextStatuses(int code, String lang) {
        return jdbcTemplate.query("SELECT status.code AS code, name, lang FROM status " +
                        "INNER JOIN statust ON status.code = statust.code " +
                        "WHERE ? >= low AND ? <= high AND lang = ? AND status.code != ?",
                new Object[] { code, code, lang, code },
                new int[] { Types.INTEGER, Types.INTEGER, Types.CHAR, Types.INTEGER  },
                statusRowMapper
        );
    }
}
