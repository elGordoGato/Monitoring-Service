package org.ylab.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Meter reading entity
 */
@NoArgsConstructor
@Getter
@Setter
public class Reading {
    /**
     * ID of meter reading
     */
    private UUID id;
    /**
     * User that submitted reading
     */
    private User owner;
    /**
     * Type of meter for this reading
     */
    private Meter meter;
    /**
     * Value of reading
     */
    private long reading;
    /**
     * Date and time when reading was submitted
     */
    private Instant collectedDate;
}
