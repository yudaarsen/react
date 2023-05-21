package ru.gadzhiev.mirea_project.repositories.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.gadzhiev.mirea_project.models.Category;
import ru.gadzhiev.mirea_project.repositories.interfaces.ClassificationRepository;
import ru.gadzhiev.mirea_project.utils.RepositoryUtils;

import java.sql.Types;
import java.util.List;

/**
 * Реализация репозитория для работы с категориями
 */
@Repository
public class ClassificationRepositoryImpl implements ClassificationRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Category> categoryRowMapper = (rs, rowNum) -> {
        Category category = new Category();
        category.setCode(rs.getInt("category_id"));
        category.setName(RepositoryUtils.getStringResult(rs, "name"));
        category.setLang(RepositoryUtils.getStringResult(rs, "lang"));
        return category;
    };

    @Override
    public Category getCategory(int id, String lang) throws DataAccessException {
        return jdbcTemplate.queryForObject("SELECT category.category_id AS category_id, name, lang FROM category " +
                "INNER JOIN categoryt ON category.category_id = categoryt.category_id " +
                "WHERE category.category_id = ? AND lang = ?",
                new Object[] { id, lang },
                new int[] {Types.INTEGER, Types.CHAR },
                categoryRowMapper
        );
    }

    @Override
    public List<Category> getCategories() throws DataAccessException {
        return jdbcTemplate.query("SELECT category.category_id AS category_id, name, lang FROM category " +
                "INNER JOIN categoryt ON category.category_id = categoryt.category_id " +
                "WHERE lang = 'RU'", categoryRowMapper);
    }
}
