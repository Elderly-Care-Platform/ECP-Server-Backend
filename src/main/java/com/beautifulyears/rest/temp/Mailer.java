package com.beautifulyears.rest.temp;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.beautifulyears.domain.User;
import com.beautifulyears.mail.MailHandler;
import com.beautifulyears.util.Util;

//import com.beautifulyears.domain.UserProfile;

@Controller
@RequestMapping("/sendMail")
public class Mailer {

	private MongoTemplate mongoTemplate;
	private String text = "<html><head></head><body><table width=\"600\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" bgcolor=\"#ffffff\" style=\"font-family:Arial; font-size:14px; color:#000000; line-height:25px;\"><tr><td align=\"center\"><a href=\"https://www.joyofage.org/\" target=\"_blank\"><img src=\"http://dev-media.joyofage.org/orig/634fd1ad-79b9-496c-82a3-8e96942f550f.png\" width=\"600\" height=\"60\" border=\"0\" align=\"center\" /></a></td></tr><tr><td align=\"center\"><a href=\"https://www.joyofage.org/elder-care-forums/technology-classes-for-senior-citizens-in-koramangala/?id=56eae176e4b059065ae2ef40\" target=\"_blank\"><img src=\"http://dev-media.joyofage.org/orig/ede636a0-3ab7-4056-a061-59dd29f2fca6.png\" width=\"600\" height=\"130\" border=\"0\" align=\"center\" /></a></td></tr><tr height=\"20\"></tr><tr><td><table width=\"560\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\"><tr><td>"
			+ "Dear JoyOfAge friends,<br/><br/>"
			+ "We are happy to announce our classes and acivities for senior citizens at the JoyOfAge Experience Centre in Bangalore.<br/><br/>"
			+ "Our first workshop will be about using smart phones, iPads and laptops. Join us to learn with your peers, at your own pace!<br/><br/> "
			+ "Date: <b>March 24th at 4:30 PM at JoyOfAge Experience Centre, No.48 2nd Main, Koramangala 1st block (near Wipro park).</b><br/><br/>"
			+ "Please call us at <b><a href=\"tel:14567\"  style=\"text-decoration:none; color:#000;\">080 6490 0333</a> / <a href=\"tel:14567\" style=\"text-decoration:none; color:#000;\">14567</a></b> to register and to ensure your participation, as we will be able to accommodate only 10 people for"
			+ " the first session. <br/>"
			+ "Please bring those devices, which you intend to use on a daily basis. Happy learning! "
			+ "</td></tr></table></td></tr><tr height=\"20\"></tr><tr><td align=\"center\"><a href=\"\" target=\"_blank\"><img src=\"http://dev-media.joyofage.org/orig/66cf3537-9f22-49a7-8aee-bdf9666524a4.png\" width=\"600\" height=\"85\" border=\"0\" align=\"center\" /></a></td></tr><tr height=\"20\"></tr><tr><td><table width=\"600\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\" bgcolor=\"#003a19\"><tr><td align=\"left\" width=\"468\"><a href=\"https://www.joyofage.org/\" target=\"_blank\"><img src=\"http://dev-media.joyofage.org/orig/9c4c0ea0-f194-4313-8a08-5228f31a1d31.png\" width=\"468\" height=\"58\" border=\"0\" align=\"center\" /></a></td><td align=\"left\" width=\"25\"><a href=\"https://www.facebook.com/JoyOfAgeIndia/\" target=\"_blank\"><img src=\"http://dev-media.joyofage.org/orig/38db494f-9b6e-436d-8713-faa0334473e4.png\" width=\"21\" height=\"58\" border=\"0\" align=\"center\" /></a></td><td align=\"left\" width=\"25\"><a href=\"https://www.linkedin.com/company/beautiful-years-technologies-&-services-pvt-ltd?trk=biz-companies-cym\" target=\"_blank\"><img src=\"http://dev-media.joyofage.org/orig/8ad3a0d4-79cd-4fb5-8ecd-0a3828906a64.png\" width=\"21\" height=\"58\" border=\"0\" align=\"center\" /></a></td><td align=\"left\" width=\"25\"><a href=\"https://plus.google.com/u/2/b/116198578173335960833/+Beautifulyears/posts?gmbpt=true&pageId=116198578173335960833&hl=en\" target=\"_blank\"><img src=\"http://dev-media.joyofage.org/orig/594634b7-28bb-491a-bde4-e9eebc4c272c.png\" width=\"21\" height=\"58\" border=\"0\" align=\"center\" /></a></td><td align=\"left\" width=\"57\"><a href=\"https://www.youtube.com/channel/UCqdlVJXp7pqXZwj-u2atFOQ?spfreload=10\" target=\"_blank\"><img src=\"http://dev-media.joyofage.org/orig/3347287a-6b2d-4a32-9f4d-e9ff2760f5ee.png\" width=\"57\" height=\"58\" border=\"0\" align=\"center\" /></a></td></tr></table></td></tr></table></body></html>";

	private String subject = "JoyOfAge.org - Introducing Workshops for Senior Citizens";

	@Autowired
	public Mailer(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@RequestMapping(method = RequestMethod.GET, value = "", produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody Object sendBulkMail(
			@RequestBody List<String> emails,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		User currentUser = Util.getSessionUser(req);
		StringBuffer userEmails = new StringBuffer();

		if (null != currentUser && !Util.isEmpty(currentUser.getEmail())
				&& currentUser.getEmail().equals("nitin.j@by.com")) {
			if (null != emails && emails.size() > 0) {
				for (Iterator iterator = emails.iterator(); iterator.hasNext();) {
					String email = (String) iterator.next();
					if (null != email) {
						userEmails.append(email).append(",");
						Thread.sleep(1000);
						MailHandler.sendMultipleMail(Arrays.asList(email),
								subject, text);
					}

				}
			} else {
				List<User> users = mongoTemplate.findAll(User.class);
				for (Iterator iterator = users.iterator(); iterator.hasNext();) {
					User user = (User) iterator.next();
					if (null != user.getEmail()) {
						userEmails.append(user.getEmail()).append(",");
						Thread.sleep(1000);
						MailHandler.sendMultipleMail(
								Arrays.asList(user.getEmail()), subject, text);
					}

				}
			}

		}
		return "{\"name\":\"" + userEmails.toString() + "\"}";
	}

}