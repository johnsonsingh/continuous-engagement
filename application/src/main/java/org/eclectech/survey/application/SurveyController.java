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
import org.eclectech.survey.persist.MongoPersistence;
import org.eclectech.survey.persist.SurveyResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SurveyController {

	public static final String ANONYMOUS = "anonymous";

	private static Logger logger = LoggerFactory.getLogger(SurveyController.class);

	@Autowired
	private SurveyResultService surveyResults;

	@Autowired
	private MongoPersistence mongoPersistence;

	//TODO should be POST
	@RequestMapping(value="/survey", method = RequestMethod.GET)
	public List<SurveyResultCount> submitSurvey(@RequestParam(name="user",defaultValue=ANONYMOUS) String user, @RequestParam("achievement") int achievement,
			@RequestParam("engagement") int engagement, @RequestParam("development") int development,
			@RequestParam("culture") int culture) {
		Instant now = Instant.now();

		SurveyResult surveyResult = new SurveyResult(user, achievement, engagement, development, culture, now);
		this.mongoPersistence.getMongoTemplate().save(surveyResult);
		logger.debug("persisted " + surveyResult);

		return getResultCountsForDay(now);
	}

	/**
	 * 
	 * @param user
	 * @return all results for user
	 */
	@RequestMapping(value="/results/{user}", method = RequestMethod.GET)
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
	@RequestMapping(value="/results/{user}/{dateAsString}", method = RequestMethod.GET)
	public List<SurveyResult> getResultsForUser(@PathVariable("user") String user, @PathVariable String dateAsString) {
		logger.debug("for user " + user + " date " + dateAsString);
		Instant instant = parseDateStringAtStartOfDay(dateAsString);
		return this.surveyResults.getSurveyResults(user, instant);
	}

	@RequestMapping(value="/results/date", method = RequestMethod.GET)
	public List<SurveyResult> getResults(@RequestParam(value = "from", required = true) String fromDateAsString,
			@RequestParam(value = "to", required = true) String toDateAsString) {
		logger.debug("getResults from {} to {} ", fromDateAsString, toDateAsString);
		Instant fromInstant = parseDateStringAtStartOfDay(fromDateAsString);
		Instant toInstant = parseDateStringAtStartOfDay(toDateAsString);

		return this.surveyResults.getAverageSurveyResults(fromInstant, toInstant);
	}

	@RequestMapping(value="/results/date/{dateAsString}", method = RequestMethod.GET)
	public List<SurveyResult> getResults(@PathVariable String dateAsString) {
		logger.debug("getResults for date {}", dateAsString);
		Instant instant = parseDateStringAtStartOfDay(dateAsString);
		return this.surveyResults.getSurveyResults(instant);
	}

	@RequestMapping(value="/resultCounts/date/{dateAsString}", method = RequestMethod.GET)
	public List<SurveyResultCount> getResultCounts(@PathVariable String dateAsString) {
		logger.debug("getResultCounts for date {}", dateAsString);
		return getResultCountsForDay(parseDateStringAtStartOfDay(dateAsString));
	}

	
	private List<SurveyResultCount> getResultCountsForDay(Instant instant) {
		List<SurveyResultCount> surveyResultCounts = new ArrayList<SurveyResultCount>();
		surveyResultCounts.add(SurveyResultCountUtil.create(SurveyCategories.ACHIEVEMENT,instant,this.surveyResults));
		surveyResultCounts.add(SurveyResultCountUtil.create(SurveyCategories.ENGAGEMENT,instant,this.surveyResults));
		surveyResultCounts.add(SurveyResultCountUtil.create(SurveyCategories.DEVELOPMENT,instant,this.surveyResults));
		surveyResultCounts.add(SurveyResultCountUtil.create(SurveyCategories.CULTURE,instant,this.surveyResults));
		return surveyResultCounts;
	}

	/**
	 * returns <code>Instant</code> for received date string
	 * 
	 * @param dateAsString
	 *            format YYYYY-MM-DD
	 * @return <code>Instant</code>
	 */
	protected static Instant parseDateStringAtStartOfDay(String dateAsString) {
		LocalDateTime dateTime = LocalDate.parse(dateAsString, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
		return dateTime.toInstant(ZoneOffset.UTC);
	}

	/**
	 * @param surveyResults
	 *            the surveyResults to set
	 */
	@Autowired
	protected void setSurveyResultService(SurveyResultService surveyResults) {
		this.surveyResults = surveyResults;
	}

	/**
	 * @param mongoPersistence
	 *            the mongoPersistence to set
	 */
	@Autowired
	protected void setMongoPersistence(MongoPersistence mongoPersistence) {
		this.mongoPersistence = mongoPersistence;
	}

}
