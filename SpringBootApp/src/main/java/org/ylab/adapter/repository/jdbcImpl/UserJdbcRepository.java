package org.ylab.adapter.repository.jdbcImpl;

import org.springframework.stereotype.Repository;
import org.ylab.domain.entity.UserEntity;
import org.ylab.domain.enums.Role;
import org.ylab.usecase.port.UserRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

@Repository
public class UserJdbcRepository implements UserRepository {
    private final static String SAVE_QUERY = "INSERT INTO entities.users (email, first_name, last_name, password) " +
            "VALUES (?,?,?,?)";
    private final static String FIND_BY_EMAIL_QUERY = "SELECT * FROM entities.users WHERE email = ?";
    private final Connection connection;

    public UserJdbcRepository(DataSource dataSource) throws SQLException {
        connection = dataSource.getConnection();
    }


    @Override
    public UserEntity save(UserEntity user) {

        try (PreparedStatement pstmt = connection.prepareStatement(
                SAVE_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getFirstName());
            pstmt.setString(3, user.getLastName());
            pstmt.setString(4, user.getPassword());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            rs.next();
            user.setId(rs.getInt(1));
            user.setRole(Role.USER);
            rs.close();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        UserEntity foundUser = null;
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
                foundUser = new UserEntity(id, email, firstName, lastName, password, role);
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(foundUser);
    }
}
