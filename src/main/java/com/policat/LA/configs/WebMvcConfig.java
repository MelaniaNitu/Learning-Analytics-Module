package com.policat.LA.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index").setViewName("index");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/about").setViewName("about-page");
        registry.addViewController("/results").setViewName("results");
        registry.addViewController("/contact").setViewName("contact");

        registry.addViewController("/descriptive").setViewName("descriptive");
        registry.addViewController("/diagnostic").setViewName("diagnostic");
        registry.addViewController("/prescriptive").setViewName("prescriptive");
        registry.addViewController("/predictive").setViewName("predictive");
        registry.addViewController("/summary").setViewName("summary");
        registry.addViewController("/data_analytics").setViewName("data_analytics");

    }
}
