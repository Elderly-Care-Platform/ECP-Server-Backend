/**
 * 
 */
package com.beautifulyears.cronJob;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;

import com.beautifulyears.repository.UserProfileRepository;
import com.beautifulyears.repository.UserRepository;
import com.beautifulyears.rest.UserProfileController;
import com.beautifulyears.rest.response.BYGenericResponseHandler;
import com.beautifulyears.rest.response.UserProfileResponse;
import com.beautifulyears.util.Util;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;

/**
 * @author Nitin
 *
 */

public class ServicesSiteMapGenerator implements Runnable {
	private String selfUrl = "";
	private String sitemapPath = "";
	private MongoTemplate mongoTemplate;
	private UserProfileRepository userProfileRepository;
	private UserRepository userRepository;
	private List<String> servicesTags;

	public ServicesSiteMapGenerator(String selfUrl, String sitemapPath,
			MongoTemplate mongoTemplate, List<String> servicesTags,
			UserProfileRepository userProfileRepository,UserRepository userRepository) {
		this.selfUrl = selfUrl;
		this.sitemapPath = sitemapPath;
		this.mongoTemplate = mongoTemplate;
		this.servicesTags = servicesTags;
		this.userProfileRepository = userProfileRepository;
		this.userRepository = userRepository;
	}

	@Override
	public void run() {
		File targetDirectory = new File(this.sitemapPath + "/sitemaps/");
		try {
			WebSitemapGenerator services_sitemap = WebSitemapGenerator
					.builder(selfUrl, targetDirectory)
					.fileNamePrefix("services_sitemap").build();
			
			SiteMapGenerator.allUrls.put("DIRECTORY LINKS", null);

			// ------------------------for all professional and institution
			// profiles
			UserProfileController profileCtrl = new UserProfileController(
					userProfileRepository, null,userRepository,mongoTemplate);

			@SuppressWarnings("unchecked")
			ResponseEntity<BYGenericResponseHandler.ByGenericResponse> profileResponse = (ResponseEntity<BYGenericResponseHandler.ByGenericResponse>) profileCtrl
					.getUserProfilebyCity(null, servicesTags, 0, 3000, null,
							"lastModifiedAt", 0, null, null);

			for (UserProfileResponse.UserProfileEntity profile : ((UserProfileResponse.UserProfilePage) profileResponse
					.getBody().getData()).getContent()) {
				addDirectoryUrl(services_sitemap, profile);
			}

			services_sitemap.write();
			System.out.println("SMG: finished with services sitemap file");

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private WebSitemapGenerator addDirectoryUrl(WebSitemapGenerator wsg,
			UserProfileResponse.UserProfileEntity profile)
			throws MalformedURLException {
		String slug = getUserSlug(profile);
		WebSitemapUrl wsmUrl = new WebSitemapUrl.Options(selfUrl + "/elder-care-services/"
				+ slug + "/?profileId=" + profile.getUserId()).lastMod(
				new Date()).build();
		wsg.addUrl(wsmUrl);
		SiteMapGenerator.allUrls.put(slug, wsmUrl.getUrl().toString());
		return wsg;
	}

	private String getUserSlug(UserProfileResponse.UserProfileEntity profile) {
		String proTitle = "others";
		if (null != profile && null != profile.getBasicProfileInfo()
				&& !Util.isEmpty(profile.getBasicProfileInfo().getFirstName())) {
			proTitle = profile.getBasicProfileInfo().getFirstName();
			if (null != profile.getBasicProfileInfo()
					&& !Util.isEmpty(profile.getIndividualInfo().getLastName())) {
				proTitle = proTitle + " "
						+ profile.getIndividualInfo().getLastName();
			}
		} else {
			proTitle = "others";
		}
		return Util.getSlug(proTitle);
	}

}
