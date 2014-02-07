package com.github.steveash.jtribespso.impl;

import com.github.steveash.jtribespso.EuclidianVector;
import com.github.steveash.jtribespso.IObjectiveFunction;
import com.github.steveash.jtribespso.rand.HyperspaceRandom;
import com.google.common.util.concurrent.ListeningExecutorService;

public class MultithreadedHypersphereSearchSpace extends MultithreadedSearchSpace<HypersphereParticle> {

    public MultithreadedHypersphereSearchSpace(ListeningExecutorService pool, IObjectiveFunction objectiveFunction,
            int workerCount) {

        super(pool, objectiveFunction, workerCount);
    }

    @Override
    protected HypersphereParticle generateParticleAtPosition(EuclidianVector position) {
        return new HypersphereParticle(this.goodnessFunction(), position, new HyperspaceRandom());
    }
}