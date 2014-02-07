package com.github.steveash.jtribespso;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.steveash.jtribespso.impl.IndependentGaussianParticle;
import com.github.steveash.jtribespso.test.Rosenbrock;

/** 
This is a test class for ParticleTest and is intended
to contain all ParticleTest Unit Tests
*/
public class IndependantGaussianParticleTest extends ParticleTest {

	@Override
	protected Particle createParticle() {
		return new IndependentGaussianParticle(new Rosenbrock(), EuclidianVector.origin(2));
	}

	@Override
	protected Particle createParticle(EuclidianVector position, IObjectiveFunction function) {
		return new IndependentGaussianParticle(function, position);
	}

	@Test
	public void calculateNewPositionTest() {

		IObjectiveFunction function = new Rosenbrock();
		EuclidianVector initialPosition = new EuclidianVector(0, 10);
		EuclidianVector bestPosition = new EuclidianVector(1, 1);

		Particle target = createParticle(initialPosition, new Rosenbrock());
		Solution idealSolution = new Solution(bestPosition, function);

		EuclidianVector newPosition = target.calculateNewPosition(idealSolution);

		//Check that we moved closer to the ideal solution
        double newMag = EuclidianVector.subtract(newPosition, bestPosition).magnitude();
        double origMag = EuclidianVector.subtract(initialPosition, bestPosition).magnitude();
        assertTrue(newMag < origMag);
	}
}
