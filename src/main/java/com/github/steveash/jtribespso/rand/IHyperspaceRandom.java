package com.github.steveash.jtribespso.rand;

import com.github.steveash.jtribespso.EuclidianVector;

/**
 * This interface allows a user to specify their own hyperspace random number generator
 * to be used with existing PSO implementations
 */
public interface IHyperspaceRandom extends IGaussianRandom {

    /**
     * @param center
     * @return a vector based on a gaussian distribution at a specified center with a standard deviation of 1
     */
    EuclidianVector nextGaussianVector(EuclidianVector center);

    /**
     * @param center
     * @param sigma
     * @return a vector based on a gaussian distribution with a specified center and standard deviation
     */
    EuclidianVector nextGaussianVector(EuclidianVector center, double sigma);

    /**
     * @param center
     * @param radius
     * @return a vector based on a uniform distribution with a specified center and standard deviation
     */
    EuclidianVector nextUniformVector(EuclidianVector center, double radius);
}
