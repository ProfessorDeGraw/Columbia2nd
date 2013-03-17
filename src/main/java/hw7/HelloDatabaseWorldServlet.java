package hw7;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet("/HelloDatabaseWorld")
public class HelloDatabaseWorldServlet extends HttpServlet {
	private static final long serialVersionUID = 5369735834465318258L;

	@Override
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

		String databaseMessage = new String("The Database is being worked on now, try agian later.");
		request.setAttribute("databaseMessage", databaseMessage);
		
		String address = "HelloDatabaseWorld.jsp";
		RequestDispatcher dispatcher = request.getRequestDispatcher(address);
		dispatcher.forward(request, response);
	}
}
