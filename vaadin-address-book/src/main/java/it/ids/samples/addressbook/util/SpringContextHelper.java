package it.ids.samples.addressbook.util;

import org.springframework.context.ApplicationContext;

public class SpringContextHelper {

	private static ApplicationContext context;

	public static void setContext(ApplicationContext context) {
		SpringContextHelper.context = context;
	}

	public static ApplicationContext getContext() {
		return context;
	}

	public static Object getBean(String beanName) {
		return context.getBean(beanName);
	}
}
