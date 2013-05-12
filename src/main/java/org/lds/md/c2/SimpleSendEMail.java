package org.lds.md.c2;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleSendEMail {
	private static final Logger log = LoggerFactory.getLogger(SimpleSendEMail.class);
	
	static void sendEmail(String toEmail, String subject, String message) {
		Email email = new SimpleEmail();
		email.setSmtpPort(587);
		email.setAuthenticator(new DefaultAuthenticator("degraw@gmail.com",
				"password"));
		email.setDebug(true);
		email.setHostName("smtp.gmail.com");
		try {
			email.setFrom("nathan.degraw@gmail.com");
			email.setSubject(subject);
			email.setMsg(message);
			email.addTo(toEmail);
			//email.setTLS(true);
			email.setSSLOnConnect(true);
			email.send();
		} catch (EmailException e) {
			log.error( "Sending email failed!", e );
		}
	}
}