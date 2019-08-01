package com.beautifulyears.config;


import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.beautifulyears.interceptors.LoggerInterceptor;
import com.beautifulyears.interceptors.SessionInterceptor;
import com.beautifulyears.rest.MenuController;

@EnableWebMvc
@ComponentScan(basePackageClasses = MenuController.class)
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
	
	@Autowired
    private LoggerInterceptor loggerInterceptor;
	@Autowired
    private SessionInterceptor sessionInterceptor;
	

	@Bean
	public Jackson2ObjectMapperBuilder jacksonBuilder() {
	    Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
	    builder.indentOutput(true).dateFormat(new SimpleDateFormat("yyyy-MM-dd"));
	    return builder;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loggerInterceptor).addPathPatterns("/**");
		registry.addInterceptor(sessionInterceptor).addPathPatterns("/**");
		;
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**");
	}
}
