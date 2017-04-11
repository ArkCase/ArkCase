package com.armedia.acm.plugins.person.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.person.model.Identification;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.xml.FrevvoPerson;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface PersonService
{

    Person get(Long id);

    /**
     * Add person identification to the Person object for given key (field name) and value
     *
     * @param key
     * @param value
     * @param person
     * @return
     */
    Person addPersonIdentification(String key, String value, Person person);

    /**
     * Add person identifications for given list of keys (field names). The values are taken from Frevvo Person object
     * using reflection
     *
     * @param keys
     * @param person
     * @return
     */
    Person addPersonIdentifications(List<String> keys, Person person);

    /**
     * This method will set all person identifications values in the appropriate field in Person object using reflection
     *
     * @param identifications
     * @param person
     */
    Person setPersonIdentifications(List<Identification> identifications, Person person);

    /**
     * The method will return the type of the person created via Frevvo form.
     *
     * @param person
     * @return
     */
    String getPersonType(FrevvoPerson person);

    /**
     * This method will return the list of person identification keys
     *
     * @param person
     * @return
     */
    List<String> getPersonIdentificationKeys(FrevvoPerson person);

    /**
     * delete existing image for given Person. If default image in person is same as we want to delete, than operation throws an exception.
     *
     * @param personId id of the Person
     * @param imageId  id of the image
     */
    @Transactional
    void deleteImageForPerson(Long personId, Long imageId) throws AcmObjectNotFoundException, AcmUserActionFailedException;

    /**
     * insert image for a person. If is the only image than is set as default image for the person
     *
     * @param personId  Long personId
     * @param image     MultipartFile image
     * @param isDefault boolean should this picture be set as default
     * @param auth      Authentication authentication
     * @return boolean true if successfully inserted
     */
    @Transactional
    EcmFile insertImageForPerson(Long personId, MultipartFile image, boolean isDefault, Authentication auth) throws IOException, AcmUserActionFailedException, AcmCreateObjectFailedException;

    /**
     * Creates new Person and persists in database
     *
     * @param person Person data
     * @param auth   Authentication
     * @return Person created Person
     */
    @Transactional
    Person createPerson(Person person, Authentication auth) throws AcmCreateObjectFailedException, AcmObjectNotFoundException, AcmUserActionFailedException;

    /**
     * save person data
     *
     * @param person         person data
     * @param authentication authentication
     * @return Person saved person
     */
    @Transactional
    Person savePerson(Person person, Authentication authentication);
}
