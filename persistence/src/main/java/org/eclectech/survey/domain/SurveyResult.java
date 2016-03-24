package org.eclectech.survey.domain;

import java.time.Instant;

public class SurveyResult {

	private String user;
	private int achievement;
	private int development;
	private int engagement;
	private int culture;
	private Instant instant;

	public SurveyResult() {
	};

	public SurveyResult(String user, int achievement, int engagement, int development, int culture, Instant instant) {
		this.user = user;
		this.achievement = achievement;
		this.engagement = engagement;
		this.development = development;
		this.culture = culture;
		this.instant = instant;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	@Override
	public String toString() {
		return "SurveyResult [user=" + user + ", achievement=" + achievement + ", engagement=" + engagement
				+ ", development=" + development + ", culture=" + culture + ", instant=" + instant + "]";
	}

	/**
	 * @return the achievement
	 */
	public int getAchievement() {
		return achievement;
	}

	/**
	 * @return the engagement
	 */
	public int getEngagement() {
		return engagement;
	}

	/**
	 * @return the development
	 */
	public int getDevelopment() {
		return development;
	}

	/**
	 * @return the culture
	 */
	public int getCulture() {
		return culture;
	}

	/**
	 * @param achievement
	 *            the achievement to set
	 */
	public void setAchievement(int achievement) {
		this.achievement = achievement;
	}

	/**
	 * @param development
	 *            the development to set
	 */
	public void setDevelopment(int development) {
		this.development = development;
	}

	/**
	 * @param engagement
	 *            the engagement to set
	 */
	public void setEngagement(int engagement) {
		this.engagement = engagement;
	}

	/**
	 * @param culture
	 *            the culture to set
	 */
	public void setCulture(int culture) {
		this.culture = culture;
	}

	/**
	 * @return the instant
	 */
	public Instant getInstant() {
		return instant;
	}

	/**
	 * @param instant
	 *            the instant to set
	 */
	public void setInstant(Instant instant) {
		this.instant = instant;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

}
