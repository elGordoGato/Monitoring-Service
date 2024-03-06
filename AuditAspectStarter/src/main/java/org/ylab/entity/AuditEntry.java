package org.ylab.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.ylab.domain.entity.UserEntity;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@ToString
public class AuditEntry {
    @Id
    private Long id;

    private String controller;

    private String method;

    @Column("users")
    private UserEntity requester;

    private List<String> params;

    private Instant createdAt = Instant.now();
}
