package com.beautifulyears.config;

import interceptors.LoggerInterceptor;
import interceptors.SessionInterceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import com.beautifulyears.rest.PingResource;

@EnableWebMvc
@ComponentScan(basePackageClasses = PingResource.class)
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Bean
	public MappingJacksonJsonView jsonView() {
		MappingJacksonJsonView jsonView = new MappingJacksonJsonView();
		jsonView.setPrefixJson(false);
		return jsonView;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoggerInterceptor()).addPathPatterns("/**");
		registry.addInterceptor(new SessionInterceptor()).addPathPatterns("/**");
		;
	}
}
