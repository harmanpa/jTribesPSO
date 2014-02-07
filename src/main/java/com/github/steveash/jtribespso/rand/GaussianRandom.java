package com.github.steveash.jtribespso.rand;

/**
 * A random number generator implementation capable of generating random numbers based
 * on a Gaussian distribution
 */
public class GaussianRandom extends JdkRandom implements IGaussianRandom {

    private static final double c_0 = 2.515517;
    private static final double c_1 = 0.802853;
    private static final double c_2 = 0.010328;
    private static final double d_1 = 1.432788;
    private static final double d_2 = 0.189269;
    private static final double d_3 = 0.001308;

    /**
     * Initializes the seed to system time
     */
    public GaussianRandom() {
    }

    public GaussianRandom(long seed) {
        super(seed);
    }

    @Override
    public double nextGaussian() {
        return nextGaussian(0, 1);
    }

    @Override
    public double nextGaussian(double mu, double sigma) {

           /* As of 12/2010 this function is the slow path in the PSO algorithm (excluding the objective function
            * for certain problems).  You could probably improve performance by replacing this gaussian function
            * with the zigurrat algorithm.  Remember to profile changes so you don't accidentally slow things down!
            */
        return mu + CumulativeGaussian(this.nextDouble()) * sigma;
    }

    /*There's a nasty bug here.  Although it's unlikely, random.NextDouble() can return 0.  In that case, p will be 0
     * and when we get to the line:
     * double t = Math.Sqrt(Math.Log(1.0 / (p * p)));
     * we're going to get a suprise!
     * The upside is that when I say it's unlikely, I mean SUPER unlikely.
     */
    private static double CumulativeGaussian(double p) {
        // p is a rectangular probability between 0 and 1
        // convert that into a gaussian.
        // Apply the inverse cumulative gaussian distribution function
        // This is an approximation by Abramowitz and Stegun; Press, et al.
        // See http://www.pitt.edu/~wpilib/statfaq/gaussfaq.html
        // Because of the symmetry of the normal
        // distribution, we need only consider 0 < p < 0.5. If you have p > 0.5,
        // then apply the algorithm below to q = 1-p, and then negate the value
        // for X obtained.
        boolean fNegate = false;

        if (p > 0.5) {
            p = 1.0 - p;
            fNegate = true;
        }

        double t = Math.sqrt(Math.log(1.0 / (p * p)));
        double tt = t * t;
        double ttt = tt * t;
        double x = t - ((c_0 + c_1 * t + c_2 * tt) / (1 + d_1 * t + d_2 * tt + d_3 * ttt));
        return fNegate ? -x : x;
    }
}
