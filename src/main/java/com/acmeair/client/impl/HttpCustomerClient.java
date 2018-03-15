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

package com.acmeair.client.impl;

import com.acmeair.client.ClientType;
import com.acmeair.client.CustomerClient;
import com.acmeair.securityutils.SecurityUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;

@ClientType("http")
public class HttpCustomerClient implements CustomerClient {

  private static final String CUSTOMER_SERVICE_LOC = 
          ((System.getenv("CUSTOMER_SERVICE") == null) ? "localhost:6379/customer" 
                  : System.getenv("CUSTOMER_SERVICE"));
  
  private static final String VALIDATE_PATH = "/validateid";
    
  private static final Boolean SECURE_SERVICE_CALLS = 
          Boolean.valueOf((System.getenv("SECURE_SERVICE_CALLS") == null) ? "false" 
                  : System.getenv("SECURE_SERVICE_CALLS"));

  private static final JsonReaderFactory factory = Json.createReaderFactory(null);
    
  static {
    System.out.println("Using HttpCustomerClient");
    System.out.println("SECURE_SERVICE_CALLS: " + SECURE_SERVICE_CALLS); 
  }
    
  @Inject
  private SecurityUtils secUtils;      
  
  /**
   * Calls the customer service to validate the login/password.
   */
  public boolean validateCustomer(String login, String password) {
    
    // Set maxConnections - this seems to help with keepalives/running out of sockets.
    if (System.getProperty("http.maxConnections") == null) {
      System.setProperty("http.maxConnections", "50");
    }
                        
    String url = "http://" + CUSTOMER_SERVICE_LOC + VALIDATE_PATH;
    String urlParameters = "login=" + login + "&password=" + password;     
                
    HttpURLConnection conn = createHttpUrlConnection(url, urlParameters, login, VALIDATE_PATH);
    String output = doHttpUrlCall(conn, urlParameters);                       
       
    JsonReader jsonReader = factory.createReader(new StringReader(output));
    JsonObject customerJson = jsonReader.readObject();
    jsonReader.close(); 
            
    return customerJson.getBoolean("validCustomer");
  }
    
  private String doHttpUrlCall(HttpURLConnection conn, String urlParameters) {        
        
    StringBuffer response = new StringBuffer();
        
    try {
      DataOutputStream wr;
      wr = new DataOutputStream(conn.getOutputStream());
        
      wr.writeBytes(urlParameters);
      wr.flush();
      wr.close();

      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String inputLine;
      response = new StringBuffer();

      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();
      conn.disconnect(); 
      
    }  catch (IOException e) {
      e.printStackTrace();
    }
            
    return response.toString();
  }
    
  private HttpURLConnection createHttpUrlConnection(String url, String urlParameters, 
          String customerId, String path) {
        
    HttpURLConnection conn = null;
      
    try {
        
      URL obj = new URL(url);
      conn = (HttpURLConnection) obj.openConnection();

      //  add request header
      conn.setRequestMethod("POST");
      conn.setDoInput(true);
      conn.setDoOutput(true);
        
      if (SECURE_SERVICE_CALLS) { 
            
        Date date = new Date();
        String sigBody = secUtils.buildHash(urlParameters);
        String signature = secUtils.buildHmac("POST",path,customerId,date.toString(),sigBody); 
            
        conn.setRequestProperty("acmeair-id", customerId);
        conn.setRequestProperty("acmeair-date", date.toString());
        conn.setRequestProperty("acmeair-sig-body", sigBody);    
        conn.setRequestProperty("acmeair-signature", signature);  
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
        
    return conn;
  }         
}
