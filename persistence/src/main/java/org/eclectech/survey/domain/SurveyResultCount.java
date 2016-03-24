package org.eclectech.survey.domain;

public class SurveyResultCount {

	private String name;
	private int veryNegative;
	private int somewhatNegative;
	private int neutral;
	private int somewhatPositive;
	private int veryPositive;
	private int sum;

	public SurveyResultCount() {
	};

	public SurveyResultCount(String name) {
		this.name = name;
	}
	
	public SurveyResultCount(String name, int veryNegative, int somewhatNegative, int neutral, int somewhatPositive,
			int veryPositive, int sum) {
		this.name = name;
		this.veryNegative = veryNegative;
		this.somewhatNegative = somewhatNegative;
		this.neutral = neutral;
		this.somewhatPositive = somewhatPositive;
		this.veryPositive = veryPositive;
		this.sum = sum;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the veryNegative
	 */
	public int getVeryNegative() {
		return veryNegative;
	}

	/**
	 * @param veryNegative
	 *            the veryNegative to set
	 */
	public void setVeryNegative(int veryNegative) {
		this.veryNegative = veryNegative;
	}

	/**
	 * @return the somewhatNegative
	 */
	public int getSomewhatNegative() {
		return somewhatNegative;
	}

	/**
	 * @param somewhatNegative
	 *            the somewhatNegative to set
	 */
	public void setSomewhatNegative(int somewhatNegative) {
		this.somewhatNegative = somewhatNegative;
	}

	/**
	 * @return the neutral
	 */
	public int getNeutral() {
		return neutral;
	}

	/**
	 * @param neutral
	 *            the neutral to set
	 */
	public void setNeutral(int neutral) {
		this.neutral = neutral;
	}

	/**
	 * @return the somewhatPositive
	 */
	public int getSomewhatPositive() {
		return somewhatPositive;
	}

	/**
	 * @param somewhatPositive
	 *            the somewhatPositive to set
	 */
	public void setSomewhatPositive(int somewhatPositive) {
		this.somewhatPositive = somewhatPositive;
	}

	/**
	 * @return the veryPositive
	 */
	public int getVeryPositive() {
		return veryPositive;
	}

	/**
	 * @param veryPositive
	 *            the veryPositive to set
	 */
	public void setVeryPositive(int veryPositive) {
		this.veryPositive = veryPositive;
	}

	/**
	 * @return the sum
	 */
	public int getSum() {
		return sum;
	}

	/**
	 * @param sum
	 *            the sum to set
	 */
	public void setSum(int sum) {
		this.sum = sum;
	}

	@Override
	public String toString() {
		return "SurveyResultCount [name=" + name + ", veryNegative=" + veryNegative + ", somewhatNegative="
				+ somewhatNegative + ", neutral=" + neutral + ", somewhatPositive=" + somewhatPositive
				+ ", veryPositive=" + veryPositive + ", sum=" + sum + "]";
	}

}
