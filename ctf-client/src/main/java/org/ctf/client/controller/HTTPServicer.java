package org.ctf.client.controller;

import org.ctf.shared.constants.Constants;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

public class HTTPServicer {
  RestClient restClient =
      RestClient.builder()
          .baseUrl("http://" + Constants.remoteIP + ":" + Constants.remotePort + "/api/")
          .build();
  RestClientAdapter adapter = RestClientAdapter.create(restClient);
  HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
  ctfHTTPClient service = factory.createClient(ctfHTTPClient.class);

  public ctfHTTPClient getService(){
    return service;
  }
}
