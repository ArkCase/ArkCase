package com.armedia.acm.plugins.person.service;

/*-
 * #%L
 * ACM Default Plugin: Person
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.objectonverter.ObjectConverter;
import com.armedia.acm.plugins.addressable.exceptions.AcmContactMethodValidationException;
import com.armedia.acm.plugins.addressable.service.ContactMethodsUtil;
import com.armedia.acm.plugins.addressable.service.PhoneRegexConfig;
import com.armedia.acm.plugins.ecm.exception.AcmFileTypesException;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.ecm.service.EcmTikaFileService;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.plugins.ecm.service.impl.EcmTikaFile;
import com.armedia.acm.plugins.ecm.utils.FolderAndFilesUtils;
import com.armedia.acm.plugins.ecm.utils.ReflectionMethodsUtils;
import com.armedia.acm.plugins.person.dao.PersonDao;
import com.armedia.acm.plugins.person.model.Identification;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.person.model.PersonConfig;
import com.armedia.acm.plugins.person.model.PersonOrganizationAssociation;
import com.armedia.acm.plugins.person.model.PersonOrganizationConstants;
import com.armedia.acm.plugins.person.model.xml.FrevvoPerson;
import com.armedia.acm.plugins.person.pipeline.PersonPipelineContext;
import com.armedia.acm.services.pipeline.PipelineManager;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.SolrCore;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author riste.tutureski
 * @author nebojsha.davidovikj
 */
public class PersonServiceImpl implements PersonService
{
    private Logger log = LogManager.getLogger(getClass());

    private PipelineManager<Person, PersonPipelineContext> personPipelineManager;
    private PersonDao personDao;
    private EcmFileParticipantService fileParticipantService;
    private EcmFileService ecmFileService;
    private AcmFolderService acmFolderService;
    private FolderAndFilesUtils folderAndFilesUtils;
    private PersonEventPublisher personEventPublisher;
    private ObjectConverter objectConverter;
    private EcmTikaFileService ecmTikaFileService;
    private PersonConfig personConfig;
    private PhoneRegexConfig phoneRegexConfig;
    private ExecuteSolrQuery executeSolrQuery;

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
                String value = ReflectionMethodsUtils.get(person, key);

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

                    person = (Person) ReflectionMethodsUtils.set(person, key, value);
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
    public EcmFile changeDescriptionForImage(Long personId, Long imageId, Boolean isDefault, String imageDescription, Authentication auth)
            throws AcmObjectNotFoundException, AcmUpdateObjectFailedException, PipelineProcessException, AcmCreateObjectFailedException
    {
        EcmFile imageFile = ecmFileService.findById(imageId);
        if (imageFile == null)
        {
            throw new AcmObjectNotFoundException("Image", imageId, "Image not found with ID:[{}];");
        }
        if (isDefault)
        {
            Person person = get(personId);
            Objects.requireNonNull(person, "Person not found.");
            person.setDefaultPicture(imageFile);
            savePerson(person, auth);
        }
        imageFile.setDescription(imageDescription);
        return ecmFileService.updateFile(imageFile);
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
            AcmObjectNotFoundException, PipelineProcessException, AcmFileTypesException
    {
        Objects.requireNonNull(person, "Person not found.");
        if (person.getContainer() == null)
        {
            person = createContainerAndPictureFolder(person, auth);
        }
        AcmFolder picturesFolderObj = acmFolderService.findByNameAndParent(personConfig.getPicturesFolder(),
                person.getContainer().getFolder());
        Objects.requireNonNull(picturesFolderObj, "Pictures folder not found.");

        File pictureFile = null;
        try
        {
            pictureFile = File.createTempFile("arkcase-insert-image-for-person-", null);
            FileUtils.copyInputStreamToFile(image.getInputStream(), pictureFile);
            EcmTikaFile ecmTikaFile = ecmTikaFileService.detectFileUsingTika(pictureFile, image.getName());
            if (!ecmTikaFile.getContentType().startsWith("image"))
            {
                throw new AcmFileTypesException("File is not a type of an image, got " + ecmTikaFile.getContentType());
            }
        }
        catch (SAXException | TikaException e)
        {
            throw new AcmFileTypesException("Error parsing contentType", e);
        }
        finally
        {
            FileUtils.deleteQuietly(pictureFile);
        }

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
    public EcmFile insertImageForPortalPerson(Person person, MultipartFile image, String imageContentType, Authentication auth)
            throws IOException, AcmUserActionFailedException, AcmCreateObjectFailedException, AcmUpdateObjectFailedException,
            AcmObjectNotFoundException, PipelineProcessException, AcmFileTypesException
    {
        Objects.requireNonNull(person, "Person not found.");
        if (person.getContainer() == null)
        {
            person = createContainerAndPictureFolder(person, auth);
        }
        AcmFolder picturesFolderObj = acmFolderService.findByNameAndParent(personConfig.getPicturesFolder(),
                person.getContainer().getFolder());
        Objects.requireNonNull(picturesFolderObj, "Pictures folder not found.");

        File pictureFile = null;
        try
        {
            pictureFile = File.createTempFile("arkcase-insert-image-for-person-", null);
            FileUtils.copyInputStreamToFile(image.getInputStream(), pictureFile);
            EcmTikaFile ecmTikaFile = ecmTikaFileService.detectFileUsingTika(pictureFile, image.getName());
            if (!ecmTikaFile.getContentType().startsWith("image"))
            {
                throw new AcmFileTypesException("File is not a type of an image, got " + ecmTikaFile.getContentType());
            }
            String fileName = image.getOriginalFilename();
            String uniqueFileName = folderAndFilesUtils.createUniqueIdentificator(fileName);

            EcmFile uploaded = ecmFileService.upload(image.getOriginalFilename(), PersonOrganizationConstants.PERSON_PICTURE_FILE_TYPE,
                    PersonOrganizationConstants.PERSON_PICTURE_CATEGORY, image.getInputStream(), ecmTikaFile.getContentType(),
                    uniqueFileName, auth, picturesFolderObj.getCmisFolderId(), PersonOrganizationConstants.PERSON_OBJECT_TYPE,
                    person.getId());

            uploaded = ecmFileService.updateFile(uploaded);

            person.setDefaultPicture(uploaded);
            savePerson(person, auth);

            return uploaded;
        }
        catch (SAXException | TikaException e)
        {
            throw new AcmFileTypesException("Error parsing contentType", e);
        }
        finally
        {
            FileUtils.deleteQuietly(pictureFile);
        }

    }

    @Override
    @Transactional
    public EcmFile saveImageForPerson(Long personId, MultipartFile image, boolean isDefault, EcmFile metadata, Authentication auth)
            throws IOException, AcmCreateObjectFailedException, AcmUpdateObjectFailedException,
            PipelineProcessException, AcmFileTypesException
    {
        Person person = personDao.find(personId);
        Objects.requireNonNull(person, "Person not found.");

        AcmFolder picturesFolderObj = acmFolderService.findByNameAndParent(personConfig.getPicturesFolder(),
                person.getContainer().getFolder());
        Objects.requireNonNull(picturesFolderObj, "Pictures folder not found.");

        File pictureFile = null;
        try
        {
            pictureFile = File.createTempFile("arkcase-update-image-for-person-", null);
            FileUtils.copyInputStreamToFile(image.getInputStream(), pictureFile);
            EcmTikaFile ecmTikaFile = ecmTikaFileService.detectFileUsingTika(pictureFile, image.getName());
            if (!ecmTikaFile.getContentType().startsWith("image"))
            {
                throw new AcmFileTypesException("File is not of type image, got " + ecmTikaFile.getContentType());
            }
        }
        catch (SAXException | TikaException e)
        {
            throw new AcmFileTypesException("Error parsing contentType", e);
        }
        finally
        {
            FileUtils.deleteQuietly(pictureFile);
        }

        String fileName = image.getOriginalFilename();
        metadata.setFileName(fileName);
        EcmFile uploaded = ecmFileService.update(metadata, image, auth);

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
        folder.setParticipants(getFileParticipantService().getFolderParticipantsFromAssignedObject(person.getParticipants()));

        String cmisFolderId = ecmFileService.createFolder(personConfig.getFolderRoot() + personRootFolderName);
        folder.setCmisFolderId(cmisFolderId);

        container.setFolder(folder);
        container.setAttachmentFolder(folder);

        person.setContainer(container);

        person = personDao.save(person);

        // create Pictures folder
        acmFolderService.addNewFolder(person.getContainer().getFolder(), personConfig.getPicturesFolder());
        return person;
    }

    private String getPersonRootFolderName(Person person)
    {
        ExpressionParser ep = new SpelExpressionParser();
        Expression exp = ep.parseExpression(personConfig.getFolderOwnSpel());
        EvaluationContext ec = new StandardEvaluationContext();
        return exp.getValue(ec, person, String.class);
    }

    @Override
    public Person savePerson(Person in, Authentication authentication) throws AcmCreateObjectFailedException,
            AcmUpdateObjectFailedException, PipelineProcessException
    {
        validateOrganizationAssociations(in);
        try
        {
            ContactMethodsUtil.validateContactMethodFields(in.getContactMethods(), phoneRegexConfig);
        }
        catch (AcmContactMethodValidationException e)
        {
            if (in.getId() == null)
            {
                throw new AcmCreateObjectFailedException("Person", e.toString(), null);
            }
            else
            {
                throw new AcmUpdateObjectFailedException("Person", in.getId(), e.toString(), null);
            }

        }

        PersonPipelineContext pipelineContext = new PersonPipelineContext();
        // populate the context
        pipelineContext.setNewPerson(in.getId() == null);
        pipelineContext.setAuthentication(authentication);

        return personPipelineManager.executeOperation(in, pipelineContext, () -> {
            boolean isNew = in.getId() == null;
            Person oldPerson = null;
            if (!isNew)
            {
                String old = getObjectConverter().getJsonMarshaller().marshal(personDao.find(in.getId()));
                oldPerson = getObjectConverter().getJsonUnmarshaller().unmarshall(old, Person.class);
            }
            Person person = personDao.save(in);
            personEventPublisher.publishPersonUpsertEvents(person, oldPerson, isNew, true);
            return person;
        });
    }

    /**
     * Validates the {@link PersonOrganizationAssociation}.
     *
     * @param person
     *            the {@link Person} to validate
     * @throws AcmCreateObjectFailedException
     *             when at least one of the {@link PersonOrganizationAssociation} is not valid.
     * @throws AcmUpdateObjectFailedException
     *             when at least one of the {@link PersonOrganizationAssociation} is not valid.
     */
    private void validateOrganizationAssociations(Person person) throws AcmCreateObjectFailedException, AcmUpdateObjectFailedException
    {
        List<PersonOrganizationAssociation> missingAssocTypes = person.getOrganizationAssociations().stream().filter(
                assoc -> assoc.getPersonToOrganizationAssociationType() == null || assoc.getOrganizationToPersonAssociationType() == null)
                .collect(Collectors.toList());

        if (missingAssocTypes.size() > 0)
        {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Missing person to organization relation type!");
            missingAssocTypes.forEach(assoc -> {
                errorMessage.append(" [OrganizationId: ").append(assoc.getPerson().getId()).append("]");
            });
            throw new AcmCreateObjectFailedException("Person", errorMessage.toString(), null);
        }

        Map<Long, List<String>> mapped = person.getOrganizationAssociations().stream()
                .collect(Collectors.toMap(d -> d.getOrganization().getId(), d -> {
                    List<String> value = new ArrayList<>();
                    value.add(d.getOrganizationToPersonAssociationType());
                    return value;
                }, (oldValue, newValue) -> {
                    oldValue.addAll(newValue);
                    return oldValue;
                }));

        List<Map.Entry<Long, List<String>>> withDupes = mapped.entrySet().stream()
                .filter(ds -> ds.getValue().size() != ((new HashSet<>(ds.getValue())).size())).collect(Collectors.toList());

        if (withDupes.size() > 0)
        {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Duplicate organizations to person relations!");
            withDupes.forEach(duplicate -> {
                errorMessage.append(" [OrganizationId: " + duplicate.getKey() + " Relation: " + duplicate.getValue() + "]");
            });
            throw new AcmCreateObjectFailedException("Person", errorMessage.toString(), null);
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
     * @throws PipelineProcessException
     */
    @Override
    public Person savePerson(Person person, List<MultipartFile> pictures, Authentication authentication)
            throws AcmUserActionFailedException, AcmCreateObjectFailedException, AcmUpdateObjectFailedException, AcmObjectNotFoundException,
            PipelineProcessException
    {
        Person savedPerson = savePerson(person, authentication);
        getPersonDao().getEm().flush();
        return uploadPicturesForPerson(savedPerson, pictures, authentication);
    }

    private Person uploadPicturesForPerson(Person person, List<MultipartFile> pictures, Authentication authentication)
            throws AcmCreateObjectFailedException, AcmUpdateObjectFailedException, AcmUserActionFailedException, PipelineProcessException
    {
        if (pictures != null)
        {
            boolean hasDefaultPicture = person.getDefaultPicture() != null;

            // change for AFDP-6287
            try
            {
                if (person.getContainer() == null)
                {
                    person = createContainerAndPictureFolder(person, authentication);
                }
            }
            catch (AcmObjectNotFoundException e)
            {
                log.error("Error uploading pictures to person id [{}]", person.getId());
                return person;
            }

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
                catch (IOException | AcmObjectNotFoundException e)
                {
                    log.error("Error uploading picture [{}] to person id [{}]", picture, person.getId());
                }
                catch (AcmFileTypesException e)
                {
                    log.error("Error uploading picture [{}] to person id [{}]", picture, person.getId(), e);
                }
            }
        }
        // because there are updates to the person we need to get fresh instance from database.
        return person;
    }

    @Override
    public String getPeopleByIds(Authentication auth, String personIds, int start, int n, String s) throws AcmObjectNotFoundException {

        List<String> personIdsArray = new ArrayList<>(Arrays.asList(personIds.split(",")));

        String query = "object_type_s:PERSON AND object_id_s:(";
        query += String.join(" OR ", personIdsArray);
        query += ")";

        try
        {
            return executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, start, n, "");
        }
        catch (SolrException e)
        {
            log.error("Error while executing Solr query: {}", query, e);
            throw new AcmObjectNotFoundException("Person", null, "Could not retrieve persons by Ids.", e);
        }
    }


    public PersonDao getPersonDao()
    {
        return personDao;
    }

    public void setPersonDao(PersonDao personDao)
    {
        this.personDao = personDao;
    }

    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
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

    public PipelineManager<Person, PersonPipelineContext> getPersonPipelineManager()
    {
        return personPipelineManager;
    }

    public void setPersonPipelineManager(PipelineManager<Person, PersonPipelineContext> personPipelineManager)
    {
        this.personPipelineManager = personPipelineManager;
    }

    public EcmFileParticipantService getFileParticipantService()
    {
        return fileParticipantService;
    }

    public void setFileParticipantService(EcmFileParticipantService fileParticipantService)
    {
        this.fileParticipantService = fileParticipantService;
    }

    public ObjectConverter getObjectConverter()
    {
        return objectConverter;
    }

    public void setObjectConverter(ObjectConverter objectConverter)
    {
        this.objectConverter = objectConverter;
    }

    public void setEcmTikaFileService(EcmTikaFileService ecmTikaFileService)
    {
        this.ecmTikaFileService = ecmTikaFileService;
    }

    public PersonConfig getPersonConfig()
    {
        return personConfig;
    }

    public void setPersonConfig(PersonConfig personConfig)
    {
        this.personConfig = personConfig;
    }

    public PhoneRegexConfig getPhoneRegexConfig()
    {
        return phoneRegexConfig;
    }

    public void setPhoneRegexConfig(PhoneRegexConfig phoneRegexConfig)
    {
        this.phoneRegexConfig = phoneRegexConfig;
    }
    public ExecuteSolrQuery getExecuteSolrQuery()
    {
        return executeSolrQuery;
    }

    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }
}
