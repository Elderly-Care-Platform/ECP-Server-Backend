package com.beautifulyears.rest;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.beautifulyears.constants.ActivityLogConstants;
import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.constants.DiscussConstants;
import com.beautifulyears.constants.UserRolePermissions;
import com.beautifulyears.domain.BasicProfileInfo;
import com.beautifulyears.domain.Session;
import com.beautifulyears.domain.User;
import com.beautifulyears.domain.UserProfile;
import com.beautifulyears.exceptions.BYErrorCodes;
import com.beautifulyears.exceptions.BYException;
import com.beautifulyears.mail.MailHandler;
import com.beautifulyears.messages.otp.OtpHandler;
import com.beautifulyears.repository.UserProfileRepository;
import com.beautifulyears.repository.UserRepository;
import com.beautifulyears.rest.response.BYGenericResponseHandler;
import com.beautifulyears.util.LoggerUtil;
import com.beautifulyears.util.ResourceUtil;
import com.beautifulyears.util.UserNameHandler;
import com.beautifulyears.util.Util;
import com.beautifulyears.util.activityLogHandler.ActivityLogHandler;
import com.beautifulyears.util.activityLogHandler.UserActivityLogHandler;

@Controller
@RequestMapping("/users")
public class UserController {

	private static UserRepository userRepository;
	@Autowired
	private UserProfileRepository userProfileRepository;
	private MongoTemplate mongoTemplate;
	private static final Logger logger = Logger.getLogger(UserController.class);
	ActivityLogHandler<User> logHandler;

	@Autowired
	public UserController(UserRepository userRepository, MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
		setUserRepository(userRepository);
		logHandler = new UserActivityLogHandler(mongoTemplate);
	}

	private static void setUserRepository(UserRepository userRepository) {
		UserController.userRepository = userRepository;
	}

	@RequestMapping(value = "/getUserInfoByIdForProducts", method = RequestMethod.GET)
	public @ResponseBody Object getUserInfoByIdForProducts(@RequestParam(value = "id", required = true) String id,
			HttpServletRequest req, HttpServletResponse res) {
		Query q = new Query();
		q.addCriteria(Criteria.where("sessionId").is(id));
		q.addCriteria(Criteria.where("status").is(DiscussConstants.SESSION_STATUS_ACTIVE));
		Session session = mongoTemplate.findOne(q, Session.class);
		User user = null;
		if (null != session) {
			user = userRepository.findOne(session.getUserId());
			session.setUser(user);
		}

		return BYGenericResponseHandler.getResponse(session);
	}

	@RequestMapping(value = "/validateSession", method = RequestMethod.GET)
	public @ResponseBody Object validateSession(HttpServletRequest req, HttpServletResponse res) {
		Session currentSession = null;
		if (null == Util.getSessionUser(req) || null == req.getSession().getAttribute("session")) {
			throw new BYException(BYErrorCodes.INVALID_SESSION);
		} else {
			currentSession = (Session) req.getSession().getAttribute("session");
		}
		return BYGenericResponseHandler.getResponse(currentSession);
	}

	// Pulkit: this function will not be called directly through api request any
	// more
	public @ResponseBody Object login(User loginRequest, HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		LoggerUtil.logEntry();
		Session session = killSession(req, res);
		try {
			Query q = new Query();
			User user = null;
			if (loginRequest.getUserIdType() == BYConstants.USER_ID_TYPE_EMAIL) {
				if (!Util.isEmpty(loginRequest.getEmail())) {
					Criteria criteria = Criteria.where("email").is(loginRequest.getEmail());
					q.addCriteria(criteria);
					user = mongoTemplate.findOne(q, User.class);
				} else {
					throw new BYException(BYErrorCodes.MISSING_PARAMETER);
				}
			} else if (loginRequest.getUserIdType() == BYConstants.USER_ID_TYPE_PHONE) {
				if (!Util.isEmpty(loginRequest.getPhoneNumber())) {
					q.addCriteria(Criteria.where("phoneNumber").is(loginRequest.getPhoneNumber()));
					user = mongoTemplate.findOne(q, User.class);
				} else {
					throw new BYException(BYErrorCodes.MISSING_PARAMETER);
				}
			}

			if (null == user) {
				logger.debug("User does not exist");
				return submitUser(loginRequest, true, req, res);
			} else if (user.getUserRegType() == BYConstants.USER_REG_TYPE_SOCIAL) {
				throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRE_SOCIAL_SIGNIN);
			} else {
				logger.debug("User logged in success for user email = " + loginRequest.getEmail() != null
						? loginRequest.getEmail()
						: loginRequest.getPhoneNumber());
				session = (Session) req.getSession().getAttribute("session");
				if (null == session) {
					session = createSession(req, res, user, true);
				}
			}
		} catch (Exception e) {
			Util.handleException(e);
		}
		Util.logStats(mongoTemplate, req, "New Login session", session.getUserId(), session.getUserEmail(),
				session.getSessionId(), null, null, null, "New Login session", "USER");
		return BYGenericResponseHandler.getResponse(session);
	}

	@RequestMapping(value = "/validateEmailPresence", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Object vaidateEmailPresence(@RequestParam(value = "email", required = true) String email,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		User user = userRepository.findByEmail(email);
		return BYGenericResponseHandler.getResponse(null == user ? false : user);
	}

	@RequestMapping(value = "/vaidateMobileNumberPresence", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Object vaidateMobileNumberPresence(
			@RequestParam(value = "phoneNumber", required = true) String phoneNumber, HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		User user = userRepository.findByPhoneNumber(phoneNumber);
		return BYGenericResponseHandler.getResponse(null == user ? false : user);
	}
	
	@RequestMapping(value = "/mergeAccounts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Object swapAccountId(
			@RequestParam(value = "newAccountId", required = true) String newAccountId,
			@RequestParam(value = "oldAccountId", required = true) String oldAccountId,
			 HttpServletRequest req,
			HttpServletResponse res) throws Exception {
				User newRegistration = userRepository.findOne(newAccountId);
				userRepository.delete(newRegistration);
				User oldRegistration = userRepository.findOne(oldAccountId);
				String tempIdHolder = oldRegistration.getId();
				oldRegistration.setId(newRegistration.getId());
				oldRegistration.setActive("In-Active");
				newRegistration.setId(tempIdHolder);
				userRepository.save(oldRegistration);
				newRegistration = userRepository.save(newRegistration);

				UserProfile oldProfile = userProfileRepository.findByUserId(oldAccountId);
				BasicProfileInfo basicProfileInfo = oldProfile.getBasicProfileInfo();
				basicProfileInfo.setFirstName(newRegistration.getUserName());
				basicProfileInfo.setPrimaryEmail(newRegistration.getEmail());
				basicProfileInfo.setPrimaryPhoneNo(newRegistration.getPhoneNumber());
				oldProfile.setBasicProfileInfo(basicProfileInfo);
				userProfileRepository.save(oldProfile);

				Session session = killSession(req, res);
				session = (Session) req.getSession().getAttribute("session");
				
				if (null == session) {
					session = createSession(req, res, newRegistration, true);
				}

		return BYGenericResponseHandler.getResponse(session);
	}

	@RequestMapping(value = "/socialLogin", method = RequestMethod.GET)
	public @ResponseBody Object socialLogin(@RequestParam(value = "platform", required = true) String platform,
			@RequestParam(value = "token", required = true) String token, HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		String email = null;
		String name = "";
		String socialId = null;
		LoggerUtil.logEntry();
		Session session = killSession(req, res);
		JSONObject response = null;
		if (platform.equals("google")) {
			String postUrl = "https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + token;
			URL u = new URL(postUrl);
			URLConnection c = u.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
			String inputLine;
			StringBuffer b = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
				b.append(inputLine + "\n");
			in.close();
			response = new JSONObject(b.toString());
			if (response != null && response.has("email")) {
				email = response.getString("email");
				if (response.has("name")) {
					name = response.getString("name");
				}
				if (response.has("id")) {
					socialId = response.getString("id");
				}
			}
		} else if (platform.equals("facebook")) {
			String postUrl = "https://graph.facebook.com/me?fields=id,name,email&access_token=" + token;
			URL u = new URL(postUrl);
			URLConnection c = u.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
			String inputLine;
			StringBuffer b = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
				b.append(inputLine + "\n");
			in.close();
			response = new JSONObject(b.toString());
			if (response != null && response.has("email")) {
				email = response.getString("email");
				if (response.has("name")) {
					name = response.getString("name");
				}
				if (response.has("id")) {
					socialId = response.getString("id");
				}
			}
		}

		if (email != null) {
			Query q = new Query();
			User user = null;
			Criteria criteria = Criteria.where("email").is(email);
			q.addCriteria(criteria);
			// q.addCriteria(Criteria.where("socialSignOnPlatform").is(platform));
			user = mongoTemplate.findOne(q, User.class);

			if (null == user) {
				logger.debug("User does not exist");
				user = new User(name, BYConstants.USER_ID_TYPE_EMAIL, BYConstants.USER_REG_TYPE_FULL, null, email, null,
						null, null, socialId, platform, null, null, BYConstants.USER_ROLE_USER, "Active", null, null);
				return BYGenericResponseHandler.getResponse(user);
				// return submitUser(user, true, req, res);
			} else {
				logger.debug("User logged in success for user email = " + user.getEmail() != null ? user.getEmail()
						: user.getPhoneNumber());
				session = (Session) req.getSession().getAttribute("session");
				if (null == session) {
					session = createSession(req, res, user, true);
				}
			}
		} else {
			throw new BYException(BYErrorCodes.INVALID_REQUEST);
		}

		return BYGenericResponseHandler.getResponse(session);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Object logout(HttpServletRequest req, HttpServletResponse res) throws Exception {
		LoggerUtil.logEntry();
		Session session = null;
		try {
			logger.debug("logging out");
			session = killSession(req, res);
		} catch (Exception e) {
			Util.handleException(e);
		}
		Util.logStats(mongoTemplate, req, "Logout", null, null, null, null, null, null, "Logout", "USER");
		return BYGenericResponseHandler.getResponse(session);
	}

	// create user - registration
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Object submitUser(@RequestBody User user,
			@RequestParam(value = "session", required = false, defaultValue = "true") boolean isSession,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		LoggerUtil.logEntry();
		Session session = (Session) req.getSession().getAttribute("session");
		boolean isPasswordEntered = true;
		boolean isUserExists = false;
		if (null != user && (Util.isEmpty(user.getId()))) {
			try {
				Query q = new Query();
				if (user.getUserIdType() == BYConstants.USER_ID_TYPE_EMAIL && null != user.getEmail()) {
					q.addCriteria(Criteria.where("email").is(user.getEmail()));
				} else if (user.getUserIdType() == BYConstants.USER_ID_TYPE_PHONE && null != user.getPhoneNumber()) {
					q.addCriteria(Criteria.where("phoneNumber").is(user.getPhoneNumber()));
				} else {
					throw new BYException(BYErrorCodes.INVALID_REQUEST);
				}
				q.addCriteria(Criteria.where("socialSignOnPlatform").is(user.getSocialSignOnPlatform()));

				User existingUser = mongoTemplate.findOne(q, User.class);
				User userWithExtractedInformation = decorateWithInformation(user);
				if (null != existingUser) {
					isUserExists = true;
					if (existingUser.getUserRegType() == BYConstants.USER_REG_TYPE_GUEST) {
						userWithExtractedInformation.setId(existingUser.getId());
						user.setId(existingUser.getId());
						user.setCreatedAt(existingUser.getCreatedAt());
						userWithExtractedInformation.setCreatedAt(existingUser.getCreatedAt());
					} else {
						logger.debug("user with the same credential already exist = " + user.getEmail() + " or "
								+ user.getPhoneNumber());
						throw new BYException(BYErrorCodes.USER_ALREADY_EXIST);
					}
				}

				userWithExtractedInformation = userRepository.save(userWithExtractedInformation);
				changeUserName(userWithExtractedInformation.getId(), userWithExtractedInformation.getUserName());
				if (!isUserExists) {
					sendWelcomeMail(userWithExtractedInformation);
					logHandler.addLog(userWithExtractedInformation, ActivityLogConstants.CRUD_TYPE_CREATE, req);
				}

				// if (Util.isEmpty(user.getPassword())) {
				// isPasswordEntered = false;
				// }
				if (isSession) {
					req.getSession().setAttribute("user", userWithExtractedInformation);
					session = createSession(req, res, userWithExtractedInformation, isPasswordEntered);
				}

			} catch (Exception e) {
				logger.error("error occured while creating the user");
				Util.handleException(e);
			}
			Util.logStats(mongoTemplate, req, "Register new User", session.getUserId(), session.getUserEmail(),
					session.getSessionId(), null, null, null, "Register new User", "USER");

		} else {
			logger.debug("EDIT USER");
			User sessionUser = Util.getSessionUser(req);
			if (sessionUser == null || !sessionUser.getId().equals(user.getId())) {
				throw new BYException(BYErrorCodes.USER_NOT_AUTHORIZED);
			}
			boolean isUserNameChanged = false;
			boolean isPasswordChanged = false;
			User editedUser = getUser(user.getId());
			if (null != editedUser && null != user.getUserName()
					&& !user.getUserName().equals(editedUser.getUserName())) {
				isUserNameChanged = true;
				editedUser.setUserName(user.getUserName());
				logger.debug(
						"trying changing the user name from " + editedUser.getUserName() + " to " + user.getUserName());
			}
			if (null != editedUser && user.getPassword() != null) {
				if (!user.getPassword().equals(editedUser.getPassword())) {
					inValidateAllSessions(user.getId());
					isPasswordChanged = true;
					user.setPassword(Util.getEncodedPwd(user.getPassword()));
				}
				editedUser.setUserRegType(BYConstants.USER_REG_TYPE_FULL);
				editedUser.setPassword(user.getPassword());
			} else {
				user.setUserRegType(BYConstants.USER_REG_TYPE_FULL);
			}

			editedUser = userRepository.save(editedUser);
			logHandler.addLog(editedUser, ActivityLogConstants.CRUD_TYPE_UPDATE, req);
			if (isUserNameChanged) {
				changeUserName(user.getId(), user.getUserName());
			}

			if (isUserNameChanged || isPasswordChanged) {
				if (Util.isEmpty(user.getPassword())) {
					isPasswordEntered = false;
				}
				session = createSession(req, res, editedUser, isPasswordEntered);
			}
			Util.logStats(mongoTemplate, req, "Edit User Info", session.getUserId(), session.getUserEmail(),
					session.getSessionId(), null, null, null, "Edit User Info", "USER");
		}
		return BYGenericResponseHandler.getResponse(session);
	}

	/**
	 * Send OTP
	 */
	@RequestMapping(value = "/sendOtp", method = RequestMethod.POST)
	public @ResponseBody Object sendOtp(@RequestParam(value = "mobile", required = true) String mobileNo,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		LoggerUtil.logEntry();
		try {
			if (!Util.isEmpty(mobileNo)) {
				OtpHandler otpHandler = new OtpHandler(mongoTemplate);
				return BYGenericResponseHandler.getResponse(otpHandler.sendOtp(mobileNo));
			} else {
				throw new BYException(BYErrorCodes.MISSING_PARAMETER);
			}

		} catch (Exception e) {
			Util.handleException(e);
		}

		return null;
	}

	/**
	 * Verify OTP
	 */
	@RequestMapping(value = "/otpLogin", method = RequestMethod.POST)
	public @ResponseBody Object otpLogin(@RequestParam(value = "mobile", required = true) String mobileNo,
			@RequestParam(value = "otp", required = true) String otp, HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		LoggerUtil.logEntry();
		Session session = killSession(req, res);
		try {
			if (!(Util.isEmpty(mobileNo) && Util.isEmpty(otp))) {
				OtpHandler otpHandler = new OtpHandler(mongoTemplate);
				JSONObject otpResp = otpHandler.verifyOtp(mobileNo, otp);
				if (otpResp != null && otpResp.has("type") && otpResp.getString("type").equals("success")) {
					Query q = new Query();
					q.addCriteria(Criteria.where("phoneNumber").is(mobileNo));
					// q.addCriteria(Criteria.where("socialSignOnPlatform").is("mobile"));
					User user = mongoTemplate.findOne(q, User.class);

					if (null == user) {
						user = new User(mobileNo, 1, BYConstants.USER_REG_TYPE_FULL, null, "", mobileNo, null, null,
								mobileNo, "mobile", null, null, BYConstants.USER_ROLE_USER, "Active", null, null);
						return BYGenericResponseHandler.getResponse(user);
						// return submitUser(user, true, req, res);
					} else {
						logger.debug(
								"User logged in success for user email = " + user.getEmail() != null ? user.getEmail()
										: user.getPhoneNumber());
						session = (Session) req.getSession().getAttribute("session");
						if (null == session) {
							session = createSession(req, res, user, true);
						}
					}
				}
			} else {
				throw new BYException(BYErrorCodes.MISSING_PARAMETER);
			}
		} catch (Exception e) {
			Util.handleException(e);
		}
		return BYGenericResponseHandler.getResponse(session);
	}

	/**
	 * Resend OTP
	 */
	@RequestMapping(value = "/resendOtp", method = RequestMethod.POST)
	public @ResponseBody Object resendOtp(@RequestParam(value = "mobile", required = true) String mobileNo,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		LoggerUtil.logEntry();
		try {
			if (!Util.isEmpty(mobileNo)) {
				OtpHandler otpHandler = new OtpHandler(mongoTemplate);
				return BYGenericResponseHandler.getResponse(otpHandler.resendOtp(mobileNo));
			} else {
				throw new BYException(BYErrorCodes.MISSING_PARAMETER);
			}

		} catch (Exception e) {
			Util.handleException(e);
		}

		return null;
	}

	boolean sendWelcomeMail(User user) {
		boolean mailStatus = false;
		try {
			ResourceUtil resourceUtil = new ResourceUtil("mailTemplate.properties");
			String body = "";
			if (user.getUserRegType() == BYConstants.USER_REG_TYPE_GUEST) {
				body = MessageFormat.format(resourceUtil.getResource("welcomeMailToFillProfile"), "");
			} else {
				body = MessageFormat.format(resourceUtil.getResource("welcomeMail1"), user.getUserName());
			}
			MailHandler.sendMail(user.getEmail(), "Welcome to JoyOfAge.org", body);
			mailStatus = true;
		} catch (Exception e) {
			logger.error(BYErrorCodes.ERROR_IN_SENDING_MAIL);
		}
		return mailStatus;
	}

	private User decorateWithInformation(User user) {
		LoggerUtil.logEntry();
		String userName = user.getUserName();
		String password = Util.getEncodedPwd(user.getPassword());
		String email = user.getEmail();
		Integer userIdType = user.getUserIdType();
		Integer userRegType = user.getUserRegType();
		String phoneNumber = user.getPhoneNumber();
		String verificationCode = user.getVerificationCode();
		Date verificationCodeExpiry = user.getVerificationCodeExpiry();
		String socialSignOnId = user.getSocialSignOnId();
		String socialSignOnPlatform = user.getSocialSignOnPlatform();
		String passwordCode = user.getPassword();
		Date passwordCodeExpiry = user.getPasswordCodeExpiry();
		List<String> userTags = user.getUserTags();
		List<String> favEvents = user.getFavEvents();

		// Users registered through the BY site will always have ROLE = USER
		String userRoleId = "USER";

		// TODO: Change this logic during user regitration phase 2
		if (userRoleId != null
				&& (userRoleId.equals(UserRolePermissions.USER) || userRoleId.equals(UserRolePermissions.WRITER))) {
			return new User(userName, userIdType, userRegType, password, email, phoneNumber, verificationCode,
					verificationCodeExpiry, socialSignOnId, socialSignOnPlatform, passwordCode, passwordCodeExpiry,
					userRoleId, "Active", userTags, favEvents);
		} else {
			return new User(userName, userIdType, userRegType, password, email, phoneNumber, verificationCode,
					verificationCodeExpiry, socialSignOnId, socialSignOnPlatform, passwordCode, passwordCodeExpiry,
					userRoleId, "Active", userTags, favEvents);
		}
	}

	private Session createSession(HttpServletRequest req, HttpServletResponse res, User user,
			boolean isPasswordEntered) {
		LoggerUtil.logEntry();
		Session session = new Session(user, isPasswordEntered, req);
		mongoTemplate.save(session);
		req.getSession().setAttribute("session", session);
		req.getSession().setAttribute("user", user);
		logger.debug("returning existing session for user " + user.getEmail() != null ? user.getEmail()
				: user.getPhoneNumber());
		return session;
	}

	private Session killSession(HttpServletRequest req, HttpServletResponse res) {
		LoggerUtil.logEntry();
		Session session = (Session) req.getSession().getAttribute("session");
		if (null != session) {
			session.setStatus(DiscussConstants.SESSION_STATUS_INACTIVE);
			mongoTemplate.save(session);
			req.getSession().invalidate();
		}
		return null;
	}

	public static User getUser(String userId) {
		LoggerUtil.logEntry();
		User user = userRepository.findOne(userId);
		return user;
	}

	public static User saveUser(User user) {
		LoggerUtil.logEntry();
		User newuser = userRepository.save(user);
		return newuser;
	}

	public static void deleteUser(User user) {
		LoggerUtil.logEntry();
		userRepository.delete(user);
	}

	private void inValidateAllSessions(String userId) {
		List<Session> sessionList = mongoTemplate.find(new Query(Criteria.where("userId").is(userId)), Session.class);
		for (Session session : sessionList) {
			session.setStatus(DiscussConstants.SESSION_STATUS_INACTIVE);
			mongoTemplate.save(session);
		}
	}

	private void changeUserName(String userId, String userName) {
		UserNameHandler userNameHandler = new UserNameHandler(mongoTemplate);
		userNameHandler.setUserParams(userId, userName);
		new Thread(userNameHandler).start();
	}
}