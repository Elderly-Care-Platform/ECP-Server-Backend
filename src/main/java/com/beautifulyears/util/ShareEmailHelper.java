/**
 * 
 */
package com.beautifulyears.util;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.beautifulyears.domain.Discuss;
import com.beautifulyears.domain.User;

/**
 * @author Nitin
 *
 */
public class ShareEmailHelper {

	public static String getEmailContent(Discuss discuss, User currentUser,
			Map<String, String> details) {
		String userName = "Anonymous user";
		String senderLink = "";
		if (null != currentUser) {
			if (!Util.isEmpty(currentUser.getUserName())) {
				userName = currentUser.getUserName();
			}
			senderLink = System.getProperty("path") + "/users/" + userName
					+ "?profileId=" + currentUser.getId();
		}else if(!Util.isEmpty(details.get("senderName"))){
			userName = details.get("senderName");
		}

		String profileImage = System.getProperty("path") + "/assets/img/by.png";
		if (null != discuss.getUserProfile()
				&& null != discuss.getUserProfile().getBasicProfileInfo()
				&& null != discuss.getUserProfile().getBasicProfileInfo()
						.getProfileImage()
				&& null != discuss.getUserProfile().getBasicProfileInfo()
						.getProfileImage().get("thumbnailImage")) {
			profileImage = System.getProperty("path")
					+ discuss.getUserProfile().getBasicProfileInfo()
							.getProfileImage().get("thumbnailImage");
		}

		String authorUserName = "Anonymous";
		if (!Util.isEmpty(discuss.getUsername())) {
			authorUserName = discuss.getUsername();
		}

		String discussLink = System.getProperty("path") + "/elder-care-forums/"
				+ details.get("title") + "?id=" + discuss.getId();

		String authorLink = System.getProperty("path") + "/users/"
				+ discuss.getUsername() + "?profileId=" + discuss.getUserId();

		Date discussDate = discuss.getCreatedAt();
		long diff = (new Date()).getTime() - discussDate.getTime();
		diff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

		String text = discuss.getShortSynopsis();
		if (Util.isEmpty(text)) {
			if (null != discuss.getLinkInfo()
					&& !Util.isEmpty(discuss.getLinkInfo().getDescription())) {
				text = discuss.getLinkInfo().getDescription();
			} else {
				Document doc = Jsoup.parse(discuss.getText());
				text = doc.text();
				text = Util.truncateText(text);
			}
		}

		String mainImagePath = "";
		if (null != discuss.getArticlePhotoFilename()
				&& !Util.isEmpty(discuss.getArticlePhotoFilename().get(
						"titleImage"))) {
			mainImagePath = System.getProperty("path")
					+ discuss.getArticlePhotoFilename().get("titleImage");
		} else if (null != discuss.getLinkInfo()
				&& !Util.isEmpty(discuss.getLinkInfo().getMainImage())) {
			mainImagePath = discuss.getLinkInfo().getMainImage();
		}

		StringBuilder email = new StringBuilder("");
		email.append("<center class='wrapper' style='display: table;table-layout: fixed;width: 100%;min-width: 480px;-webkit-text-size-adjust: 100%;-ms-text-size-adjust: 100%;background: #fff;'>");

		// for beautifulyears logo
		email.append("<table class='centered' style='text-align: center;border-collapse: collapse;border-spacing: 0;Margin-left: auto;Margin-right: auto;background-color: #ffffff;'>");
		email.append("<tbody>");
		email.append("<tr><td class='column first' style='text-align: center;padding: 0;vertical-align: top;line-height: 17px;font-weight: 400;font-size: 11px;Margin-right: auto;Margin-left: auto;padding-top: 50px;width: 480px;color: #fff;font-family: helvetica'>");
		email.append("<a style='color: #222; text-decoration:none; outline:none;' href='http://joyofage.org' target='_blank' >");
		email.append("<img style='width:180px;Margin-top:-20px;'src='http://joyofage.org/uploaded_files/c5a6c771-a28e-417e-91ae-557112aaa36a.png' />");
		email.append("</a>");
		email.append("</td>");
		email.append("</tr>");
		email.append("</tbody>");
		email.append("</table>");

		// for horizontal line
		email.append("<table class='border' style='border-collapse: collapse;border-spacing: 0;font-size: 1px;line-height: 1px;background-color: #d4dbdd;Margin-left: auto;Margin-right: auto' width='480'>");
		email.append("<tbody>");
		email.append("<tr class='border' style='font-size: 1px;line-height: 1px;background-color: #e3e3e3;height: 1px'></tr>");
		email.append("</tbody>");
		email.append("</table>");

		// user shared a post with you text
		email.append("<table class='centered' style='border-collapse: collapse;border-spacing: 0;Margin-left: auto;Margin-right: auto;background-color: #ffffff;'>");
		email.append("<tbody>");
		email.append("<tr>");
		email.append("<td class='column first' style='padding: 0;vertical-align: top;line-height: 17px;font-weight: 400;font-size: 11px;Margin-right: auto;Margin-left: auto;padding-top: 20px;width: 480px; color: #fff;font-family: helvetica'>");
		email.append(" <h3 style ='font-size:18px; font-weight: 300; text-align:left; line-height:20px; color:#818181;'>");
		email.append("<b style='color:#222'>");
		email.append("<u><a style='color: #222; text-decoration:none; outline:none;' href='");
		email.append(senderLink);
		email.append("'>");
		email.append(userName);
		email.append("</a></u>");
		email.append("</b> shared a JoyOfAge post with you.");
		email.append("</h3>");
		email.append("</td>");
		email.append("</tr>");
		email.append("</tbody>");
		email.append("</table>");

		if (!Util.isEmpty(details.get("msg"))) {

			// user's message
			// for pointer above message
			email.append("<table class='centered' style='border-collapse: collapse;border-spacing: 0;Margin-left: auto;Margin-right: auto;background-color: #ffffff;'>");
			email.append(" <tbody>");
			email.append(" <tr>");
			email.append(" <td class='column first' style='padding: 0;vertical-align: top;line-height: 0px;font-weight: 400;font-size: 11px;Margin-right: auto;Margin-left: auto;width: 480px; color: #fff;font-family: helvetica'>");
			email.append(" <center>");
			email.append(" <table class='social' style='border-collapse: collapse;border-spacing: 0;Margin-left: 8%;margin-bottom: -1px;Margin-right: auto;'>");
			email.append(" <tbody>");
			email.append(" <tr>");
			email.append("<td style='padding: 0;vertical-align: top;'>");
			email.append("<img style='width:12px;Margin-bottom:-6px;' src='");
			email.append(System.getProperty("path") + "/assets/img/icons/mail-bubble-pointer.png");
			email.append("'/>");
			email.append("</td>");
			email.append(" </tr>");
			email.append(" </tbody>");
			email.append(" </table>");
			email.append(" </center>");
			email.append(" </td> ");
			email.append("</tr>");
			email.append(" </tbody>");
			email.append(" </table>");

			// for msg bubble
			email.append(" <table class='social' style='border-collapse: collapse;border-spacing: 0;margin-top: -1px;Margin-left: auto;Margin-right: auto;'>");
			email.append(" <tbody>");
			email.append(" <tr> ");
			email.append("<td style='padding: 0;vertical-align: top;padding-bottom:20px;'>");
			email.append("<div style='background: #f2ebeb none repeat scroll 0% 0%; width:480px; height:auto; min-height:30px; height:auto !important; height:30px; overflow: hidden; border-radius: 5px; border: 1px solid #d2d2d2;'>");
			email.append("<p style='margin-top: 0px; color: #818181; font-weight: 300; font-size: 16px; font-family: helvetica; line-height: 22px; margin-bottom: 0px; text-align: justify; padding:10px;'>");
			email.append(details.get("msg") + "</p>");
			email.append("</div>");
			email.append("</td>");
			email.append("</tr>");
			email.append("</tbody>");
			email.append("</table>");
		}

		// user profile image and time ago
		email.append("<table class='social' style='border-collapse: collapse;border-spacing: 0;Margin-left: auto;Margin-right: auto;'> ");
		email.append("<tbody> ");
		email.append("<tr> ");
		email.append("<td style='padding: 0;vertical-align: top; padding-left: 20px;padding-right: 20px'>");
		email.append("<img style='width:50px; height:50px; display: inline; border:2px solid #F1F1F1' src='");
		email.append(profileImage);
		email.append("'/>");
		email.append("<div style='display: inline; float: right; margin: 0px 15px'>");
		email.append("<p style='color: rgb(65, 65, 65); font-size: 15px; font-family: helvetica; Margin-top: 6px; line-height: 15px; margin-bottom: 6px; font-weight: 500;'>");
		email.append("<a href='");
		email.append(authorLink);
		email.append("' style='color: rgb(128, 124, 124); text-decoration:none; outline:none;'>By ");
		email.append(authorUserName);
		email.append("</a>");
		email.append("</p>");
		email.append("<p style='margin-top: 0px; color: rgb(128, 124, 124); font-weight: 300; font-size: 15px; font-family: helvetica; line-height: 21px; text-align: justify;'>");
		email.append(diff);
		email.append(" days ago</p>");
		email.append("</div>");
		email.append("</td>");
		email.append(" </tr>");
		email.append(" </tbody>");
		email.append(" </table>");

		if ("P".equals(discuss.getDiscussType())) {
			// title
			email.append(" <table class='social' style='border-collapse: collapse;border-spacing: 0;Margin-left: auto;Margin-right: auto;'>");
			email.append(" <tbody>");
			email.append(" <tr>");
			email.append(" <td style='padding: 0;vertical-align:top;'>");
			email.append("<h3 style='Margin-top: 0;color: #333; font-weight: 300;font-size: 20px; Margin-bottom:10px; font-family: helvetica; line-height: 22px; text-align:center'>");
			email.append(details.get("title"));
			email.append("</h3>");
			email.append(" </td>");
			email.append(" </tr>");
			email.append(" </tbody>");
			email.append(" </table>");
		}

		// discussImage
		if (!Util.isEmpty(mainImagePath)) {
			email.append(" <table class='social' style='border-collapse: collapse;border-spacing: 0;Margin-left: auto;Margin-right: auto;'>");
			email.append(" <tbody>");
			email.append(" <tr>");
			email.append(" <td style='padding: 0;vertical-align: top;padding-left: 20px;padding-right: 20px'>");

			email.append("<div class='image' style='font-size: 12px;Margin-bottom: 21px;mso-line-height-rule: at-least;color: #757575;font-family: helvetica' align='center'>");
			email.append("<img style='border: 0;display: block;max-width: 100%; height:auto; border:5px solid #F1F1F1' src='");
			email.append(mainImagePath);
			email.append("' alt='' width='470'/>");
			email.append("</div>");

			email.append("</td>");
			email.append("</tr>");
			email.append("</tbody>");
			email.append("</table>");

			email.append("<table class='centered' style='border-collapse: collapse;border-spacing: 0;Margin-left: auto;Margin-right: auto;background-color: #ffffff;'>");
			email.append(" <tbody>");
			email.append(" <tr>");
			email.append(" <td class='column first' style='padding: 0;vertical-align: top;line-height: 17px;font-weight: 400;font-size: 11px;Margin-right: auto;Margin-left: auto;width: 480px;color: #fff;font-family: helvetica'>");
		}

		// text of the article
		if (!Util.isEmpty(text)) {
			email.append("<p style='Margin-top: 0;color: #777;font-size: 16px;font-family: helvetica; line-height: 22px;Margin-bottom: 21px; width:100%; height:auto; min-height:30px; height:auto !important; height:30px; overflow:hidden; text-align:center'>");
			email.append(text);
			email.append("</p>");
		}

		// read article button
		email.append("<a href='");
		email.append(discussLink);
		email.append("' target='_blank' style='text-align:center; display:block; text-decoration:none; outline:none;'>");
		email.append("<h3 style='background: #004f24 none repeat scroll 20px 20px; width: 155px; height: 35px; line-height: 35px; border-radius: 3px; color: rgb(255, 255, 255); font-size: 15px; margin: 20px 35%;'>Read More");
		email.append("</h3>");
		email.append("</a>");

		email.append("</td>");
		email.append("</tr>");
		email.append("</tbody>");
		email.append("</table>");
		email.append("</center>");

		return email.toString();
	}
}
