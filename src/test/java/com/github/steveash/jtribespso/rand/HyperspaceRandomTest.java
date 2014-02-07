package com.github.steveash.jtribespso.rand;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.steveash.jtribespso.EuclidianVector;
import com.github.steveash.jtribespso.rand.HyperspaceRandom;

/**
 * This is a test class for HyperspaceRandomTest and is intended
 * to contain all HyperspaceRandomTest Unit Tests
 */
public class HyperspaceRandomTest {
    private static final Logger log = LoggerFactory.getLogger(HyperspaceRandomTest.class);

    @Test
    public void nextGaussianVectorTest() {
        int samples = 500000;

        HyperspaceRandom target = new HyperspaceRandom(); // TODO: Initialize to an appropriate value
        EuclidianVector center = new EuclidianVector(0, 0);
        double sigma = 1F; // TODO: Initialize to an appropriate value

        java.util.ArrayList<Double> angles = new java.util.ArrayList<Double>();
        for (int n = 0; n < samples; n++) {
            EuclidianVector vector = target.nextGaussianVector(center, sigma);
            double angle = Math.atan(vector.get(1) / vector.get(0));
            angles.add(angle);
        }

        for (double n = -Math.PI / 2 + Math.PI / 100; n <= Math.PI / 2; n += Math.PI / 100) {
            int hits = 0;
            for (Double a : angles) {
                if ((a >= (n - Math.PI / 100)) && (a <= (n + Math.PI / 100))) {
                    hits += 1;
                }
            }

            double generatedPDF = ((double)hits) / samples;
            Assert.assertEquals("Try running the test again because there's a bit of probability involved",
                    .02, generatedPDF, .005);
        }
    }

    /**
     * A test for NextUniformVector that makes sure it always generates vectors with uniformly distributed lengths
     * <p/>
     * Because this relies on probability, it can occasionally fail.  It's pretty uncommon though
     */
    @Test
    public void nextUniformVectorTest() {
        HyperspaceRandom target = new HyperspaceRandom();
        double radius = 1F;
        java.util.ArrayList<Double> lengths = new java.util.ArrayList<Double>();

        for (int dimensions = 1; dimensions <= 10; dimensions++) {
            EuclidianVector center = EuclidianVector.origin(dimensions);
            for (int n = 0; n < 1000; n++) {
                EuclidianVector actual = target.nextUniformVector(center, radius);
                lengths.add(actual.magnitude());
            }
        }

        for (double n = .05; n < 1; n += .05) {

            int hits = 0;
            for (Double dd : lengths) {
                if ((dd >= (n - .05)) && (dd <= (n + .05))) {
                    hits += 1;
                }
            }

            log.info("Got {} hits", hits);
            double generatedPDF = ((double)hits) / 10000.0;
            Assert.assertEquals("Try running the test again because there's a bit of probability involved",
                    0.1, generatedPDF, 0.01);
        }
    }
}
