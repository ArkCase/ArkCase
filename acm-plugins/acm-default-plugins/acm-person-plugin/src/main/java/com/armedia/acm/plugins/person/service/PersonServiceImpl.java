/**
 *
 */
package com.armedia.acm.plugins.person.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.frevvo.config.FrevvoFormUtils;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Identification;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonOrganizationAssociation;
import com.armedia.acm.plugins.person.model.PersonOrganizationConstants;
import com.armedia.acm.plugins.person.model.xml.FrevvoPerson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
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
    private FolderAndFilesUtils folderAndFilesUtils;
    private PersonEventPublisher personEventPublisher;

    @Override
    public Person get(Long id)
    {
        Person person = getPersonDao().find(id);
        if (person != null)
        {
            getPersonEventPublisher().publishPersonViewedEvent(person, true);
        }
        return person;
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
                        person.setIdentifications(new ArrayList<>());
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
                }
                catch (Exception e)
                {
                    log.debug(
                            "Silent catch of exception while setting value of the property in the object Person. The property name maybe not exist, but execution should go forward.");
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
        Objects.requireNonNull(person.getDefaultPicture(), "default Image must not be null");
        Objects.requireNonNull(person.getDefaultPicture().getId(), "default Image id must not be null");
        Objects.requireNonNull(imageId, "imageId must not be null");

        if (person.getDefaultPicture().getId().equals(imageId))
        {
            throw new AcmUserActionFailedException("Delete picture", "FILE", imageId,
                    "Can't delete picture which is already set as default.", null);
        }
        ecmFileService.deleteFile(imageId);
    }

    @Override
    public EcmFile insertImageForPerson(Person person, MultipartFile image, boolean isDefault, String description, Authentication auth)
            throws IOException, AcmUserActionFailedException, AcmCreateObjectFailedException, AcmUpdateObjectFailedException,
            AcmObjectNotFoundException
    {
        Objects.requireNonNull(person, "Person not found.");
        if (person.getContainer() == null)
        {
            person = createContainerAndPictureFolder(person, auth);
        }
        AcmFolder picturesFolderObj = acmFolderService.findByNameAndParent(picturesFolder, person.getContainer().getFolder());
        Objects.requireNonNull(picturesFolderObj, "Pictures folder not found.");

        EcmFile uploaded = ecmFileService.upload(image.getOriginalFilename(), PersonOrganizationConstants.PERSON_PICTURE_FILE_TYPE,
                PersonOrganizationConstants.PERSON_PICTURE_CATEGORY, image.getInputStream(), image.getContentType(),
                image.getOriginalFilename(), auth, picturesFolderObj.getCmisFolderId(), PersonOrganizationConstants.PERSON_OBJECT_TYPE,
                person.getId());

        uploaded.setDescription(description);
        uploaded = ecmFileService.updateFile(uploaded);

        if (isDefault)
        {
            person.setDefaultPicture(uploaded);
            savePerson(person, auth);
        }
        getPersonEventPublisher().publishPersonImageEvent(person, true);
        return uploaded;
    }

    @Override
    @Transactional
    public EcmFile saveImageForPerson(Long personId, MultipartFile image, boolean isDefault, EcmFile metadata, Authentication auth)
            throws IOException, AcmUserActionFailedException, AcmCreateObjectFailedException, AcmUpdateObjectFailedException,
            AcmObjectNotFoundException
    {
        Person person = personDao.find(personId);
        Objects.requireNonNull(person, "Person not found.");

        AcmFolder picturesFolderObj = acmFolderService.findByNameAndParent(picturesFolder, person.getContainer().getFolder());
        Objects.requireNonNull(picturesFolderObj, "Pictures folder not found.");

        EcmFile uploaded;

        if (image != null)
        {
            String fileName = image.getOriginalFilename();
            String uniqueFileName = folderAndFilesUtils.createUniqueIdentificator(fileName);

            metadata.setFileName(fileName);
            uploaded = ecmFileService.upload(auth, PersonOrganizationConstants.PERSON_OBJECT_TYPE, personId,
                    picturesFolderObj.getCmisFolderId(), uniqueFileName, image.getInputStream(), metadata);
        }
        else
        {
            uploaded = ecmFileService.updateFile(metadata);
        }

        if (isDefault)
        {
            person.setDefaultPicture(uploaded);
            savePerson(person, auth);
        }
        getPersonEventPublisher().publishPersonImageEvent(person, true);
        return uploaded;
    }

    protected Person createContainerAndPictureFolder(Person person, Authentication auth)
            throws AcmCreateObjectFailedException, AcmUserActionFailedException, AcmObjectNotFoundException
    {
        // generate person root folder
        String personRootFolderName = getPersonRootFolderName(person);

        // created container and add root folder
        AcmContainer container = new AcmContainer();
        container.setContainerObjectType(PersonOrganizationConstants.PERSON_OBJECT_TYPE);
        container.setContainerObjectId(person.getId());
        container.setContainerObjectTitle(person.getGivenName() + "-" + person.getFamilyName() + "-" + person.getId());
        AcmFolder folder = new AcmFolder();
        folder.setName("ROOT");

        String cmisFolderId = ecmFileService.createFolder(peopleRootFolder + personRootFolderName);
        folder.setCmisFolderId(cmisFolderId);

        container.setFolder(folder);
        container.setAttachmentFolder(folder);

        person.setContainer(container);

        person = personDao.save(person);

        // create Pictures folder
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
    public Person savePerson(Person in, Authentication authentication)
            throws AcmObjectNotFoundException, AcmCreateObjectFailedException, AcmUpdateObjectFailedException, AcmUserActionFailedException
    {
        validateOrganizationAssociations(in);
        boolean isNew = in.getId() == null;
        Person person = personDao.save(in);
        getPersonEventPublisher().publishPersonUpsertEvent(person, isNew, true);
        return person;
    }

    /**
     * Validates the {@link PersonOrganizationAssociation}.
     *
     * @param person
     *            the {@link Person} to validate
     * @throws AcmCreateObjectFailedException
     *             when at least one of the {@link PersonOrganizationAssociation} is not valid.
     * @throws AcmDuplicatePersonAssociationException
     *             when at least one of the {@link PersonOrganizationAssociation} is not valid.
     */
    private void validateOrganizationAssociations(Person person) throws AcmCreateObjectFailedException, AcmUpdateObjectFailedException
    {
        for (PersonOrganizationAssociation association : person.getOrganizationAssociations())
        {
            for (PersonOrganizationAssociation otherAssociation : person.getOrganizationAssociations())
            {
                if (!association.equals(otherAssociation))
                {
                    if (association.getOrganization().getId().equals(otherAssociation.getOrganization().getId()))
                    {
                        String errorMessage = null;
                        if ((association.getOrganizationToPersonAssociationType() == null)
                                || (association.getPersonToOrganizationAssociationType() == null))
                        {
                            errorMessage = "Organization to person association must have a type";
                        }

                        if ((errorMessage == null) && (association.getOrganizationToPersonAssociationType()
                                .equals(otherAssociation.getOrganizationToPersonAssociationType())))
                        {
                            errorMessage = "Duplicate organization to person relation type";
                        }

                        if (errorMessage != null)
                        {
                            if (person.getId() == null)
                            {
                                throw new AcmCreateObjectFailedException("Person", errorMessage, null);
                            }
                            else
                            {
                                throw new AcmUpdateObjectFailedException("Person", person.getId(), errorMessage, null);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * save person data
     *
     * @param person
     *            person data
     * @param pictures
     *            person pictures
     * @param authentication
     *            authentication
     * @return Person saved person
     */
    @Override
    public Person savePerson(Person person, List<MultipartFile> pictures, Authentication authentication)
            throws AcmUserActionFailedException, AcmCreateObjectFailedException, AcmUpdateObjectFailedException, AcmObjectNotFoundException
    {
        Person savedPerson = savePerson(person, authentication);
        return uploadPicturesForPerson(savedPerson, pictures, authentication);
    }

    private Person uploadPicturesForPerson(Person person, List<MultipartFile> pictures, Authentication authentication)
            throws AcmCreateObjectFailedException, AcmUpdateObjectFailedException, AcmUserActionFailedException
    {
        if (pictures != null)
        {
            boolean hasDefaultPicture = person.getDefaultPicture() != null;
            for (MultipartFile picture : pictures)
            {
                // TODO we need to send description from front end
                String description = "";
                try
                {
                    insertImageForPerson(person, picture, !hasDefaultPicture, description, authentication);
                    if (!hasDefaultPicture)
                    {
                        hasDefaultPicture = true;
                    }
                }
                catch (IOException e)
                {
                    log.error("Error uploading picture [{}] to person id [{}]", picture, person.getId());
                }
                catch (AcmObjectNotFoundException e)
                {
                    log.error("Error uploading picture [{}] to person id [{}]", picture, person.getId());
                }
            }
        }
        // because there are updates to the person we need to get fresh instance from database.
        return person;
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

    public void setFolderAndFilesUtils(FolderAndFilesUtils folderAndFilesUtils)
    {
        this.folderAndFilesUtils = folderAndFilesUtils;
    }

    /**
     * @return the personEventPublisher
     */
    public PersonEventPublisher getPersonEventPublisher()
    {
        return personEventPublisher;
    }

    /**
     * @param personEventPublisher
     *            the personEventPublisher to set
     */
    public void setPersonEventPublisher(PersonEventPublisher personEventPublisher)
    {
        this.personEventPublisher = personEventPublisher;
    }
}
