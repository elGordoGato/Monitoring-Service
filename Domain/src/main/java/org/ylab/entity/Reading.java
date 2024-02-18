package org.ylab.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Meter reading entity
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reading {
    /**
     * ID of meter reading
     */
    private Long id;
    /**
     * User that submitted reading
     */
    private UserEntity owner;
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

