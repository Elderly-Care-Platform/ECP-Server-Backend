/**
 * 
 */
package com.beautifulyears.cronJob;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.beautifulyears.repository.HousingRepository;
import com.beautifulyears.repository.UserProfileRepository;
import com.beautifulyears.repository.UserRepository;
import com.beautifulyears.util.Util;
import com.redfin.sitemapgenerator.SitemapIndexGenerator;

/**
 * @author Nitin
 *
 */

@Component
@Controller
@RequestMapping(value = { "/generateSiteMap" })
@EnableAsync
@EnableScheduling
public class SiteMapGenerator {
	public static Map<String, String> allUrls = new LinkedHashMap<String, String>();
	private static boolean isInitialized = false;

	private String sitemapPath = "c:/sitemap";
	private String selfUrl = "https://www.beautifulyears.com";

	private final String communityMenuId = "564071623e60f5b66f62df27";
	private String communityMenuUrl = "/elder-care-forums/564071623e60f5b66f62df27/all";
	private final int MODULE_ID_DISCUSS = 0;
	private final String communityUrlPrefix = "communities";

	private final int MODULE_ID_SERVICES = 1;
	private final String servicesUrlPrefix = "directory";
	private String servicesMenuUrl = "/elder-care-services/56406cd03e60f5b66f62df26/all";
	private final String serviceMenuId = "56406cd03e60f5b66f62df26";
	private final List<String> servicesTags = Arrays.asList(
			"55bc9da5e4b0ac8d31666b48", "55bc9de3e4b0ac8d31666b49");

	private String housingMenuUrl = "/senior-living/55bcadaee4b08970a736784c/all";
	private final List<String> housingTags = Arrays.asList(
			"55bc9de3e4b0ac8d31666b49", "55f06f64e4b04c28cae2927d",
			"55f06f74e4b04c28cae2927e", "55f06f85e4b04c28cae2927f",
			"55f06f93e4b04c28cae29280", "55f06fa1e4b04c28cae29281");

	private String shopMenuUrl = "/elder-care-products/55bcad7be4b08970a736784b";
	private String productServerHost = "qa.beautifulyears.com";
	private String productServerPort = "8083";

	private ProductsSiteMapGenerator productsSMG;
	private CommunitiesSiteMapGenerator communitySMG;
	private ServicesSiteMapGenerator servicesSMG;
	private HousingSiteMapGenerator housingsSMG;
	private ListingsSiteMapGenerator listingsSMG;
	private SearchSiteMapGenerator searchSMG;

	private MongoTemplate mongoTemplate;
	private UserProfileRepository userProfileRepository;
	private UserRepository userRepository;
	private HousingRepository housingRepository;

	@Autowired
	public SiteMapGenerator(HousingRepository housingRepository,
			MongoTemplate mongoTemplate,
			UserProfileRepository userProfileRepository,UserRepository userRepository) {
		this.mongoTemplate = mongoTemplate;
		this.userProfileRepository = userProfileRepository;
		this.userRepository = userRepository;
		this.housingRepository = housingRepository;

	}

	private static int count = 0;

	@RequestMapping(method = { RequestMethod.POST }, consumes = { "application/json" })
	@ResponseBody
	public void reGenerate() throws Exception {
		try {
			communitySMG.run();
			listingsSMG.run();
			productsSMG.run();
			servicesSMG.run();
			housingsSMG.run();
			searchSMG.run();
			createIndexSiteMap();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}

		createMasterSiteMapPage();
	}

	@Scheduled(initialDelay = 20000, fixedDelay = 3500000)
	public void generate() throws Exception {
		if (!SiteMapGenerator.isInitialized) {
			initializeSMG();
		}

		if ((Util.isEmpty(System.getProperty("path")) || Util.isEmpty(System
				.getProperty("sitemapPath")))) {
			return;
		}

		if ((count > 0 && !isMidnight())) {
			return;
		}
		count++;
		try {
			communitySMG.run();
			listingsSMG.run();
			productsSMG.run();
			servicesSMG.run();
			housingsSMG.run();
			searchSMG.run();
			createIndexSiteMap();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}

		createMasterSiteMapPage();

	}

	private void initializeSMG() {
		if (!Util.isEmpty(System.getProperty("path"))) {
			selfUrl = System.getProperty("path");
		}
		if (!Util.isEmpty(System.getProperty("sitemapPath"))) {
			sitemapPath = System.getProperty("sitemapPath");
		}
		if (!Util.isEmpty(System.getProperty("productServerHost"))) {
			productServerHost = System.getProperty("productServerHost");
		}
		if (!Util.isEmpty(System.getProperty("productServerPort"))) {
			productServerPort = System.getProperty("productServerPort");
		}

		communityMenuUrl = selfUrl + communityMenuUrl;
		servicesMenuUrl = selfUrl + servicesMenuUrl;
		shopMenuUrl = selfUrl + shopMenuUrl;
		housingMenuUrl = selfUrl + housingMenuUrl;

		communitySMG = new CommunitiesSiteMapGenerator(selfUrl, sitemapPath,
				mongoTemplate);
		servicesSMG = new ServicesSiteMapGenerator(selfUrl, sitemapPath,
				mongoTemplate, servicesTags, userProfileRepository,userRepository);
		listingsSMG = new ListingsSiteMapGenerator(selfUrl, sitemapPath,
				mongoTemplate, communityMenuId, communityMenuUrl,
				communityUrlPrefix, servicesMenuUrl, MODULE_ID_DISCUSS,
				serviceMenuId, servicesUrlPrefix, MODULE_ID_SERVICES,
				housingMenuUrl, shopMenuUrl, productServerHost,
				productServerPort);
		housingsSMG = new HousingSiteMapGenerator(selfUrl, sitemapPath,
				mongoTemplate, housingTags, housingRepository);
		productsSMG = new ProductsSiteMapGenerator(selfUrl, sitemapPath,
				servicesMenuUrl, productServerHost, productServerPort);
		searchSMG = new SearchSiteMapGenerator(selfUrl, sitemapPath);
		SiteMapGenerator.isInitialized = true;

	}

	private void createIndexSiteMap() throws IOException {
		File targetDirectory = new File(sitemapPath + "/sitemap.xml");
		// SitemapIndexGenerator sig = new SitemapIndexGenerator(selfUrl,
		// targetDirectory);
		SitemapIndexGenerator sitemap = (new SitemapIndexGenerator.Options(
				selfUrl, targetDirectory)).defaultLastMod(new Date())
				.autoValidate(false).build();

		sitemap.addUrl(selfUrl + "/sitemaps/community_sitemap.xml");
		sitemap.addUrl(selfUrl + "/sitemaps/housing_sitemap.xml");
		sitemap.addUrl(selfUrl + "/sitemaps/listings_sitemap.xml");
		sitemap.addUrl(selfUrl + "/sitemaps/products_sitemap.xml");
		sitemap.addUrl(selfUrl + "/sitemaps/services_sitemap.xml");
		sitemap.addUrl(selfUrl + "/sitemaps/search_sitemap.xml");
		sitemap.write();

		System.out.println("SMG: finished with inndex file");

	}

	private boolean isMidnight() {
		boolean isMidnight = false;

		int from = 2300;
		int to = 2400;
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int t = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE);
		isMidnight = to > from && t >= from && t <= to || to < from
				&& (t >= from || t <= to);

		return isMidnight;
	}

	private boolean createMasterSiteMapPage() throws IOException {

		File newHtmlFile = new File(sitemapPath + "/siteMap_all_master_by.html");
		String htmlStringStart = "<html><head><meta name='robots' content='noindex, follow'></head><body><table "
				+ "style='text-align: center;width: 100%;border: 1px solid;'>";
		Iterator<Entry<String, String>> it = allUrls.entrySet().iterator();
		boolean isProduct = false;
		while (it.hasNext()) {
			htmlStringStart += "<tr>";
			Map.Entry<String, String> pair = (Map.Entry<String, String>) it
					.next();
			if (null == pair.getValue()) {
				htmlStringStart += "<td style='border: 1px solid #748494;'><h1>"
						+ pair.getKey() + "</h1></td>";
				if ("PRODUCTS LINKS".equals(pair.getKey())) {
					isProduct = true;
				} else {
					isProduct = false;
				}
			} else {
				htmlStringStart += "<td style='border-bottom: 1px dotted silver;'><a href='"
						+ pair.getValue() + "'>" + pair.getKey() + "</a></td>";
			}
			htmlStringStart += "</tr>";

			it.remove(); // avoids a ConcurrentModificationException
		}
		htmlStringStart += "</table></body></html>";
		FileUtils.writeStringToFile(newHtmlFile, htmlStringStart);
		System.out.println("SMG:updating master sitemap.html finished");
		return false;
	}
}
