package com.github.steveash.jtribespso.test;


public class ImmutableGaussianCurve {
	private double mean;
	private double variance;
	private double height;

	public double getMean() {
		return mean;
	}
	public double getVariance() {
		return variance;
	}
	public double getHeight() {
		return height;
	}

	public ImmutableGaussianCurve(double mean, double variance) {
		this.mean = mean;
		this.variance = variance;
		this.height = .398942;
	}

	public ImmutableGaussianCurve(double mean, double variance, double height) {
		this.mean = mean;
		this.variance = variance;
		this.height = height;
	}

	public double density(double X) {
		double difference = X - mean;
		double exponent = (-1 * difference * difference / (2 * variance));
		return height * Math.pow(Math.E, exponent);
	}

	@Override
	public String toString() {
		return "Mean: " + (new Double(getMean())).toString() + " Variance: " + (new Double(getVariance())).toString();
	}
}
