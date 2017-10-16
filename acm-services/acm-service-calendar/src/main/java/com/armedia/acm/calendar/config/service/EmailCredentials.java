package com.armedia.acm.calendar.config.service;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.voodoodyne.jackson.jsog.JSOGGenerator;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 11, 2017
 *
 */
@JsonIdentityInfo(generator = JSOGGenerator.class)
public class EmailCredentials
{

    private String email;

    private String password;

    public EmailCredentials()
    {
    }

    public EmailCredentials(String email, String password)
    {
        this.email = email;
        this.password = password;
    }

    /**
     * @return the email
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email)
    {
        this.email = email;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        EmailCredentials other = (EmailCredentials) obj;
        if (email == null)
        {
            if (other.email != null)
            {
                return false;
            }
        } else if (!email.equals(other.email))
        {
            return false;
        }
        if (password == null)
        {
            if (other.password != null)
            {
                return false;
            }
        } else if (!password.equals(other.password))
        {
            return false;
        }
        return true;
    }

}
