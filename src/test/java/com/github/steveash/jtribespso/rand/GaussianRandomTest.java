package com.github.steveash.jtribespso.rand;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import com.github.steveash.jtribespso.rand.GaussianRandom;
import com.github.steveash.jtribespso.test.ImmutableGaussianCurve;

/** 
This is a test class for GaussianRandomTest and is intended
to contain all GaussianRandomTest Unit Tests
*/
public class GaussianRandomTest {

	@Test
	public void nextGaussianTest() {
		int samples = 500000;

		GaussianRandom target = new GaussianRandom(); // TODO: Initialize to an appropriate value
		java.util.ArrayList<Double> generated = new java.util.ArrayList<Double>();
		for (int n = 0; n < samples; n++) {
			generated.add(target.nextGaussian());
		}

		ImmutableGaussianCurve curve = new ImmutableGaussianCurve(0, 1);

		for (double n = -5; n <= 5; n += .1) {
            int hits = 0;
            for (Double dd : generated) {
                if ((dd >= (n - 0.1)) && (dd <= (n+ 0.1))) {
                    hits += 1;
                }
            }

			double generatedPDF = hits / (samples * .2);
			double calculatedPDF = curve.density(n);
			Assert.assertEquals(calculatedPDF, generatedPDF, .01);
		}
	}
}