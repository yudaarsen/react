package ru.gadzhiev.mirea_project.repositories.interfaces;

import org.springframework.dao.DataAccessException;
import ru.gadzhiev.mirea_project.models.Category;

import java.util.List;

public interface ClassificationRepository {

    Category getCategory(int id, String lang) throws DataAccessException;
    List<Category> getCategories() throws DataAccessException;

}
