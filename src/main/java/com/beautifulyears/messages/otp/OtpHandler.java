package com.beautifulyears.messages.otp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.beautifulyears.domain.Setting;
import com.beautifulyears.util.LoggerUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class OtpHandler {
	private static final Logger logger = Logger.getLogger(OtpHandler.class);

	private String baseUrl = "https://api.msg91.com/api/v5/otp";
	private String authkey = "288637ABlqJNxoGya5d4ac03d";
	private String messageOtp = "?";
	private String resendOtp = "/retry?";
	private String verfiyOtp = "/verify?";
	//private String message = "Your verification code for Joy of Age website is ##OTP##. This one-time-password is valid for 10 minutes.";
	private String senderId = "joyage";
	private String templateId = "5eec73add6fc056590482982";
	private MongoTemplate mongoTemplate;
	
	public OtpHandler(MongoTemplate mongoTemplate){
		this.mongoTemplate = mongoTemplate;
	}

	public String sendOtp(String mobileNo) {
		LoggerUtil.logEntry();
		String response = null;
		try {
			String postUrl = this.baseUrl + this.messageOtp;
			// encoding message
			//String encoded_message = URLEncoder.encode(this.message);

			// Prepare parameter string
			StringBuilder sbPostData = new StringBuilder(postUrl);
			sbPostData.append("authkey=" + this.authkey);
			if(mobileNo.contains("@")){
				sbPostData.append("&email=" + mobileNo);
				Query query = new Query();
				query.addCriteria(Criteria.where("key").is("smsMobileNum"));
				Setting setting = this.mongoTemplate.findOne(query, Setting.class);
				mobileNo= setting.getValue();
			}
			sbPostData.append("&mobile=+91" + mobileNo);
			//sbPostData.append("&message=" + encoded_message);
			sbPostData.append("&sender=" + this.senderId);
			sbPostData.append("&template_id=" + this.templateId);

			// final string
			postUrl = sbPostData.toString();

			logger.debug(postUrl);

			URL u = new URL(postUrl);
			URLConnection c = u.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
			String inputLine;
			StringBuffer b = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
				b.append(inputLine + "\n");
			in.close();
			response = b.toString();
			logger.debug(response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("ERROR in getting Otp message. " + e);
		}
		return response;
	}

	public JSONObject verifyOtp(String mobileNo, String otp) {
		LoggerUtil.logEntry();
		JSONObject response = null;
		try {

			String postUrl = this.baseUrl + this.verfiyOtp;

			// Prepare parameter string
			StringBuilder sbPostData = new StringBuilder(postUrl);
			sbPostData.append("authkey=" + this.authkey);
			if(mobileNo.contains("@")){
				Query query = new Query();
				query.addCriteria(Criteria.where("key").is("smsMobileNum"));
				Setting setting = this.mongoTemplate.findOne(query, Setting.class);
				mobileNo= setting.getValue();
			}
			sbPostData.append("&mobile=+91" + mobileNo);
			sbPostData.append("&otp=" + otp);

			// final string
			postUrl = sbPostData.toString();

			logger.debug(postUrl);

			URL u = new URL(postUrl);
			URLConnection c = u.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
			String inputLine;
			StringBuffer b = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
				b.append(inputLine + "\n");
			in.close();
			response = new JSONObject(b.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("ERROR in verifying Otp message. " + e);
		}
		return response;
	}

	public String resendOtp(String mobileNo) {
		LoggerUtil.logEntry();
		String response = null;
		try {

			String postUrl = this.baseUrl + this.resendOtp;

			// Prepare parameter string
			StringBuilder sbPostData = new StringBuilder(postUrl);
			sbPostData.append("authkey=" + this.authkey);
			if(mobileNo.contains("@")){
				Query query = new Query();
				query.addCriteria(Criteria.where("key").is("smsMobileNum"));
				Setting setting = this.mongoTemplate.findOne(query, Setting.class);
				mobileNo= setting.getValue();
			}
			sbPostData.append("&mobile=+91" + mobileNo);
			sbPostData.append("&retrytype=" + "text");
			// final string
			postUrl = sbPostData.toString();

			logger.debug(postUrl);

			URL u = new URL(postUrl);
			URLConnection c = u.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
			String inputLine;
			StringBuffer b = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
				b.append(inputLine + "\n");
			in.close();
			response = b.toString();
			logger.debug(response);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("ERROR in verifying Otp message. " + e);
		}
		return response;
	}

}
