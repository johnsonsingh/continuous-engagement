package org.eclectech.survey.test.data;

import java.net.UnknownHostException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.eclechtech.markov.Chain;
import org.eclectech.survey.dto.SurveyResult;
import org.eclectech.survey.persist.MongoPersistence;
import org.eclectech.survey.persist.MongoPersistenceImpl;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = { Loader.class })
public class Loader {
	private static Logger logger = LoggerFactory.getLogger(Loader.class);

	private static final int MAX_USERS = 100;
	private MongoPersistence mongoPersistence;
	private final List<String> users;
	private int baseAchievement = -1;
	private int baseEngagement = -2;
	private int baseDevelopment = 0;
	private int baseCulture = 0;

	private static final int[] range = { -2, -1, 0, 1, 2 };

	private static final int[][] probabilities = { { 20, 25, 35, 15, 05 }, { 05, 15, 35, 25, 20 },
			{ 25, 20, 15, 35, 05 }, { 35, 20, 05, 15, 25 }, { 05, 35, 25, 20, 15 } };
	private Chain chain;

	public Loader() throws UnknownHostException {
		mongoPersistence = new MongoPersistenceImpl();
		this.users = buildUsers();
		this.chain = new Chain(range, probabilities);
	}

	private List<String> buildUsers() {
		List<String> result = new ArrayList<>();
		final String userRoot = "user";
		for (int i = 0; i < MAX_USERS; ++i) {
			result.add(userRoot + i);
		}
		return result;
	}

//	@Test
	public void populateSurveyResultsForYear() {
		// drop it
		this.mongoPersistence.getMongoOperations().dropCollection(SurveyResult.class);
		Instant now = Instant.now().truncatedTo(ChronoUnit.DAYS);
		Instant instant = now.minus(Duration.ofDays(365));
		// we'll use the same value for all survey users for now
		while (instant.isBefore(now.plus(Duration.ofDays(1)))) {
			assignSurveyResultsForUsers(instant);
			// setup next values for survey results
			instant = instant.plus(Duration.ofDays(1));
		}
	}

	private void assignSurveyResultsForUsers(Instant instant) {
		logger.debug("assignSurveyResultsForUsers {} ", instant);
		// calculate number of users completing survey today
		double base = 0.2 * MAX_USERS;
		// number of users will be base + a random value between base and max
		long numberOfUsers = Math.round(base + Math.random() * (MAX_USERS - base));
		String user = null;
		SurveyResult surveyResult = new SurveyResult(user, baseAchievement, baseEngagement, baseDevelopment,
				baseCulture, instant);
		for (int i = 0; i < numberOfUsers; ++i) {
			// get random user , so users complete more than once
			double offset = Math.random() * users.size() - 1;
			if (offset < 0) {
				offset = 0;
			}
			//get a random user
			user = users.get((int)Math.round( Math.random() * (MAX_USERS - 1)));
			logger.debug("add for user {}", user);
			surveyResult.setUser(user);
			Query query = new Query();
			query.addCriteria(Criteria.where("user").is(user).and("instant").is(instant));
			Update update = new Update();
			update.set("user", user);
			update.set("achievement", surveyResult.getAchievement());
			update.set("culture", surveyResult.getCulture());
			update.set("development", surveyResult.getDevelopment());
			update.set("engagement", surveyResult.getEngagement());
			update.set("instant", instant);
			mongoPersistence.getMongoOperations().upsert(query,update,SurveyResult.class);
			//TODO test that no duplicates for users and day
			//transition for next iteration
			surveyResult.setAchievement(transition(surveyResult.getAchievement()));
			surveyResult.setCulture(transition(surveyResult.getCulture()));
			surveyResult.setDevelopment(transition(surveyResult.getDevelopment()));
			surveyResult.setEngagement(transition(surveyResult.getEngagement()));
		}
	}

	private int transition(int value) {
		int roll = (int) Math.round(Math.random() * 100);
		return this.chain.transition(value, roll);
	}
}