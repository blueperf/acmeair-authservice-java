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

package com.acmeair.utils;

import java.io.FileInputStream;

import java.security.Key;
import java.security.KeyStore;

import java.security.PrivateKey;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import org.jose4j.jwk.RsaJsonWebKey;
// This class is used by the auth-service to build JWTs. This is done by a third party at the moment, because I am
// not aware of a spec api to use for this.

@ApplicationScoped
public class SecurityUtils { 

  @Inject @ConfigProperty(name = "KEYSTORE_LOCATION", defaultValue = "/config/resources/security/key.jks")
  private String keyStoreLocation;

  //probably not a good idea to use as an env variable? But doing this for now.
  @Inject @ConfigProperty(name = "KEYSTORE_PASSWORD", defaultValue = "secret")
  private String keyStorePassword;

  @Inject @ConfigProperty(name = "KEYSTORE_ALIAS", defaultValue = "default")
  private String keyStoreAlias;

  @Inject @ConfigProperty(name = "JWT_ISSUER", defaultValue = "http://acmeair-ms")
  private String jwtIssuer;

  @Inject @ConfigProperty(name = "JWT_AUDIENCE", defaultValue = "acmeair-ms")
  private String jwtAudience;

  /**
   *  Generate simple JWT with login as the Subject. 
   */
  public String generateJwt(String jwtSubject, String jwtGroup) {

    String token = "";

    try {
      FileInputStream is = new FileInputStream(keyStoreLocation);

      // Get Private Key
      KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
      keystore.load(is, keyStorePassword.toCharArray());
      Key key = keystore.getKey(keyStoreAlias, keyStorePassword.toCharArray());

      if (key instanceof PrivateKey) {

        
        new RsaJsonWebKey((RSAPublicKey) publicKey);
        // Get public Algorithm.
        Algorithm algorithm = Algorithm.RSA256((RSAPrivateKey) key);

        LocalDateTime now = LocalDateTime.now();
        Instant instant = now.atZone(ZoneId.systemDefault()).toInstant();
        Date date = Date.from(instant);

        LocalDateTime plusHour = LocalDateTime.now().plusHours(1);
        Instant instantPlusHour = plusHour.atZone(ZoneId.systemDefault()).toInstant();
        Date datePlusHour = Date.from(instantPlusHour);

        token = JWT.create()
            .withSubject(jwtSubject)
            .withIssuer(jwtIssuer)
            .withExpiresAt(datePlusHour)
            .withAudience(jwtAudience)
            .withIssuedAt(date)
            .withArrayClaim("groups", new String[]{jwtGroup})
            .withClaim("upn", jwtSubject)
            .sign(algorithm);
      }
    } catch (Exception exception) {
      exception.printStackTrace(); 
    }

    return token;
  }   
}
