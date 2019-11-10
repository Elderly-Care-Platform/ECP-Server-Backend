/**
 * 
 */
package com.beautifulyears.rest.likeHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.mongodb.core.MongoTemplate;

import com.beautifulyears.domain.DiscussLike;
import com.beautifulyears.domain.User;
import com.beautifulyears.repository.DiscussLikeRepository;
import com.beautifulyears.util.LoggerUtil;
import com.beautifulyears.util.Util;
import com.beautifulyears.util.activityLogHandler.ActivityLogHandler;
import com.beautifulyears.util.activityLogHandler.LikeActivityLogHandler;

/**
 * @author Nitin
 *
 */
public abstract class LikeController<T> {

	private DiscussLikeRepository discussLikeRepository;
	protected ActivityLogHandler<Object> logHandler;

	public LikeController(DiscussLikeRepository discussLikeRepository,
			MongoTemplate mongoTemplate) {
		this.discussLikeRepository = discussLikeRepository;
		logHandler = new LikeActivityLogHandler(mongoTemplate);
	}

	public Object submitLike(User user, String contentId, int contentType)
			throws Exception {
		LoggerUtil.logEntry();
		DiscussLike discussLike = null;
		try {
			discussLike = new DiscussLike(user, contentId, contentType);
			discussLikeRepository.save(discussLike);
		} catch (Exception e) {
			Util.handleException(e);
		}
		return discussLike;
	}

	public Object submitUnLike(User user, String contentId, int contentType)
			throws Exception {
		LoggerUtil.logEntry();
		DiscussLike discussLike = null;
		try {
			discussLike = discussLikeRepository.findByContentIdAndUserId(contentId,user.getId()).get(0);
			discussLikeRepository.delete(discussLike.getId());
		} catch (Exception e) {
			Util.handleException(e);
		}
		return discussLike;
	}

	abstract Object likeContent(String id, String type, String url,
			HttpServletRequest req, HttpServletResponse res) throws Exception;

	abstract Object unlikeContent(String id, String type,
			HttpServletRequest req, HttpServletResponse res) throws Exception;

	abstract void sendMailForLike(T LikedEntity, User user, String url);

}
