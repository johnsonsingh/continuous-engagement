package org.eclectech.survey.application;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclectech.survey.SurveyResultValues;
import org.eclectech.survey.domain.SurveyResult;
import org.eclectech.survey.domain.SurveyResultCount;
import org.eclectech.survey.persist.MongoPersistence;
import org.eclectech.survey.persist.SurveyResultService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { MockServletContext.class, WebApplicationContext.class })
@WebAppConfiguration
public class SurveyControllerTest {
	private final String user = "user";
	private int achievment = 1;
	private int engagement = 2;
	private int development = 3;
	private int culture = 4;
	private HashMap<Integer, Integer> ach = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> eng = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> dev = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> cul = new HashMap<Integer, Integer>();

	private SurveyResultService surveyResultService = mock(SurveyResultService.class);
	private MongoPersistence mongoPersistence = mock(MongoPersistence.class);
	private MongoTemplate mongoTemplate = mock(MongoTemplate.class);
	
	@Before
	public void setUp() throws Exception {
		SurveyController surveyController = new SurveyController();
		surveyController.setSurveyResultService(surveyResultService);
		surveyController.setMongoPersistence(mongoPersistence);
		RestAssuredMockMvc.standaloneSetup(surveyController);
		setupUpAggregateResults();
	}

	protected ObjectMapper newMapper() {
		return new ObjectMapper().registerModule(new JavaTimeModule());
	}

	@Test
	public void setSurveyResults() {
		final String user = "user2";
		when(mongoPersistence.getMongoTemplate()).thenReturn(mongoTemplate);
		when(surveyResultService.getAggregateCountForDay(eq(SurveyCategories.ACHIEVEMENT), any(Instant.class))).thenReturn(ach);
		when(surveyResultService.getAggregateCountForDay(eq(SurveyCategories.ENGAGEMENT), any(Instant.class))).thenReturn(eng);
		when(surveyResultService.getAggregateCountForDay(eq(SurveyCategories.CULTURE), any(Instant.class))).thenReturn(cul);
		when(surveyResultService.getAggregateCountForDay(eq(SurveyCategories.DEVELOPMENT), any(Instant.class))).thenReturn(dev);
		SurveyResultCount[] results = given().header("accept", "application/json").param("user", user)
				.param(SurveyCategories.ACHIEVEMENT, achievment).param(SurveyCategories.ENGAGEMENT, engagement).param(SurveyCategories.DEVELOPMENT, development)
				.param(SurveyCategories.CULTURE, culture).when().get("/survey").as(SurveyResultCount[].class);

		verify(mongoPersistence,times(1)).getMongoTemplate();
		//TODO verify contents of survey result
		verify(mongoTemplate).save(any(SurveyResult.class));
		verify(surveyResultService).getAggregateCountForDay(eq(SurveyCategories.ACHIEVEMENT),any(Instant.class));
		verify(surveyResultService).getAggregateCountForDay(eq(SurveyCategories.ENGAGEMENT),any(Instant.class));
		verify(surveyResultService).getAggregateCountForDay(eq(SurveyCategories.CULTURE),any(Instant.class));
		verify(surveyResultService).getAggregateCountForDay(eq(SurveyCategories.DEVELOPMENT),any(Instant.class));
 	}

	@Test
	public void getResultsForUser() throws Exception {
		LocalDateTime dateTime = LocalDateTime.now();
		Instant instant = dateTime.toInstant(ZoneOffset.UTC);
		SurveyResult surveyResult = new SurveyResult(user, achievment, engagement, development, culture, instant);
		ArrayList<SurveyResult> list = new ArrayList<SurveyResult>();
		list.add(surveyResult);
		when(surveyResultService.getSurveyResults(user)).thenReturn(list);

		SurveyResult[] results = given().header("accept", "application/json").param("user", user).when()
				.get("/results/user").as(SurveyResult[].class);

		checkExpectedSurveyResult(instant, results);

		verify(surveyResultService).getSurveyResults(user);
	}

	@Test
	public void getResultsForUserAndDate() {
		//YYYYY-MM-DD
		final String dateAsString = "2016-05-10";
		final Instant instant = SurveyController.parseDateStringAtStartOfDay(dateAsString);
		SurveyResult surveyResult = new SurveyResult(user, achievment, engagement, development, culture, instant);
		ArrayList<SurveyResult> list = new ArrayList<SurveyResult>();
		list.add(surveyResult);
		when(surveyResultService.getSurveyResults(user, instant)).thenReturn(list);
		
		SurveyResult[] results = given().header("accept", "application/json").
				param("user", user).
				param("dateAsString", dateAsString).
				when().get("/results/user/"+dateAsString).as(SurveyResult[].class);
		checkExpectedSurveyResult(instant, results);
		verify(surveyResultService).getSurveyResults(user,instant);
	}

	@Test
	public void getResultsForDateInterval() {
		final String from = "2016-05-10";
		final String to = "2016-05-11";
		Instant fromInstant = SurveyController.parseDateStringAtStartOfDay(from);
		Instant toInstant = SurveyController.parseDateStringAtStartOfDay(to);

		SurveyResult surveyResult = new SurveyResult(user, achievment, engagement, development, culture, fromInstant);
		ArrayList<SurveyResult> list = new ArrayList<SurveyResult>();
		list.add(surveyResult);
		when(surveyResultService.getAverageSurveyResults(fromInstant,toInstant)).thenReturn(list);

		SurveyResult[] results = given().header("accept", "application/json").
				param("from", from).
				param("to", to).
				when().get("/results/date").as(SurveyResult[].class);
		checkExpectedSurveyResult(fromInstant, results);
		verify(surveyResultService).getAverageSurveyResults(fromInstant,toInstant);
	}
	
	@Test 
	public void getResultsForDate() {
		final String date = "2016-05-10";
		Instant instant = SurveyController.parseDateStringAtStartOfDay(date);

		SurveyResult surveyResult = new SurveyResult(user, achievment, engagement, development, culture, instant);
		ArrayList<SurveyResult> list = new ArrayList<SurveyResult>();
		list.add(surveyResult);
		when(surveyResultService.getSurveyResults(instant)).thenReturn(list);

		SurveyResult[] results = given().header("accept", "application/json").
				when().get("/results/date/"+date).as(SurveyResult[].class);
		checkExpectedSurveyResult(instant, results);
		verify(surveyResultService).getSurveyResults(instant);
	}

	@Test 
	public void getResultCountsForDate() {
		final String date = "2016-05-10";
		Instant instant = SurveyController.parseDateStringAtStartOfDay(date);
		when(surveyResultService.getAggregateCountForDay(SurveyCategories.ACHIEVEMENT,instant)).thenReturn(ach);
		when(surveyResultService.getAggregateCountForDay(SurveyCategories.CULTURE,instant)).thenReturn(cul);
		when(surveyResultService.getAggregateCountForDay(SurveyCategories.DEVELOPMENT,instant)).thenReturn(dev);
		when(surveyResultService.getAggregateCountForDay(SurveyCategories.ENGAGEMENT,instant)).thenReturn(eng);
		
		SurveyResultCount[] results = given().header("accept", "application/json").
				when().get("/resultCounts/date/"+date).as(SurveyResultCount[].class);
		checkExpectedSurveyResultCounts(instant, results);
		
		verify(surveyResultService).getAggregateCountForDay(SurveyCategories.ACHIEVEMENT, instant);
		verify(surveyResultService).getAggregateCountForDay(SurveyCategories.CULTURE, instant);
		verify(surveyResultService).getAggregateCountForDay(SurveyCategories.DEVELOPMENT, instant);
		verify(surveyResultService).getAggregateCountForDay(SurveyCategories.ENGAGEMENT, instant);
	}
	/*

	@RequestMapping(value="/resultCounts/date/{dateAsString}", method = RequestMethod.GET)
	public List<SurveyResultCount> getResultCounts(@PathVariable String dateAsString) {
		logger.debug("getResultCounts for date {}", dateAsString);
		return getResultCountsForDay(parseDateStringAtStartOfDay(dateAsString));
	}

	 */

	private void checkExpectedSurveyResultCounts(Instant instant, SurveyResultCount[] results) {
		assertEquals(results.length, 4);
		Map<String, SurveyResultCount> map = new HashMap<String, SurveyResultCount>();
		for (int i=0; i< results.length; ++i) {
			map.put(results[i].getName(), results[i]);
		}
		checkExpectedSurveyResultCount(SurveyCategories.ACHIEVEMENT, map, ach);
		checkExpectedSurveyResultCount(SurveyCategories.CULTURE, map, cul);
		checkExpectedSurveyResultCount(SurveyCategories.DEVELOPMENT, map, dev);
		checkExpectedSurveyResultCount(SurveyCategories.ENGAGEMENT, map, eng);
	}

	private void checkExpectedSurveyResultCount(String surveyCategory, Map<String, SurveyResultCount> map, HashMap<Integer, Integer> testData) {
		SurveyResultCount surveyResultCount = map.get(surveyCategory);
		assertEquals(surveyResultCount.getName(), surveyCategory);
		assertEquals(surveyResultCount.getVeryNegative(), testData.get(SurveyResultValues.VERY_NEGATIVE).intValue());
		assertEquals(surveyResultCount.getSomewhatNegative(), testData.get(SurveyResultValues.NEGATIVE).intValue());
		assertEquals(surveyResultCount.getNeutral(), testData.get(SurveyResultValues.NEUTRAL).intValue());
		assertEquals(surveyResultCount.getSomewhatPositive(), testData.get(SurveyResultValues.POSITIVE).intValue());
		assertEquals(surveyResultCount.getVeryPositive(), testData.get(SurveyResultValues.VERY_POSITIVE).intValue());
	}

	private void setupUpAggregateResults() {
		setupUpAggregateResult(ach, 2, 3, 4, 5, 6);
		setupUpAggregateResult(eng, 6, 5, 4, 3, 2);
		setupUpAggregateResult(dev, 2, 3, 6, 5, 4);
		setupUpAggregateResult(cul, 3, 2, 5, 4, 5);
	}

	private void setupUpAggregateResult(HashMap<Integer, Integer> aggMap, int veryNegative, int negative, int neutral,
			int positive, int veryPositive) {
		aggMap.put(SurveyResultValues.VERY_NEGATIVE, veryNegative);
		aggMap.put(SurveyResultValues.NEGATIVE, negative);
		aggMap.put(SurveyResultValues.NEUTRAL, neutral);
		aggMap.put(SurveyResultValues.POSITIVE, positive);
		aggMap.put(SurveyResultValues.VERY_POSITIVE, veryPositive);
	}

	private void checkExpectedSurveyResult(Instant instant, SurveyResult[] results) {
		assertEquals(results[0].getUser(), user);
		assertEquals(results[0].getAchievement(), achievment);
		assertEquals(results[0].getEngagement(), engagement);
		assertEquals(results[0].getDevelopment(), development);
		assertEquals(results[0].getCulture(), culture);
		assertEquals(results[0].getInstant(), instant);
	}
	
}