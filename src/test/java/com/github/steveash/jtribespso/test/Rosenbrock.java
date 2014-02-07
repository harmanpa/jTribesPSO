package com.github.steveash.jtribespso.test;

import com.github.steveash.jtribespso.EuclidianVector;
import com.github.steveash.jtribespso.IObjectiveFunction;

public class Rosenbrock implements IObjectiveFunction {

	private int evaluations;

	public int getEvaluations() {
		return evaluations;
	}

    @Override
	public int getDimensions() {
		return 2;
	}

    @Override
	public EuclidianVector getMinBounds() {
		return new EuclidianVector(-5, -5);
	}

    @Override
	public EuclidianVector getMaxBounds() {
		return new EuclidianVector(5, 5);
	}

    @Override
	public EuclidianVector getInitialGuess() {
		return null;
	}

    @Override
	public double evaluate(EuclidianVector position) {
		evaluations++;
		return Math.pow(1 - position.get(0), 2) + 100 * Math.pow(position.get(1) - (position.get(0) * position.get(0)), 2);
	}

//	public boolean tryEvaluate(EuclidianVector position, double abortValue, tangible.RefObject<Double> result) {
//		evaluations++;
//		result.argValue = Math.pow(1 - position[0], 2) + 100 * Math.pow(position[1] - (position[0] * position[0]), 2);
//		return true;
//	}

	public Rosenbrock() {
		evaluations = 0;
	}
}
