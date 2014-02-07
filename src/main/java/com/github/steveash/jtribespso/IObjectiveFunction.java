package com.github.steveash.jtribespso;

/**
 * function to minimize with an existing PSO implementation
 */
public interface IObjectiveFunction {
    /**
     * @return The number of dimensions in the function to be minimized
     */
    int getDimensions();

    /**
     * @return The minimum bounds of the search space.  Depending on the implementation of the searchspace,
     * Particles may go outside of the minimum bounds but new particles should never be generated
     * outside of the minimum bounds
     */
    EuclidianVector getMinBounds();

    /**
     * @return the maximium bounds of the search space
     */
    EuclidianVector getMaxBounds();

    /**
     * @return initial guess to bootstrap the swarm
     */
    EuclidianVector getInitialGuess();

    /**
     * @param guess
     * @return fitness of this guess
     */
    double evaluate(EuclidianVector guess);
}