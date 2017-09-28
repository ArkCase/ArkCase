package com.armedia.acm.plugins.person.service;

import com.armedia.acm.objectdiff.*;
import com.armedia.acm.plugins.addressable.model.ContactMethod;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.person.model.Identification;
import com.armedia.acm.plugins.person.model.IdentificationConstants;
import com.armedia.acm.plugins.person.model.Person;
import org.apache.commons.io.IOUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PersonDiff {

    private AcmObjectDiffUtils acmObjectChangeUtils;

    /**
     * compares two persons are return diff
     *
     * @param oldPerson
     * @param updatedPerson
     * @return
     */
    public AcmObjectChange compare(Person oldPerson, Person updatedPerson) {
        if (!acmObjectChangeUtils.idMatches(oldPerson, updatedPerson)) {
            throw new IllegalArgumentException("oldPerson don't have same id as updatedPerson");
        }

        //check changes in person properties which are primitive types or their wrappers


        AcmObjectChange objectChange =
                acmObjectChangeUtils.getObjectChange(null, oldPerson, updatedPerson);


        return objectChange;
    }

    public void setAcmObjectChangeUtils(AcmObjectDiffUtils acmObjectChangeUtils) {
        this.acmObjectChangeUtils = acmObjectChangeUtils;
    }
}
