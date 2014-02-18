package com.armedia.acm.auth;

import org.springframework.security.core.GrantedAuthority;

public class AcmGrantedAuthority implements GrantedAuthority
{

    private final String authority;

    public AcmGrantedAuthority(String authority)
    {
        this.authority = authority;
    }

    @Override
    public String getAuthority()
    {
        return authority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AcmGrantedAuthority that = (AcmGrantedAuthority) o;

        if (authority != null ? !authority.equals(that.authority) : that.authority != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return authority != null ? authority.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AcmGrantedAuthority{" +
                "authority='" + authority + '\'' +
                '}';
    }
}
