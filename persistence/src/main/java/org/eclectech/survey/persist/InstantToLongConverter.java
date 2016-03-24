package org.eclectech.survey.persist;

import java.time.Instant;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;;

public class InstantToLongConverter implements Converter<Instant, Date> {

	@Override
	public Date convert(Instant source) {
		return Date.from(source);
	}

}
