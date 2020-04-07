package com.beautifulyears.rest.temp;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.beautifulyears.constants.CDNConstants;
import com.beautifulyears.domain.Discuss;
import com.beautifulyears.domain.HousingFacility;
import com.beautifulyears.domain.UserProfile;
import com.beautifulyears.util.Util;

//import com.beautifulyears.domain.UserProfile;

@Controller
@RequestMapping("/addShortDesc")
public class TempShortDescriptionController {

	private MongoTemplate mongoTemplate;
	private String cdnPath;

	@Autowired
	public TempShortDescriptionController(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@RequestMapping(method = RequestMethod.GET, value = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Object changePassword(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		cdnPath = System.getProperty("cdnPath");

		List<UserProfile> userProfiles = mongoTemplate
				.findAll(UserProfile.class);
		List<Discuss> discuss = mongoTemplate.findAll(Discuss.class);
		List<HousingFacility> housingFacility = mongoTemplate
				.findAll(HousingFacility.class);
		for (Iterator iterator = userProfiles.iterator(); iterator.hasNext();) {
			UserProfile userProfile = (UserProfile) iterator.next();
			if (null != userProfile
					&& null != userProfile.getBasicProfileInfo()
					&& null != userProfile.getBasicProfileInfo()
							.getPhotoGalleryURLs()) {
				List<Map<String, String>> photos = userProfile
						.getBasicProfileInfo().getPhotoGalleryURLs();

				for (int i = 0; i < photos.size(); i++) {
					Map<String, String> array_element = photos.get(i);
					photos.set(i, checkImageMap(array_element));
				}

			}

			if (null != userProfile
					&& null != userProfile.getBasicProfileInfo()
					&& null != userProfile.getBasicProfileInfo()
							.getDescription()) {
				userProfile.getBasicProfileInfo().setDescription(
						changeText(userProfile.getBasicProfileInfo()
								.getDescription()));
			}

			if (null != userProfile
					&& null != userProfile.getBasicProfileInfo()
					&& null != userProfile.getBasicProfileInfo()
							.getProfileImage()) {
				userProfile.getBasicProfileInfo().setProfileImage(
						checkImageMap(userProfile.getBasicProfileInfo()
								.getProfileImage()));
			}
			mongoTemplate.save(userProfile);
		}

		for (Iterator iterator = discuss.iterator(); iterator.hasNext();) {
			Discuss singleDiscuss = (Discuss) iterator.next();
			if (null != singleDiscuss
					&& null != singleDiscuss.getArticlePhotoFilename()) {
				singleDiscuss
						.setArticlePhotoFilename(checkImageMap(singleDiscuss
								.getArticlePhotoFilename()));
			}
			if ("Cancer is no more a deadly disease.".equals(singleDiscuss
					.getTitle())) {
				System.out.println("found");
			}

			singleDiscuss.setText(changeText(singleDiscuss.getText()));
			mongoTemplate.save(singleDiscuss);
		}

		for (Iterator iterator = housingFacility.iterator(); iterator.hasNext();) {
			HousingFacility singleHousing = (HousingFacility) iterator.next();
			if (null != singleHousing
					&& null != singleHousing.getPhotoGalleryURLs()) {

				List<Map<String, String>> photos = singleHousing
						.getPhotoGalleryURLs();

				for (int i = 0; i < photos.size(); i++) {
					Map<String, String> array_element = photos.get(i);
					photos.set(i, checkImageMap(array_element));
				}

			}

			if (null != singleHousing
					&& null != singleHousing.getProfileImage()) {
				singleHousing.setProfileImage(checkImageMap(singleHousing
						.getProfileImage()));
			}

			if (null != singleHousing && null != singleHousing.getDescription()) {
				singleHousing.setDescription(changeText(singleHousing
						.getDescription()));
			}

			mongoTemplate.save(singleHousing);
		}

		return null;
	}

	private Map<String, String> checkImageMap(Map<String, String> imageMap) {
		if (!Util.isEmpty(imageMap.get("original"))) {
			if (imageMap.get("original").contains(
					CDNConstants.S3_HOST + "bymedia")) {
				String str = imageMap.get("original");
				str = str.replace(CDNConstants.S3_HOST + "bymedia", "https://media.joyofage.org");
				System.out.println("original = " + str);
				imageMap.put("original", str);
			}else if (imageMap.get("original").contains(
					CDNConstants.S3_HOST + "by-dev-media")) {
				String str = imageMap.get("original");
				str = str.replace(CDNConstants.S3_HOST + "by-dev-media",
						"https://media.joyofage.org");
				System.out.println("original = " + str);
				imageMap.put("original", str);
			}else if (imageMap.get("original").contains(cdnPath)) {
				String str = imageMap.get("original");
				str = str.replace(cdnPath,
						"https://media.joyofage.org");
				System.out.println("original = " + str);
				imageMap.put("original", str);
			}
		}
		if (!Util.isEmpty(imageMap.get("titleImage"))) {
			if (imageMap.get("titleImage").contains(
					CDNConstants.S3_HOST + "bymedia")) {
				String str = imageMap.get("titleImage");
				str = str.replace(CDNConstants.S3_HOST + "bymedia", "https://media.joyofage.org");
				System.out.println("titleImage = " + str);
				imageMap.put("titleImage", str);
			}else if (imageMap.get("titleImage").contains(
					CDNConstants.S3_HOST + "by-dev-media")) {
				String str = imageMap.get("titleImage");
				str = str.replace(CDNConstants.S3_HOST + "by-dev-media",
						"https://media.joyofage.org");
				System.out.println("titleImage = " + str);
				imageMap.put("titleImage", str);
			}else if (imageMap.get("titleImage").contains(cdnPath)) {
				String str = imageMap.get("titleImage");
				str = str.replace(cdnPath,
						"https://media.joyofage.org");
				System.out.println("titleImage = " + str);
				imageMap.put("titleImage", str);
			}
		}
		if (!Util.isEmpty(imageMap.get("thumbnailImage"))) {
			if (imageMap.get("thumbnailImage").contains(
					CDNConstants.S3_HOST + "bymedia")) {
				String str = imageMap.get("thumbnailImage");
				str = str.replace(CDNConstants.S3_HOST + "bymedia", "https://media.joyofage.org");
				System.out.println("thumbnail = " + str);
				imageMap.put("thumbnailImage", str);
			}else if (imageMap.get("thumbnailImage").contains(
					CDNConstants.S3_HOST + "by-dev-media")) {
				String str = imageMap.get("thumbnailImage");
				str = str.replace(CDNConstants.S3_HOST + "by-dev-media",
						"https://media.joyofage.org");
				System.out.println("thumbnail = " + str);
				imageMap.put("thumbnailImage", str);
			}else if (imageMap.get("thumbnailImage").contains(cdnPath)) {
				String str = imageMap.get("thumbnailImage");
				str = str.replace(cdnPath,
						"https://media.joyofage.org");
				System.out.println("thumbnail = " + str);
				imageMap.put("thumbnailImage", str);
			}
		}
		return imageMap;

	}

	private String changeText(String text) {

		if (!Util.isEmpty(text)) {
			if (text.matches("(?is).*" + CDNConstants.S3_HOST + "bymedia"
					+ ".*")) {
				text = text.replaceAll(CDNConstants.S3_HOST + "bymedia",
						"https://media.joyofage.org");
				System.out.println(text);
			}
			if (text.matches("(?is).*" + CDNConstants.S3_HOST + "by-dev-media"
					+ ".*")) {
				text = text.replaceAll(CDNConstants.S3_HOST + "by-dev-media",
						"https://media.joyofage.org");
				System.out.println(text);
			}
			if (text.matches("(?is).*" + cdnPath
					+ ".*")) {
				text = text.replaceAll(cdnPath,
						"https://media.joyofage.org");
				System.out.println(text);
			}
		}
		return text;
	}
}