package com.github.steveash.jtribespso.rand;

/**
 * Abstraction of a random number generator
 */
public interface IRandom {

    /**
     * @return A 32-bit signed integer greater than or equal to zero and less than Int.MaxValue
     */
    int nextInt();

    /**
     * @param maxValue exclusive bounds
     * @return
     */
    int nextInt(int maxValue);

    /**
     * @param minValue inclusive bounds
     * @param maxValue exclusive bounds
     * @return
     */
    int nextInt(int minValue, int maxValue);

    /**
     * @return random value in [0.0, 1.0]
     */
    double nextDouble();

    /**
     * @param minValue
     * @param maxValue
     * @return random value in [minValue, maxValue]
     */
    double nextDouble(double minValue, double maxValue);
}