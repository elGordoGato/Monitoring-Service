package org.ylab.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class MeterReading {
    private UUID id;
    private User owner;
    private MeterType meter;
    private long reading;
    private Instant collectedDate;
}
