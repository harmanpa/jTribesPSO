package com.github.steveash.jtribespso;

import com.github.steveash.jtribespso.impl.IndependentGaussianParticle;
import com.github.steveash.jtribespso.test.BaseSearchSpace;
import com.github.steveash.jtribespso.test.Rosenbrock;

/**
This is a test class for SearchSpace and is intended
to contain all SearchSpace Unit Tests for methods with implementations in the base class and 
to provide a base class for test of derviced SearchSpace implementations
*/
public class BaseSearchSpaceTest extends SearchSpaceTest<IndependentGaussianParticle> {

	@Override
	protected SearchSpace<IndependentGaussianParticle> createSearchSpace() {
		Rosenbrock objectiveFunction = new Rosenbrock();
		return new BaseSearchSpace(objectiveFunction);
	}
}
