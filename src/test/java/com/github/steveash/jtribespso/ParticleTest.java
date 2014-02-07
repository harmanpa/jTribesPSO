package com.github.steveash.jtribespso;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import com.github.steveash.jtribespso.exception.DimensionMismatchException;
import com.github.steveash.jtribespso.rand.HyperspaceRandom;
import com.github.steveash.jtribespso.test.Rosenbrock;
import com.google.common.collect.ImmutableList;

/**
This is a test class for ParticleTest and is intended
to contain all ParticleTest Unit Tests
*/
public abstract class ParticleTest {

	protected abstract Particle createParticle();
	protected abstract Particle createParticle(EuclidianVector position, IObjectiveFunction function);

	@Test
	public void memorizePerformanceTest() {
		Particle target = createParticle();
		target.memorizePerformance(false);
		target.memorizePerformance(false);
		assertFalse(target.isGood());
		assertFalse(target.isExcellent());
		target.memorizePerformance(true);
		target.memorizePerformance(false);
        assertFalse(target.isGood());
        assertFalse(target.isExcellent());
    }

	@Test
	public void memorizeGoodPerformanceTest() {
		Particle target = createParticle();
		target.memorizePerformance(false);
		target.memorizePerformance(true);
        assertTrue(target.isGood());
        assertFalse(target.isExcellent());
        target.memorizePerformance(true);
		target.memorizePerformance(true);
        assertTrue(target.isGood());
        assertTrue(target.isExcellent());
    }

	@Test
	public void parentTest() {
		Particle target = createParticle();
		Tribe expected = null;
		Tribe actual;
		actual = target.getParent();
		assertNull(actual);

		//Now add the parent to a tribe
		Tribe parentTribe = new Tribe(target);
		assertTrue(parentTribe == target.getParent());

		//Finally make sure we can't change the parent
		try {
			Tribe imposterTribe = new Tribe(target);
			Assert.fail("Expected: InvalidOperationException.  Got no exception");
		}
		catch (IllegalStateException e) {
			//Good, we got the exception we were looking for
		}
		catch (RuntimeException e) {
			Assert.fail("Expected: InvalidOperationException.  Got: " + e.toString());
		}
	}

	@Test
	public void wrongParentTest() {
		Particle target = createParticle();
		Particle dummy = createParticle();

		Tribe wrongTribe = new Tribe(dummy);

		try {
			//This should throw because the target is not actually a member of the tribe we're trying
			//to assign here.
			target.setParent(wrongTribe);
			Assert.fail("Expected: InvalidOperationException.  Got no exception");
		}
		catch (IllegalStateException e) {
			//Good, we got the right exception
		}
		catch (RuntimeException e) {
			Assert.fail("Expected: InvalidOperationException.  Got: {0}" + e.getClass());
		}
	}

	/**
	 A test for the particle constructor when a null euclidian vector is passed
	*/
    @Test(expected = DimensionMismatchException.class)
	public void dimensionMismatchCtorTest() {
		//Rosenbrock is a 2d function, but we're going to pass in a 3d initial position
		Particle p = createParticle(new EuclidianVector(0, 0, 0), new Rosenbrock());
	}

	@Test
	public void moveTest() {
		java.util.ArrayList<Boolean> movedCloser = new java.util.ArrayList<Boolean>();
		HyperspaceRandom rng = new HyperspaceRandom();

        int goodCount = 0;
        int badCount = 0;
		for (int n = 0; n < 100000; n++) {
			boolean goodMove = rosenbrockMoveTest(rng, 50);
            if (goodMove) {
                goodCount += 1;
            } else {
                badCount += 1;
            }
		}

		assertTrue("Failed with " + goodCount + " good moves and " + badCount + "bad moves. Type:" + this.getClass(),
                goodCount > badCount);
	}

	/** 
	 Creates a particle at the optimal solution and a particle somewhere within the specified radius.
	 The non-optimal particle is moved and we check if it got closer to the optimial solution or farther away
	 
	 @param rng
	 @param radius
	 @return 
	*/
	private boolean rosenbrockMoveTest(HyperspaceRandom rng, int radius) {
		EuclidianVector bestSolution = new EuclidianVector(1, 1);
		IObjectiveFunction rosenbrock = new Rosenbrock();
		Particle goodParticle = createParticle(bestSolution, rosenbrock);
		Particle moveCandidate = createParticle(rng.nextUniformVector(bestSolution, radius), rosenbrock);

		double initialDistance = (EuclidianVector.subtract(moveCandidate.getPosition(), bestSolution)).magnitude();

		//Now, we add the two particles to a tribe so they can inform one-another
		Tribe tribe = new Tribe(ImmutableList.of(goodParticle, moveCandidate));

		moveCandidate.move();

		double finalDistance = (EuclidianVector.subtract(moveCandidate.getPosition(), bestSolution)).magnitude();

		return finalDistance < initialDistance;
	}
}