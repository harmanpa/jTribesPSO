package com.github.steveash.jtribespso.gauss;

import java.util.List;

import com.github.steveash.jtribespso.test.ImmutableGaussianCurve;
import com.google.common.collect.Lists;

public class MultiGaussianCurve {
    private List<ImmutableGaussianCurve> componentFunctions;

    public List<ImmutableGaussianCurve> componentFunction() {
        return componentFunctions;
    }

    public MultiGaussianCurve(Iterable<ImmutableGaussianCurve> pdfs) {
        componentFunctions = Lists.newArrayList(pdfs);
    }

    public double evaluate(double x) {

        double agrigator = 0;
        for (ImmutableGaussianCurve componentFunction : componentFunctions) {
            agrigator += componentFunction.density(x);
        }
        return agrigator;
    }
}
