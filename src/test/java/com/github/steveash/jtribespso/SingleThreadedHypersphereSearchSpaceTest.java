package com.github.steveash.jtribespso;

import com.github.steveash.jtribespso.impl.HypersphereParticle;
import com.github.steveash.jtribespso.impl.SingleThreadedHypersphereSearchSpace;
import com.github.steveash.jtribespso.test.Rosenbrock;

/**
This is a test class for SingleThreadedHypersphereSearchSpaceTest and is intended
to contain all SingleThreadedHypersphereSearchSpaceTest Unit Tests
*/
public class SingleThreadedHypersphereSearchSpaceTest extends SearchSpaceTest<HypersphereParticle> {

	@Override
	protected SearchSpace<HypersphereParticle> createSearchSpace() {
		return new SingleThreadedHypersphereSearchSpace(new Rosenbrock());
	}
}
