package com.open.capacity.client.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.open.capacity.client.config.OauthLogoutHandler;
import com.open.capacity.filter.TokenFilter;

/**
 * @author 作者 owen E-mail: 624191343@qq.com
 * @version 创建时间：2017年11月12日 上午22:57:51
 * 类说明
 * @EnableOAuth2Sso注解。如果WebSecurityConfigurerAdapter类上注释了@EnableOAuth2Sso注解， 那么将会添加身份验证过滤器和身份验证入口。
 * 如果只有一个@EnableOAuth2Sso注解没有编写在WebSecurityConfigurerAdapter上，
 * 那么它将会为所有路径启用安全，并且会在基于HTTP Basic认证的安全链之前被添加。详见@EnableOAuth2Sso的注释。
 */
@Component
@Configuration
@EnableOAuth2Sso
public class UnieapSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private OauthLogoutHandler oauthLogoutHandler;
	
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.formLogin().and()
                .antMatcher("/**")
                // 所有请求都得经过认证和授权
                .authorizeRequests().anyRequest().authenticated()
                .and().authorizeRequests().antMatchers("/", "/anon").permitAll()
                .and()
                // 这里之所以要禁用csrf，是为了方便。
                // 否则，退出链接必须要发送一个post请求，请求还得带csrf token
                // 那样我还得写一个界面，发送post请求
                .csrf().disable()
                // 退出的URL是/logout
                .logout().logoutUrl("/logout")
                .clearAuthentication(true)
				.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
				.addLogoutHandler(oauthLogoutHandler).permitAll()
				.deleteCookies("JSESSIONID","FP-UID")
                .invalidateHttpSession(true)
                // 退出成功后，跳转到/路径。
                .logoutSuccessUrl("/login");


        //新增token过滤器

    }


    @Bean
    public FilterRegistrationBean mytokenFilter(SecurityProperties security, AuthorizationCodeResourceDetails authorizationCodeResourceDetails, ResourceServerProperties resourceServerProperties) {
        FilterRegistrationBean registration = new FilterRegistrationBean();

        TokenFilter tokenFilter = new TokenFilter();
        registration.setFilter(tokenFilter);
        registration.setOrder(security.getFilterOrder() - 11);

//			authorizationCodeResourceDetails.setUserAuthorizationUri("http://127.0.0.1:8000/auth/oauth/authorize");
//			authorizationCodeResourceDetails.setAccessTokenUri("http://127.0.0.1:8000/auth/oauth/token");  //http://127.0.0.1:8000/auth/oauth/authorize
//			
//			resourceServerProperties.setTokenInfoUri("http://127.0.0.1:8000/auth/oauth/check_token");

        return registration;
    }
}
	

	
