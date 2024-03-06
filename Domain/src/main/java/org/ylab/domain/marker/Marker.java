package org.ylab.domain.marker;

/**
 * Interface marker for setting validation groups
 */
public interface Marker {
    /**
     * Used when creating new entry
     */
    interface OnCreate {
    }

    /**
     * Used when updating existing entry
     */
    interface OnUpdate {
    }
}
