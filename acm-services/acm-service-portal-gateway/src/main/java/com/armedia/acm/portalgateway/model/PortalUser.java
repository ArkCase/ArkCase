package com.armedia.acm.portalgateway.model;

import java.util.Arrays;
import java.util.stream.Collectors;

/*-
 * #%L
 * ACM Service: Portal Gateway Service
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

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 28, 2018
 *
 */
public class PortalUser
{
    public static final String PENDING_USER = "PENDING_USER";

    public static final String REGISTERED_USER = "REGISTERED_USER";

    public static final String REJECTED_USER = "REJECTED_USER";

    /**
     * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 29, 2018
     *
     */
    public enum SalutationPrefix
    {
        MADAM("Madam"), MISS("Miss"), MS("Ms."), MR("Mr.");

        private SalutationPrefix(String prefix)
        {
            this.prefix = prefix;
        }

        private String prefix;

        /**
         * @return the prefix
         */
        public String getPrefix()
        {
            return prefix;
        }

    }

    private String portalUserId;

    private String acmUserId;

    private String prefix;

    private String firstName;

    private String middleName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private String address1;

    private String address2;

    private String city;

    private String zipCode;

    private String state;

    private String country;

    private String role;

    private Long ecmFileId;

    private String organization;

    private String position;

    /**
     * @return the portalUserId
     */
    public String getPortalUserId()
    {
        return portalUserId;
    }

    /**
     * @param portalUserId
     *            the portalUserId to set
     */
    public void setPortalUserId(String portalUserId)
    {
        this.portalUserId = portalUserId;
    }

    /**
     * @return the acmUserId
     */
    public String getAcmUserId()
    {
        return acmUserId;
    }

    /**
     * @param acmUserId
     *            the acmUserId to set
     */
    public void setAcmUserId(String acmUserId)
    {
        this.acmUserId = acmUserId;
    }

    /**
     * @return the prefix
     */
    public String getPrefix()
    {
        return prefix;
    }

    /**
     * @param prefix
     *            the prefix to set
     */
    public void setPrefix(String prefix)
    {
        // SalutationPrefix.valueOf(prefix);
        this.prefix = prefix;
    }

    /**
     * @return the firstName
     */
    public String getFirstName()
    {
        return firstName;
    }

    /**
     * @param firstName
     *            the firstName to set
     */
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    /**
     * @return the middleName
     */
    public String getMiddleName()
    {
        return middleName;
    }

    /**
     * @param middleName
     *            the middleName to set
     */
    public void setMiddleName(String middleName)
    {
        this.middleName = middleName;
    }

    /**
     * @return the lastName
     */
    public String getLastName()
    {
        return lastName;
    }

    /**
     * @param lastName
     *            the lastName to set
     */
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
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
     * @return the phoneNumber
     */
    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    /**
     * @param phoneNumber
     *            the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the address1
     */
    public String getAddress1()
    {
        return address1;
    }

    /**
     * @param address1
     *            the address1 to set
     */
    public void setAddress1(String address1)
    {
        this.address1 = address1;
    }

    /**
     * @return the address2
     */
    public String getAddress2()
    {
        return address2;
    }

    /**
     * @param address2
     *            the address2 to set
     */
    public void setAddress2(String address2)
    {
        this.address2 = address2;
    }

    /**
     * @return the city
     */
    public String getCity()
    {
        return city;
    }

    /**
     * @param city
     *            the city to set
     */
    public void setCity(String city)
    {
        this.city = city;
    }

    /**
     * @return the zipCode
     */
    public String getZipCode()
    {
        return zipCode;
    }

    /**
     * @param zipCode
     *            the zipCode to set
     */
    public void setZipCode(String zipCode)
    {
        this.zipCode = zipCode;
    }

    /**
     * @return the state
     */
    public String getState()
    {
        return state;
    }

    /**
     * @param state
     *            the state to set
     */
    public void setState(String state)
    {
        this.state = state;
    }

    /**
     * @return the country
     */
    public String getCountry()
    {
        return country;
    }

    /**
     * @param country
     *            the country to set
     */
    public void setCountry(String country)
    {
        this.country = country;
    }

    /**
     * @return the organization
     */
    public String getOrganization()
    {
        return organization;
    }

    /**
     * @param organization
     *            the organization to set
     */
    public void setOrganization(String organization)
    {
        this.organization = organization;
    }

    public String getPosition()
    {
        return position;
    }

    public void setPosition(String position)
    {
        this.position = position;
    }

    /**
     * @return the role
     */
    public String getRole()
    {
        return role;
    }

    /**
     * @param role
     *            the role to set
     */
    public void setRole(String role)
    {
        if (!PENDING_USER.equals(role) && !REGISTERED_USER.equals(role) && !REJECTED_USER.equals(role))
        {
            // TODO throw an exception here
        }
        this.role = role;
    }

    public Long getEcmFileId()
    {
        return ecmFileId;
    }

    public void setEcmFileId(Long ecmFileId)
    {
        this.ecmFileId = ecmFileId;
    }

    public static String composeUserName(PortalUser user)
    {
        return Arrays.asList(user.getFirstName(), user.getMiddleName(), user.getLastName()).stream().filter(PortalUser::isEmpty)
                .collect(Collectors.joining(" "));
    }

    protected static boolean isEmpty(String string)
    {
        return string != null && !string.isEmpty();
    }

}
