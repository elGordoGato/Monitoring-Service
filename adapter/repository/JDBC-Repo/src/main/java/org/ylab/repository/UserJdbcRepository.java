package org.ylab.repository;

import org.ylab.ConnectionManager;
import org.ylab.entity.User;
import org.ylab.enums.Role;
import org.ylab.port.UserRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserJdbcRepository implements UserRepository {
    private final static String SAVE_QUERY = "INSERT INTO entities.users (email, first_name, last_name, password) " +
            "VALUES (?,?,?,?) RETURNING id";
    private final static String FIND_BY_EMAIL_QUERY = "SELECT * FROM entities.users WHERE email = ?";
    private final Connection connection;

    public UserJdbcRepository(ConnectionManager connectionManager) {
        connection = connectionManager.getConnection();
    }

    @Override
    public User save(User user) {

        try (PreparedStatement pstmt = connection.prepareStatement(
                SAVE_QUERY)) {
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getFirstName());
            pstmt.setString(3, user.getLastName());
            pstmt.setString(4, user.getPassword());
            pstmt.execute();
            ResultSet rs = pstmt.getResultSet();
            rs.next();
            user.setId(rs.getInt("id"));
            user.setRole(Role.USER);
            connection.commit();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        User foundUser = null;
        try (PreparedStatement pstmt = connection.prepareStatement(
                FIND_BY_EMAIL_QUERY)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String password = rs.getString("password");
                Role role = Role.valueOf(rs.getString("role"));
                foundUser = new User(id, email, firstName, lastName, password, role);
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(foundUser);
    }
}
