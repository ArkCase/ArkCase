/**
 *
 */
package gov.foia.model;

import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;

import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Aug 18, 2016
 */
public class FOIARequestUtils
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
