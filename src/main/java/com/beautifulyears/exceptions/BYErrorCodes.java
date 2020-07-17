/**
 * 
 */
package com.beautifulyears.exceptions;

/**
 * @author Nitin
 *
 */
public enum BYErrorCodes {
	// generic errors
	INVALID_REQUEST(1001, "The request is invalid"),
	MISSING_PARAMETER(1002, "Required query parameter is missing"),
	INTERNAL_SERVER_ERROR(1003, "Some unknown internal server error occured"),
	ERROR_IN_SENDING_MAIL(1004,"Some unexpected error occured while sending the mail"),
	NO_CONTENT_FOUND(1005,"No content was found with queries criteria"),
	HTML_INJECTION_FOUND(1006,"HTML Injection Found."),
	// discuss related error
	DISCUSS_NOT_FOUND(2001, "Discuss content with provided discussId doesn't exist"),
	DISCUSS_ALREADY_LIKED_BY_USER(2002, "Discuss content already liked by the logged in user"),
	DISCUSS_ALREADY_UNLIKED_BY_USER(2002, "Discuss content already unliked by the logged in user"),
	// product
	PRODUCT_NOT_FOUND(6001, "Product content with provided productId doesn't exist"),
	// ask question
	ASK_QUESTION_NOT_FOUND(7001, "Ask Question content with provided askQuesId doesn't exist"),

	// user error
	USER_NOT_AUTHORIZED(3001, "User is not authorized to perform the selected operation"),
	USER_LOGIN_REQUIRED(3002, "User is required to login to perform such operation"),
	USER_LOGIN_FAILED(3003,"User login failed. Invalid user/password combination."),
	USER_ALREADY_EXIST(3004,"User with the same credentials already exists"),
	USER_DETAILS_EXIST(3004, "User with same email or phone number already exists"),
	INVALID_SESSION(3005,"Invalid session, please login to continue"),
	USER_EMAIL_DOES_NOT_EXIST(3006,"User's emailId or mobile number is not registered"),
	USER_CODE_EXPIRED(3007,"Validation code has been expired, please generate a new one"),
	USER_CODE_DOES_NOT_EXIST(3008,"Validation code entered is invalid. Please enter a valid code."),
	USER_LOGIN_REQUIRE_SOCIAL_SIGNIN(3009,"User is required to login using social sign in."),
	USER_PROFILE_INCOMPLETE(3010,"User is required to create full profile for this operation."),
	USER_FULL_LOGIN_REQUIRED(3011,"User is required to login with his password or social sign in for this operation."),
	
	//review rate
	REVIEW_TYPE_INVALID(4001,"Invalid review type"),
	RATING_VALUE_INVALID(4002,"Invalid rating value,rating percentage value should be between 0 to 100."),
	
	//profile error
	USER_PROFILE_DOES_NOT_EXIST(5001,"User profile for the sent userId does not exist"),

	//Jd serives setting 
	NO_JD_SETTINGS(4003,"Invalid justdial settings, Please check justdial setting limit")
	;

	private final int id;
	private final String msg;

	BYErrorCodes(int id, String msg) {
		this.id = id;
		this.msg = msg;
	}

	public int getId() {
		return this.id;
	}

	public String getMsg() {
		return this.msg;
	}
}
