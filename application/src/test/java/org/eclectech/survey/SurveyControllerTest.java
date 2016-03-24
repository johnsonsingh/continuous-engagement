package org.eclectech.survey;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

import org.eclectech.survey.application.SurveyController;
import org.eclectech.survey.domain.SurveyResult;
import org.eclectech.survey.persist.SurveyResults;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { MockServletContext.class, WebApplicationContext.class })
@WebAppConfiguration
public class SurveyControllerTest {

	private MockMvc mvc;
	private SurveyResults surveyResults = mock(SurveyResults.class);
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	@Before
	public void setUp() throws Exception {
		// this.mvc = webAppContextSetup(webApplicationContext).build();
		SurveyController surveyController = new SurveyController();
		surveyController.setSurveyResults(surveyResults);
		StandaloneMockMvcBuilder standaloneBuilder = MockMvcBuilders.standaloneSetup(surveyController);
		standaloneBuilder.setMessageConverters(new MappingJackson2HttpMessageConverter());
		mvc = standaloneBuilder.build();

	}

	protected ObjectMapper newMapper() {
		return new ObjectMapper().registerModule(new JavaTimeModule());
	}

	@Test
	public void getResultsForUser() throws Exception {
		final String user = "user";
		LocalDateTime dateTime = LocalDateTime.now();
		Instant instant = dateTime.toInstant(ZoneOffset.UTC);
		final ObjectMapper mapper = newMapper();
		String value = mapper.writer().with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.with(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS).writeValueAsString(instant);
		SurveyResult surveyResult = new SurveyResult(user, 1, 2, 3, 4, instant);
		ArrayList<SurveyResult> list = new ArrayList<SurveyResult>();
		list.add(surveyResult);
		when(surveyResults.getSurveyResults(user)).thenReturn(list);
		BigDecimal instantAsBigDecimal = new BigDecimal(value);
		mvc.perform(MockMvcRequestBuilders.get("/results/user").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].user", is(surveyResult.getUser())))
				.andExpect(jsonPath("$[0].achievement", is(surveyResult.getAchievement())))
				.andExpect(jsonPath("$[0].development", is(surveyResult.getDevelopment())))
				.andExpect(jsonPath("$[0].engagement", is(surveyResult.getEngagement())))
				.andExpect(jsonPath("$[0].culture", is(surveyResult.getCulture())))
				.andExpect(jsonPath("$[0].instant").value(instantAsBigDecimal));

		verify(surveyResults).getSurveyResults(user);

	}
}