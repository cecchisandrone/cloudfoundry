package it.ids.samples.addressbook.util;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.context.ConfigurableWebApplicationContext;

public class ContextProfileInitializer implements ApplicationContextInitializer<ConfigurableWebApplicationContext> {

	protected static Logger logger = Logger.getLogger(ContextProfileInitializer.class);

	public void initialize(ConfigurableWebApplicationContext ctx) {
		ConfigurableEnvironment environment = ctx.getEnvironment();
		// Cloud environment?
		String services = System.getenv("VCAP_SERVICES");
		if (services != null) {
			logger.info("VCAP_SERVICES " + java.lang.System.getenv("VCAP_SERVICES"));
			environment.setActiveProfiles("cloud");

		} else {
			environment.setActiveProfiles("development");
		}

		logger.info("Initializing Spring application context with profiles: " + Arrays.toString(environment.getActiveProfiles()));
	}
}