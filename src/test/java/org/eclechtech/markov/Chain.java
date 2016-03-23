package org.eclechtech.markov;

import java.security.InvalidParameterException;

/**
 * Something like a Markov chain to create variations in simulated survey data
 *
 */
public class Chain {

	private final int[] range;
	private final int[][] probabilities;

	/**
	 * 
	 * @param range
	 *            of values
	 * @param probabilities
	 *            matrix of probability to transition to the range value in the
	 *            corresponding offset
	 */
	public Chain(int[] range, int[][] probabilities) {
		this.range = range;
		this.probabilities = probabilities;

		if (!(this.range.length > 0)) {
			throw new InvalidParameterException("range.length must be > 0");
		}
		if (!(this.probabilities.length > 0)) {
			throw new InvalidParameterException("probabilities.length must be > 0");
		}

		if (this.probabilities.length != this.range.length) {
			throw new InvalidParameterException("probabilities.length != range.length");
		}
		for (int i = 0; i < this.probabilities.length; ++i) {
			if (this.probabilities[i].length != this.range.length) {
				throw new InvalidParameterException("probabilities[" + i + "].length != range.length");
			}
			int sum = 0;
			for (int j = 0; j < this.probabilities[i].length; ++j) {
				sum = sum + this.probabilities[i][j];
			}
			if (sum != 100) {
				throw new InvalidParameterException("sum of extent " + i + " is not 100");
			}
		}
	}

	/**
	 * 
	 * @param value
	 *            between 1 and 100
	 * @param j
	 * @return the next state
	 */
	public int transition(int value, int roll) {
		int rangeOffset = 0;
		// find the value in the range
		while (rangeOffset < range.length && value != range[rangeOffset]) {
			++rangeOffset;
		}
		if (rangeOffset == range.length) {
			throw new InvalidParameterException("Recevied value : " + value + " not present in range.");
		}
		// use the corresponding extent from the matrix
		int transitionProbability = 0;
		for (int i = 0; i < this.probabilities[rangeOffset].length; ++i) {
			transitionProbability = transitionProbability + this.probabilities[rangeOffset][i];
			if (roll <= transitionProbability) {
				// return the value from the range corresponding to the roll
				return range[i];
			}
		}
		// should not get here
		System.err.println("should not get here");
		return range[this.range.length - 1];
	}

}
