package com.playtech.ptargame5.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "security")
public class ApplicationProperties {

	private String guestPassword;
	private String apiPassword;

	public String getGuestPassword() {
		return guestPassword;
	}

	public void setGuestPassword(String guestPassword) {
		this.guestPassword = guestPassword;
	}

	public String getApiPassword() {
		return apiPassword;
	}

	public void setApiPassword(String apiPassword) {
		this.apiPassword = apiPassword;
	}
}
