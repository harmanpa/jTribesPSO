package com.github.steveash.jtribespso;

import com.github.steveash.jtribespso.impl.IndependentGaussianParticle;
import com.github.steveash.jtribespso.impl.SingleThreadedGaussianSearchSpace;
import com.github.steveash.jtribespso.test.Rosenbrock;

/**
This is a test class for SingleThreadedGaussianSearchSpaceTest and is intended
to contain all SingleThreadedGaussianSearchSpaceTest Unit Tests
*/
public class SingleThreadedGaussianSearchSpaceTest extends SearchSpaceTest<IndependentGaussianParticle> {

	@Override
	protected SearchSpace<IndependentGaussianParticle> createSearchSpace() {
		return new SingleThreadedGaussianSearchSpace(new Rosenbrock());
	}
}
