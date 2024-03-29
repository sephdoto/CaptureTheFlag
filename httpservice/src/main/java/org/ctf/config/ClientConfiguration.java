package org.ctf.config;

import org.ctf.controller.cfpServiceClient;
import org.ctf.shared.constants.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * Configures a RESTClient to be used with the HTTP Interface Pulls Data from Constants file and
 * connects to the specified server
 *
 * @author rsyed
 */
@Configuration
public class ClientConfiguration {
  @Bean
  cfpServiceClient interfacesClient() {
    RestClient restClient =
        RestClient.builder()
            .baseUrl("http://" + Constants.remoteIP + ":" + Constants.remotePort + "/api/")
            .build();
    RestClientAdapter adapter = RestClientAdapter.create(restClient);
    HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
    return factory.createClient(cfpServiceClient.class);
  }
}
