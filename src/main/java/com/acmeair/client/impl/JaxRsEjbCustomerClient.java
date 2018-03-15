/*******************************************************************************
* Copyright (c) 2017 IBM Corp.
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
/* This version of the jaxrs client works on liberty, tomee, wildfly, but brings
 * in ejbLite, which is not part of the microprofile spec. 
 */

package com.acmeair.client.impl;

import com.acmeair.client.ClientType;
import com.acmeair.client.CustomerClient;
import com.acmeair.securityutils.SecurityUtils;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

@ClientType("jaxrs-ejb")
@Stateless
public class JaxRsEjbCustomerClient implements CustomerClient {

  private WebTarget customerTarget;
    
  private static final String CUSTOMER_SERVICE_LOC = 
          ((System.getenv("CUSTOMER_SERVICE") == null) ? "localhost:6379/customer" 
                  : System.getenv("CUSTOMER_SERVICE"));
  private static final String VALIDATE_PATH = "/validateid";
    
  private static final Boolean SECURE_SERVICE_CALLS = 
          Boolean.valueOf((System.getenv("SECURE_SERVICE_CALLS") == null) ? "false" 
                  : System.getenv("SECURE_SERVICE_CALLS"));

  @Inject
  private SecurityUtils secUtils;
    
  static {
    System.out.println("Using JAXRSEJBCustomerClient");
    System.out.println("SECURE_SERVICE_CALLS: " + SECURE_SERVICE_CALLS); 
  }
    
  @PostConstruct
  public void init() {
    Client client = ClientBuilder.newClient();
    customerTarget =  client.target("http://" + CUSTOMER_SERVICE_LOC + VALIDATE_PATH);
  }
     
  /**
   * Calls the customer service to validate the login/password.
   */
  public boolean validateCustomer(String login, String password) {
    Form form = new Form();
    form.param("login", login);
    form.param("password", password);

    Builder builder = createInvocationBuilder(customerTarget, form, login, VALIDATE_PATH);
    builder.accept(MediaType.TEXT_PLAIN);
                              
    Response res = builder.post(Entity.entity(form,MediaType.APPLICATION_FORM_URLENCODED),
            Response.class);                

    JsonObject jsonObject = res.readEntity(JsonObject.class); 
    return jsonObject.getBoolean("validCustomer");
  }
    
  private Builder createInvocationBuilder(WebTarget target, Form form, 
          String customerId, String path) {
        
    Builder builder = target.request(MediaType.APPLICATION_JSON_TYPE);
        
    if (SECURE_SERVICE_CALLS) { 
      try {
        Date date = new Date();
                
        String body = "";
        MultivaluedMap<String,String> map = form.asMap();
                
        for (String key:map.keySet()) {
          body = body + key + "=" + map.getFirst(key) + "&";
        }
        body = body.substring(0,body.length() - 1);
                
        String sigBody = secUtils.buildHash(body);
        String signature = secUtils.buildHmac("POST",path,customerId,date.toString(),sigBody); 
        
        builder.header("acmeair-id", customerId);
        builder.header("acmeair-date", date.toString());
        builder.header("acmeair-sig-body", sigBody);    
        builder.header("acmeair-signature", signature); 
            
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
        
    return builder;
  }  
}