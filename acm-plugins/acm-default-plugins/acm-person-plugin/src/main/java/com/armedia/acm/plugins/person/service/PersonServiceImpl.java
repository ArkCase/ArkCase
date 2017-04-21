/**
 *
 */
package com.armedia.acm.plugins.person.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmListObjectsFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.frevvo.config.FrevvoFormUtils;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Identification;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonConstants;
import com.armedia.acm.plugins.person.model.xml.FrevvoPerson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author riste.tutureski
 * @author nebojsha.davidovikj
 */
public class PersonServiceImpl implements PersonService
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private PersonDao personDao;
    /**
     * Root folder for all People
     */
    private String peopleRootFolder = null;
    /**
     * folder where pictures will be kept
     */
    private String picturesFolder = null;
    /**
     * Person own folder which contains spel expression to generate folder name
     */
    private String personOwnFolder = null;
    private EcmFileService ecmFileService;
    private AcmFolderService acmFolderService;

    @Override
    public Person get(Long id)
    {
        return getPersonDao().find(id);
    }

    @Override
    public Person addPersonIdentification(String key, String value, Person person)
    {
        if (person != null)
        {
            if (key != null && value != null && !value.trim().isEmpty())
            {
                boolean exists = false;
                if (person.getIdentifications() != null)
                {
                    for (Identification pi : person.getIdentifications())
                    {
                        if (key.equals(pi.getIdentificationType()))
                        {
                            pi.setIdentificationNumber(value);
                            exists = true;
                            break;
                        }
                    }
                }

                if (!exists)
                {
                    if (person.getIdentifications() == null)
                    {
                        person.setIdentifications(new ArrayList<Identification>());
                    }

                    Identification pi = new Identification();
                    pi.setIdentificationNumber(value);
                    pi.setIdentificationType(key);


                    person.getIdentifications().add(pi);
                }
            }
        }

        return person;
    }

    @Override
    public Person addPersonIdentifications(List<String> keys, Person person)
    {
        if (keys != null && person != null)
        {
            for (String key : keys)
            {
                String value = FrevvoFormUtils.get(person, key);

                Person personFromDatabase = new Person();
                if (person.getId() != null)
                {
                    personFromDatabase = get(person.getId());
                }
                personFromDatabase = addPersonIdentification(key, value, personFromDatabase);

                person.setIdentifications(personFromDatabase.getIdentifications());
            }
        }

        return person;
    }

    @Override
    public Person setPersonIdentifications(List<Identification> identifications, Person person)
    {
        if (identifications != null && person != null)
        {
            for (Identification identification : identifications)
            {
                try
                {
                    String key = identification.getIdentificationType();
                    String value = identification.getIdentificationNumber();

                    person = (Person) FrevvoFormUtils.set(person, key, value);
                } catch (Exception e)
                {
                    log.debug("Silent catch of exception while setting value of the property in the object Person. The property name maybe not exist, but execution should go forward.");
                }
            }
        }

        return person;
    }

    @Override
    public String getPersonType(FrevvoPerson person)
    {
        if (person != null)
        {
            return person.getType();
        }

        return null;
    }

    @Override
    public List<String> getPersonIdentificationKeys(FrevvoPerson person)
    {
        if (person != null)
        {
            return person.getPersonIdentificationKeys();
        }

        return null;
    }

    @Override
    public void deleteImageForPerson(Long personId, Long imageId) throws AcmObjectNotFoundException, AcmUserActionFailedException
    {
        Person person = get(personId);
        Objects.requireNonNull(person, "Person must not be null");
        if (person.getDefaultPictureId() == imageId)
        {
            throw new AcmUserActionFailedException("Delete picture", "FILE", imageId, "Can't delete picture which is already set as default.", null);
        }
        ecmFileService.deleteFile(imageId);
    }

    @Override
    public EcmFile insertImageForPerson(Long personId, MultipartFile image, boolean isDefault, Authentication auth) throws IOException, AcmUserActionFailedException, AcmCreateObjectFailedException
    {
        Person person = personDao.find(personId);
        Objects.requireNonNull(person, "Person not found.");
        AcmFolder picturesFolderObj = acmFolderService.findByNameAndParent(picturesFolder, person.getContainer().getFolder());
        Objects.requireNonNull(picturesFolderObj, "Pictures folder not found.");
        try
        {
            List objects = ecmFileService.allFilesForFolder(auth, person.getContainer(), picturesFolderObj.getId()).getChildren();
            if (objects.size() < 1)
            {
                //there are not images in pictures folder, so first uploaded image is the default image
                isDefault = true;
            }
        } catch (AcmListObjectsFailedException e)
        {
            log.error("Could't list files for folder [{}].", picturesFolderObj.getId(), e);
        }

        EcmFile uploaded = ecmFileService.upload(image.getOriginalFilename(),
                PersonConstants.PERSON_PICTURE_FILE_TYPE,
                PersonConstants.PERSON_PICTURE_CATEGORY,
                image.getInputStream(),
                image.getContentType(),
                image.getOriginalFilename(),
                auth,
                picturesFolderObj.getCmisFolderId(),
                PersonConstants.PERSON_OBJECT_TYPE,
                personId);
        if (isDefault)
        {
            person.setDefaultPictureId(uploaded.getId());
            savePerson(person, auth);
        }

        return uploaded;
    }

    @Override
    public Person createPerson(Person person, Authentication auth) throws AcmCreateObjectFailedException, AcmObjectNotFoundException, AcmUserActionFailedException
    {
        //save person
        person = savePerson(person, auth);

        //generate person root folder
        String personRootFolderName = getPersonRootFolderName(person);


        //created container and add root folder
        AcmContainer container = new AcmContainer();
        container.setContainerObjectType(PersonConstants.PERSON_OBJECT_TYPE);
        container.setContainerObjectId(person.getId());
        container.setContainerObjectTitle(person.getGivenName() + "-" + person.getFamilyName() + "-" + person.getId());
        AcmFolder folder = new AcmFolder();
        folder.setName("ROOT");

        String cmisFolderId = ecmFileService.createFolder(peopleRootFolder + personRootFolderName);
        folder.setCmisFolderId(cmisFolderId);

        container.setFolder(folder);
        container.setAttachmentFolder(folder);

        person.setContainer(container);

        person = savePerson(person, auth);

        //create Pictures folder
        acmFolderService.addNewFolder(person.getContainer().getFolder(), picturesFolder);

        return person;
    }

    private String getPersonRootFolderName(Person person)
    {
        ExpressionParser ep = new SpelExpressionParser();
        Expression exp = ep.parseExpression(personOwnFolder);
        EvaluationContext ec = new StandardEvaluationContext();
        return exp.getValue(ec, person, String.class);
    }

    @Override
    public Person savePerson(Person person, Authentication authentication)
    {
        return personDao.save(person);
    }

    public PersonDao getPersonDao()
    {
        return personDao;
    }

    public void setPersonDao(PersonDao personDao)
    {
        this.personDao = personDao;
    }

    public void setPeopleRootFolder(String peopleRootFolder)
    {
        this.peopleRootFolder = peopleRootFolder;
    }

    public void setPicturesFolder(String picturesFolder)
    {
        this.picturesFolder = picturesFolder;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    public void setPersonOwnFolder(String personOwnFolder)
    {
        this.personOwnFolder = personOwnFolder;
    }

    public void setAcmFolderService(AcmFolderService acmFolderService)
    {
        this.acmFolderService = acmFolderService;
    }
}
