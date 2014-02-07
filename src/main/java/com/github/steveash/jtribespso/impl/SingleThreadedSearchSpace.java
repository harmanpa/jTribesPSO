package com.github.steveash.jtribespso.impl;

import java.util.ArrayList;
import java.util.Collections;

import com.github.steveash.jtribespso.IObjectiveFunction;
import com.github.steveash.jtribespso.Particle;
import com.github.steveash.jtribespso.SearchSpace;
import com.github.steveash.jtribespso.Tribe;
import com.github.steveash.jtribespso.rand.HyperspaceRandom;
import com.github.steveash.jtribespso.rand.IHyperspaceRandom;
import com.google.common.collect.Lists;

/**
 * A single threaded (partial) implementation of SearchSpace that moves the particles in a random order everytime Move() is called
 * SingleThreadedSearchSpace does not supply an implementation of GenerateParticleAtPosition(EuclidianVector position).
 * For a concrete implementation of SingleThreadedSearchSpace, see SingleThreadedGaussianSearchSpace and
 * SingleThreadedHypersphereSearchSpace
 * @param <TParticle>
 */
public abstract class SingleThreadedSearchSpace<TParticle extends Particle> extends SearchSpace<TParticle> {

    /**
     * Initializes a SingleThreadedSearchSpace object to optimize the specified objective function using the default
     * TribesPSO.HyperspaceRandom random number generator
     * @param objectiveFunction
     */
    protected SingleThreadedSearchSpace(IObjectiveFunction objectiveFunction) {
        super(objectiveFunction, new HyperspaceRandom());
    }

    /**
     * Initializes a SingleThreadedSearchSpace object to optimize the specified objective function
     * @param objectiveFunction
     * @param randomNumberGenerator
     */
    protected SingleThreadedSearchSpace(IObjectiveFunction objectiveFunction, IHyperspaceRandom randomNumberGenerator) {
        super(objectiveFunction, randomNumberGenerator);
    }

    /**
     * Moves all of the particles in the swarm. Particles are moved serially within a tribe.
     * The tribes are moved in a random order
     */
    @Override
    protected void move() {
        ArrayList<Tribe> randomOrder = Lists.newArrayList(this.tribes());
        Collections.shuffle(randomOrder);

        for (Tribe tribe : randomOrder) {
            for (Particle particle : tribe.tribeMembers()) {
                particle.move();
            }
        }
    }
}
