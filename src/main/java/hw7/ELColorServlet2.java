package hw7;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet("/elColor2")
public class ELColorServlet2 extends HttpServlet {
	private static final long serialVersionUID = -8047161873689380351L;

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

		HttpSession session = request.getSession();
		synchronized (session) {
			ColorBean elColor2 = (ColorBean) session.getAttribute("elColor2");
			if (elColor2 == null) {
				elColor2 = new ColorBean();
				session.setAttribute("elColor2", elColor2);
			}
			elColor2.setBgColor(bgColor);
			elColor2.setFgColor(fgColor);
			String address = "ELMessage2.jsp";
			RequestDispatcher dispatcher = request
					.getRequestDispatcher(address);
			dispatcher.forward(request, response);
		}
	}
}