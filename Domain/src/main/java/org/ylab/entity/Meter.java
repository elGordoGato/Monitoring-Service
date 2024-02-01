package org.ylab.entity;

import lombok.*;

/**
 * Meter type entity
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Meter {
    /**
     * ID of meter
     */
    private int id;
    /**
     * Name of meter type
     */
    private String type;
}
