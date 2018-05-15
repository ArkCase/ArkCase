package com.armedia.acm.auth.okta.model;

/*-
 * #%L
 * ACM Service: User Login and Authentication
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

public interface OktaAPIConstants
{
    String GET_FACTOR = "/api/v1/users/%s/factors/%s";
    String LIST_ENROLLED_FACTORS = "/api/v1/users/%s/factors";
    String LIST_AVAILABLE_ENROLLED_FACTORS = "/api/v1/users/%s/factors/catalog";
    String LIST_SECURITY_QUESTIONS = "/api/v1/users/%s/factors/questions";
    String GET_USER = "/api/v1/users?q=%s";
    String ENROLL_FACTOR = "/api/v1/users/%s/factors";
    String ENROLL_FACTOR_ACTIVATE = "/api/v1/users/%s/factors?activate=true";
    String ENROLL_FACTOR_ACTIVATE_UPDATE_PHONE = "/api/v1/users/%s/factors?updatePhone=true";
    String ACTIVATE_FACTOR = "/api/v1/users/%s/factors/%s/lifecycle/activate";
    String RESET_FACTORS = "/api/v1/users/%s/lifecycle/reset_factors";

    String CREATE_USER = "/api/v1/users?activate=false";
    String USER_OPERATION = "/api/v1/users/%s";
    String ACTIVATE_USER = "/api/v1/users/%s/lifecycle/activate?sendEmail=false";

    String VERIFY_FACTOR = "/api/v1/users/%s/factors/%s/verify";

    String FACTOR_TYPE = "factorType";
    String PROVIDER = "provider";
    String PROFILE = "profile";
    String EMAIL = "email";
    String QUESTION = "question";
    String ANSWER = "answer";
    String PHONE_NUMBER = "phoneNumber";
    String PASS_CODE = "passCode";
    String INVALID_PASS_CODE = "Challenge code is invalid or can't be verified";
    String INVALIDATION_FAILED = "Failed to execute 2nd factor challenge";
    String ERROR = "error";
    String SOFTWARE_TOKEN_TYPE = "token:software:totp";
    String OKTA = "OKTA";

    String FIRST_NAME = "firstName";
    String LAST_NAME = "lastName";
    String LOGIN_NAME = "login";
    String MOBILE_PHONE = "mobilePhone";
    String ROLE_PRE_AUTHENTICATED = "ROLE_PRE_AUTHENTICATED";
    String SPRING_PROFILES_ACTIVE = "spring.profiles.active";
    String AUTH_DEFAULT_PROFILE = "ldap";
}
