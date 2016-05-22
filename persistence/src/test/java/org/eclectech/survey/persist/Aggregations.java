package org.eclectech.survey.persist;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import org.eclectech.survey.domain.SurveyResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mongodb.DBObject;

//@RunWith(SpringJUnit4ClassRunner.class)
public class Aggregations {

	private MongoPersistenceImpl mongoPersistence;
	private SurveyResultServiceImpl surveyResults;
	private static Logger logger = LoggerFactory.getLogger(Aggregations.class);

	public Aggregations() throws UnknownHostException {
		mongoPersistence = new MongoPersistenceImpl();
		surveyResults = new SurveyResultServiceImpl();
		surveyResults.setMongoPersistenceImpl(mongoPersistence);
	}

	//@Test
	public void testForDate() {
		logger.info("***************** testForDate **************************");

		Instant instant = LocalDateTime.of(2015, 12, 11, 0, 12).toInstant(ZoneOffset.UTC);
		List<SurveyResult> surveyResults = this.surveyResults.getAverageSurveyResults(instant,
				instant.plus(Duration.ofDays(1)));
		for (SurveyResult surveyResult : surveyResults) {
			logger.info("SurveyResult {}", surveyResult);
		}
	}

	//@Test
	public void testForDate2() {
		logger.info("***************** testForDate2 **************************");
		Instant instant = LocalDateTime.of(2015, 12, 11, 0, 12).toInstant(ZoneOffset.UTC);
		List<SurveyResult> surveyResults = this.surveyResults.getAverageSurveyResults(instant,
				instant.plus(Duration.ofDays(5)));
		for (SurveyResult surveyResult : surveyResults) {
			logger.info("SurveyResult {}", surveyResult);
		}
	}
	
	//@Test
	public void getAggregateSurveyResults() {
		logger.info("***************** getAggregateSurveyResults **************************");
		Instant instant = LocalDateTime.of(2015, 12, 11, 0, 12).toInstant(ZoneOffset.UTC);
		List<SurveyResult> surveyResults = this.surveyResults.getSurveyResults(instant);
		for (SurveyResult surveyResult: surveyResults) {
			logger.info("surveyResult {} ", surveyResult);
		}
		logger.info("***************** queryAggregation **************************");
		queryAggregation("achievement", instant);
		queryAggregation("culture", instant);
		queryAggregation("development", instant);
		queryAggregation("engagement", instant);

	}
	 /*
	@Test
	public void SurveyResultCountUtilTest() {
		logger.info("***************** SurveyResultCountUtilTest **************************");

		Instant instant = LocalDateTime.of(2015, 12, 11, 0, 12).toInstant(ZoneOffset.UTC);
		List<SurveyResultCount> surveyResultCounts = new ArrayList<SurveyResultCount>();
		surveyResultCounts.add(SurveyResultCountUtil.create("achievement",instant,this.surveyResults));
		surveyResultCounts.add(SurveyResultCountUtil.create("engagement",instant,this.surveyResults));
		surveyResultCounts.add(SurveyResultCountUtil.create("development",instant,this.surveyResults));
		surveyResultCounts.add(SurveyResultCountUtil.create("culture",instant,this.surveyResults));
		for (SurveyResultCount surveyResultCount : surveyResultCounts) {
			logger.info("SurveyResultCount {}", surveyResultCount);
		}
		}*/
	
	//@Test
	public void testAggregateForCountForDay() {
		logger.info("***************** testAggregateForCountForDay **************************");
		Instant instant = LocalDateTime.of(2015, 12, 11, 0, 12).toInstant(ZoneOffset.UTC);
		logger.info("instances {} {} ", instant, instant.plus(Duration.ofDays(1)));
		String attribute = "achievement";
		Map<Integer,Integer> result = this.surveyResults.getAggregateCountForDay(attribute, instant);
		outputMap(attribute, result);
		attribute = "culture";
		result = this.surveyResults.getAggregateCountForDay(attribute, instant);
		outputMap(attribute, result);
		attribute = "development";
		result = this.surveyResults.getAggregateCountForDay(attribute, instant);
		outputMap(attribute, result);
		attribute = "engagement";
		result = this.surveyResults.getAggregateCountForDay(attribute, instant);
		outputMap(attribute, result);
	}

	private void outputMap(String attribute, Map<Integer,Integer> result) {
		logger.info("outputMap {}", attribute);
		for (Map.Entry<Integer,Integer> entry : result.entrySet()) {
			logger.info("testAggregateForCountForDay {} : {} ", entry.getKey(), entry.getValue());
		}
	}
	
	private void queryAggregation(String attribute, Instant instant) {
		logger.info("queryAggregation : {} ", attribute);
		TypedAggregation<SurveyResult> aggregation = newAggregation(SurveyResult.class,
				match(Criteria.where("instant").gt(instant).lt(instant.plus(Duration.ofDays(1)))),
				group(attribute).count().as("counts"));
		AggregationResults<DBObject> result = this.mongoPersistence.getMongoTemplate().aggregate(aggregation,
				DBObject.class);
		List<DBObject>dbObjects = result.getMappedResults();
		for (DBObject dbObject : dbObjects) {
			logger.info("dbobject {}", dbObject);
		}
	}

}
