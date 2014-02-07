package com.github.steveash.jtribespso.test;

import com.github.steveash.jtribespso.EuclidianVector;
import com.github.steveash.jtribespso.IObjectiveFunction;
import com.github.steveash.jtribespso.Particle;
import com.github.steveash.jtribespso.SearchSpace;
import com.github.steveash.jtribespso.impl.IndependentGaussianParticle;

/**
 * @author Steve Ash
 */
public class BaseSearchSpace extends SearchSpace<IndependentGaussianParticle> {

    public BaseSearchSpace(IObjectiveFunction objectiveFunction) {
        super(objectiveFunction);
    }

    @Override
    protected void move() {
        for (Particle particle : this.tribeMembers()) {
            particle.move();
        }
    }

    @Override
    protected IndependentGaussianParticle generateParticleAtPosition(EuclidianVector position) {
        return new IndependentGaussianParticle(this.goodnessFunction(), position);
    }
}
