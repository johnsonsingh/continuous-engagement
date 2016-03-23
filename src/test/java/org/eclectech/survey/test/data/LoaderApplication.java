package org.eclectech.survey.test.data;

import java.net.UnknownHostException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration
public class LoaderApplication {
	private static Logger logger = LoggerFactory.getLogger(LoaderApplication.class);

	public static void main(String[] args) throws UnknownHostException {
		ApplicationContext ctx = SpringApplication.run(LoaderApplication.class, args);

		logger.info("Test Data Loader");

		String[] beanNames = ctx.getBeanDefinitionNames();
		Arrays.sort(beanNames);
		for (String beanName : beanNames) {
			logger.info("beanName : {}", beanName);
		}
	}
}