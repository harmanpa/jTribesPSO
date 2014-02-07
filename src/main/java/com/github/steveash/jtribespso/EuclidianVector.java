package com.github.steveash.jtribespso;

import java.util.Collection;

import com.carrotsearch.hppc.DoubleArrayList;
import com.github.steveash.jtribespso.exception.DimensionMismatchException;

/**
 * A euclidian vector is an immutable n-dimensional vector used to represent a point in N dimensional space
 */
public class EuclidianVector {

    public static EuclidianVector createUsingBacking(DoubleArrayList backing) {
        return new EuclidianVector(backing);
    }

    public static EuclidianVector copyOf(DoubleArrayList source) {
        return new EuclidianVector(new DoubleArrayList(source));
    }

    private final DoubleArrayList dimensionData;

    public int getDimensions() {
        return dimensionData.size();
    }

    public EuclidianVector(double... data) {
        this.dimensionData = new DoubleArrayList(data.length);
        dimensionData.add(data);
    }

    public EuclidianVector(Collection<Double> data) {
        this.dimensionData = new DoubleArrayList(data.size());
        for (Double datum : data) {
            dimensionData.add(datum);
        }
    }

    private EuclidianVector(DoubleArrayList backing) {
        this.dimensionData = backing;
    }

    public double magnitude() {
        double sum = 0;
        for (int n = 0; n < this.dimensionData.size(); n++) {
            sum += Math.pow(dimensionData.get(n), 2.0);
        }
        return Math.sqrt(sum);
    }

    public double get(int index) {
        return dimensionData.get(index);
    }

    /**
     * Subtract N-Dimensional vector b from a.  a and b must have the same number of dimensions
     * @param a
     * @param b
     * @return
     */
    public static EuclidianVector subtract(EuclidianVector a, EuclidianVector b) {
        throwIfUnequalDimensions(a, b);

        DoubleArrayList deltas = new DoubleArrayList(a.getDimensions());
        for (int i = 0; i < a.getDimensions(); i++) {
            deltas.add(a.get(i) - b.get(i));
        }
        return createUsingBacking(deltas);
    }

    /**
     * Add N-Dimensional vector a to b.  a and b must be the same length
     * @param a
     * @param b
     * @return
     */
    public static EuclidianVector add(EuclidianVector a, EuclidianVector b) {
        throwIfUnequalDimensions(a, b);

        DoubleArrayList deltas = new DoubleArrayList(a.getDimensions());
        for (int i = 0; i < a.getDimensions(); i++) {
            deltas.add(a.get(i) + b.get(i));
        }
        return createUsingBacking(deltas);
    }

    private static void throwIfUnequalDimensions(EuclidianVector a, EuclidianVector b) {
        if (a.getDimensions() != b.getDimensions())
            throw new DimensionMismatchException();
    }

    /**
     * Perform scalar division on N-Dimensional vector a
     * @param a
     * @param divisor
     * @return
     */
    public static EuclidianVector divide(EuclidianVector a, double divisor) {
        DoubleArrayList deltas = new DoubleArrayList(a.getDimensions());
        for (int i = 0; i < a.getDimensions(); i++) {
            deltas.add(a.get(i) / divisor);
        }
        return createUsingBacking(deltas);
    }

    /**
     * Perform scalar multiplication on N-Dimensional vector a
     * @param a
     * @param scalar
     * @return
     */
    public static EuclidianVector multiply(EuclidianVector a, double scalar) {
        DoubleArrayList deltas = new DoubleArrayList(a.getDimensions());
        for (int i = 0; i < a.getDimensions(); i++) {
            deltas.add(a.get(i) * scalar);
        }
        return createUsingBacking(deltas);
    }

    /**
     * Returns an N-Dimensional representation of the origion. {0,0} in 2 dimensions.  {0,0,0} in 3 dimensions etc.
     * @param dimensions
     * @return
     */
    public static EuclidianVector origin(int dimensions) {
        DoubleArrayList zeroes = new DoubleArrayList(dimensions);
        for (int i = 0; i < dimensions; i++) {
            zeroes.add(0);
        }
        return createUsingBacking(zeroes);
    }

    /**
     * Computes an N-dimensional vector pointing two the center of Point1 and Point2.
     * The same as calling CenterOfGravity(Point1, 1, Point2, 1);
     * @param point1
     * @param point2
     * @return
     */
    public static EuclidianVector centerOfGravity(EuclidianVector point1, EuclidianVector point2) {
        return EuclidianVector.centerOfGravity(point1, 1, point2, 1);
    }

    /**
     * Computes an N-dimensional vector pointing to the weighted center of Point1 and Point2.  The greater the mass
     * of Point1 compared to the mass of Point2, the closer the result will be to Point1 and vice versa
     * @param a
     * @param aMass
     * @param b
     * @param bMass
     * @return
     */
    public static EuclidianVector centerOfGravity(EuclidianVector a, double aMass, EuclidianVector b, double bMass) {
        throwIfUnequalDimensions(a, b);
        DoubleArrayList deltas = new DoubleArrayList(a.getDimensions());
        for (int i = 0; i < a.getDimensions(); i++) {
            double aa = a.get(i);
            double bb = b.get(i);
            double mass = (aa * aMass + bb * bMass) / (aMass + bMass);
            deltas.add(mass);
        }
        return createUsingBacking(deltas);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EuclidianVector that = (EuclidianVector) o;

        if (this.dimensionData.size() != that.dimensionData.size()) return false;
        for (int i = 0; i < this.dimensionData.size(); i++) {
            if (Double.compare(this.dimensionData.get(i), that.dimensionData.get(i)) != 0) return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return dimensionData.hashCode();
    }

    @Override
    public String toString() {
        return "EuclidianVector{" + dimensionData + '}';
    }
}