package com.github.steveash.jtribespso.event;

import com.github.steveash.jtribespso.EuclidianVector;

/**
 * Provides data for the Particle.ParticleMoved event
 */
public class ParticleMovedEvent {

    private final EuclidianVector oldPosition;
    private final EuclidianVector newPosition;

    public EuclidianVector getOldPosition() {
        return oldPosition;
    }

    public EuclidianVector getNewPosition() {
        return newPosition;
    }

    public ParticleMovedEvent(EuclidianVector oldPosition, EuclidianVector newPosition) {
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
    }
}
