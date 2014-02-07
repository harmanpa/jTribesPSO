package com.github.steveash.jtribespso;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.github.steveash.jtribespso.event.SwarmMovedEvent;
import com.github.steveash.jtribespso.test.EventHandler;
import com.google.common.collect.Iterables;

/** 
This is a test class for SearchSpace and is intended
to contain all SearchSpace Unit Tests for methods with implementations in the base class and 
to provide a base class for test of derviced SearchSpace implementations
*/
public abstract class SearchSpaceTest<TParticle extends Particle> {

	protected abstract SearchSpace<TParticle> createSearchSpace();

    @Test
	public void generateNewParticleTest() {
		SearchSpace<TParticle> target = createSearchSpace();

		Particle actual = null;
		actual = target.generateNewParticle();

        assertNotNull(actual);
	}

    @Test
	public void generatePositionTest() {
		SearchSpace<TParticle> target = createSearchSpace();

		EuclidianVector actual;
		actual = target.generatePosition();

        assertNotNull(actual);
	}

    @Test
	public void generateParticleAtPositionTest() {
		SearchSpace<TParticle> target = createSearchSpace();

		EuclidianVector expectedPosition = target.generatePosition();

		Particle actual = target.generateParticleAtPosition(expectedPosition);

		assertEquals(expectedPosition, actual.getPosition());
	}

    @Test
	public void generateMultipleNewParticleTest() {
		SearchSpace<TParticle> target = createSearchSpace();

		java.util.Random rng = new java.util.Random();

		for (int n = 0; n < 10; n++) {
			int numberToGenerate = rng.nextInt(50);

			List<? extends Particle> actual = target.generateNewParticle(numberToGenerate);

			for (Particle p : actual) {
				assertTrue(p != null);
			}
			assertEquals(numberToGenerate, actual.size());
		}
	}

    @Test
	public void moveThenAdaptTest() {
		SearchSpace<TParticle> target = createSearchSpace();
        EventHandler eh = EventHandler.make(target.eventBus());

        //Check that the first time we call moveThanAdapt that we seed the search space but don't
		//actually move
        assertEquals(0, target.swarmSize());
        target.moveThenAdapt();
        assertEquals(1, target.swarmSize());
        assertEquals(0, eh.countForEvent(SwarmMovedEvent.class));

        target.moveThenAdapt();
        assertEquals(1, eh.countForEvent(SwarmMovedEvent.class));
	}

    @Test
	public void bestSolutionTest() {
		SearchSpace<TParticle> target = createSearchSpace();
		Solution actual = null;

		//The first time through, this should be null because the search space shouldn't have any particles
		actual = target.bestSolution();
		assertNull(actual);

		target.moveThenAdapt();

		//This time through, the best solution should be the system's only solution
        EuclidianVector onlyPosition = Iterables.getOnlyElement(target.particlePositions());
        assertEquals(onlyPosition, target.bestSolution().getPosition());
	}
}
