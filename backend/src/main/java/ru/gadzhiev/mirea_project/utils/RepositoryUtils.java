package ru.gadzhiev.mirea_project.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RepositoryUtils {
    public static String getStringResult(ResultSet resultSet, String fieldName) throws SQLException {
        String result = resultSet.getString(fieldName);
        if(result == null)
            result = "";
        return result;
    }

    public static String getStringResult(ResultSet resultSet, String fieldName, String defaultValue) throws SQLException {
        String result = resultSet.getString(fieldName);
        if(result == null)
            result = defaultValue;
        return result;
    }

}
