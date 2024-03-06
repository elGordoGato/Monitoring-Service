package org.ylab.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.ylab.domain.entity.UserEntity;
import org.ylab.entity.AuditEntry;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuditRepository {
    private final JdbcTemplate jdbcTemplate;

    public AuditEntry save(AuditEntry entry) {
        Integer requesterId = Optional.ofNullable(entry.getRequester())
                .map(UserEntity::getId)
                .orElse(null);
        Map<String, Object> args = new HashMap<>();
        args.put("controller", entry.getController());
        args.put("method", entry.getMethod());
        args.put("requester", requesterId);
        args.put("params", entry.getParams());
        args.put("created_at", Timestamp.from(entry.getCreatedAt()));
        Map<String, Object> keys = new SimpleJdbcInsert(this.jdbcTemplate)
                .withSchemaName("audit")
                .withTableName("entry")
                .usingColumns("controller", "method", "requester", "params", "created_at")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKeyHolder(args)
                .getKeys();
        assert keys != null;
        Long id = (Long) keys.get("id");
        entry.setId(id);
        return entry;
    }
}
