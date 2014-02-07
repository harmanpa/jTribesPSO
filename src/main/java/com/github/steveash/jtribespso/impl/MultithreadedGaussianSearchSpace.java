package com.github.steveash.jtribespso.impl;

import com.github.steveash.jtribespso.EuclidianVector;
import com.github.steveash.jtribespso.IObjectiveFunction;
import com.google.common.util.concurrent.ListeningExecutorService;

public class MultithreadedGaussianSearchSpace extends MultithreadedSearchSpace<IndependentGaussianParticle> {

    public MultithreadedGaussianSearchSpace(ListeningExecutorService pool, IObjectiveFunction objectiveFunction,
            int workerCount) {

        super(pool, objectiveFunction, workerCount);
    }

    @Override
    protected IndependentGaussianParticle generateParticleAtPosition(EuclidianVector position) {
        return new IndependentGaussianParticle(this.goodnessFunction(), position);
    }
}