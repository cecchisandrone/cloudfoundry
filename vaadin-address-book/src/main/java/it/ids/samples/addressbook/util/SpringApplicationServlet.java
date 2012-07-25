package it.ids.samples.addressbook.util;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;

public class SpringApplicationServlet extends AbstractApplicationServlet {
	private static final long serialVersionUID = 1L;
	private WebApplicationContext applicationContext;
	private Class<? extends Application> applicationClass;
	private String applicationBean;

	@SuppressWarnings("unchecked")
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);

		applicationBean = servletConfig.getInitParameter("applicationBean");

		if (applicationBean == null) {
			throw new ServletException("ApplicationBean not specified in servlet parameters");
		}

		applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());

		applicationClass = (Class<? extends Application>) applicationContext.getType(applicationBean);

		SpringContextHelper.setContext(applicationContext);
	}

	@Override
	protected Class<? extends Application> getApplicationClass() throws ClassNotFoundException {
		return applicationClass;
	}

	@Override
	protected Application getNewApplication(HttpServletRequest request) {
		return (Application) applicationContext.getBean(applicationBean);
	}

}
