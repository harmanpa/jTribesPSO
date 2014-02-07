package com.github.steveash.jtribespso;

/**
 * An immutable class that represents a solution to an IObjectiveFunction
 */
public class Solution {

    private final double error;
    private final EuclidianVector position;

    public double getError() {
        return error;
    }

    public EuclidianVector getPosition() {
        return position;
    }

    public Solution(EuclidianVector position, double error) {
        this.position = position;
        this.error = error;
    }

    public Solution(EuclidianVector position, IObjectiveFunction fitnessFunction) {
        this.position = position;
        this.error = fitnessFunction.evaluate(position);
    }
}