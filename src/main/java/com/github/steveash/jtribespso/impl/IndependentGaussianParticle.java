package com.github.steveash.jtribespso.impl;

import com.github.steveash.jtribespso.EuclidianVector;
import com.github.steveash.jtribespso.EuclidianVectorBuilder;
import com.github.steveash.jtribespso.IObjectiveFunction;
import com.github.steveash.jtribespso.Particle;
import com.github.steveash.jtribespso.Solution;
import com.github.steveash.jtribespso.event.ParticleMovedEvent;
import com.github.steveash.jtribespso.rand.IHyperspaceRandom;
import com.google.common.eventbus.Subscribe;

/**
 * A particle implementation that moves using independant gaussian curves.  This is based on an approach described in
 * the 2003 paper "TRIBES, a Parameter Free Particle Swarm Optimizer" by Maurice Clerc
 */
public class IndependentGaussianParticle extends Particle {

    private static final double c = 0.71440817; //precomputed C for use in the formula used in CalculateNewPosition
    private EuclidianVector velocity;

    /**
     * Initializes a new Particle that moves based on independent gaussian distributions.  This particle will use
     * the default TribesPSO.HyperspaceRandom RNG to move
     * @param objectiveFunction
     * @param initialPosition
     */
    public IndependentGaussianParticle(IObjectiveFunction objectiveFunction, EuclidianVector initialPosition) {
        super(objectiveFunction, initialPosition);
        velocity = EuclidianVector.origin(objectiveFunction.getDimensions());

        eventBus().register(this);
    }

    @Subscribe
    public void onParticleMoved(ParticleMovedEvent e) {
        this.velocity = EuclidianVector.subtract(e.getNewPosition(), e.getOldPosition());
    }

    /**
     * Initializes a new Particle that moves based on independent gaussian distributions.
     * @param objectiveFunction
     * @param initialPosition
     * @param randomNumberGenerator
     */
    public IndependentGaussianParticle(IObjectiveFunction objectiveFunction, EuclidianVector initialPosition, IHyperspaceRandom randomNumberGenerator) {
        super(objectiveFunction, initialPosition, randomNumberGenerator);
        velocity = EuclidianVector.origin(objectiveFunction.getDimensions());
        eventBus().register(this);
    }

    /**
     * Calculate a new position using independant gaussian functions.
     * The formula used to calculate the next position of a particle using independant gaussian functions is as follows:
     * Vd = X(t)d - X(t-1)d (Velocity is the current position minus the last position)
     * Di = Pi,d - X(t)d   (Delta sub i is the current particle's best solution's position minus its current position)
     * Dg = Pg,d - X(t)d   (Delta sub g is the best informer's best solution's position minus this particle's current position)
     * X(t+1)d = X(t)d + C * (Vd + gauss_rand(Di,|Di|/2) + gauss_rand(Dg,|Dg|/2))
     * C = 1/(s-1+sqrt(s^2-2s))
     * s = 2/0.97225
     * @param bestInformerSolution
     * @return
     */
    @Override
    protected EuclidianVector calculateNewPosition(Solution bestInformerSolution) {
        EuclidianVectorBuilder vectorBuilder = new EuclidianVectorBuilder();
        for (int n = 0; n < this.getPosition().getDimensions(); n++) {
            double deltaI = this.bestSolution().getPosition().get(n) - this.getPosition().get(n);
            double deltaG = bestInformerSolution.getPosition().get(n) - this.getPosition().get(n);

            double gaussianI = getRandomNumberGenerator().nextGaussian(deltaI, Math.abs(deltaI) / 2);
            double gaussianG = getRandomNumberGenerator().nextGaussian(deltaG, Math.abs(deltaG) / 2);

            double xNext = this.getPosition().get(n) + c * (this.velocity.get(n) + gaussianI + gaussianG);

            vectorBuilder.add(xNext);
        }
        return vectorBuilder.build();
    }
}

