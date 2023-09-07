package org.ownbit.springboot.jsf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class WebApplication extends SpringBootServletInitializer {

  public WebApplication() {
    log.debug("Initialised Web Application");
  }

  public static void main(String[] args) {
    SpringApplication.run(WebApplication.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(WebApplication.class);
  }
}