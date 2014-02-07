package com.github.steveash.jtribespso.rand;

public interface IGaussianRandom extends IRandom {

    /**
     * @return a random number based on a gaussian distribution with a mean of zero and a standard
     */
    double nextGaussian();

    /**
     * @param mu
     * @param sigma
     * @return a random number with a specified mean and standard deviation
     */
    double nextGaussian(double mu, double sigma);
}
