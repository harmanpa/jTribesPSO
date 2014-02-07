package com.github.steveash.jtribespso.rand;

import java.util.Random;

public class JdkRandom implements IRandom {

    private final Random delegate;

    public JdkRandom(long seed) {
        this.delegate = new Random(seed);
    }

    public JdkRandom() {
        this.delegate = new Random();
    }

    @Override
    public int nextInt() {
        return delegate.nextInt();
    }

    @Override
    public int nextInt(int maxValue) {
        return delegate.nextInt(maxValue);
    }

    @Override
    public int nextInt(int minValue, int maxValue) {
        return delegate.nextInt(maxValue - minValue) + minValue;
    }

    @Override
    public double nextDouble() {
        return delegate.nextDouble();
    }

    @Override
    public double nextDouble(double minValue, double maxValue) {
        double range = maxValue - minValue;
        return (range * nextDouble() + minValue);
    }
}