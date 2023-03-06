package org.flywithme.test

import org.flywithme.test.config.AcceptanceTestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification

@ContextConfiguration(classes = [AcceptanceTestConfig.class])
@ActiveProfiles("test")
class AcceptanceBaseSpec extends Specification{

    @Autowired RestTemplate restTemplate

    @LocalServerPort
    protected int PORT
    protected static final String BASE_URL = "http://localhost"

    HttpEntity createHttpEntity(Object object, MediaType mediaType = MediaType.APPLICATION_JSON) {
        def header = new HttpHeaders()
        header.setContentType(mediaType)
        new HttpEntity(object, header)
    }

    HttpEntity createHttpEntityWithParameters(List<String> keys, List<String> values) {
        def headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED)
        def map = new LinkedMultiValueMap<String, String>()
        keys.eachWithIndex { String key, int i ->
            map.add(key, values[i])
        }
        new HttpEntity<MultiValueMap<String, String>>(map, headers)
    }
}
