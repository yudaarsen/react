package ru.gadzhiev.mirea_project.repositories.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.gadzhiev.mirea_project.models.*;
import ru.gadzhiev.mirea_project.repositories.interfaces.AppealRepository;
import ru.gadzhiev.mirea_project.repositories.interfaces.ClassificationRepository;
import ru.gadzhiev.mirea_project.repositories.interfaces.EmployeeRepository;
import ru.gadzhiev.mirea_project.repositories.interfaces.StatusRepository;
import ru.gadzhiev.mirea_project.utils.AppealUtils;
import ru.gadzhiev.mirea_project.utils.RepositoryUtils;

import java.sql.Types;
import java.util.List;

/**
 * Реализация репозитория обращений, основанная на взаимодействии с PostgreSQL
 */
@Repository
public class AppealRepositoryImpl implements AppealRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private ClassificationRepository classificationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private final RowMapper<Appeal> appealRowMapper = (rs, rowNum) -> {
        String lang = RepositoryUtils.getStringResult(rs, "lang");
        Appeal appeal = new Appeal();
        appeal.setId(rs.getInt("appeal_id"));
        appeal.setTitle(RepositoryUtils.getStringResult(rs, "title"));
        appeal.setTextBody(RepositoryUtils.getStringResult(rs, "text_body"));
        appeal.setFirstName(RepositoryUtils.getStringResult(rs, "apl_fname"));
        appeal.setLastName(RepositoryUtils.getStringResult(rs, "apl_lname"));
        appeal.setMiddleName(RepositoryUtils.getStringResult(rs, "apl_mname"));
        appeal.setEmail(RepositoryUtils.getStringResult(rs, "apl_email"));
        appeal.setCreateDate(rs.getTimestamp("create_date"));
        appeal.setDeadLine(rs.getTimestamp("deadline"));

        appeal.setStatus(statusRepository.getStatus(rs.getInt("status_code"), lang));

        Category category = null;
        Department department = null;
        Employee employee = null;
        List<AppealComm> appealComms = null;
        List<Attachment> attachments = null;

        try {
            category = classificationRepository.getCategory(rs.getInt("category_id"), lang);
        } catch (DataAccessException e) {
            //dummy
        }
        try {
            department = employeeRepository.getDepartment(rs.getInt("department_id"), lang);
        } catch (DataAccessException e) {
            //dummy
        }
        try {
            employee = employeeRepository.getEmployeeById(rs.getInt("employee_id"), lang);
        } catch (DataAccessException e) {
            //dummy
        }
        try {
            appealComms = getAppealComms(appeal.getId(), lang);
        } catch (DataAccessException e) {
            //dummy
        }
        try {
            attachments = getAttachments(appeal.getId());
        } catch (DataAccessException e) {
            //dummy
        }

        appeal.setCategory(category);
        appeal.setDepartment(department);
        appeal.setEmployee(employee);
        appeal.setAppealComms(appealComms);
        appeal.setAttachments(attachments);
        return appeal;
    };

    private final RowMapper<AppealComm> appealCommRowMapper = (rs, rowNum) -> {
        String lang = RepositoryUtils.getStringResult(rs, "lang");
        AppealComm appealComm = new AppealComm();
        appealComm.setAppealId(rs.getInt("appeal_id"));
        appealComm.setCreateDate(rs.getTimestamp("create_date"));
        appealComm.setTextBody(RepositoryUtils.getStringResult(rs, "text_body"));
        appealComm.setEmployee(employeeRepository.getEmployeeById(rs.getInt("author"), lang));
        return appealComm;
    };

    private final RowMapper<Attachment> attachmentRowMapper = (rs, rowNum) -> {
        Attachment attachment = new Attachment();
        attachment.setAppealId(rs.getInt("appeal_id"));
        attachment.setAttachNum(rs.getInt("attach_num"));
        attachment.setName(RepositoryUtils.getStringResult(rs, "name"));
        return attachment;
    };

    @Override
    public int createAppeal(AppealUtils.AppealCreatePayload appealCreatePayload) throws DataAccessException {
        Integer appealId = jdbcTemplate.queryForObject("INSERT INTO appeal VALUES (DEFAULT, NULL, 1, NULL, ?, ?, ?, ?, ?, ?, ?," +
                        " NOW(), CURRENT_DATE + INTERVAL '1 day 23 hours 59 minutes 59 seconds') RETURNING appeal_id",
                new Object[]{
                    statusRepository.getInitialStatus(),
                    appealCreatePayload.getTitle(),
                    appealCreatePayload.getTextBody(),
                    appealCreatePayload.getFirstName(),
                    appealCreatePayload.getLastName(),
                    appealCreatePayload.getMiddleName(),
                    appealCreatePayload.getEmail()
                },
                new int[] {
                    Types.INTEGER,
                    Types.VARCHAR,
                    Types.VARCHAR,
                    Types.VARCHAR,
                    Types.VARCHAR,
                    Types.VARCHAR,
                    Types.VARCHAR
                },
                Integer.class
        );
        return appealId == null ? 0 : appealId;
    }

    @Override
    public Appeal getAppeal(int appealId, String lang) throws DataAccessException {
        Appeal appeal = jdbcTemplate.queryForObject("SELECT *, ? AS lang FROM appeal WHERE appeal_id = ?",
                new Object[] { lang, appealId }, new int[] { Types.CHAR, Types.INTEGER }, appealRowMapper);
        if(appeal == null)
            throw new EmptyResultDataAccessException("No appeal entries", 1);

        return appeal;
    }

    private int getDeadLine(int categoryId) {
        Integer res = jdbcTemplate.queryForObject("SELECT days FROM dl_property WHERE department_id = 1 AND category_id = ?",
                new Object[] { categoryId }, new int[] {Types.INTEGER}, Integer.class);
        return res == null ? 0 : res;
    }

    @Override
    public void updateAppeal(AppealUtils.AppealUpdatePayload payload) throws DataAccessException {
        StringBuilder sql = new StringBuilder("UPDATE appeal SET ");
        if(payload.getCategoryId() > 0) {
            sql.append("category_id = ").append(payload.getCategoryId()).append(", ");
            int deadLineDays = getDeadLine(payload.getCategoryId());
            if(deadLineDays > 0) {
                sql.append("deadline = create_date + interval '").append(deadLineDays).append(" days', ");
            }
        }
        if(payload.getEmployeeId() > 0)
            sql.append("employee_id = ").append(payload.getEmployeeId()).append(", ");
        sql.append("status_code = ? WHERE appeal_id = ?");

        jdbcTemplate.update(sql.toString(), payload.getStatusId(), payload.getId());
    }

    @Override
    public List<AppealComm> getAppealComms(int appealId, String lang) throws DataAccessException {
        return jdbcTemplate.query("SELECT *, ? AS lang FROM appeal_comm " +
                "WHERE appeal_id = ?",
                new Object[]{ lang, appealId },
                new int[] { Types.CHAR, Types.INTEGER },
                appealCommRowMapper
        );
    }

    @Override
    public List<Attachment> getAttachments(int appealId) throws DataAccessException {
        return jdbcTemplate.query("SELECT * FROM attachment " +
                "WHERE appeal_id = ?",
                new Object[] { appealId },
                new int[] { Types.INTEGER },
                attachmentRowMapper
        );
    }
    @Override

    public void addComm(AppealUtils.AppealCommPayload payload) throws DataAccessException {
        jdbcTemplate.update("INSERT INTO appeal_comm VALUES (?, DEFAULT, ?, ?)",
                payload.getAppealId(), payload.getAuthor(), payload.getTextBody());
    }

    @Override
    public void addAttach(int appealId, int attachNum, String name, String path) throws DataAccessException {
        jdbcTemplate.update("INSERT INTO attachment VALUES (?, ?, ?, ?)",
                appealId, attachNum, name, path);
    }

    @Override
    public List<Appeal> getAppeals(int start, int limit) throws DataAccessException {
        return jdbcTemplate.query("SELECT *, 'RU' AS lang FROM appeal ORDER BY create_date DESC LIMIT ? OFFSET ?",
                new Object[]{limit, start}, new int[] {Types.INTEGER, Types.INTEGER}, appealRowMapper);
    }
}
