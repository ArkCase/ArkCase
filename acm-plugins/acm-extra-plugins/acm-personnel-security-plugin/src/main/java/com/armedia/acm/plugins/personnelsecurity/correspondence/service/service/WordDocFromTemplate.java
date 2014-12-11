package com.armedia.acm.plugins.personnelsecurity.correspondence.service.service;


import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.personnelsecurity.correspondence.service.exception.AcmPersonnelSecurityCorrespondenceException;
import com.armedia.acm.plugins.personnelsecurity.correspondence.service.model.ObjectType;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 08.12.2014.
 */
public interface WordDocFromTemplate {
    void create(ObjectType objectType, String objectId,String ecmFolderId, Map<String,String> mapForSubstitution, Authentication auth, HttpServletRequest request) throws AcmPersonnelSecurityCorrespondenceException;
}
