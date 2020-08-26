/*******************************************************************************
 * Copyright (c) 2018 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.acmeair.health;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@Liveness
@ApplicationScoped
public class AuthHealthCheck implements HealthCheck {
  
  Config config = ConfigProvider.getConfig();
  private String hostname = config.getValue("SYS_APP_HOSTNAME", String.class);
  private int port = config.getValue("SYS_APP_PORT", int.class);


  public HealthCheckResponse call() {
    HealthCheckResponseBuilder builder = HealthCheckResponse.named(hostname);
    if (isServiceReachable()) {
      builder = builder.up();
    } else {
      builder = builder.down();
    }

    return builder.build();
  }

  private boolean isServiceReachable() {
    Client client = ClientBuilder.newClient();
    try {
      client.target("http://" + hostname + ":" + port + "/").request();
      return true;
    } catch (Exception ex) {
      return false;
    } finally {
    	client.close();
    }
  }
}
