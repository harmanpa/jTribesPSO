package com.github.steveash.jtribespso.rand;

import com.github.steveash.jtribespso.EuclidianVector;
import com.github.steveash.jtribespso.EuclidianVectorBuilder;

/**
 * HyperspaceRandom is a wrapper for GaussianRandom that can generate vectors instead of scalars
 */
public class HyperspaceRandom extends GaussianRandom implements IHyperspaceRandom {

    public HyperspaceRandom() {
    }

    public HyperspaceRandom(long seed) {
        super(seed);
    }

    @Override
    public EuclidianVector nextGaussianVector(EuclidianVector center) {
        return nextGaussianVector(center, 1);
    }

    @Override
    public EuclidianVector nextGaussianVector(EuclidianVector center, double sigma) {
        EuclidianVectorBuilder builder = new EuclidianVectorBuilder();
        for (int n = 0; n < center.getDimensions(); n++) {
            builder.add(center.get(n) + this.nextGaussian(0, sigma));
        }
        return builder.build();
    }

    @Override
    public EuclidianVector nextUniformVector(EuclidianVector center, double radius) {
        //Generate a vector that points in a random direction and normalize it
        EuclidianVector rawVector = this.nextGaussianVector(EuclidianVector.origin(center.getDimensions()));

        //Compute a length for the vector between 0 and radius
        double randomRadius = nextDouble() * radius;
        double normalizer = (randomRadius / rawVector.magnitude());

        EuclidianVectorBuilder builder = new EuclidianVectorBuilder();
        for (int n = 0; n < center.getDimensions(); n++) {
            builder.add(center.get(n) + (rawVector.get(n) * normalizer));
        }
        return builder.build();
    }
}
