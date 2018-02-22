package com.armedia.acm.auth.okta.model;

public final class OktaAPIConstants
{
    public static final String GET_FACTOR = "/api/v1/users/%s/factors/%s";
    public static final String LIST_ENROLLED_FACTORS = "/api/v1/users/%s/factors";
    public static final String LIST_AVAILABLE_ENROLLED_FACTORS = "/api/v1/users/%s/factors/catalog";
    public static final String LIST_SECURITY_QUESTIONS = "/api/v1/users/%s/factors/questions";
    public static final String GET_USER = "/api/v1/users?q=%s";
    public static final String ENROLL_FACTOR = "/api/v1/users/%s/factors";
    public static final String ENROLL_FACTOR_ACTIVATE = "/api/v1/users/%s/factors?activate=true";
    public static final String ENROLL_FACTOR_ACTIVATE_UPDATE_PHONE = "/api/v1/users/%s/factors?updatePhone=true";
    public static final String ACTIVATE_FACTOR = "/api/v1/users/%s/factors/%s/lifecycle/activate";
    public static final String RESET_FACTORS = "/api/v1/users/%s/lifecycle/reset_factors";

    public static final String CREATE_USER = "/api/v1/users?activate=false";
    public static final String USER_OPERATION = "/api/v1/users/%s";
    public static final String ACTIVATE_USER = "/api/v1/users/%s/lifecycle/activate?sendEmail=false";

    public static final String VERIFY_FACTOR = "/api/v1/users/%s/factors/%s/verify";

    public static final String FACTOR_TYPE = "factorType";
    public static final String PROVIDER = "provider";
    public static final String PROFILE = "profile";
    public static final String EMAIL = "email";
    public static final String QUESTION = "question";
    public static final String ANSWER = "answer";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String PASS_CODE = "passCode";
    public static final String INVALID_PASS_CODE = "Challenge code is invalid or can't be verified";
    public static final String INVALIDATION_FAILED = "Failed to execute 2nd factor challenge";
    public static final String ERROR = "error";
    public static final String SUPPORT_LINK = "supportLink";
    public static final String SOFTWARE_TOKEN_TYPE = "token:software:totp";
    public static final String OKTA = "OKTA";

    public final static String FIRST_NAME = "firstName";
    public final static String LAST_NAME = "lastName";
    public final static String LOGIN_NAME = "login";
    public final static String MOBILE_PHONE = "mobilePhone";

    private OktaAPIConstants()
    {
        throw new AssertionError();
    }
}