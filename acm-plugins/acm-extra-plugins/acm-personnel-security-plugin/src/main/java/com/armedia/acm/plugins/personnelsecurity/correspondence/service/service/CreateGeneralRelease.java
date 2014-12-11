package com.armedia.acm.plugins.personnelsecurity.correspondence.service.service;


import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.correspondence.utils.SubstituteText;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.personnelsecurity.correspondence.service.model.ObjectType;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by marjan.stefanoski on 08.12.2014.
 */
public class CreateGeneralRelease implements WordDocFromTemplate {

    private final String wordTemplate ="General Release.docx";

    @Override
    public void create(ObjectType objectType, String objectId, String ecmFolderId, Map<String, String> mapForSubstitution, Authentication auth, HttpServletRequest request) {
        UploadWordDocument uploadWordDocument = new UploadWordDocument();
        SubstituteText substituteText = new SubstituteText();

        String fileDiskPath="";
        try {
            substituteText.substitute(mapForSubstitution,wordTemplate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            fileDiskPath="C:\\Users\\marjan.stefanoski\\Downloads\\" + wordTemplate;
            uploadWordDocument.uploadNewCreatedWordDoc(objectType.getObjectType(),objectId, ecmFolderId, fileDiskPath,auth,request);
        } catch (AcmCreateObjectFailedException e) {
            e.printStackTrace();
        }

    }



}
