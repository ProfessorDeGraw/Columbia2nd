package hw7;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet("/color2")
public class ColorServlet2 extends HttpServlet {
	private static final long serialVersionUID = 3983229934097524983L;

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
			ColorBean mvcColor2 = (ColorBean) session.getAttribute("mvcColor2");
			if (mvcColor2 == null) {
				mvcColor2 = new ColorBean();
				session.setAttribute("mvcColor2", mvcColor2);
			}
			mvcColor2.setBgColor(bgColor);
			mvcColor2.setFgColor(fgColor);
			String address = "MVCMessage2.jsp";
			RequestDispatcher dispatcher = request
					.getRequestDispatcher(address);
			dispatcher.forward(request, response);
		}
	}
}