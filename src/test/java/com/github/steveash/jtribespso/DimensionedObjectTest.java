package com.github.steveash.jtribespso;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Assert;
import org.junit.Test;

import com.github.steveash.jtribespso.exception.DimensionMismatchException;

/** 
This is a test class for DimensionedObjectTest and is intended
to contain all DimensionedObjectTest Unit Tests
*/
public class DimensionedObjectTest {

    @Test
	public void addTest() {
		EuclidianVector a = new EuclidianVector(1.0, 2.0, 3.0, 4.0, 5.0);
		EuclidianVector b = new EuclidianVector(15.0, 33.0, 12.0, 2.0, 32.0);
		EuclidianVector expected = new EuclidianVector(16, 35, 15, 6, 37);
		EuclidianVector actual;
		actual = EuclidianVector.add(a, b);
        assertEquals(expected, actual);

		actual = EuclidianVector.add(b, a);
        assertEquals(expected, actual);
	}

    @Test
	public void op_EqualityTest() {
		//Test non reference equality
		EuclidianVector a = new EuclidianVector(5, 1, 7, 348, 3);
		EuclidianVector b = new EuclidianVector(5, 1, 7, 348, 3);
		boolean expected = true;
		boolean actual;
		actual = (a.equals(b));
        assertEquals(expected, actual);

		//non reference inequality
		b = new EuclidianVector(5, 2, 7, 348, 3);
		expected = false;
		actual = (a.equals(b));
        assertEquals(expected, actual);

		//Reference Equality
		b = a;
		expected = true;
		actual = (a.equals(b));
		assertEquals(expected, actual);
	}

    @Test
	public void op_EqualityUnequalLengthTest() {
		EuclidianVector a = new EuclidianVector(5, 1, 7, 348, 3);
		EuclidianVector b = new EuclidianVector(5, 1, 7, 348, 3, 0);
		boolean expected = false;
		boolean actual;
		actual = (a.equals(b));
		assertEquals(expected, actual);

		actual = (a.equals(b));
		assertEquals(expected, actual);
	}

    @Test
	public void nullOp_EqualityTest() {
		//Test non reference equality
		EuclidianVector a = new EuclidianVector(5, 1, 7, 348, 3);
		EuclidianVector b = null;
		boolean expected = false;
		boolean actual;
		actual = (a.equals(b));
		assertEquals(expected, actual);
    }

	@Test
	public void addUnequalTest() {
		EuclidianVector a = new EuclidianVector(1, 6, 5, 3, 34);
		EuclidianVector b = new EuclidianVector(1, 6, 5, 3, 34, 0);
		EuclidianVector actual;
		try {
			actual = EuclidianVector.add(a, b);
            Assert.fail("Expected to get an exeption");
		}
		catch (DimensionMismatchException e) {
		}
		try {
			actual = EuclidianVector.add(b, a);
			Assert.fail("Expected to get an exeption");
		}
		catch (DimensionMismatchException e2) {
		}
	}

	@Test
	public void getHashCodeTest() {
		EuclidianVector target = new EuclidianVector(6, 34, 2, 7, 3, 8, 1);
		int trial1 = target.hashCode();
		int trial2 = target.hashCode();
        assertEquals(trial1, trial2);

		EuclidianVector identical = new EuclidianVector(6, 34, 2, 7, 3, 8, 1);
		int trial3 = target.hashCode();
		int trial4 = target.hashCode();
        assertEquals(trial3, trial4);

		EuclidianVector different = new EuclidianVector(6, 34, 2, 7, 3, 8, 0);
		int trial5 = target.hashCode();
		int trial6 = different.hashCode();
		assertNotEquals(trial5, trial6);
	}

	@Test
	public void equalsTest() {
		EuclidianVector target = new EuclidianVector(6, 4, 21, 7, 3, 8, 0);
		EuclidianVector b = new EuclidianVector(6, 4, 21, 7, 3, 8, 1);
		boolean expected = false;
		boolean actual;
		actual = target.equals(b);
        assertEquals(expected, actual);
	}

	@Test
	public void op_InequalityTest() {
		EuclidianVector a = new EuclidianVector(1, 3, 6, 8, 4, 2, 3);
		EuclidianVector b = new EuclidianVector(1, 2, 6, 7, 1, 2, 3);
		boolean expected = true;
        boolean actual = (!a.equals(b));
        assertEquals(expected, actual);
	}
}