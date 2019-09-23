/**
 * 
 */
package com.beautifulyears.cronJob;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.beautifulyears.domain.menu.Menu;
import com.beautifulyears.rest.MenuController;
import com.beautifulyears.util.Util;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;

/**
 * @author Nitin
 *
 */

public class ListingsSiteMapGenerator implements Runnable {
	private String selfUrl = "";
	private String sitemapPath = "";
	private MongoTemplate mongoTemplate;
	private String communityMenuId;
	private String communityMenuUrl;
	private String communityUrlPrefix;
	private int MODULE_ID_DISCUSS;
	private String serviceMenuId;
	private String servicesMenuUrl;
	private String servicesUrlPrefix;
	private int MODULE_ID_SERVICES;
	private String housingMenuUrl;
	private String shopMenuUrl;
	private String productServerHost;
	private String productServerPort;

	public ListingsSiteMapGenerator(String selfUrl, String sitemapPath,
			MongoTemplate mongoTemplate, String communityMenuId,
			String communityMenuUrl, String communityUrlPrefix,
			String servicesMenuUrl, int MODULE_ID_DISCUSS,
			String serviceMenuId, String servicesUrlPrefix,
			int MODULE_ID_SERVICES, String housingMenuUrl, String shopMenuUrl,
			String productServerHost, String productServerPort) {
		this.selfUrl = selfUrl;
		this.sitemapPath = sitemapPath;
		this.mongoTemplate = mongoTemplate;
		this.communityMenuId = communityMenuId;
		this.communityMenuUrl = communityMenuUrl;
		this.communityUrlPrefix = communityUrlPrefix;
		this.MODULE_ID_DISCUSS = MODULE_ID_DISCUSS;
		this.serviceMenuId = serviceMenuId;
		this.servicesMenuUrl = servicesMenuUrl;
		this.servicesUrlPrefix = servicesUrlPrefix;
		this.MODULE_ID_SERVICES = MODULE_ID_SERVICES;
		this.housingMenuUrl = housingMenuUrl;
		this.shopMenuUrl = shopMenuUrl;
		this.productServerHost = productServerHost;
		this.productServerPort = productServerPort;
	}

	@Override
	public void run() {
		File targetDirectory = new File(this.sitemapPath + "/sitemaps/");
		try {
			WebSitemapGenerator listings_sitemap = WebSitemapGenerator
					.builder(selfUrl, targetDirectory)
					.fileNamePrefix("listings_sitemap").build();

			SiteMapGenerator.allUrls.put("MENU LINKS", null);

			// -------------------for adding main community listing page
			MenuController menuController = new MenuController(mongoTemplate);
			@SuppressWarnings("unchecked")
			List<Menu> communityMenuList = (List<Menu>) menuController.getMenu(
					null, communityMenuId,null);
			WebSitemapUrl wsmUrl = new WebSitemapUrl.Options(communityMenuUrl)
					.lastMod(new Date()).build();
			listings_sitemap.addUrl(wsmUrl);
			SiteMapGenerator.allUrls.put("COMMUNITY_URL", communityMenuUrl);

			// ----------------------for adding all the community listing pages

			for (Menu menu : communityMenuList) {
				listings_sitemap = addMenuUrl(listings_sitemap, menu,
						communityUrlPrefix, MODULE_ID_DISCUSS);
			}

			// ----------------------for adding directory listing url

			@SuppressWarnings("unchecked")
			List<Menu> directoryMenuList = (List<Menu>) menuController.getMenu(
					null, serviceMenuId,null);
			wsmUrl = new WebSitemapUrl.Options(servicesMenuUrl).lastMod(
					new Date()).build();
			listings_sitemap.addUrl(wsmUrl);
			SiteMapGenerator.allUrls.put("SERVICE_URL", servicesMenuUrl);

			// ----------------------for adding all the directory menus
			for (Menu menu : directoryMenuList) {
				listings_sitemap = addMenuUrl(listings_sitemap, menu,
						servicesUrlPrefix, MODULE_ID_SERVICES);
			}

			// --------------------- for adding listing page of housing
			wsmUrl = new WebSitemapUrl.Options(housingMenuUrl).lastMod(
					new Date()).build();
			listings_sitemap.addUrl(wsmUrl);
			SiteMapGenerator.allUrls.put("HOUSING_URL", housingMenuUrl);

			// -------------------for adding main product listing page
			wsmUrl = new WebSitemapUrl.Options(shopMenuUrl).lastMod(new Date())
					.build();
			listings_sitemap.addUrl(wsmUrl);

			// for adding all the listing pages
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Accept", "application/json");
			HttpEntity<String> entity = new HttpEntity<String>(headers);
			URI uri = new URI("http", null, productServerHost,
					Integer.parseInt(productServerPort),
					"/beautifulyears/api/v1/catalog/categories",
					"limit=100000", null);
			System.out.println("product listing uri = "+uri);
			ResponseEntity<String> responseEntity = restTemplate.exchange(uri,
					HttpMethod.GET, entity, String.class);
			JSONObject json = new JSONObject(responseEntity.getBody());
			System.out.println("product categories = "+responseEntity.getBody());
			JSONArray categories = json.getJSONArray("category");
			addProductCategory(listings_sitemap, categories);

			listings_sitemap.write();
			System.out.println("SMG: finished with listings sitemap file");

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private WebSitemapGenerator addMenuUrl(WebSitemapGenerator wsg, Menu menu,
			String parentMenuName, int moduleId) throws MalformedURLException {
		if (!menu.isHidden() && menu.getModule() == moduleId) {
			String slug = Util.getSlug(menu.getDisplayMenuName());
			WebSitemapUrl wsmUrl = new WebSitemapUrl.Options(selfUrl + "/"
					+ parentMenuName + "/" + slug + "/" + menu.getId() + "/all")
					.lastMod(new Date()).build();
			wsg.addUrl(wsmUrl);
			SiteMapGenerator.allUrls.put(slug, wsmUrl.getUrl().toString());
		}
		if (menu.getChildren().size() > 0) {
			for (Menu childMenu : menu.getChildren()) {
				wsg = addMenuUrl(wsg, childMenu, parentMenuName, moduleId);
			}
		}
		return wsg;
	}

	private WebSitemapGenerator addProductCategory(WebSitemapGenerator wsg,
			JSONArray categories) throws MalformedURLException {
		for (int i = 0, size = categories.length(); i < size; i++) {
			
			JSONObject category = categories.getJSONObject(i);
			String categoryName = category.getString("name");
			String slug = Util.getSlug(categoryName);
			int categoryId = category.getInt("id");
			WebSitemapUrl wsmUrl = new WebSitemapUrl.Options(selfUrl
					+ "/elder-care-products/" + slug + "/"
					+ categoryId).lastMod(new Date()).build();
			wsg.addUrl(wsmUrl);
			SiteMapGenerator.allUrls.put(slug, wsmUrl.getUrl().toString());

			if (category.has("subcategories")) {
				JSONArray subcategories = category
						.getJSONArray("subcategories");
				addProductCategory(wsg, subcategories);
			}
		}
		return wsg;
	}

}
