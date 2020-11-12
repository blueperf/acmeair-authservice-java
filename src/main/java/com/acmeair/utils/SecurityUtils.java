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

import java.util.Arrays;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.ibm.websphere.security.jwt.InvalidBuilderException;
import com.ibm.websphere.security.jwt.InvalidClaimException;
import com.ibm.websphere.security.jwt.JwtBuilder;
import com.ibm.websphere.security.jwt.JwtException;
import com.ibm.websphere.security.jwt.KeyException;

import org.eclipse.microprofile.config.inject.ConfigProperty;

// This class is used by the auth-service to build JWTs. This is the Liberty way to do it.

@ApplicationScoped
public class SecurityUtils {

  @Inject
  @ConfigProperty(name = "JWT_ISSUER", defaultValue = "http://acmeair-ms")
  private String jwtIssuer;

  @Inject
  @ConfigProperty(name = "JWT_ALGORITHM", defaultValue = "RS256")
  private String jwtAlgorithm;

  /* This does not work yet 
  @Inject
  @ConfigProperty(name = "ENCRYPT_JWT", defaultValue = "false")
  private boolean buildJWE;

  @Inject
  @ConfigProperty(name = "JWE_ALGORITHM_HEADER_VALUE", defaultValue = "RSA-OAEP-256")
  private String jweAlgorithmHeaderValue;

  @Inject
  @ConfigProperty(name = "JWE_ENC_METHOD_HEADER_PARAM", defaultValue = "A256GCM")
  private String encryptionMethodHeaderParameter;
  */
  // Only used for testing the authservice itself.
  @Inject
  @ConfigProperty(name = "DISABLE_CUSTOMER_VALIDATION", defaultValue = "false")
  private boolean customerValidationDisabled;

  /**
   * Generate a JWT with login as the Subject.
   * 
   * @throws InvalidBuilderException
   * @throws InvalidClaimException
   * @throws KeyException
   * @throws JwtException
   */
  public String generateJwt(String jwtSubject, String jwtGroup)
      throws InvalidBuilderException, InvalidClaimException, KeyException, JwtException {
    
    return JwtBuilder.create()
      .issuer(jwtIssuer)
      .claim("upn", jwtSubject)
      .claim("groups",Arrays.asList(jwtGroup))
      .jwtId(true)
      .subject(jwtSubject)
      .buildJwt().compact();       
  }

  public boolean isCustomerValidationDisabled() {
    return customerValidationDisabled;
  }
}
