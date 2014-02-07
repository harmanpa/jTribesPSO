package com.github.steveash.jtribespso;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

/** 
This is a test class for EuclidianVectorBuilderTest and is intended
to contain all EuclidianVectorBuilderTest Unit Tests
*/
public class EuclidianVectorBuilderTest {

	@Test
	public void addDimensionDataTest() {
		EuclidianVectorBuilder target = new EuclidianVectorBuilder(); // TODO: Initialize to an appropriate value
		for (int n = 0; n < 10; n++) {
			target.add(n);
            assertEquals(n, target.get(n), 0.0001);
		}
	}

	@Test
	public void toEuclidianVectorTest() {
		EuclidianVectorBuilder target = new EuclidianVectorBuilder();
		for (int n = 0; n <= 5; n++) {
			target.add(n);
		}
		EuclidianVector expected = new EuclidianVector(0, 1, 2, 3, 4, 5);
		EuclidianVector actual;
		actual = target.build();
        assertEquals(expected, actual);
	}

	@Test
	public void addDimensionDataToFrozenTest() {
		EuclidianVectorBuilder target = new EuclidianVectorBuilder();
		for (int n = 0; n <= 5; n++) {
			target.add(n);
		}
		target.build();
		try {
			target.add(3);
            Assert.fail("Expected an invalidOperationException");
		}
		catch (IllegalStateException e) {
		}
		catch (java.lang.Exception e2) {
			Assert.fail("Expected an invalidOperationException");
		}
	}
}