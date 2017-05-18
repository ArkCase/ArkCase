package com.armedia.acm.services.email.service;

import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Mar 27, 2017
 *
 */
public interface AcmAddressBook
{

    List<AcmAddressBookItem> getContacts(String sort, String sortDirection, int start, int maxItems);

}
