package com.github.steveash.jtribespso;

import com.carrotsearch.hppc.DoubleArrayList;

/**
 * EuclidianVectorBuilder is designed to create Euclidian Vectors without constructing an extra List of doubles.
 *
 * Without the euclidian vector builder, if you wanted to do some math and create a Euclidian vector you would end
 * up creating a list of doubles to hold the dimension values and then pass that into the EuclidianVector constructor.
 * 
 * Internally, the public EuclidianVector constructor would create another list of doubles to guarantee that it's immutable.
 * 
 * EuclidianVectorBuilder builds a list of doubles internally, but it won't let you change it after calling
 *  build.  This makes the underlying list de-facto immutable.
 * This means that the EuclidianVector can make a shallow copy of EuclidianVectorBuilder's list without worrying
 *  about it changing later.
 */
public class EuclidianVectorBuilder {
    private DoubleArrayList dimensionData;
    private boolean frozen = false;

    public double get(int index) {
        return dimensionData.get(index);
    }

    public EuclidianVectorBuilder() {
        this.dimensionData = new DoubleArrayList();
    }

    public void add(double data) {
        throwIfFrozen();
        dimensionData.add(data);
    }

    public EuclidianVector build() {
        frozen = true;
        return EuclidianVector.createUsingBacking(this.dimensionData);
    }

    private void throwIfFrozen() {
        if (frozen)
            throw new IllegalStateException("cant add elements when the builder is frozen");
    }
}