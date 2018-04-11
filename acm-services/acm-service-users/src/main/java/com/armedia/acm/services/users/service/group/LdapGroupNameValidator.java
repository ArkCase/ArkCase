package com.armedia.acm.services.users.service.group;


import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.group.AcmGroupType;
import com.armedia.acm.services.users.model.group.LdapGroupNameValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LdapGroupNameValidator implements ConstraintValidator<LdapGroupNameValidation, AcmGroup> {


    @Override
    public void initialize(LdapGroupNameValidation ldapGroupNameValidation) {

    }

    @Override
    public boolean isValid(AcmGroup acmGroup, ConstraintValidatorContext context) {

        if(acmGroup == null){
            return false;
        }

        AcmGroupType groupType = acmGroup.getType();
        String groupName = acmGroup.getName();

        if(groupType == null){
            return false;
        }

        if(groupType.equals(AcmGroupType.LDAP_GROUP))
        {
            if(groupName.length() <= 64){
                return true;
            }
            else{
                return false;
            }
        }
        else if(groupType.equals(AcmGroupType.ADHOC_GROUP))
        {
            return true;
        }
        return false;
    }
}
