package com.playtech.ptargame5.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LogUtils {
	
	private static final ObjectMapper mapper = new ObjectMapper();

	public static String toString(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			return object.toString();
		}
	}
}
