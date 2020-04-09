/**
 * 
 */
package com.beautifulyears.mail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import javax.mail.Address;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Iterator;
import javax.mail.Message;
import com.beautifulyears.domain.User;
import com.beautifulyears.rest.UserController;

/**
 * @author Nitin
 *
 */
public class MailHandler {
	private static final String USER = "admin@joyofage.org";
	private static final String PASS = "joyofage@123";
	private static final String SMTP = "smtp.gmail.com";
	private static final String SMTP_PORT = "587";
	private static final String FROM = "admin@joyofage.org";
	private static final String AUTH = "true";
	private static final String TLS_ENABLE = "true";

	private static class MailDispatcher implements Runnable {
		private String to;
		private String subject;
		private String body;
		private List<String> recepients = new ArrayList<String>();
		private final static Logger logger = Logger.getLogger(MailDispatcher.class);
        
		public MailDispatcher(List<String> recepients, String subject, String body) {
			this.subject = subject;
			this.body = body;
			this.recepients = recepients;
		}

		@Override
		public void run() {
		Properties props = new Properties();
		props.put("mail.smtp.host", SMTP);
		props.put("mail.smtp.port", SMTP_PORT);
		props.put("mail.smtp.auth", AUTH);
		props.put("mail.smtp.starttls.enable", TLS_ENABLE);
		Session session = Session.getInstance(props,
			new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(USER, PASS);
			}
			});
		
		try {
			logger.debug("mail request for "+ recepients +" with subject "+ subject +" arrived");
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(FROM));
			message.setReplyTo(new Address [] {new InternetAddress(FROM)});
			Iterator iterator = recepients.iterator();
			while(iterator.hasNext()) {
				message.addRecipient(Message.RecipientType.TO,
					new InternetAddress(iterator.next().toString()));
			}
			
			message.setSubject(subject);
			message.setContent(body, "text/html");
			Transport.send(message);
			logger.debug("email sent successfully to "+to);
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
	}

	public static void sendMail(String to, String subject, String body) {
		new Thread(new MailDispatcher(Arrays.asList(to), subject, body)).start();
	}

	public static void sendMultipleMail(List<String> to, String subject, String body) {
			new Thread(new MailDispatcher(to, subject, body)).start();
	}

	public static void sendMailToUserId(String userId, String subject,
			String body) {
		User user = UserController.getUser(userId);
		if (null != user && user.getEmail() != null && !user.getEmail().isEmpty()) {
			sendMail(user.getEmail(), subject, body);
		}
	}
}
