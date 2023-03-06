package org.flywithme.test.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@TestConfiguration
class AcceptanceTestConfig {

    @Bean
    RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate()
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory()))
        restTemplate
    }
}
