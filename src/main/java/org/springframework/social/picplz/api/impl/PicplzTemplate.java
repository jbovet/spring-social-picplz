/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.picplz.api.impl;

import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.picplz.api.Picplz;
import org.springframework.social.picplz.api.UserOperations;
import org.springframework.social.support.URIBuilder;
import org.springframework.web.client.RestTemplate;

/***
 * 
 * @author Jose Bovet Derpich
 *
 */
public class PicplzTemplate extends AbstractOAuth2ApiBinding implements Picplz {

	private final UserOperations userOperations;
	
	private final String accessToken;
	
	public PicplzTemplate(String accessToken) {
	    super(accessToken);
	    this.accessToken = accessToken;
		MappingJacksonHttpMessageConverter json = new MappingJacksonHttpMessageConverter();
        json.setSupportedMediaTypes(Arrays.asList(new MediaType("text", "javascript")));
		getRestTemplate().getMessageConverters().add(json);
		registerPicplzJsonModule(getRestTemplate());

		getRestTemplate().setErrorHandler(new PicplzErrorHandler());
		userOperations = new UserTemplate(this);
	}
	
	private void registerPicplzJsonModule(RestTemplate restTemplate) {
		List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
		for (HttpMessageConverter<?> converter : converters) {
			if(converter instanceof MappingJacksonHttpMessageConverter) {
				MappingJacksonHttpMessageConverter jsonConverter = (MappingJacksonHttpMessageConverter) converter;
				ObjectMapper objectMapper = new ObjectMapper();				
				objectMapper.registerModule(new PicplzModule());
				jsonConverter.setObjectMapper(objectMapper);
			}
		}
	}
	
	public URIBuilder withOauth_token(String uri) {
		return URIBuilder.fromUri(uri).queryParam("oauth_token", accessToken);
	}
	
	@Override
	public UserOperations userOperations() {
		return userOperations;
	}
}