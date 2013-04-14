package org.lds.md.c2;

import java.io.IOException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.HttpRequestHandler;


//@WebServlet("/HelloDatabaseWorld")
//@WebServlet(description = "DispachHandler", urlPatterns = { "/*" }, name = "DispachHandler")
public class DispachHandler implements HttpRequestHandler {

	KeyValueDatabase database = null;

	public void setDatabase(KeyValueDatabase database) {
		this.database = database;
	}

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//SimpleSendEMail.sendEmail("4434162561@messaging.sprintpcs.com", "Hi!", "This is a new test Msg ;)....");
		
		String a =request.getServletPath();
		String b = request.getRequestURI();
		String c = request.getContextPath();
		String d = request.getQueryString();
		String e = request.getServletPath();
		
		
		Vector<String> values = new Vector<String>();
		
		Pattern pattern = Pattern.compile(".*"+a+"/(.*)*");
		Matcher matcher = pattern.matcher(b);

		if (matcher.find()) {
			for (int i = 1; i <= matcher.groupCount(); i++) {
				values.add(matcher.group(i));
				System.out.println(matcher.group(i));
			}
		}
		
		database.actionWriter();
		database.actionReader();

		request.setAttribute("databaseMessage", database.getDbMessage());

		String address = "../HelloDatabaseWorld.jsp";
		RequestDispatcher dispatcher = request.getRequestDispatcher(address);
		dispatcher.forward(request, response);;
	}
}
