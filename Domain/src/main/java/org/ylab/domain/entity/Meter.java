package org.ylab.domain.entity;

import lombok.*;

/**
 * Meter type entity
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Meter {
    /**
     * ID of meter
     */
    private Short id;
    /**
     * Name of meter type
     */
    private String type;
}
