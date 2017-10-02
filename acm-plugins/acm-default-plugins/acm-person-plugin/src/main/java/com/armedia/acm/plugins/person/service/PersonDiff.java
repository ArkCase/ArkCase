package com.armedia.acm.plugins.person.service;

import com.armedia.acm.objectdiff.model.AcmObjectModified;
import com.armedia.acm.objectdiff.service.AcmObjectDiffUtils;
import com.armedia.acm.plugins.person.model.Person;

public class PersonDiff {

    private AcmObjectDiffUtils acmObjectChangeUtils;

    /**
     * compares two persons are return diff
     *
     * @param oldPerson
     * @param updatedPerson
     * @return
     */
    public AcmObjectModified compare(Person oldPerson, Person updatedPerson) {
        if (!acmObjectChangeUtils.idMatches(oldPerson, updatedPerson)) {
            throw new IllegalArgumentException("oldPerson don't have same id as updatedPerson");
        }

        //check changes in person properties which are primitive types or their wrappers


        AcmObjectModified objectChange =
                acmObjectChangeUtils.getObjectChange(null, oldPerson, updatedPerson);


        return objectChange;
    }

    public void setAcmObjectChangeUtils(AcmObjectDiffUtils acmObjectChangeUtils) {
        this.acmObjectChangeUtils = acmObjectChangeUtils;
    }
}
