package ru.gadzhiev.mirea_project.repositories.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.gadzhiev.mirea_project.models.Department;
import ru.gadzhiev.mirea_project.models.Employee;
import ru.gadzhiev.mirea_project.repositories.interfaces.EmployeeRepository;
import ru.gadzhiev.mirea_project.utils.RepositoryUtils;

import java.sql.Types;
import java.util.List;

/**
 * Реализация репозитория для работы с сотрудниками
 */
@Repository
public class EmployeeRepositoryImpl implements EmployeeRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Department> departmentRowMapper = (rs, rowNum) -> {
        Department department = new Department();
        department.setId(rs.getInt("department_id"));
        department.setName(RepositoryUtils.getStringResult(rs, "dep_name"));
        department.setLang(RepositoryUtils.getStringResult(rs, "lang"));
        return department;
    };

    private final RowMapper<Employee> employeeRowMapper = (rs, rowNum) -> {
        Employee employee = new Employee();
        employee.setId(rs.getInt("emp_id"));
        employee.setEmail(RepositoryUtils.getStringResult(rs, "email"));
        employee.setPhone(RepositoryUtils.getStringResult(rs, "phone"));
        employee.setFirstName(RepositoryUtils.getStringResult(rs, "first_name"));
        employee.setLastName(RepositoryUtils.getStringResult(rs, "last_name"));
        employee.setMiddleName(RepositoryUtils.getStringResult(rs, "middle_name"));
        employee.setPassword(RepositoryUtils.getStringResult(rs, "passwrd"));
        Department department = new Department();
        department.setId(rs.getInt("department_id"));
        employee.setDepartment(department);
        return employee;
    };

    @Override
    public Department getDepartment(int id, String lang) throws DataAccessException {
        return jdbcTemplate.queryForObject("SELECT department.department_id AS department_id, lang, dep_name FROM department " +
                "INNER JOIN departmentt ON department.department_id = departmentt.department_id " +
                "WHERE department.department_id = ? AND lang = ?",
                new Object[] { id, lang },
                new int[] { Types.INTEGER, Types.CHAR },
                departmentRowMapper
        );
    }

    @Override
    public Employee getEmployeeById(int id, String lang) throws DataAccessException {
        Employee employee = jdbcTemplate.queryForObject("SELECT * FROM employee WHERE emp_id = ?",
                new Object[] { id },
                new int[] { Types.INTEGER },
                employeeRowMapper
        );

        if(employee == null)
            return null;

        employee.setDepartment(getDepartment(employee.getDepartment().getId(), lang));
        return employee;
    }

    @Override
    public Employee getEmployeeByEmailAndPassword(String email, String password) throws DataAccessException {
        return jdbcTemplate.queryForObject("SELECT * FROM employee WHERE email = ? AND passwrd = ?",
                new Object[] { email, password },
                new int[] { Types.VARCHAR, Types.VARCHAR },
                employeeRowMapper
        );
    }

    @Override
    public List<Employee> getEmployees() throws DataAccessException {
        return jdbcTemplate.query("SELECT * FROM employee", employeeRowMapper);
    }
}
