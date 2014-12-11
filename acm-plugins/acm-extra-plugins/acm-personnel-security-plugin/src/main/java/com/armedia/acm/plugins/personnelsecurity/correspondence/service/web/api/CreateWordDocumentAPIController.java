package com.armedia.acm.plugins.personnelsecurity.correspondence.service.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.plugins.addressable.model.PostalAddress;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.complaint.dao.ComplaintDao;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.person.dao.PersonAssociationDao;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonAssociation;
import com.armedia.acm.plugins.person.model.PersonIdentification;
import com.armedia.acm.plugins.personnelsecurity.correspondence.service.exception.AcmPersonnelSecurityCorrespondenceException;
import com.armedia.acm.plugins.personnelsecurity.correspondence.service.model.CorrespondenceType;
import com.armedia.acm.plugins.personnelsecurity.correspondence.service.model.ObjectType;
import com.armedia.acm.plugins.personnelsecurity.correspondence.service.service.CreateWordDocFactory;
import com.armedia.acm.plugins.personnelsecurity.correspondence.service.service.WordDocFromTemplate;
import com.armedia.acm.plugins.task.exception.AcmTaskException;
import com.armedia.acm.plugins.task.model.AcmTask;
import com.armedia.acm.plugins.task.service.TaskDao;
import javax.servlet.http.HttpServletRequest;
import org.mule.api.MuleException;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by marjan.stefanoski on 08.12.2014.
 */

@Controller
@RequestMapping( { "/api/v1/plugin/personnelsecurity/correspondence", "/api/latest/plugin/personnelsecurity/correspondence"} )
public class CreateWordDocumentAPIController {

    private PersonAssociationDao personAssociationDao;
    private ComplaintDao complaintDao;
    private CaseFileDao caseFileDao;
    private TaskDao taskDao;

    private Person retFile = null;

    @RequestMapping(value = "/createDoc", method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE
    })
    @ResponseBody
    public Person createWordDoc(
            @RequestParam("objectId") Long objectId,
            @RequestParam("objectType") String objectType,
            @RequestHeader("correspondenceType") String correspondenceType,
            Authentication authentication,
            HttpServletRequest request) throws AcmCreateObjectFailedException, AcmObjectNotFoundException {

        String ecmFolderId = null;
        switch (ObjectType.getObjectType(objectType)){
            case COMPLAINT:
                Complaint complaint = getComplaintDao().find(objectId);
                if( complaint != null ) {
                    ecmFolderId = complaint.getEcmFolderId();
                } else {
                    //throw an ex
                }
                break;
            case CASE:
                CaseFile caseFile = getCaseFileDao().find(objectId);
                if(caseFile!=null) {
                    ecmFolderId = caseFile.getEcmFolderId();
                } else {
                    // throw an ex
                }
                break;
            case TASK:
                AcmTask task = null;
                try {
                     task = getTaskDao().findById(objectId);
                } catch (AcmTaskException e) {
                    e.printStackTrace();
                }
                if(task !=null) {
                    //For task where to find EcmFolderID???
                    //ecmFolderId = task.get
                } else {
                    //throw an ex
                }
                break;
            default:
                //throw an ex
                break;
        }

        CreateWordDocFactory createWordDocFactory = new CreateWordDocFactory();
        WordDocFromTemplate wordDocFromTemplate = createWordDocFactory.getWordCreator(
                CorrespondenceType.getCorrespondenceType(correspondenceType));

        try {
            wordDocFromTemplate.create(ObjectType.getObjectType(objectType), Long.toString(objectId),ecmFolderId, createMapForSubstitution(objectType,objectId),authentication,request);
        } catch (AcmPersonnelSecurityCorrespondenceException e) {
            e.printStackTrace();
        }
        return retFile;
    }

    private Map<String, String> createMapForSubstitution(String objectTypeAsString, Long objectId){
        List<Person> list = getPersonAssociationDao().findPersonByParentIdAndParentType(objectTypeAsString,objectId);
        Person thePerson = null;
        boolean isRelevantPerson = false;
        Map<String,String> subMap = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM  dd  yyyy");
        subMap.put("DATE",sdf.format(new Date()));
        for ( Person person:list ) {
            List<PersonAssociation> assList = person.getPersonAssociations();
            for( PersonAssociation ass: assList ) {
                if( ass.getParentType().equals(objectTypeAsString) && "Subject".equals(ass.getPersonType())){
                    isRelevantPerson = true;
                    thePerson = person;
                    retFile = person;
                    break;
                }
            }
            if( isRelevantPerson )
                break;
        }
        if( thePerson != null ) {
            List<PersonIdentification> personIdentifications = thePerson.getPersonIdentification();
            List<PostalAddress> postalAddresses = thePerson.getAddresses();
            PostalAddress address = null;
            if ( postalAddresses != null ){
                address = postalAddresses.get(0);
            }

            PersonIdentification employeeId = null;
            for(PersonIdentification identification: personIdentifications){
                if("EMPLOYEE_ID".equals(identification.getIdentificationType())){
                    employeeId = identification;
                    break;
                }
            }

            if( thePerson.getGivenName() != null && thePerson.getFamilyName() !=null ) {
                subMap.put("EMPLOYEE_NAME", thePerson.getGivenName() + " " + thePerson.getFamilyName());
            } else if(thePerson.getGivenName()!=null && thePerson.getFamilyName() ==null){
                subMap.put("EMPLOYEE_NAME", thePerson.getGivenName());
            } else if(thePerson.getGivenName()==null && thePerson.getFamilyName()!=null ) {
                subMap.put("EMPLOYEE_NAME", thePerson.getFamilyName());
            } else {
                //throw an exception
            }
            if ( employeeId!=null ) {
                subMap.put("EMPLOYEE_ID", employeeId.getIdentificationNumber());
            } else {
                //throw an exception
            }
            if ( address != null ) {
                String fullAddr = null;
                //add other cases when some fields are null
                if ( address.getStreetAddress() != null && address.getCity() != null && address.getZip() !=null && address.getState() !=null && address.getCountry() != null){
                   fullAddr = address.getStreetAddress() + " " + address.getZip() + " " + address.getCity() + " " + address.getState() + " " + address.getCountry();
                } else {
                    //throw an excetion
                }
                if( fullAddr!=null ){
                    subMap.put("CURRENT_LOCATION",fullAddr);
                }
            } else {
                //throw an exception
            }
        }
        return subMap;
    }

    public PersonAssociationDao getPersonAssociationDao() {
        return personAssociationDao;
    }

    public void setPersonAssociationDao(PersonAssociationDao personAssociationDao) {
        this.personAssociationDao = personAssociationDao;
    }

    public ComplaintDao getComplaintDao() {
        return complaintDao;
    }

    public void setComplaintDao(ComplaintDao complaintDao) {
        this.complaintDao = complaintDao;
    }

    public CaseFileDao getCaseFileDao() {
        return caseFileDao;
    }

    public void setCaseFileDao(CaseFileDao caseFileDao) {
        this.caseFileDao = caseFileDao;
    }

    public TaskDao getTaskDao() {
        return taskDao;
    }

    public void setTaskDao(TaskDao taskDao) {
        this.taskDao = taskDao;
    }
}
