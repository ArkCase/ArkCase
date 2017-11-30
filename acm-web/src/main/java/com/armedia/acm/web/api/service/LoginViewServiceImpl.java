package com.armedia.acm.web.api.service;

public class LoginViewServiceImpl implements LoginViewService
{
    @Override
    public String getLoginView()
    {
        return "login";
    }
}
