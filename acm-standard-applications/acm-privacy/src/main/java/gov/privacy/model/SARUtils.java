/**
 *
 */
package gov.privacy.model;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;

import java.util.List;

/**
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class SARUtils
{

    /**
     * @param person
     * @return
     */
    public static String extractRequestorName(Person person)
    {
        StringBuilder nameBuilder = new StringBuilder();

        if (person.getTitle() != null && !person.getTitle().isEmpty())
        {
            nameBuilder.append(person.getTitle());
        }
        if (person.getGivenName() != null && !person.getGivenName().isEmpty())
        {
            if (nameBuilder.length() > 0)
            {
                nameBuilder.append(' ');
            }
            nameBuilder.append(person.getGivenName());
        }
        if (person.getMiddleName() != null && !person.getMiddleName().isEmpty())
        {
            if (nameBuilder.length() > 0)
            {
                nameBuilder.append(' ');
            }
            nameBuilder.append(person.getMiddleName());
        }
        if (person.getFamilyName() != null && !person.getFamilyName().isEmpty())
        {
            if (nameBuilder.length() > 0)
            {
                nameBuilder.append(' ');
            }
            nameBuilder.append(person.getFamilyName());
        }

        return nameBuilder.toString();
    }

    /**
     * @param person
     * @return
     */
    public static String extractRequestorAddress(Person person)
    {
        StringBuilder addressBuilder = new StringBuilder();

        List<PostalAddress> addresses = person.getAddresses();

        extractAddress(addressBuilder, addresses);

        return addressBuilder.toString();
    }

    /**
     * @param person
     * @return
     */
    public static String extractRequestorEmailAddress(Person person)
    {
        List<ContactMethod> contactMethods = person.getContactMethods();

        if (contactMethods != null && !contactMethods.isEmpty())
        {

            for (ContactMethod contactMethod : contactMethods)
            {

                // Is `email` the correct type? Is there a constant somewhere for the email contact method type?
                if (contactMethod.getType().equalsIgnoreCase("email"))
                {
                    return contactMethod.getValue();
                }

            }

        }
        else
        {
            if (person.getDefaultEmail() != null)
            {
                return person.getDefaultEmail().getValue();
            }
        }

        return "";
    }

    /**
     * @param person
     * @return
     */
    public static String extractRequestorOrganization(Person person)
    {

        List<Organization> organizations = person.getOrganizations();

        if (organizations != null && !organizations.isEmpty())
        {

            // Is it OK to just get the first organization, or we are supposed to search for a specific organization by
            // organization type?
            Organization organization = organizations.get(0);

            return organization.getOrganizationValue();

        }

        return "";
    }

    /**
     * @param person
     * @return
     */
    public static String extractRequestorOrganizationAddress(Person person)
    {
        StringBuilder organizationAddressBuilder = new StringBuilder();

        List<Organization> organizations = person.getOrganizations();

        if (organizations != null && !organizations.isEmpty())
        {

            // Is it OK to just get the first organization, or we are supposed to search for a specific organization by
            // organization type?
            Organization organization = organizations.get(0);

            List<PostalAddress> addresses = organization.getAddresses();

            extractAddress(organizationAddressBuilder, addresses);

        }

        return organizationAddressBuilder.toString();
    }

    /**
     * @param organizationAddressBuilder
     * @param addresses
     */
    private static void extractAddress(StringBuilder organizationAddressBuilder, List<PostalAddress> addresses)
    {
        if (addresses != null && !addresses.isEmpty())
        {

            // Is it OK to just get the first address, or we are supposed to search for a specific address by address
            // type?
            PostalAddress address = addresses.get(0);

            if (address.getStreetAddress() != null && !address.getStreetAddress().isEmpty())
            {
                organizationAddressBuilder.append(address.getStreetAddress());
            }
            if (address.getStreetAddress2() != null && !address.getStreetAddress2().isEmpty())
            {
                if (organizationAddressBuilder.length() > 0)
                {
                    organizationAddressBuilder.append(System.lineSeparator());
                }
                organizationAddressBuilder.append(address.getStreetAddress2());
            }
            if (address.getCity() != null && !address.getCity().isEmpty())
            {
                if (organizationAddressBuilder.length() > 0)
                {
                    organizationAddressBuilder.append(System.lineSeparator());
                }
                organizationAddressBuilder.append(address.getCity());
            }
            if (address.getState() != null && !address.getState().isEmpty())
            {
                if (organizationAddressBuilder.length() > 0)
                {
                    organizationAddressBuilder.append(System.lineSeparator());
                }
                organizationAddressBuilder.append(address.getState());
            }
            if (address.getZip() != null && !address.getZip().isEmpty())
            {
                if (organizationAddressBuilder.length() > 0)
                {
                    organizationAddressBuilder.append(System.lineSeparator());
                }
                organizationAddressBuilder.append(address.getZip());
            }
            if (address.getCountry() != null && !address.getCountry().isEmpty())
            {
                if (organizationAddressBuilder.length() > 0)
                {
                    organizationAddressBuilder.append(System.lineSeparator());
                }
                organizationAddressBuilder.append(address.getCountry());
            }

        }
    }

}
