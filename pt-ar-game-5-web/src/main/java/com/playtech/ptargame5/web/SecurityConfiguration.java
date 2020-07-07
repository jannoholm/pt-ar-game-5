package com.playtech.ptargame5.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private ApplicationProperties conf;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		auth
				.inMemoryAuthentication()
				.withUser("guest").password(encoder.encode(conf.getGuestPassword())).roles("guest-role").and()
				.passwordEncoder(encoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests().antMatchers("/bg-leaderboard.jpg").permitAll();
		
		http.csrf().disable();
		http.formLogin()
				.loginPage("/login.html")
				.permitAll();
		
		// TODO Add regular HTTP auth for /private/ APIs
		http.authorizeRequests().antMatchers("/*").hasRole("guest-role");
		;
	}
}
