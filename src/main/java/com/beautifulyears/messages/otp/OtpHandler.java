package com.beautifulyears.messages.otp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.beautifulyears.util.LoggerUtil;

public class OtpHandler {
	private static final Logger logger = Logger.getLogger(OtpHandler.class);

	private String baseUrl = "https://control.msg91.com/api";
	private String authkey = "288637ABlqJNxoGya5d4ac03d";
	private String messageOtp = "/sendotp.php?";
	private String resendOtp = "/retryotp.php?";
	private String verfiyOtp = "/verifyRequestOTP.php?";
	private String message = "Your verification code for Elderly Care Platform is ##OTP##.";
	private String senderId = "OTPSMS";

	public String sendOtp(String mobileNo) {
		LoggerUtil.logEntry();
		String response = null;
		try {

			String postUrl = this.baseUrl + this.messageOtp;
			// encoding message
			String encoded_message = URLEncoder.encode(this.message);

			// Prepare parameter string
			StringBuilder sbPostData = new StringBuilder(postUrl);
			sbPostData.append("authkey=" + this.authkey);
			sbPostData.append("&mobile=+91" + mobileNo);
			sbPostData.append("&message=" + encoded_message);
			sbPostData.append("&sender=" + this.senderId);

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

	public String verifyOtp(String mobileNo, String otp) {
		LoggerUtil.logEntry();
		String response = null;
		try {

			String postUrl = this.baseUrl + this.verfiyOtp;

			// Prepare parameter string
			StringBuilder sbPostData = new StringBuilder(postUrl);
			sbPostData.append("authkey=" + this.authkey);
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
			response = b.toString();
			logger.debug(response);
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
