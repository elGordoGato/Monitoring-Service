package org.ylab.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Meter type entity
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
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
