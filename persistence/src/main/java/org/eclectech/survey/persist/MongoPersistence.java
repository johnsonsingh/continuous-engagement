package org.eclectech.survey.persist;

import org.springframework.data.mongodb.core.MongoTemplate;

public interface MongoPersistence {

	public MongoTemplate getMongoTemplate();
}