<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
<TITLE>A Message</TITLE>
</HEAD>

<jsp:useBean id="color" class="hw7.ColorBean" />
<jsp:setProperty name="color" property="*" />

<BODY BGCOLOR="<jsp:getProperty name="color" property="bgColor" />"
	text="<jsp:getProperty name="color" property="fgColor" />">
	
	<H2 ALIGN="CENTER">This is an important message using the color
		scheme you selected.</H2>
	<H2>Have a great day!</H2>
</BODY>
</HTML>