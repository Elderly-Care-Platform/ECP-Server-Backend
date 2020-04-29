package com.beautifulyears.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.beautifulyears.servlet.GenerateBarCode;
import com.beautifulyears.servlet.InitServlet;
import com.beautifulyears.servlet.Surl;
import com.beautifulyears.servlet.UploadFile;

public class ByWebAppInitializer implements
		WebApplicationInitializer {
	
    public static ServletContext servletContext;

	@Override
	public void onStartup(ServletContext servletContext)
			throws ServletException {
		servletContext.getSessionCookieConfig().setSecure(true);
		System.out.println("***Starting by application ***");
		ByWebAppInitializer.servletContext = servletContext;
		//root path used in logs config
		System.setProperty("rootPath", servletContext.getRealPath("/"));
		AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();
		webApplicationContext.register(ApplicationConfig.class,
				WebMvcConfig.class);
		
		Dynamic dynamc = servletContext.addServlet("dispatcherServlet",
				new DispatcherServlet(webApplicationContext));
		dynamc.addMapping("/api/v1/*");
		dynamc.setLoadOnStartup(1);
		
		Dynamic initServlet = servletContext.addServlet("InitServlet",
				new InitServlet());
		initServlet.setLoadOnStartup(2);
		
		Dynamic dynamc2 = servletContext.addServlet("UploadFile",
				new UploadFile());
		dynamc2.addMapping("/UploadFile");
		dynamc2.setLoadOnStartup(3);
		dynamc2.setAsyncSupported(true);
		
		Dynamic dynamc3 = servletContext.addServlet("generateBarCode",
				new GenerateBarCode());
		dynamc3.addMapping("/generateBarCode");
		dynamc3.setLoadOnStartup(4);
		dynamc3.setAsyncSupported(true);
		
		Dynamic dynamc4 = servletContext.addServlet("surl",
				new Surl());
		dynamc4.addMapping("/surl");
		dynamc4.setLoadOnStartup(5);
		dynamc4.setAsyncSupported(true);

	}

}
