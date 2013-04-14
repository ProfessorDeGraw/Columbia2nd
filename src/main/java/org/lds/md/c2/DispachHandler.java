package org.lds.md.c2;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.HttpRequestHandler;

//@WebServlet("/action/*")
//@WebServlet("/HelloDatabaseWorld")
//@WebServlet(description = "DispachHandler", urlPatterns = { "/*" }, name = "DispachHandler")
//@Component("DispachHandler")
public class DispachHandler implements HttpRequestHandler,
		ApplicationContextAware {

	private ApplicationContext context = null;

	@Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// SimpleSendEMail.sendEmail("4434162561@messaging.sprintpcs.com",
		// "Hi!", "This is a new test Msg ;)....");

		String baseServletPath = request.getServletPath();
		String requestPath = request.getRequestURI();

		List<String> actions = getRequestedActions(baseServletPath, requestPath);

		for (String a : actions) {
			System.out.println(a);
		}

		try {
			BeanRequest bean = (BeanRequest) context.getBean(actions.get(0));
			request.setAttribute("databaseMessage", bean.get(actions.subList(1, actions.size())));
		} catch (BeansException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String address = "/HelloDatabaseWorld.jsp";
		RequestDispatcher dispatcher = request.getRequestDispatcher(address);
		dispatcher.forward(request, response);
	}

	private List<String> getRequestedActions(String baseServletPath,
			String requestPath) {
		Vector<String> values = new Vector<String>();

		Pattern pattern = Pattern.compile(".*" + baseServletPath
				+ "([/[^/].]+)");
		Matcher matcher = pattern.matcher(requestPath);

		if (matcher.find()) {
			for (int i = 1; i <= matcher.groupCount(); i++) {
				values.add(matcher.group(i));
				System.out.println(matcher.group(i));
			}
		}

		String actionsString = "";
		if (values.size() > 0) {
			actionsString = values.elementAt(0);
		}
		List<String> actions = Arrays.asList(actionsString.split("/"));

		if (actions.size() > 0 && actions.get(0).length() == 0) {
			actions = actions.subList(1, actions.size());
		}
		return actions;
	}

	@Override
	public void setApplicationContext(ApplicationContext appContext)
			throws BeansException {
		context = appContext;
	}
}
