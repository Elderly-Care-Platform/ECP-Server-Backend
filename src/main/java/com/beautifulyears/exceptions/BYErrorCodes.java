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

	// discuss related error
	DISCUSS_NOT_FOUND(2001, "Discuss content with provided discussId doesn't exist"),
	DISCUSS_ALREADY_LIKED_BY_USER(2002, "Discuss content already liked by the logged in user"),

	// user error
	USER_NOT_AUTHORIZED(3001, "User is not authorized to persform the selected operation"),
	USER_LOGIN_REQUIRED(3002, "User is required to login to perform such operation"),
	USER_LOGIN_FAILED(3003,"User login failed. Invalid user/password combination."),
	USER_ALREADY_EXIST(3004,"User with the same emailId already exists"),
	INVALID_SESSION(3005,"Invalid session, please login to continue"),
	
	//review rate
	REVIEW_TYPE_INVALID(4001,"Invalid review type"),
	RATING_VALUE_INVALID(4002,"Invalid rating value,rating percentage value should be between 0 to 100.")

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
