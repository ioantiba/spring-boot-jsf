package org.ownbit.springboot.jsf.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.Host;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class WebConfig implements WebMvcConfigurer, WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
  
  public static final String COOKIE_NAME = "portal.ownbit.org";
  public static final Locale ROMANIAN = new Locale("ro", "RO");

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addRedirectViewController("/", UIConstants.DASHBOARD_PAGE);
  }

  @Bean
  public LocaleResolver localeResolver() {
    final CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver(COOKIE_NAME);
    cookieLocaleResolver.setDefaultLocale(ROMANIAN);
    cookieLocaleResolver.setCookieMaxAge(Duration.ofHours(8));
    return cookieLocaleResolver;
  }

  @Bean
  public MessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setBasenames("classpath:i18n/messages");
    messageSource.setUseCodeAsDefaultMessage(true);
    messageSource.setDefaultEncoding(StandardCharsets.UTF_8.displayName());
    messageSource.setFallbackToSystemLocale(false);
    messageSource.setCacheSeconds((int) TimeUnit.HOURS.toSeconds(1));
    return messageSource;
  }
  
  @Override
  public Validator getValidator() {
    LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
    factory.setValidationMessageSource(messageSource());
    return factory;
  }
  
  @Bean
  public TomcatServletWebServerFactory servletWebServerFactory() {
    return new TomcatServletWebServerFactory() {

      @Override
      protected void prepareContext(Host host, ServletContextInitializer[] initializers) {
        super.prepareContext(host, initializers);
        StandardContext child = new StandardContext();
        child.addLifecycleListener(new Tomcat.FixContextListener());
        child.setPath("");
        ServletContainerInitializer initializer = getServletContextInitializer(getContextPath());
        child.addServletContainerInitializer(initializer, Collections.emptySet());
        child.setCrossContext(true);
        host.addChild(child);
      }
    };
  }

  private ServletContainerInitializer getServletContextInitializer(String contextPath) {
    return (c, context) -> {
      HttpServlet servlet = new HttpServlet() {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
          resp.sendRedirect(contextPath);
        }
      };
      context.addServlet("root", servlet).addMapping("/*");
    };
  }
  
  @Override
  public void customize(ConfigurableServletWebServerFactory factory) {
    MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
    mappings.add("xsd", "text/xml; charset=utf-8");
    mappings.add("otf", "font/opentype");
    mappings.add("ico", "image/x-icon");
    mappings.add("svg", "image/svg+xml");
    mappings.add("svg#fontawesome", "image/svg+xml");
    mappings.add("eot", "application/vnd.ms-fontobject");
    mappings.add("eot?#iefix", "application/vnd.ms-fontobject");
    mappings.add("ttf", "application/x-font-ttf");
    mappings.add("woff", "application/x-font-woff");
    mappings.add("woff2", "application/x-font-woff2");
    mappings.add("xhtml", "application/xml");
    mappings.add("webmanifest", "application/manifest+json"); //images/favicons/site.webmanifest
    factory.setMimeMappings(mappings);
  }
}