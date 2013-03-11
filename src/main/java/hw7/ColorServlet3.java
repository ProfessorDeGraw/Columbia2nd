package hw7;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet("/color3")
public class ColorServlet3 extends HttpServlet {
	private static final long serialVersionUID = 7617638561183588977L;

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
			ColorBean mvcColor3 = (ColorBean) context.getAttribute("mvcColor3");
			if (mvcColor3 == null) {
				mvcColor3 = new ColorBean();
				context.setAttribute("mvcColor3", mvcColor3);

			}
			mvcColor3.setBgColor(bgColor);
			mvcColor3.setFgColor(fgColor);
			String address = "MVCMessage3.jsp";
			RequestDispatcher dispatcher = request
					.getRequestDispatcher(address);
			dispatcher.forward(request, response);
		}
	}

}