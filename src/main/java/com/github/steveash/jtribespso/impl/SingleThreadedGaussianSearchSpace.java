package com.github.steveash.jtribespso.impl;

import com.github.steveash.jtribespso.EuclidianVector;
import com.github.steveash.jtribespso.IObjectiveFunction;
import com.github.steveash.jtribespso.rand.IHyperspaceRandom;

/**
 * A single threaded search space implementation that uses IndependantGaussian particles
 */
public class SingleThreadedGaussianSearchSpace extends SingleThreadedSearchSpace<IndependentGaussianParticle> {

    public SingleThreadedGaussianSearchSpace(IObjectiveFunction objectiveFunction) {
        super(objectiveFunction);
    }

    public SingleThreadedGaussianSearchSpace(IObjectiveFunction objectiveFunction, IHyperspaceRandom randomNumberGenerator) {
        super(objectiveFunction, randomNumberGenerator);
    }

    @Override
    protected IndependentGaussianParticle generateParticleAtPosition(EuclidianVector position) {
        return new IndependentGaussianParticle(this.goodnessFunction(), position);
    }
}

