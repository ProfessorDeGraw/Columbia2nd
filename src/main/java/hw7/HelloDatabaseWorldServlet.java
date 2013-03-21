package hw7;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.HttpRequestHandler;


//@WebServlet("/HelloDatabaseWorld")
//@WebServlet(description = "Http Servlet using pure java / annotations", urlPatterns = { "/HelloDatabaseWorld" }, name = "HelloDatabaseWorldServlet")
public class HelloDatabaseWorldServlet implements HttpRequestHandler {

	KeyValueDatabase database = null;

	public void setDatabase(KeyValueDatabase database) {
		this.database = database;
	}

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		database.actionWriter();
		database.actionReader();

		request.setAttribute("databaseMessage", database.getDbMessage());

		String address = "HelloDatabaseWorld.jsp";
		RequestDispatcher dispatcher = request.getRequestDispatcher(address);
		dispatcher.forward(request, response);;
	}
}
