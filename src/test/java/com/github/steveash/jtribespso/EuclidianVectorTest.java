package com.github.steveash.jtribespso;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.github.steveash.jtribespso.exception.DimensionMismatchException;
import com.github.steveash.jtribespso.rand.HyperspaceRandom;
import com.google.common.collect.Lists;


/** 
This is a test class for EuclidianVectorTest and is intended
to contain all EuclidianVectorTest Unit Tests
*/
public class EuclidianVectorTest {

    private static List<Double> makeDoubles(int... args) {
        ArrayList<Double> output = Lists.newArrayList();
        for (int arg : args) {
            output.add((double) arg);
        }
        return output;
    }
    
	@Test
	public void lengthTest() {
		List<Double> dimensionData = makeDoubles(1, -2);
		EuclidianVector target = new EuclidianVector(dimensionData);
		double actual;
		actual = target.magnitude();
		//The length of the above vector shoudl be equal to the square root of 5
		assertEquals(actual, 2.236, .001);

		dimensionData = makeDoubles(3, 10);
		target = new EuclidianVector(dimensionData);
		actual = target.magnitude();
		//The length of the above vector shoudl be equal to the square root of 109
		assertEquals(actual, 10.44,.01);

		dimensionData = makeDoubles(-5, -6);
		target = new EuclidianVector(dimensionData);
		actual = target.magnitude();
		//The length of the above vector shoudl be equal to the square root of 109
		assertEquals(actual, 7.81,.01);

		//Do a 3 dimensional test just for fun
		dimensionData = makeDoubles(3, 6, 2);
		target = new EuclidianVector(dimensionData);
		actual = target.magnitude();
		//The length of the above vector shoudl be equal to the square root of 49
		assert actual == 7;
	}

	@Test
	public void op_SubtractionTest() {
		EuclidianVector a = new EuclidianVector(makeDoubles(5, -4, 12, 0));
		EuclidianVector b = new EuclidianVector(makeDoubles(7, 1, -2, -6));
		EuclidianVector expected = new EuclidianVector(makeDoubles(-2, -5, 14, 6));
		EuclidianVector actual;
		actual = EuclidianVector.subtract(a, b);
		assertEquals(expected, actual);
	}

	@Test
	public void centerOfGravityTest() {
		EuclidianVector point1 = new EuclidianVector(1, 1);
		EuclidianVector point2 = new EuclidianVector(2, 2);
		EuclidianVector expected = new EuclidianVector(1.5, 1.5);
		EuclidianVector actual;
		actual = EuclidianVector.centerOfGravity(point1, point2);
		assertEquals(expected, actual);

		//Quadrent 4 -> Quadrent 3 test
		point1 = new EuclidianVector(1.59, -5.65);
		point2 = new EuclidianVector(-2.1, -0.79);
		expected = new EuclidianVector(-.255, -3.22);
		actual = EuclidianVector.centerOfGravity(point1, point2);
		assertEquals(expected, actual);
	}

	@Test
	public void weightedcenterOfGravityTest() {
		//Test in quadrant 1
		EuclidianVector point1 = new EuclidianVector(1, 1);
		double point1Mass = 9;
		EuclidianVector point2 = new EuclidianVector(4, 4);
		double point2Mass = 1; // TODO: Initialize to an appropriate value
		EuclidianVector expected = new EuclidianVector(1.3, 1.3);
		EuclidianVector actual;
		actual = EuclidianVector.centerOfGravity(point1, point1Mass, point2, point2Mass);
		assertEquals(expected, actual);

		//quandrant 2
		point2 = new EuclidianVector(-4, 4);
		expected = new EuclidianVector(.5, 1.3);
		actual = EuclidianVector.centerOfGravity(point1, point1Mass, point2, point2Mass);
		assertEquals(expected, actual);

		//quandrant 2 reversed
		actual = EuclidianVector.centerOfGravity(point2, point2Mass, point1, point1Mass);
		assertEquals(expected, actual);

		//A real example
		point1 = new EuclidianVector(1.59, -5.65);
		point1Mass = .006;
		point2 = new EuclidianVector(-2.1, -0.79);
		point2Mass = .9936;
	}

	@Test
	public void centerOfGravityErrorTest() {
		HyperspaceRandom rng = new HyperspaceRandom();

		EuclidianVector point1 = rng.nextGaussianVector(new EuclidianVector(0, 0, 0, 0));
		double point1Mass = rng.nextDouble();
		EuclidianVector point2 = rng.nextGaussianVector(new EuclidianVector(0, 0));
		double point2Mass = rng.nextDouble();

		try {
			EuclidianVector actual;
			actual = EuclidianVector.centerOfGravity(point1, point1Mass, point2, point2Mass);
			Assert.fail("Expected: DimensionMismatchException. Got no exception");
		}
		catch (DimensionMismatchException e) {
			//Good, we got the exception we were expecting
		}
		catch (RuntimeException e) {
			Assert.fail("Expected: DimensionMismatchException Got: {0}" + e.getClass());
		}
	}

	@Test
	public void subtractErrorTest() {
		HyperspaceRandom rng = new HyperspaceRandom();

		EuclidianVector point1 = rng.nextGaussianVector(new EuclidianVector(0, 0, 0, 0));
		EuclidianVector point2 = rng.nextGaussianVector(new EuclidianVector(0, 0));

		try {
			EuclidianVector actual;
			actual = EuclidianVector.subtract(point1, point2);
			Assert.fail("Expected: DimensionMismatchException. Got no exception");
		}
		catch (DimensionMismatchException e) {
			//Good, we got the exception we were expecting
		}
		catch (RuntimeException e) {
			Assert.fail("Expected: DimensionMismatchException Got: {0}" + e.getClass());
		}
	}

	@Test
	public void divideTest() {
		HyperspaceRandom rng = new HyperspaceRandom();
		EuclidianVector a = rng.nextUniformVector(new EuclidianVector(0, 0, 0, 0), 10);
		double divisor = rng.nextDouble() * 5;

		EuclidianVector actual;
		actual = EuclidianVector.divide(a, divisor);

		for (int n = 0; n < a.getDimensions(); n++) {
			assertEquals(a.get(n) / divisor, actual.get(n), 0.001);
		}
	}
}
