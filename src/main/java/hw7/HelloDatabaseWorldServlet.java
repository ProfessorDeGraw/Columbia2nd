package hw7;

import java.io.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

//@WebServlet("/HelloDatabaseWorld")
//@WebServlet(description = "Http Servlet using pure java / annotations", urlPatterns = { "/HelloDatabaseWorld" }, name = "HelloDatabaseWorldServlet")
public class HelloDatabaseWorldServlet implements HttpRequestHandler {
//public class HelloDatabaseWorldServlet extends HttpServlet implements HttpRequestHandler {
	private static final long serialVersionUID = 5369735834465318258L;
	
	KeyValueDatabase database=null;
	
	

	public void setDatabase(KeyValueDatabase database) {
		this.database = database;
	}

//	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String bgColor = request.getParameter("bgColor");
		String fgColor = request.getParameter("fgColor");

		if (bgColor != null && fgColor != null
				&& bgColor.trim().equals(fgColor.trim())) {
			bgColor = "";
			fgColor = "";
		}

		ColorBeanImmutable elColor = new ColorBeanImmutable(bgColor, fgColor);
		request.setAttribute("elColor", elColor);
		
		String databaseMessage;
		
		try {
			if (database == null) {
			//WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServletContext());
			 //database = (KeyValueDatabase) ctx.getBean("KeyValueDatabase");
			}
			
			
			database.actionWriter();
			database.actionReader();

			databaseMessage =  database.getDbMessage();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			
			databaseMessage = new String("The Database is being worked on now, try agian later.");
		}
		request.setAttribute("databaseMessage", databaseMessage);
		
		String address = "HelloDatabaseWorld.jsp";
		RequestDispatcher dispatcher = request.getRequestDispatcher(address);
		dispatcher.forward(request, response);
	}

	@Override
	public void handleRequest(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(arg0, arg1);
	}
}
