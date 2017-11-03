package com.unilog.app.security;

import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@EnableGlobalMethodSecurity(securedEnabled = true)
public class ApplicationSecurity extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(final ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/access").setViewName("access");
    }

//    @Order(Ordered.HIGHEST_PRECEDENCE)
//    protected static class AuthenticationSecurity
//            extends GlobalAuthenticationConfigurerAdapter {
//
//        @Override
//        public void init(final AuthenticationManagerBuilder auth) throws Exception {
//            auth.inMemoryAuthentication()
//                    .withUser("admin").password("admin").roles("ADMIN", "USER", "ACTUATOR")
//                    .and()
//                    .withUser("qubuni@qub.ac.uk").password("password").roles("USER");
//        }
//
//    }
}
