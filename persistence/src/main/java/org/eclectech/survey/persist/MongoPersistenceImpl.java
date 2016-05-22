package org.eclectech.survey.persist;

import java.net.UnknownHostException;
import java.util.Arrays;

import org.eclectech.survey.domain.SurveyResult;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.stereotype.Service;

import com.mongodb.MongoClient;

@Service
public class MongoPersistenceImpl implements MongoPersistence {

	private SimpleMongoDbFactory mongoDbFactory;
	private final MongoTemplate mongoTemplate;

	public MongoPersistenceImpl() throws UnknownHostException {
		mongoDbFactory = new SimpleMongoDbFactory(new MongoClient(), "ContinuousEngagement");
		MongoMappingContext context = new MongoMappingContext();
		MappingMongoConverter mongoConverter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory),
				context);
		mongoConverter.setCustomConversions(getCustomConversions());
		mongoConverter.afterPropertiesSet();

		this.mongoTemplate = new MongoTemplate(mongoDbFactory, mongoConverter);
		this.mongoTemplate.indexOps(SurveyResult.class).ensureIndex(new Index().on("user", Direction.ASC));
		this.mongoTemplate.indexOps(SurveyResult.class).ensureIndex(new Index().on("instant", Direction.ASC));
	}

	/**
	 * @return CustomConversions
	 */
	private CustomConversions getCustomConversions() {
		return new CustomConversions(Arrays.asList(new DateToInstantConverter(), new InstantToDateConverter()));
	}

	/**
	 * @return the mongoOperations
	 */
	@Override
	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

}
