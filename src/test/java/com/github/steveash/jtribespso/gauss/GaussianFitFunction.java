package com.github.steveash.jtribespso.gauss;

import java.util.List;

import com.github.steveash.jtribespso.EuclidianVector;
import com.github.steveash.jtribespso.IObjectiveFunction;
import com.github.steveash.jtribespso.test.ImmutableGaussianCurve;
import com.google.common.collect.Lists;

public class GaussianFitFunction implements IObjectiveFunction {
    int evaluations = 0;

    private List<double[]> data;
    private int dimensions;

    private EuclidianVector maxBounds;
    private EuclidianVector minBounds;

    public GaussianFitFunction(int numberOfCurves, Iterable<double[]> input) {
        data = Lists.newArrayList(input);
        dimensions = 3 * numberOfCurves;

        maxBounds = computeMinBounds(dimensions, data);
        minBounds = computeMaxBounds(dimensions, data);
    }

    @Override
    public int getDimensions() {
        return this.dimensions;
    }

    @Override
    public EuclidianVector getMinBounds() {
        return this.minBounds;
    }

    @Override
    public EuclidianVector getMaxBounds() {
        return this.maxBounds;
    }

    @Override
    public EuclidianVector getInitialGuess() {
        return null;
    }

    /**
     * Evaluates the performance of a point at the normalized Euclidian Vector
     * @param position
     * @return
     */
    public double evaluate(EuclidianVector position) {
        evaluations += 1;
        MultiGaussianCurve candidateFunction = getSolution(position);
        double sqrerr = 0;
        for (double[] doubles : data) {

            double delta = candidateFunction.evaluate(doubles[0]) - doubles[1];
            sqrerr += (delta * delta);
        }
        return sqrerr;
    }

    /**
     * Decomposes the solution at the normalized Position p into a collection of gaussian functions that have been
     * de-normalized to match the original data
     * returns A normalized position representing a solution to the problem
     * @param position
     * @return
     */
    public List<ImmutableGaussianCurve> decomposeSolution(EuclidianVector position) {
        //Divide each dimension by the normalization constant for that dimension to get the denormalized dimensions
        List<ImmutableGaussianCurve> components = Lists.newArrayList();
        for (int n = 0; n < position.getDimensions() / 3; n++) {
            int baseIndex = n * 3;
            components.add(
                    new ImmutableGaussianCurve(
                            position.get(baseIndex),
                            position.get(baseIndex + 1),
                            position.get(baseIndex + 2)
                    )
            );
        }
        return components;
    }

    /**
     * Decomposes the solution at a normalized position p into a MultiGaussianCurve.  The position P will be
     * de-normalized so the resulting MultiGaussianCurve will have the same dimensions as the data passed into
     * the constructer
     * @param position
     * @return
     */
    public MultiGaussianCurve getSolution(EuclidianVector position) {
        //Construct the multi gaussian function
        //Decompose Solution will handle de-normilization
        List<ImmutableGaussianCurve> Components = decomposeSolution(position);
        return new MultiGaussianCurve(Components);
    }

    private static EuclidianVector computeMinBounds(int dimensions, List<double[]> data) {
        double min = Double.POSITIVE_INFINITY;
        for (double[] doubles : data) {
            min = Math.min(min, doubles[0]);
        }

        List<Double> mins = Lists.newArrayList();
        for (int n = 0; n < dimensions / 3; n++) {
            mins.add(min);
            mins.add(0.0);
            mins.add(0.0);
        }
        return new EuclidianVector(mins);
    }

    private static EuclidianVector computeMaxBounds(int dimensions, List<double[]> data) {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (double[] doubles : data) {
            min = Math.min(min, doubles[0]);
            max = Math.max(max, doubles[0]);
        }
        double range = max - min;
        double increment = range / data.size();
        double volume = 0.0;
        for (double[] doubles : data) {
            volume += (doubles[1] * increment);
        }

        List<Double> maxes = Lists.newArrayList();
        for (int n = 0; n < dimensions / 3; n++) {
            maxes.add(max);
            maxes.add((range / 6) * (range / 6));
            maxes.add(volume);
        }
        return new EuclidianVector(maxes);
    }
}

