package hw7;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet("/elColor3")
public class ELColorServlet3 extends HttpServlet {
	private static final long serialVersionUID = -1789787339669586093L;

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

		ServletContext context = getServletContext();
		synchronized (this) {
			ColorBean elColor3 = (ColorBean) context.getAttribute("elColor3");
			if (elColor3 == null) {
				elColor3 = new ColorBean();
				context.setAttribute("elColor3", elColor3);

			}
			elColor3.setBgColor(bgColor);
			elColor3.setFgColor(fgColor);
			String address = "ELMessage3.jsp";
			RequestDispatcher dispatcher = request
					.getRequestDispatcher(address);
			dispatcher.forward(request, response);
		}
	}

}