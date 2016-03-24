package org.eclectech.survey.application;

import java.time.Instant;
import java.util.Map;

import org.eclectech.survey.SurveyResultCategories;
import org.eclectech.survey.domain.SurveyResultCount;
import org.eclectech.survey.persist.SurveyResults;

public class SurveyResultCountUtil {

	/**
	 * Creates SurveyResultCount for the day on which the received Instant occurs
	 * @param attribute
	 * @param instant
	 * @param surveyResults
	 * @return
	 */
	public static SurveyResultCount create(String attribute, Instant instant, SurveyResults surveyResults) {
		SurveyResultCount surveyResultCount = new SurveyResultCount(attribute);
		Map<Integer, Integer> result = surveyResults.getAggregateCountForDay(attribute, instant);

		int sum = 0;
		Integer value = result.get(SurveyResultCategories.VERY_NEGATIVE);
		if (null != value) {
			surveyResultCount.setVeryNegative(value);
			sum = sum + value;
		}

		value = result.get(SurveyResultCategories.NEGATIVE);
		if (null != value) {
			surveyResultCount.setSomewhatNegative(value);
			sum = sum + value;
		}

		value = result.get(SurveyResultCategories.NEUTRAL);
		if (null != value) {
			surveyResultCount.setNeutral(value);
			sum = sum + value;
		}

		value = result.get(SurveyResultCategories.POSITIVE);
		if (null != value) {
			surveyResultCount.setSomewhatPositive(value);
			sum = sum + value;
		}

		value = result.get(SurveyResultCategories.VERY_POSITIVE);
		if (null != value) {
			surveyResultCount.setVeryPositive(value);
			sum = sum + value;
		}

		surveyResultCount.setSum(sum);
		return surveyResultCount;
	}

}
