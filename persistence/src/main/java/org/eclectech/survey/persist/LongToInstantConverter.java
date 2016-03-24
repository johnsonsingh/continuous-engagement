package org.eclectech.survey.persist;

import java.time.Instant;

import org.springframework.core.convert.converter.Converter;;

public class LongToInstantConverter implements Converter<Long, Instant> {

	@Override
	public Instant convert(Long source) {
		return Instant.ofEpochMilli(source);
	}

}
