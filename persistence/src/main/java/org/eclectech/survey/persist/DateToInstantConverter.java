package org.eclectech.survey.persist;

import java.time.Instant;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;;

public class DateToInstantConverter implements Converter<Date, Instant> {

	@Override
	public Instant convert(Date source) {
		return source.toInstant();
	}

}
