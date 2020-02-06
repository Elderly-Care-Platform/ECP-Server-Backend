package com.beautifulyears.rest;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
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
import com.beautifulyears.domain.LoginRequest;
import com.beautifulyears.domain.Session;
import com.beautifulyears.domain.User;
//import com.beautifulyears.domain.UserProfile;
import com.beautifulyears.exceptions.BYErrorCodes;
import com.beautifulyears.exceptions.BYException;
import com.beautifulyears.mail.MailHandler;
import com.beautifulyears.messages.otp.OtpHandler;
import com.beautifulyears.repository.UserRepository;
import com.beautifulyears.rest.response.BYGenericResponseHandler;
import com.beautifulyears.social.facebook.FBConnection;
import com.beautifulyears.social.facebook.FBGraph;
import com.beautifulyears.social.google.GGConnection;
import com.beautifulyears.social.google.GGraph;
import com.beautifulyears.util.LoggerUtil;
import com.beautifulyears.util.ResourceUtil;
import com.beautifulyears.util.UserNameHandler;
import com.beautifulyears.util.Util;
import com.beautifulyears.util.activityLogHandler.ActivityLogHandler;
import com.beautifulyears.util.activityLogHandler.UserActivityLogHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/users")
public class UserController {

	private static UserRepository userRepository;
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

	@RequestMapping(value = "/addGuestUser", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Object addGuestUser(@RequestBody User user, HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		LoginRequest loginRequest = new LoginRequest(user.getEmail(), null);
		User newUser = createGuestUser(loginRequest, false, req, res);
		Util.logStats(mongoTemplate, req, "New Guest User", newUser.getId(), newUser.getEmail(), newUser.getId(), null,
				null, null, "new guest user", "USER");
		return null;
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

	/**
	 * Login/Signup user
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public @ResponseBody Object login(@RequestBody User loginRequest, HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		LoggerUtil.logEntry();
		Session session = killSession(req, res);
		try {
			Query q = new Query();
			User user = null;
			if (loginRequest.getUserIdType() == BYConstants.USER_ID_TYPE_EMAIL) {
				if (!Util.isEmpty(loginRequest.getEmail())) {
					Criteria criteria = Criteria.where("email").is(loginRequest.getEmail());

					// if (!Util.isEmpty(loginRequest.getPassword())) {
					// criteria = criteria.and("password").is(
					// loginRequest.getPassword());
					// }
					q.addCriteria(criteria);
					user = mongoTemplate.findOne(q, User.class);
					/* Removed Guest user login */
					// if (null == user && Util.isEmpty(loginRequest.getPassword())) {
					// user = createGuestUser(loginRequest, true, req, res);
					// } else if (!Util.isEmpty(loginRequest.getPassword())) {
					// Util.isPasswordMatching(loginRequest.getPassword(), user.getPassword());
					// }
				} else {
					throw new BYException(BYErrorCodes.MISSING_PARAMETER);
				}
			} else if (loginRequest.getUserIdType() == BYConstants.USER_ID_TYPE_PHONE) {
				if (!Util.isEmpty(loginRequest.getPhoneNumber())) {
					q.addCriteria(Criteria.where("phoneNumber").is(loginRequest.getPhoneNumber()));
					// .and("password")
					// .is(loginRequest.getPassword()));
					user = mongoTemplate.findOne(q, User.class);
					/* Removed poswword user login */
					// if (null != user) {
					// Util.isPasswordMatching(loginRequest.getPassword(), user.getPassword());
					// }
				} else {
					throw new BYException(BYErrorCodes.MISSING_PARAMETER);
				}
			}

			if (null == user) {
				logger.debug("User does not exist");
				return submitUser(loginRequest, true, req, res);
				// throw new BYException(BYErrorCodes.USER_EMAIL_DOES_NOT_EXIST);	
			
			
			}  else {
				logger.debug("User logged in success for user email = " + loginRequest.getEmail() != null
						? loginRequest.getEmail()
						: loginRequest.getPhoneNumber());
				session = (Session) req.getSession().getAttribute("session");
				if (null == session) {
					session = createSession(req, res, user, true);
				}

			}

		} catch (Exception e) {
				throw e;
			// Util.handleException(e);
		}
		Util.logStats(mongoTemplate, req, "New Login session", session.getUserId(), session.getUserEmail(),
				session.getSessionId(), null, null, null, "New Login session", "USER");
		return BYGenericResponseHandler.getResponse(session);

	}

	private User createGuestUser(LoginRequest loginRequest, boolean isSessionRequired, HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		logger.info("creating guest user with emailId = " + loginRequest.getEmail());
		User user = new User();
		user.setEmail(loginRequest.getEmail());
		user.setUserRegType(BYConstants.USER_REG_TYPE_GUEST);
		user.setUserIdType(BYConstants.USER_ID_TYPE_EMAIL);
		submitUser(user, isSessionRequired, req, res);
		user = (User) req.getSession().getAttribute("user");
		return user;
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

				// if (isGuestUser(user)) {
				// userWithExtractedInformation.setUserRegType(BYConstants.USER_REG_TYPE_GUEST);
				// } else {
				// if (null != user.getId()) {
				// userWithExtractedInformation.setId(user.getId());
				// }
				// userWithExtractedInformation.setUserRegType(BYConstants.USER_REG_TYPE_FULL);
				// }

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
				// Util.handleException(e);
				throw e;
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
				// if (isGuestUser(editedUser)) {
				// 	user.setUserRegType(BYConstants.USER_REG_TYPE_GUEST);
				// } else {
				// }
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

	@RequestMapping(value = "/getFbURL", method = RequestMethod.GET)
	public @ResponseBody Object getFbURL(HttpServletRequest req) {
		LoggerUtil.logEntry();
		return BYGenericResponseHandler.getResponse(new FBConnection().getFBAuthUrl(req));
	}

	@RequestMapping(value = "/fbRes", method = RequestMethod.GET)
	public @ResponseBody Object fbRes(@RequestParam(value = "code", required = false) String code,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		LoggerUtil.logEntry();
		try {
			FBConnection fbConnection = new FBConnection();
			ObjectMapper mapper = new ObjectMapper();
			String accessToken = fbConnection.getAccessToken(code);
			FBGraph fbGraph = new FBGraph(accessToken);
			String graph = fbGraph.getFBGraph();
			Map<String, String> fbProfileData = fbGraph.getGraphData(graph);

			User newFbUser = null;
			Query q = new Query();
			q.addCriteria(Criteria.where("email").is(fbProfileData.get("email")));

			newFbUser = mongoTemplate.findOne(q, User.class);
			if (null == newFbUser) {
				newFbUser = new User();
				newFbUser.setSocialSignOnPlatform(BYConstants.SOCIAL_SIGNON_PLATFORM_FACEBOOK);
				newFbUser.setSocialSignOnId(fbProfileData.get("id"));
				newFbUser.setEmail(fbProfileData.get("email"));
				newFbUser.setUserName(fbProfileData.get("displayName"));
				newFbUser.setUserRegType(BYConstants.USER_REG_TYPE_SOCIAL);
				newFbUser.setUserIdType(BYConstants.USER_ID_TYPE_EMAIL);
				logger.debug("creating new social sign on user : " + newFbUser.toString());
				newFbUser = userRepository.save(decorateWithInformation(newFbUser));
				logHandler.addLog(newFbUser, ActivityLogConstants.CRUD_TYPE_CREATE,
						"new user with facebook social sign on", req);
				sendWelcomeMail(newFbUser);
				Util.logStats(mongoTemplate, req, "New Social facebook user registration", newFbUser.getId(),
						newFbUser.getEmail(), newFbUser.getId(), null, null, null,
						"New Social facebook user registration", "USER");

			}
			Session session = createSession(req, res, newFbUser, true);

			ServletOutputStream out = res.getOutputStream();
			out.println("<script>parent.window.opener.postMessage("
					+ mapper.writeValueAsString(BYGenericResponseHandler.getResponse(session)) + ",'*');</script>");
			out.println("<script>window.close();</script>");
			logger.debug("returning response for fbRes");
			Util.logStats(mongoTemplate, req, "New Social facebook Login session", session.getUserId(),
					session.getUserEmail(), session.getSessionId(), null, null, null,
					"New Social facebook Login session", "USER");
		} catch (Exception e) {
			Util.handleException(e);
		}

		return null;
	}

	/**
	 * Send OTP
	 */
	@RequestMapping(value = "/sendOtp", method = RequestMethod.GET)
	public @ResponseBody Object sendOtp(@RequestParam(value = "mobile", required = true) String mobileNo,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		LoggerUtil.logEntry();
		try {
			if (!Util.isEmpty(mobileNo)) {
				OtpHandler otpHandler = new OtpHandler();
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
	@RequestMapping(value = "/verifyOtp", method = RequestMethod.GET)
	public @ResponseBody Object verifyOtp(@RequestParam(value = "mobile", required = true) String mobileNo,
			@RequestParam(value = "otp", required = true) String otp, HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		LoggerUtil.logEntry();
		try {
			if (!(Util.isEmpty(mobileNo) && Util.isEmpty(otp))) {
				OtpHandler otpHandler = new OtpHandler();
				return BYGenericResponseHandler.getResponse(otpHandler.verifyOtp(mobileNo, otp));
			} else {
				throw new BYException(BYErrorCodes.MISSING_PARAMETER);
			}

		} catch (Exception e) {
			Util.handleException(e);
		}

		return null;
	}

	/**
	 * Resend OTP
	 */
	@RequestMapping(value = "/resendOtp", method = RequestMethod.GET)
	public @ResponseBody Object resendOtp(@RequestParam(value = "mobile", required = true) String mobileNo,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		LoggerUtil.logEntry();
		try {
			if (!Util.isEmpty(mobileNo)) {
				OtpHandler otpHandler = new OtpHandler();
				return BYGenericResponseHandler.getResponse(otpHandler.resendOtp(mobileNo));
			} else {
				throw new BYException(BYErrorCodes.MISSING_PARAMETER);
			}

		} catch (Exception e) {
			Util.handleException(e);
		}

		return null;
	}

	@RequestMapping(value = "/getGgURL", method = RequestMethod.GET)
	public @ResponseBody Object getGgURL(HttpServletRequest req) {
		LoggerUtil.logEntry();
		return BYGenericResponseHandler.getResponse(new GGConnection().getGGAuthUrl(req));
	}

	@RequestMapping(value = "/ggRes", method = RequestMethod.GET)
	public @ResponseBody Object ggRes(@RequestParam(value = "code", required = false) String code,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		LoggerUtil.logEntry();
		try {
			ObjectMapper mapper = new ObjectMapper();
			GGConnection ggConnection = new GGConnection();
			String accessToken = ggConnection.getAccessToken(code);
			GGraph gGraph = new GGraph(accessToken);
			String graph = gGraph.getGBGraph();
			Map<String, String> ggProfileData = gGraph.getGraphData(graph);

			User newGoogleUser = null;
			Query q = new Query();
			q.addCriteria(Criteria.where("email").is(ggProfileData.get("email")));

			newGoogleUser = mongoTemplate.findOne(q, User.class);
			if (null == newGoogleUser) {
				newGoogleUser = new User();
				newGoogleUser.setSocialSignOnPlatform(BYConstants.SOCIAL_SIGNON_PLATFORM_GOOGLE);
				newGoogleUser.setSocialSignOnId(ggProfileData.get("id"));
				newGoogleUser.setEmail(ggProfileData.get("email"));
				newGoogleUser.setUserName(ggProfileData.get("displayName"));
				newGoogleUser.setUserRegType(BYConstants.USER_REG_TYPE_SOCIAL);
				newGoogleUser.setUserIdType(BYConstants.USER_ID_TYPE_EMAIL);
				logger.debug("creating new social sign on user : " + newGoogleUser.toString());
				newGoogleUser = userRepository.save(decorateWithInformation(newGoogleUser));
				logHandler.addLog(newGoogleUser, ActivityLogConstants.CRUD_TYPE_CREATE,
						"new user with facebook social sign on", req);
				sendWelcomeMail(newGoogleUser);
				Util.logStats(mongoTemplate, req, "New Social google user registration", newGoogleUser.getId(),
						newGoogleUser.getEmail(), newGoogleUser.getId(), null, null, null,
						"New Social google user registration", "USER");
			}
			Session session = createSession(req, res, newGoogleUser, true);
			Util.logStats(mongoTemplate, req, "New Social google Login session", session.getUserId(),
					session.getUserEmail(), session.getSessionId(), null, null, null, "New Social google Login session",
					"USER");
			ServletOutputStream out = res.getOutputStream();
			out.println("<script>parent.window.opener.postMessage("
					+ mapper.writeValueAsString(BYGenericResponseHandler.getResponse(session)) + ",'*');</script>");
			out.println("<script>window.close();</script>");
		} catch (Exception e) {
			Util.handleException(e);
		}
		return null;
	}

	@RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
	public @ResponseBody Object getResetPasswordLink(@RequestParam(value = "email", required = true) String email,
			HttpServletRequest req) {
		LoggerUtil.logEntry();
		if (!Util.isEmpty(email)) {
			Query q = new Query();
			q.addCriteria(Criteria.where("email").regex(email, "i"));
			User user = mongoTemplate.findOne(q, User.class);
			if (null != user) {
				user.setVerificationCode(UUID.randomUUID().toString());
				Date t = new Date();
				user.setVerificationCodeExpiry(
						new Date(t.getTime() + (BYConstants.FORGOT_PASSWORD_CODE_EXPIRY_IN_MIN * 60000)));
				boolean mailStatus = sendMailForResetPassword(user);
				if (mailStatus == false) {
					throw new BYException(BYErrorCodes.ERROR_IN_SENDING_MAIL);
				}
				mongoTemplate.save(user);
				Util.logStats(mongoTemplate, req, "New reset password get request", user.getId(), user.getEmail(),
						user.getId(), null, null, null, "New reset password get request", "USER");
			} else {
				throw new BYException(BYErrorCodes.USER_EMAIL_DOES_NOT_EXIST);
			}
		} else {
			throw new BYException(BYErrorCodes.MISSING_PARAMETER);
		}

		return true;
	}

	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	public @ResponseBody Object getResetPasswordLink(@RequestBody User user, HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		LoggerUtil.logEntry();
		User user1 = null;
		if (null != user && !Util.isEmpty(user.getVerificationCode()) && !Util.isEmpty(user.getPassword())) {
			Query q = new Query();
			q.addCriteria(Criteria.where("verificationCode").is(user.getVerificationCode()));
			user1 = mongoTemplate.findOne(q, User.class);
			if (null != user1) {
				Date currentDate = new Date();
				if (currentDate.compareTo(user1.getVerificationCodeExpiry()) <= 0) {
					user1.setVerificationCodeExpiry(currentDate);
					user1.setPassword(Util.getEncodedPwd(user.getPassword()));
					logger.debug("password changed successfuully for user " + user1.getEmail() + " or "
							+ user.getPhoneNumber());
					// send mail on successful changing the password
					mongoTemplate.save(user1);
				} else {
					throw new BYException(BYErrorCodes.USER_CODE_EXPIRED);
				}
			} else {
				throw new BYException(BYErrorCodes.USER_EMAIL_DOES_NOT_EXIST);
			}
		} else {
			throw new BYException(BYErrorCodes.MISSING_PARAMETER);
		}
		inValidateAllSessions(user.getId());
		user1.setPassword(user.getPassword());
		Util.logStats(mongoTemplate, req, "New reset password request", user.getId(), user.getEmail(), user.getId(),
				null, null, null, "New reset password request", "USER");
		// return login(new LoginRequest(user1), req, res);
		return null;
	}

	@RequestMapping(value = "/verifyPwdCode", method = RequestMethod.GET)
	public @ResponseBody Object verifyPwdCode(
			@RequestParam(value = "verificationCode", required = true) String verificationCode,
			HttpServletRequest req) {
		LoggerUtil.logEntry();
		if (!Util.isEmpty(verificationCode)) {
			Query q = new Query();
			q.addCriteria(Criteria.where("verificationCode").is(verificationCode));
			User user1 = mongoTemplate.findOne(q, User.class);
			if (null != user1) {
				Date currentDate = new Date();
				if (currentDate.compareTo(user1.getVerificationCodeExpiry()) <= 0) {
				} else {
					throw new BYException(BYErrorCodes.USER_CODE_EXPIRED);
				}
			} else {
				throw new BYException(BYErrorCodes.USER_CODE_DOES_NOT_EXIST);
			}
		} else {
			throw new BYException(BYErrorCodes.MISSING_PARAMETER);
		}

		return true;
	}

	boolean sendMailForResetPassword(User user) {
		boolean mailStatus = false;
		try {
			ResourceUtil resourceUtil = new ResourceUtil("mailTemplate.properties");
			String url = System.getProperty("path") + "/users/resetPassword/" + user.getVerificationCode();
			String userName = !Util.isEmpty(user.getUserName()) ? user.getUserName() : "Anonymous User";
			String body = MessageFormat.format(resourceUtil.getResource("resetPassword"), userName, url, url, url);
			MailHandler.sendMail(user.getEmail(), "Reset Beutifulyears' password", body);
			mailStatus = true;
		} catch (Exception e) {
			logger.error(BYErrorCodes.ERROR_IN_SENDING_MAIL);
		}
		return mailStatus;
	}

	boolean sendWelcomeMail(User user) {
		boolean mailStatus = false;
		try {
			ResourceUtil resourceUtil = new ResourceUtil("mailTemplate.properties");
			String body = "";
			if (user.getUserRegType() == BYConstants.USER_REG_TYPE_GUEST) {
				body = MessageFormat.format(resourceUtil.getResource("welcomeMailToFillProfile"), "");
			} else {
				body = MessageFormat.format(resourceUtil.getResource("welcomeMail"), "");
			}
			MailHandler.sendMail(user.getEmail(), "Welcome to Beautifulyears.com", body);
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
					userRoleId, "In-Active", userTags,favEvents);
		} else {
			return new User(userName, userIdType, userRegType, password, email, phoneNumber, verificationCode,
					verificationCodeExpiry, socialSignOnId, socialSignOnPlatform, passwordCode, passwordCodeExpiry,
					userRoleId, "In-Active", userTags,favEvents);
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

	public static User saveUser(User user){
		LoggerUtil.logEntry();
		User newuser = userRepository.save(user);
		return newuser;
	}

	public static void deleteUser(User user){
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

	private boolean isGuestUser(User user) {
		boolean isGuestUser = false;
		if (Util.isEmpty(user.getPassword())) {
			isGuestUser = true;
		}

		return isGuestUser;
	}

	private void changeUserName(String userId, String userName) {
		UserNameHandler userNameHandler = new UserNameHandler(mongoTemplate);
		userNameHandler.setUserParams(userId, userName);
		new Thread(userNameHandler).start();
	}
}