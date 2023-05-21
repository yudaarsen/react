package ru.gadzhiev.mirea_project.repositories.interfaces;

import org.springframework.dao.DataAccessException;
import ru.gadzhiev.mirea_project.models.Department;
import ru.gadzhiev.mirea_project.models.Employee;

import java.util.List;

public interface EmployeeRepository {
    Department getDepartment(int id, String lang) throws DataAccessException;
    Employee getEmployeeById(int id, String lang) throws DataAccessException;
    Employee getEmployeeByEmailAndPassword(String email, String password) throws DataAccessException;
    List<Employee> getEmployees() throws DataAccessException;
}
