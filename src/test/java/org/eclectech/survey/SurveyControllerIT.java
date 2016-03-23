package org.eclectech.survey;

import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.eclectech.survey.Application;
import org.eclectech.survey.dto.SurveyResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest({ "server.port=0" })
public class SurveyControllerIT {
	private static Logger logger = LoggerFactory.getLogger(SurveyControllerIT.class);

	@Value("${local.server.port}")
	private int port;

	private URL base;
	private RestTemplate template;

	@Before
	public void setUp() throws Exception {
		this.base = new URL("http://localhost:" + port + "/results/" + "user");
		template = new TestRestTemplate();
	}

	@Test
	public void getSurveyResultsForUser() throws Exception {
		// Add the Jackson message converter
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		template.getMessageConverters().add(converter);
		SurveyResult[] results = template.getForObject(base.toString(), SurveyResult[].class);
		// List<SurveyResult> results = (List<SurveyResult>)response.getBody();
		for (SurveyResult result : results) {
			logger.info("result : {} ", result.toString());
		}
		assertTrue(results.length > 0);
	}

	@Test
	public void getSurveyResultsForUserInstant() throws Exception {
		Instant instant = LocalDateTime.of(2015, 12, 11, 0, 12).toInstant(ZoneOffset.UTC);

	}
}