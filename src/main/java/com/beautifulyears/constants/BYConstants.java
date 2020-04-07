package com.beautifulyears.constants;

import java.util.Arrays;
import java.util.List;

public final class BYConstants {
	// public static final String IMAGE_CDN_PATH = "/home/ubuntu/uploads"; //for
	// remote
	public static final String IMAGE_CDN_PATH = "c:/uploads"; // for local
	public static final List<String> ADMIN_EMAILS = Arrays.asList("admin@joyofage.org");

	public static final String SOCIAL_SIGNON_PLATFORM_GOOGLE = "google"; 

	public static final String SOCIAL_SIGNON_PLATFORM_FACEBOOK = "facebook";

	public static final int FORGOT_PASSWORD_CODE_EXPIRY_IN_MIN = 30;

	public static final int USER_ID_TYPE_EMAIL = 0;
	public static final int USER_ID_TYPE_PHONE = 1;
	
	public static final String USER_ROLE_SUPER_USER = "SUPER_USER";
	public static final String USER_ROLE_EDITOR = "EDITOR";
	public static final String USER_ROLE_WRITER = "WRITER";
	public static final String USER_ROLE_USER = "USER"; 
	public static final String USER_ROLE_EXPERT = "EXPERT"; 
	
	public static final int USER_REG_TYPE_FULL = 0;
	public static final int USER_REG_TYPE_GUEST = 1;
	public static final int USER_REG_TYPE_SOCIAL = 2;
	
	public static final int SESSION_TYPE_FULL = 0;
	public static final int SESSION_TYPE_GUEST = 1;
	public static final int SESSION_TYPE_PARTIAL = 2;
	
	public static final String SERVICE_SOURCE_JD = "JD"; 
	public static final String SERVICE_SOURCE_ELDERSPRING = "Elder Spring";
}
