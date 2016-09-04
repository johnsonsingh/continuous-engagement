package org.eclectech.survey.persist;

import static org.junit.Assert.assertEquals;

import org.eclectech.survey.domain.SurveyResultCount;
import org.junit.Test;

public class SurveyResultCountTest {

	
	private static final int _3 = 3;
	private static final int _2 = 2;
	private static final int _1 = 1;
	private static final int _0 = 0;
	private static final String _NAME = "name";
	private static final int _4 = 4;
	private static final int _10 = 10;

	@Test
	public void construct() {
		String name = _NAME;
		int veryNegative = _0;
		int somewhatNegative = _1;
		int neutral = _2;
		int somewhatPositive = _3;
		int veryPositive =_4;
		int sum = _10;
		SurveyResultCount count = new SurveyResultCount(name, 
				                                        veryNegative, 
				                                        somewhatNegative, 
				                                        neutral, 
				                                        somewhatPositive, 
				                                        veryPositive, 
				                                        sum);
		assertEquals(count.getSum(), _10);
		assertEquals(count.getName(), _NAME);
		assertEquals(count.getVeryNegative(), _0);
		
		assertEquals(count.getSomewhatNegative(), _1);
		assertEquals(count.getNeutral(), _2);
		assertEquals(count.getSomewhatPositive(), _3);
		assertEquals(count.getVeryPositive(), _4);

	}
}
