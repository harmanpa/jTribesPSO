package com.github.steveash.jtribespso.impl;

import static com.github.steveash.jtribespso.EuclidianVector.subtract;

import com.github.steveash.jtribespso.EuclidianVector;
import com.github.steveash.jtribespso.EuclidianVectorBuilder;
import com.github.steveash.jtribespso.IObjectiveFunction;
import com.github.steveash.jtribespso.Particle;
import com.github.steveash.jtribespso.Solution;
import com.github.steveash.jtribespso.rand.IHyperspaceRandom;

/**
 * A particle implementation that moves based on hyperspherical distributions.  This implementation is based on the
 * 2003 paper "TRIBES, a Parameter Free Particle Swarm Optimizer" by Maurice Clerc
 */
public class HypersphereParticle extends Particle {

    /**
     * Initializes a new Particle that moves based on hyperspherical distributions.  This particle will use
     * the default TribesPSO.HyperspaceRandom RNG to move
     * @param objectiveFunction
     * @param initialPosition
     */
    public HypersphereParticle(IObjectiveFunction objectiveFunction, EuclidianVector initialPosition) {
        super(objectiveFunction, initialPosition);
    }

    /**
     * Initializes a new Particle that moves based on hyperspherical distributions
     * @param objectiveFunction
     * @param initialPosition
     * @param randomNumberGenerator
     */
    public HypersphereParticle(IObjectiveFunction objectiveFunction, EuclidianVector initialPosition,
            IHyperspaceRandom randomNumberGenerator) {
        super(objectiveFunction, initialPosition, randomNumberGenerator);
    }

    /**
     * Computes the next position of a particle by creating a hypersphere around this particle's best solution and the best
     * solution of its informers.  The new position is the center of gravity of two points chosen within these two hypersphers weighted
     * by how good this particle's best solution is and how good the solution of its best informer is.
     * Furthermore, if a particle has not improved its best performance two times in a row, we'll add a bit of gaussian noise
     * to the final position of the particle
     * @param bestInformerSolution
     * @return
     */
    @Override
    protected EuclidianVector calculateNewPosition(Solution bestInformerSolution) {
        // Begin with two points in the search space:  The particle's best perfomance P and the best of it's informers G
        EuclidianVector P = this.bestSolution().getPosition();
        EuclidianVector G = bestInformerSolution.getPosition();

        // Consider two hyperspheres Hp and Hg centered on P and G with a radius equal to the distance between
        // P and G Pick two points P' and G' based on a uniform distribution within their respective hyperspheres
        double radius = subtract(P, G).magnitude();
        EuclidianVector Pprime = this.getRandomNumberGenerator().nextUniformVector(P, radius);
        EuclidianVector Gprime = this.getRandomNumberGenerator().nextUniformVector(G, radius);

        // Weight each point based on the relative qualities of the solutions at P and G
        double totalError = this.bestSolution().getError() + bestInformerSolution.getError();
        double PprimeWeight = bestInformerSolution.getError() / totalError;
        double GprimeWeight = this.bestSolution().getError() / totalError;

        // Finally, compute the new position as the weighted center of gravity between Pprime and Gprime
        EuclidianVector newPosition = EuclidianVector.centerOfGravity(Pprime, PprimeWeight, Gprime, GprimeWeight);

        if (!this.isExcellent()) {
            double noiseStdev = Math.abs(this.bestSolution().getError() - bestInformerSolution.getError()) /
                    (this.bestSolution().getError() + bestInformerSolution.getError());

                /* Here is the more readable, original, slower, version of the code.
                 * The uncommented code is about 17% faster according to the benchmarks*/
            //EuclidianVector Velocity = NewPosition - this.Position;
            //List<double> NewVelocityVectors = new List<double>();
            //for (int n = 0; n < Velocity.Dimensions; n++)
            //{
            //    NewVelocityVectors.Add(Velocity[n] * (1 + this.RandomNumberGenerator.NextGaussian(0, noiseStdev)));
            //}
            //return this.Position + new EuclidianVector(NewVelocityVectors);

                /* Faster version */
            EuclidianVectorBuilder noisyNewPosition = new EuclidianVectorBuilder();
            for (int n = 0; n < newPosition.getDimensions(); n++) {
                double noisyVelocity = (newPosition.get(n) - this.getPosition().get(n)) *
                        (1 + this.getRandomNumberGenerator().nextGaussian(0, noiseStdev));

                noisyNewPosition.add(this.getPosition().get(n) + noisyVelocity);
            }
            // TODO seems a little strange to me that in the excellent case you just blindly adopt the new
            // position which isn't really based on the old position... and in the not excellent case you
            // adopt a position which is just a "move" from the old positoin (which seems reasonable in both
            // cases to me... although I kinda can see it the other way...
            newPosition = noisyNewPosition.build();
        }
        return newPosition;
    }
}

