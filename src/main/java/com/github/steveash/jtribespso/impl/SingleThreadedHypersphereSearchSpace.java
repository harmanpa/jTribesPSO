package com.github.steveash.jtribespso.impl;

import com.github.steveash.jtribespso.EuclidianVector;
import com.github.steveash.jtribespso.IObjectiveFunction;
import com.github.steveash.jtribespso.rand.IHyperspaceRandom;

/**
 * A single threaded implementation of search space that uses HypersphereParticles
 */

public class SingleThreadedHypersphereSearchSpace extends SingleThreadedSearchSpace<HypersphereParticle> {

    public SingleThreadedHypersphereSearchSpace(IObjectiveFunction objectiveFunction) {
        super(objectiveFunction);
    }

    public SingleThreadedHypersphereSearchSpace(IObjectiveFunction objectiveFunction, IHyperspaceRandom randomNumberGenerator) {
        super(objectiveFunction, randomNumberGenerator);
    }

    /**
     * Creates a new HypersphereParticle at the specified location and the SearchSpaces own IObjectiveFunction
     * @param position
     * @return
     */
    @Override
    protected HypersphereParticle generateParticleAtPosition(EuclidianVector position) {
        return new HypersphereParticle(this.goodnessFunction(), position);
    }
}

