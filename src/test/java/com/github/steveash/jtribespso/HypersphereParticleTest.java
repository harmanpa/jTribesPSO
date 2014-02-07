package com.github.steveash.jtribespso;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.steveash.jtribespso.impl.HypersphereParticle;
import com.github.steveash.jtribespso.test.Rosenbrock;

/** 
This is a test class for ParticleTest and is intended
to contain all ParticleTest Unit Tests
*/
public class HypersphereParticleTest extends ParticleTest {

	@Override
	protected Particle createParticle() {
		return new HypersphereParticle(new Rosenbrock(), EuclidianVector.origin(2));
	}

	@Override
	protected Particle createParticle(EuclidianVector position, IObjectiveFunction function) {
		return new HypersphereParticle(function, position);
	}

	@Test
	public void calculateNewPositionTest() {
		IObjectiveFunction function = new Rosenbrock();
		EuclidianVector initialPosition = new EuclidianVector(0, 10);
		EuclidianVector bestPosition = new EuclidianVector(1, 1);

		Particle target = createParticle(initialPosition, new Rosenbrock());
		//Set up the particle so that it's "Excellent" otherwise the random part of the movement strategy
		//will muck up this test
		target.memorizePerformance(true);
		target.memorizePerformance(true);
		Solution idealSolution = new Solution(bestPosition, function);

		EuclidianVector newPosition = target.calculateNewPosition(idealSolution);

		//Check that we moved closer to the ideal solution
        double newMag = (EuclidianVector.subtract(newPosition, bestPosition).magnitude());
        double oldMag = (EuclidianVector.subtract(initialPosition, bestPosition).magnitude());
        assertTrue(newMag < oldMag);
    }
}
