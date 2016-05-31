package org.eclectech.survey;

import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.eclectech.survey.application.Application;
import org.eclectech.survey.domain.SurveyResult;
import org.eclectech.survey.domain.SurveyResultCount;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest({ "server.port=0", "spring.data.mongodb.uri=mongodb://127.0.0.1:32770/ContinuousIntegration" })
@ComponentScan(basePackages = { "org.eclectech.survey" })
@EnableAutoConfiguration
//TODO use embedded mongo for tests ?
public class SurveyControllerIT {
	private static final String HTTP_LOCALHOST = "http://localhost:";

	private static final String USER1 = "user1";
	private static final String USER = "user";

	private static Logger logger = LoggerFactory.getLogger(SurveyControllerIT.class);

	@Value("${local.server.port}")
	private int port;
	private RestTemplate template;
	private String hostAndPort;
	
	@Before
	public void setUp() throws Exception {
		template = new TestRestTemplate();
		// Add the Jackson message converter
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		template.getMessageConverters().add(converter);
		hostAndPort = HTTP_LOCALHOST + port;
	}

	@Test
	public void getSurveyResultsForUser() throws Exception {
		URL url = new URL( hostAndPort + "/results/" + USER1);
		SurveyResult[] results = template.getForObject(url.toString(), SurveyResult[].class);
		for (SurveyResult result : results) {
			logger.info("result : {} ", result.toString());
		}
		assertTrue(results.length > 0);
	}

	@Test
	public void getSurveyResultsForUserInstant() throws Exception {
		///Instant instant = LocalDateTime.of(2015, 12, 11, 0, 12).toInstant(ZoneOffset.UTC);
		//RequestMapping(value="/results/{user}/{dateAsString}", method = RequestMethod.GET)
		//TODO yes there is a race condition here
		Instant now = Instant.now();
		URL url = new URL( hostAndPort + "/survey/?achievement=0&engagement=1&development=2&culture=-1" );
		template.getForObject(url.toString(), SurveyResultCount[].class);
		Instant startOfDay = now.truncatedTo(ChronoUnit.DAYS);
		String dateStr = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault()).format(startOfDay);
		url = new URL( hostAndPort + "/results/" + USER + "/" + dateStr);
		SurveyResult[] results = template.getForObject(url.toString(), SurveyResult[].class);
		for (SurveyResult result : results) {
			logger.info("result : {} ", result.toString());
		}
		assertTrue(results.length > 0);
	}
	
	//TODO test submit as above
}