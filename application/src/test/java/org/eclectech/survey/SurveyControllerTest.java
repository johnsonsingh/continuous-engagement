package org.eclectech.survey;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

import org.eclectech.survey.application.SurveyController;
import org.eclectech.survey.domain.SurveyResult;
import org.eclectech.survey.persist.SurveyResultService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { MockServletContext.class, WebApplicationContext.class })
@WebAppConfiguration
public class SurveyControllerTest {

	private SurveyResultService surveyResultService = mock(SurveyResultService.class);

	@Before
	public void setUp() throws Exception {
		SurveyController surveyController = new SurveyController();
		surveyController.setSurveyResultService(surveyResultService);
		RestAssuredMockMvc.standaloneSetup(surveyController);
	}

	protected ObjectMapper newMapper() {
		return new ObjectMapper().registerModule(new JavaTimeModule());
	}

	@Test
	public void getResultsForUser() throws Exception {
		final String user = "user";
		LocalDateTime dateTime = LocalDateTime.now();
		Instant instant = dateTime.toInstant(ZoneOffset.UTC);
		int achievment = 1;
		int engagement = 2;
		int development = 3;
		int culture = 4;
		SurveyResult surveyResult = new SurveyResult(user, achievment, engagement, development, culture, instant);
		ArrayList<SurveyResult> list = new ArrayList<SurveyResult>();
		list.add(surveyResult);
		when(surveyResultService.getSurveyResults(user)).thenReturn(list);
		
//		MockMvcResponse result = given().header("accept", "application/json").param("user", user).when()
//				.get("/results/user").then().extract().response();
//		result.print();

		SurveyResult[] results = given().header("accept", "application/json").param("user", user).when()
				.get("/results/user").as(SurveyResult[].class);

		assertEquals(results[0].getUser(), user);
		assertEquals(results[0].getAchievement(), achievment);
		assertEquals(results[0].getEngagement(), engagement);
		assertEquals(results[0].getDevelopment(), development);
		assertEquals(results[0].getCulture(), culture);
		assertEquals(results[0].getInstant(), instant);

		verify(surveyResultService).getSurveyResults(user);

	}
}