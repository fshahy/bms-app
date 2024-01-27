/*
 * Copyright (c) 2019, 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bms.employees;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import io.helidon.common.context.Contexts;
import io.helidon.config.Config;
import io.helidon.dbclient.DbClient;
import io.helidon.dbclient.DbRow;

/**
 * Implementation of the {@link EmployeeRepository}. This implementation uses an
 * Oracle database to persist the Employee objects.
 */
final class EmployeeRepository {

    private final DbClient dbClient;

    /**
     * Creates the database connection using the parameters specified in the
     * <code>application.yaml</code> file located in the <code>resources</code> directory.
     *
     * @param config Represents the application configuration.
     */
    EmployeeRepository() {
        Config dbConfig = Config.global().get("db");
        dbClient = Contexts.globalContext()
                            .get(DbClient.class)
                            .orElseGet(() -> DbClient.create(dbConfig));
    }

    public List<Employee> getAll() {
        String queryStr = "SELECT * FROM EMPLOYEE";

        return toEmployeeList(dbClient.execute().query(queryStr));
    }

    public List<Employee> getByLastName(String name) {
        String queryStr = "SELECT * FROM EMPLOYEE WHERE lastName LIKE ?";

        return toEmployeeList(dbClient.execute().query(queryStr, name));
    }

    public List<Employee> getByTitle(String title) {
        String queryStr = "SELECT * FROM EMPLOYEE WHERE title LIKE ?";

        return toEmployeeList(dbClient.execute().query(queryStr, title));
    }

    public List<Employee> getByDepartment(String department) {
        String queryStr = "SELECT * FROM EMPLOYEE WHERE department LIKE ?";

        return toEmployeeList(dbClient.execute().query(queryStr, department));
    }

    public Employee save(Employee employee) {
        String insertTableSQL = "INSERT INTO EMPLOYEE "
                + "(id, firstName, lastName, email, phone, birthDate, title, department) "
                + "VALUES(?,?,?,?,?,?,?,?)";

        dbClient.execute()
                .createInsert(insertTableSQL)
                .addParam(employee.getId())
                .addParam(employee.getFirstName())
                .addParam(employee.getLastName())
                .addParam(employee.getEmail())
                .addParam(employee.getPhone())
                .addParam(employee.getBirthDate())
                .addParam(employee.getTitle())
                .addParam(employee.getDepartment())
                .execute();
        // let's always return the employee once the insert finishes
        return employee;
    }

    public long deleteById(String id) {
        String deleteRowSQL = "DELETE FROM EMPLOYEE WHERE id=?";

        return dbClient.execute().delete(deleteRowSQL, id);
    }

    public Optional<Employee> getById(String id) {
        String queryStr = "SELECT * FROM EMPLOYEE WHERE id =?";

        return dbClient.execute()
                .get(queryStr, id)
                .map(row -> row.as(Employee.class));
    }

    public long update(Employee updatedEmployee, String id) {
        String updateTableSQL = "UPDATE EMPLOYEE SET firstName=?, lastName=?, email=?, phone=?, birthDate=?, title=?, "
                + "department=?  WHERE id=?";

        return dbClient.execute()
                .createUpdate(updateTableSQL)
                .addParam(updatedEmployee.getFirstName())
                .addParam(updatedEmployee.getLastName())
                .addParam(updatedEmployee.getEmail())
                .addParam(updatedEmployee.getPhone())
                .addParam(updatedEmployee.getBirthDate())
                .addParam(updatedEmployee.getTitle())
                .addParam(updatedEmployee.getDepartment())
                .addParam(id)
                .execute();
    }

    private static List<Employee> toEmployeeList(Stream<DbRow> rows) {
        return rows.map(EmployeeDbMapper::read).toList();
    }

    private static final class EmployeeDbMapper {
        private EmployeeDbMapper() {
        }

        static Employee read(DbRow row) {
            // map named columns to an object
            return Employee.of(
                    row.column("id").get(String.class),
                    row.column("firstName").get(String.class),
                    row.column("lastName").get(String.class),
                    row.column("email").get(String.class),
                    row.column("phone").get(String.class),
                    row.column("birthDate").get(String.class),
                    row.column("title").get(String.class),
                    row.column("department").get(String.class)
            );
        }
    }
}
