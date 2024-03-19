package org.ctf.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.Data;

@Component
@PropertySource("classpath:application.properties")
public class ExternalData {

 /*  @Value("${datasource.destinationUrl}")
  public final String destinationUrl;
  @Value("${datasource.destinationPort}")
  public final String destinationPort;

  @Autowired
  public ExternalData(
      @Value("${datasource.destinationUrl}") String destinationUrl,
      @Value("${datasource.destinationPort}") String destinationPort) {
    this.destinationUrl = destinationUrl;
    this.destinationPort = destinationPort;
    System.out.println("================== " + destinationUrl + "================== ");
  } */
}