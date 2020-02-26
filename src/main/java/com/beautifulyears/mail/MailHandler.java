/**
 * 
 */
package com.beautifulyears.mail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

// import com.amazonaws.auth.BasicAWSCredentials;
// import com.amazonaws.regions.Region;
// import com.amazonaws.regions.Regions;
// import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
// import com.amazonaws.services.simpleemail.model.Body;
// import com.amazonaws.services.simpleemail.model.Content;
// import com.amazonaws.services.simpleemail.model.Destination;
// import com.amazonaws.services.simpleemail.model.Message;
// import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.beautifulyears.constants.BYConstants;

import javax.mail.Address;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Iterator;
import javax.mail.Message;

import com.beautifulyears.domain.User;
import com.beautifulyears.exceptions.BYErrorCodes;
import com.beautifulyears.exceptions.BYException;
import com.beautifulyears.rest.UserController;
import com.beautifulyears.util.Util;

//import javax.mail.Message;

/**
 * @author Nitin
 *
 */
public class MailHandler {
	private final static Logger logger = Logger.getLogger(MailHandler.class);
	// private static final String user = "support@beautifulyears.com";
	// private static final String pass = "BY2015@)!%";
	// private static final String SMTP = "smtp.gmail.com";
	// private static final String SMTP_PORT = "587";
	// private static final String FROM = "support@beautifulyears.com";
	private static final String FROM = "test@ritzyware.com";
	// private static final AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient(
	// 		new BasicAWSCredentials("AKIAJGOEKIENWMH5NXBQ",
	// 				"LcHQh962s+0jyfv/agtrc0yZo0pX2lIINJ5Vgg4y"));
	// private static final Region REGION = Region.getRegion(Regions.EU_WEST_1);

	private static class MailDispatcher implements Runnable {
		private String to;
		private String subject;
		private String body;
		private List<String> recepients = new ArrayList<String>();
		private final static Logger logger = Logger.getLogger(MailDispatcher.class);
        final String user = "admin@joyofage.org";
        final String pass = "joyofage@123";

		public MailDispatcher(List<String> recepients, String subject,
				String body) {
			//this.to = to;
			this.subject = subject;
			this.body = body;
			this.recepients = recepients;
		}

		@Override
		public void run() {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		Session session = Session.getInstance(props,
			new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, pass);
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

	// 	@Override
	// 	public void run() {
	// 		logger.debug("mail request for " + this.recepients + " with subject " + subject
	// 				+ " arrived");

	// 		Destination destination = new Destination()
	// 				.withToAddresses(this.recepients);

	// 		Content subject = new Content().withData(this.subject);
	// 		Content textBody = new Content().withData(this.body);
	// 		Body body = new Body().withHtml(textBody);

	// 		Message message = new Message().withSubject(subject).withBody(body);
	// 		SendEmailRequest request = new SendEmailRequest().withSource(FROM)
	// 				.withDestination(destination).withMessage(message);
	// 		try {
	// 			System.out
	// 					.println("Attempting to send an email through Amazon SES by using the AWS SDK for Java...");
	// 			client.setRegion(REGION);
	// 			client.sendEmail(request);
	// 			System.out.println("Email sent to " + this.recepients);
	// 		} catch (Exception ex) {
	// 			System.out.println("The email was not sent to ."+this.recepients);
	// 			System.out.println("Error message: " + ex.getMessage());
	// 		}
	// 	}

	}

	public static void sendMail(String to, String subject, String body) {
		//if (!Util.isEmpty(System.getProperty("mailSupported"))) {
			new Thread(new MailDispatcher(Arrays.asList(to), subject, body))
					.start();
		// } else {
		// 	logger.debug("not sending mail as it is disabled in context config");
		// 	throw new BYException(BYErrorCodes.ERROR_IN_SENDING_MAIL);
		// }

	}

	public static void sendMultipleMail(List<String> to, String subject,
			String body) {
		// if (!Util.isEmpty(System.getProperty("mailSupported"))) {
			// for (String email : to) {
			// if (!Util.isEmpty(email)) {
			new Thread(new MailDispatcher(to, subject, body)).start();
			// }
			// }

		// } else {
		// 	logger.debug("not sending mail as it is disabled in context config");
		// 	throw new BYException(BYErrorCodes.ERROR_IN_SENDING_MAIL);
		// }

	}

	public static void sendMailToUserId(String userId, String subject,
			String body) {
		User user = UserController.getUser(userId);
		if (null != user
				&& user.getUserIdType() == BYConstants.USER_ID_TYPE_EMAIL) {
			sendMail(user.getEmail(), subject, body);
		}

	}
}
