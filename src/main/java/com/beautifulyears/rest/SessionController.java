/**
 * 
 */
package com.beautifulyears.rest;

import javax.servlet.http.HttpServletRequest;

import com.beautifulyears.constants.BYConstants;
import com.beautifulyears.domain.Session;
import com.beautifulyears.exceptions.BYErrorCodes;
import com.beautifulyears.exceptions.BYException;
import com.beautifulyears.util.Util;

/**
 * @author Nitin
 *
 */
public class SessionController {

	public static boolean checkCurrentSessionFor(HttpServletRequest request,
			String operation) {
		boolean permission = true;
		Session currentSession = (Session) request.getSession().getAttribute(
				"session");
		if (null != currentSession) {
			switch (operation) {
			case "POST":
			case "LIKE":
			case "COMMENT":
			case "ANSWER":
				permission = true;
				break;

			case "RATE_REVIEW":
			case "NEW_ADDRESS":
			case "SUBMIT_PROFILE":
				if (currentSession.getSessionType() == BYConstants.SESSION_TYPE_FULL) {
					permission = true;
				} else if (currentSession.getSessionType() == BYConstants.SESSION_TYPE_GUEST) {
					throw new BYException(BYErrorCodes.USER_PROFILE_INCOMPLETE);
				} 
				// else if (currentSession.getSessionType() == BYConstants.SESSION_TYPE_PARTIAL) {
				// 	throw new BYException(BYErrorCodes.USER_FULL_LOGIN_REQUIRED);
				// }
				break;
			default:
				throw new BYException(BYErrorCodes.INTERNAL_SERVER_ERROR);
			}
		} else {
			throw new BYException(BYErrorCodes.USER_LOGIN_REQUIRED);
		}

		return permission;
	}

}
