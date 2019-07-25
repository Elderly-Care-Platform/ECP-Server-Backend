package com.beautifulyears.servlet;

import javax.servlet.http.HttpServlet;

import com.beautifulyears.config.ByWebAppInitializer;
import com.beautifulyears.util.Util;

public class InitServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String host = "http://localhost";
	private String contextPath = "/ROOT";
	private String apiContextPath = "/BY";
	private String productServerHost = "qa.beautifulyears.com";
	private String productServerPort = "8083";
	private String mailSupported = "";
	private String imageUploadPath = "/Users/pulkit/Work/SocialAlpha/server_data/uploads";
	private String sitemapPath = "/Users/pulkit/Work/SocialAlpha/server_data/sitemap";
	private String s3MediaBucketName = "dev-media.beautifulyears.com";
	private String cdnPath = "https://d33mlq9vmeqlx3.cloudfront.net";

	public void init() {
		System.out.println("initializing servlet ==================");

		if (!Util.isEmpty(ByWebAppInitializer.servletContext
				.getInitParameter("host"))) {
			host = ByWebAppInitializer.servletContext.getInitParameter("host");
		}

		if (!Util.isEmpty(ByWebAppInitializer.servletContext
				.getInitParameter("sitemapPath"))) {
			sitemapPath = ByWebAppInitializer.servletContext
					.getInitParameter("sitemapPath");
		}

		if (!Util.isEmpty(ByWebAppInitializer.servletContext
				.getInitParameter("contextPath"))) {
			contextPath = ByWebAppInitializer.servletContext
					.getInitParameter("contextPath");
			if ("/".equals(contextPath)) {
				contextPath = "";
			}
		}
		
		if (!Util.isEmpty(ByWebAppInitializer.servletContext
				.getInitParameter("apiContextPath"))) {
			apiContextPath = ByWebAppInitializer.servletContext
					.getInitParameter("apiContextPath");
			if ("/".equals(apiContextPath)) {
				apiContextPath = "";
			}
		}

		if (!Util.isEmpty(ByWebAppInitializer.servletContext
				.getInitParameter("productServerHost"))) {
			productServerHost = ByWebAppInitializer.servletContext
					.getInitParameter("productServerHost");
		}

		if (!Util.isEmpty(ByWebAppInitializer.servletContext
				.getInitParameter("productServerPort"))) {
			productServerPort = ByWebAppInitializer.servletContext
					.getInitParameter("productServerPort");
		}

		if (!Util.isEmpty(ByWebAppInitializer.servletContext
				.getInitParameter("mail"))) {
			mailSupported = ByWebAppInitializer.servletContext
					.getInitParameter("mail");
		}

		if (!Util.isEmpty(ByWebAppInitializer.servletContext
				.getInitParameter("imageUploadPath"))) {
			imageUploadPath = ByWebAppInitializer.servletContext
					.getInitParameter("imageUploadPath");
		}
		
		if (!Util.isEmpty(ByWebAppInitializer.servletContext
				.getInitParameter("s3MediaBucketName"))) {
			s3MediaBucketName = ByWebAppInitializer.servletContext
					.getInitParameter("s3MediaBucketName");
		}
		
		if (!Util.isEmpty(ByWebAppInitializer.servletContext
				.getInitParameter("cdnPath"))) {
			cdnPath = ByWebAppInitializer.servletContext
					.getInitParameter("cdnPath");
		}

		System.setProperty("host", host);
		System.setProperty("apiContextPath", apiContextPath);
		System.setProperty("path", host + contextPath);
		System.setProperty("s3MediaBucketName", s3MediaBucketName);
		System.setProperty("productServerHost", productServerHost);
		System.setProperty("productServerPort", productServerPort);
		System.setProperty("mailSupported", mailSupported);
		System.setProperty("imageUploadPath", imageUploadPath);
		System.setProperty("sitemapPath", sitemapPath);
		System.setProperty("cdnPath", cdnPath);

		System.out.println(System.getProperty("path") + ","
				+ System.getProperty("apiContextPath") + ","
				+ System.getProperty("productServerHost") + ","
				+ System.getProperty("productServerPort") + ","
				+ System.getProperty("mailSupported") + ","
				+ System.getProperty("sitemapPath") + ","
				+ System.getProperty("s3MediaBucketName") + ","
				+ System.getProperty("cdnPath") + ","
				+ System.getProperty("imageUploadPath"));

	}

}