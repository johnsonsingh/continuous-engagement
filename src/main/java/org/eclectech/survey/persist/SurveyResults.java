package org.eclectech.survey.persist;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.eclectech.survey.dto.SurveyResult;

public interface SurveyResults {

	public List<SurveyResult> getSurveyResults(String user);

	public List<SurveyResult> getSurveyResults(String user, Instant instant);

	public List<SurveyResult> getAverageSurveyResults(Instant fromInstant, Instant toInstant);

	public List<SurveyResult> getSurveyResults(Instant instant);

	Map<Integer, Integer> getAggregateCountForDay(String attribute, Instant instant);

}
