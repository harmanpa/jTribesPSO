package com.github.steveash.jtribespso;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

/**
 * @author Steve Ash
 */
public interface SolutionHolder {

    public static final Ordering<SolutionHolder> OrderBySolutionErrorAsc = Ordering
            .natural()
            .onResultOf(new Function<SolutionHolder, Comparable<Double>>() {
                @Override
                public Comparable<Double> apply(SolutionHolder input) {
                    return input.bestSolution().getError();
                }
            });

    Solution bestSolution();
}
