package org.eclectech.survey.persist;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclectech.survey.domain.SurveyResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.DBObject;

@Service
public class SurveyResultServiceImpl implements SurveyResultService {
	private static Logger logger = LoggerFactory.getLogger(SurveyResultService.class);

	@Autowired
	private MongoPersistenceImpl mongoPersistenceImpl;

	/**
	 * @param mongoPersistenceImpl
	 *            the mongoPersistenceImpl to set
	 */
	@Autowired
	public void setMongoPersistenceImpl(MongoPersistenceImpl mongoPersistenceImpl) {
		this.mongoPersistenceImpl = mongoPersistenceImpl;
	}

	@Override
	public List<SurveyResult> getSurveyResults(String user) {
		logger.debug("calling for user " + user);
		Query query = new Query(Criteria.where("user").is(user));
		return this.mongoPersistenceImpl.getMongoOperations().find(query, SurveyResult.class);
	}

	@Override
	public List<SurveyResult> getSurveyResults(String user, Instant instant) {
		logger.info("query for user {} instant {}", user, instant);
		Query query = new Query(
				Criteria.where("user").is(user).and("instant").gte(instant).lt(instant.plus(Duration.ofDays(1))));
		return this.mongoPersistenceImpl.getMongoOperations().find(query, SurveyResult.class);
	}

	@Override
	public List<SurveyResult> getAverageSurveyResults(Instant fromInstant, Instant toInstant) {
		logger.info("query for fromInstant {} toInstant {}", fromInstant, toInstant);
		TypedAggregation<SurveyResult> aggregation = newAggregation(SurveyResult.class,
				match(Criteria.where("instant").gte(fromInstant).lt(toInstant)),
				group("user").avg("achievement").as("achievement").avg("culture").as("culture").avg("development")
						.as("development").avg("engagement").as("engagement"));
		AggregationResults<DBObject> result = mongoPersistenceImpl.getMongoOperations().aggregate(aggregation,
				DBObject.class);
		List<DBObject> DBObjects = result.getMappedResults();
		List<SurveyResult> results = new ArrayList<SurveyResult>();
		for (DBObject dbObject : DBObjects) {
			logger.info("dbobject {}", dbObject);
			SurveyResult surveyResult = new SurveyResult(dbObject.get("_id").toString(),
					(int) Math.round(Double.parseDouble(dbObject.get("achievement").toString())),
					(int) Math.round(Double.parseDouble(dbObject.get("engagement").toString())),
					(int) Math.round(Double.parseDouble(dbObject.get("development").toString())),
					(int) Math.round(Double.parseDouble(dbObject.get("culture").toString())), null);
			results.add(surveyResult);
		}
		return results;
	}

	@Override
	public List<SurveyResult> getSurveyResults(Instant instant) {
		logger.info("query for instant {}", instant);
		Query query = new Query(Criteria.where("instant").gte(instant).lt(instant.plus(Duration.ofDays(1))));
		return mongoPersistenceImpl.getMongoOperations().find(query, SurveyResult.class);
	}
	
	@Override
	public Map<Integer, Integer> getAggregateCountForDay(String attribute, Instant instant) {
		Instant startOfDay = instant.truncatedTo(ChronoUnit.DAYS);
		logger.info("getAggregateCountForDay {}",attribute);
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		TypedAggregation<SurveyResult> aggregation = newAggregation(SurveyResult.class,
				match(Criteria.where("instant").gte(startOfDay).lt(startOfDay.plus(Duration.ofDays(1)))),
				group(attribute).count().as("counts"));
		List<DBObject>dbObjects = this.mongoPersistenceImpl.getMongoOperations().aggregate(aggregation,
				DBObject.class).getMappedResults();
		for (DBObject dbObject : dbObjects) {
			logger.info("dbobject {} {} {} ", dbObject, dbObject.get("_id"), dbObject.get("counts"));
			result.put(Integer.parseInt(dbObject.get("_id").toString()), 
					   Integer.parseInt(dbObject.get("counts").toString()));
		}
		return result;
	}

}
