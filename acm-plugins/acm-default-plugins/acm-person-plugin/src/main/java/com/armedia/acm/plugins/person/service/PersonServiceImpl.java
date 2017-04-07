/**
 *
 */
package com.armedia.acm.plugins.person.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
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

/**
 * @author riste.tutureski
 */
public class PersonServiceImpl implements PersonService
{

    private Logger LOG = LoggerFactory.getLogger(getClass());

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
                    LOG.debug("Silent catch of exeption while setting value of the property in the object Person. The property name maybe not exist, but execution should go forward.");
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
    public boolean deleteImageForPerson(Long personId, Long imageId)
    {
        return false;
    }

    @Override
    public EcmFile insertImageForPerson(Long personId, MultipartFile image, Authentication auth) throws IOException, AcmUserActionFailedException, AcmCreateObjectFailedException
    {
        Person person = personDao.find(personId);


        AcmFolder picturesFolderObj = acmFolderService.findByNameAndParent(picturesFolder, person.getContainer().getFolder());


        return ecmFileService.upload(image.getOriginalFilename(),
                "image",
                "Document",
                image.getInputStream(),
                image.getContentType(),
                image.getOriginalFilename(),
                auth,
                picturesFolderObj.getCmisFolderId(),
                PersonConstants.PERSON_OBJECT_TYPE,
                personId);
    }

    @Override
    public Person createPerson(Person person, Authentication auth) throws AcmCreateObjectFailedException, AcmObjectNotFoundException, AcmUserActionFailedException
    {
        //save person
        person = personDao.save(person);

        //generate person root folder
        ExpressionParser ep = new SpelExpressionParser();
        Expression exp = ep.parseExpression(personOwnFolder);
        EvaluationContext ec = new StandardEvaluationContext();
        String personRootFolderName = exp.getValue(ec, person, String.class);


        //created container and add root folder
        AcmContainer container = new AcmContainer();
        container.setContainerObjectType(PersonConstants.PERSON_OBJECT_TYPE);
        container.setContainerObjectId(person.getId());
        container.setContainerObjectTitle(person.getTitle());
        AcmFolder folder = new AcmFolder();
        folder.setName("ROOT");

        String cmisFolderId = ecmFileService.createFolder(peopleRootFolder + personRootFolderName);
        folder.setCmisFolderId(cmisFolderId);

        container.setFolder(folder);
        container.setAttachmentFolder(folder);

        person.setContainer(container);

        person = personDao.save(person);

        //create Pictures folder
        acmFolderService.addNewFolder(person.getContainer().getFolder(), picturesFolder);

        return person;
    }

    @Override
    public Person savePerson(Person person)
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
