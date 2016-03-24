package org.eclectech.survey.application;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.eclectech.survey.domain.SurveyResult;
import org.eclectech.survey.domain.SurveyResultCount;
import org.eclectech.survey.persist.MongoPersistenceImpl;
import org.eclectech.survey.persist.SurveyResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SurveyController {

	private static Logger logger = LoggerFactory.getLogger(SurveyController.class);

	@Autowired
	private SurveyResults surveyResults;

	@Autowired
	private MongoPersistenceImpl mongoPersistenceImpl;

	@RequestMapping("/survey")
	public List<SurveyResultCount> submitSurvey(@RequestParam("achievement") int achievement,
			@RequestParam("engagement") int engagement, @RequestParam("development") int development,
			@RequestParam("culture") int culture) {
		Instant now = Instant.now();

		SurveyResult surveyResult = new SurveyResult("user", achievement, engagement, development, culture, now);
		mongoPersistenceImpl.getMongoOperations().save(surveyResult);
		logger.debug("persisted " + surveyResult);

		return getResultCountsForDay(now);
	}

	private List<SurveyResultCount> getResultCountsForDay(Instant now) {
		List<SurveyResultCount> surveyResultCounts = new ArrayList<SurveyResultCount>();
		surveyResultCounts.add(SurveyResultCountUtil.create("achievement",now,this.surveyResults));
		surveyResultCounts.add(SurveyResultCountUtil.create("engagement",now,this.surveyResults));
		surveyResultCounts.add(SurveyResultCountUtil.create("development",now,this.surveyResults));
		surveyResultCounts.add(SurveyResultCountUtil.create("culture",now,this.surveyResults));
		return surveyResultCounts;
	}

	/**
	 * 
	 * @param user
	 * @return all results for user
	 */
	@RequestMapping("/results/{user}")
	public List<SurveyResult> getResultsForUser(@PathVariable("user") String user) {
		logger.debug("for user " + user);
		return this.surveyResults.getSurveyResults(user);

	}

	/**
	 * 
	 * @param user
	 * @param dateAsString
	 *            - format YYYYY-MM-DD
	 * @return results for user on received day
	 */
	@RequestMapping("/results/{user}/{dateAsString}")
	public List<SurveyResult> getResultsForUser(@PathVariable("user") String user, @PathVariable String dateAsString) {
		logger.debug("for user " + user + " date " + dateAsString);
		Instant instant = parseDateStringAtStartOfDay(dateAsString);
		return this.surveyResults.getSurveyResults(user, instant);
	}

	@RequestMapping("/results/date")
	public List<SurveyResult> getResults(@RequestParam(value = "from", required = true) String fromDateAsString,
			@RequestParam(value = "to", required = true) String toDateAsString) {
		logger.debug("getResults from {} to {} ", fromDateAsString, toDateAsString);
		Instant fromInstant = parseDateStringAtStartOfDay(fromDateAsString);
		Instant toInstant = parseDateStringAtStartOfDay(toDateAsString);

		return this.surveyResults.getAverageSurveyResults(fromInstant, toInstant);
	}

	@RequestMapping("/results/date/{dateAsString}")
	public List<SurveyResult> getResults(@PathVariable String dateAsString) {
		logger.debug("getResults for date {}", dateAsString);
		Instant instant = parseDateStringAtStartOfDay(dateAsString);
		return this.surveyResults.getSurveyResults(instant);
	}

	@RequestMapping("/resultCounts/date/{dateAsString}")
	public List<SurveyResultCount> getResultCounts(@PathVariable String dateAsString) {
		logger.debug("getResultCounts for date {}", dateAsString);
		return getResultCountsForDay(parseDateStringAtStartOfDay(dateAsString));
	}

	
	/**
	 * returns <code>Instant</code> for received date string
	 * 
	 * @param dateAsString
	 *            format YYYYY-MM-DD
	 * @return <code>Instant</code>
	 */
	private Instant parseDateStringAtStartOfDay(String dateAsString) {
		LocalDateTime dateTime = LocalDate.parse(dateAsString, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
		return dateTime.toInstant(ZoneOffset.UTC);
	}

	/**
	 * @param surveyResults
	 *            the surveyResults to set
	 */
	@Autowired
	public void setSurveyResults(SurveyResults surveyResults) {
		this.surveyResults = surveyResults;
	}

	/**
	 * @param mongoPersistenceImpl
	 *            the mongoPersistenceImpl to set
	 */
	@Autowired
	private void setMongoPersistenceImpl(MongoPersistenceImpl mongoPersistenceImpl) {
		this.mongoPersistenceImpl = mongoPersistenceImpl;
	}

}
