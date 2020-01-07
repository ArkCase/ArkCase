package com.armedia.acm.correspondence.service;

/*-
 * #%L
 * ACM Service: Correspondence Library
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
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.core.provider.TemplateModelProvider;
import com.armedia.acm.correspondence.model.CorrespondenceMergeField;
import com.armedia.acm.correspondence.model.CorrespondenceTemplate;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.AcmEntity;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.spring.SpringContextHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.docx4j.dml.CTBlip;
import org.docx4j.jaxb.XPathBinderAssociationIsPartialException;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.FooterPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.CTRel;
import org.docx4j.wml.FooterReference;
import org.docx4j.wml.HdrFtrRef;
import org.docx4j.wml.HeaderReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.armedia.acm.correspondence.service.CorrespondenceGenerator.CORRESPONDENCE_CATEGORY;
import static com.armedia.acm.correspondence.service.CorrespondenceGenerator.WORD_MIME_TYPE;

public class CorrespondenceServiceImpl implements CorrespondenceService
{
    private static final String TEMP_FILE_PREFIX = "template-";
    private static final String TEMP_FILE_SUFFIX = ".docx";
    private static final String MULTITEMPLATE_DOC_TYPE = "Multi Correspondence";

    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private SpringContextHolder springContextHolder;
    private CorrespondenceGenerator correspondenceGenerator;
    private CorrespondenceEventPublisher eventPublisher;
    private CorrespondenceTemplateManager templateManager;
    private CorrespondenceMergeFieldManager mergeFieldManager;
    private SpringContextHolder contextHolder;

    /**
     * For use from MVC controllers and any other client with an Authentication object.
     *
     * @param authentication
     * @param templateName
     * @param parentObjectType
     * @param parentObjectId
     * @param targetCmisFolderId
     * @return
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws AcmCreateObjectFailedException
     */
    @Override
    public EcmFile generate(Authentication authentication, String templateName, String parentObjectType, Long parentObjectId,
            String targetCmisFolderId)
            throws IOException, IllegalArgumentException, AcmCreateObjectFailedException, AcmUserActionFailedException
    {
        CorrespondenceTemplate template = findTemplate(templateName);

        File file = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);

        try (FileInputStream fisForUploadToEcm = new FileInputStream(file);
                FileOutputStream fosToWriteFile = new FileOutputStream(file))
        {

            log.debug("writing correspondence to file: " + file.getCanonicalPath());

            EcmFile retval = getCorrespondenceGenerator().generateCorrespondence(authentication, parentObjectType, parentObjectId,
                    targetCmisFolderId, template, new Object[] { parentObjectId }, fosToWriteFile, fisForUploadToEcm);

            log.debug("Correspondence CMIS ID: " + retval.getVersionSeriesId());

            getEventPublisher().publishCorrespondenceAdded(retval, authentication, true);

            return retval;
        }
        finally
        {
            FileUtils.deleteQuietly(file);
        }

    }

    @Override
    public EcmFile generateMultiTemplate(Authentication authentication, List<CorrespondenceTemplate> templates, String parentObjectType,
            Long parentObjectId, String targetCmisFolderId, String documentName) throws Exception
    {

        EcmFile retval = null;

        List<String> templateNames = templates
                .stream()
                .map(correspondenceTemplate -> correspondenceTemplate.getTemplateFilename())
                .collect(Collectors.toList());

        List<File> templateFiles = new ArrayList<>();

        File multiTemplateCorrespondence = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);
        try (InputStream multiTemplateCorrespondenceInputStream = new FileInputStream(multiTemplateCorrespondence);
                OutputStream multiTemplateCorrespondenceOutputStream = new FileOutputStream(multiTemplateCorrespondence))
        {

            // GENERATE TEMP TEMPLATE DOCUMENTS AND ADD THEM TO A LIST
            for (String templateName : templateNames)
            {
                CorrespondenceTemplate template = findTemplate(templateName);
                File currentCorrespondenceTemplateFile = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);

                try (FileOutputStream currentCorrespondenceTemplateFileOutputStream = new FileOutputStream(
                        currentCorrespondenceTemplateFile))
                {
                    log.debug("Writing correspondence to file: " + currentCorrespondenceTemplateFile.getCanonicalPath());
                    getCorrespondenceGenerator().generateCorrespondenceOutputStream(template, new Object[] { parentObjectId },
                            currentCorrespondenceTemplateFileOutputStream, parentObjectId);
                    templateFiles.add(currentCorrespondenceTemplateFile);
                }
            }

            // MERGE TEMP TEMPLATE DOCUMENTS INTO ONE FINAL DOCUMENT AND UPLOAD IT
            if (!templateFiles.isEmpty())
            {
                if (templateFiles.size() > 1)
                {
                    mergeTemplates(templateFiles, multiTemplateCorrespondenceOutputStream);
                }
                else if (templateFiles.size() == 1)
                {
                    try (InputStream correspondenceTemplateIS = new FileInputStream(templateFiles.get(0)))
                    {
                        IOUtils.copy(correspondenceTemplateIS, multiTemplateCorrespondenceOutputStream);
                    }
                }

                String currDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMdd-HHmmss-SSS"));
                String fileName = documentName + " " + currDateTime + ".docx";
                retval = getCorrespondenceGenerator().getEcmFileService().upload(documentName + ".docx", MULTITEMPLATE_DOC_TYPE,
                        CORRESPONDENCE_CATEGORY,
                        multiTemplateCorrespondenceInputStream, WORD_MIME_TYPE, fileName, authentication, targetCmisFolderId,
                        parentObjectType, parentObjectId);

                getEventPublisher().publishCorrespondenceAdded(retval, authentication, true);

                // CLEANUP
                for (File tempFile : templateFiles)
                {
                    FileUtils.deleteQuietly(tempFile);
                }
            }
            // CLEANUP
            FileUtils.deleteQuietly(multiTemplateCorrespondence);
        }
        return retval;
    }

    @Override
    public CorrespondenceTemplate findTemplate(String templateName)
    {
        Collection<CorrespondenceTemplate> templates = templateManager.getActiveVersionTemplates();
        for (CorrespondenceTemplate template : templates)
        {
            if (templateName.equalsIgnoreCase(template.getTemplateFilename()))
            {
                return template;
            }
        }

        throw new IllegalArgumentException("Template '" + templateName + "' is not a registered template name!");
    }

    /**
     * Helper method for use from Activiti and other clients with no direct access to an Authentication, but in the call
     * stack of a Spring MVC authentication... so there is an Authentication in the Spring Security context holder.
     */
    @Override
    public EcmFile generate(String templateName, String parentObjectType, Long parentObjectId, String targetCmisFolderId)
            throws IOException, IllegalArgumentException, AcmCreateObjectFailedException, AcmUserActionFailedException

    {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();

        return generate(currentUser, templateName, parentObjectType, parentObjectId, targetCmisFolderId);
    }

    /**
     * @return
     */
    @Override
    public List<CorrespondenceTemplate> getAllTemplates()
    {
        return templateManager.getAllTemplates();
    }

    /**
     * @return
     */
    @Override
    public List<CorrespondenceTemplate> getActiveVersionTemplates()
    {
        return templateManager.getActiveVersionTemplates();
    }

    /**
     * @param objectType
     * @return
     */
    @Override
    public List<CorrespondenceTemplate> getActivatedActiveVersionTemplatesByObjectType(String objectType)
    {
        return templateManager.getActivatedActiveVersionTemplatesByObjectType(objectType);
    }

    /**
     * @param templateId
     * @return
     */
    @Override
    public List<CorrespondenceTemplate> getTemplateVersionsById(String templateId)
    {
        return templateManager.getTemplateVersionsById(templateId);
    }

    /**
     * @param templateId
     * @return
     */
    @Override
    public Optional<CorrespondenceTemplate> getActiveTemplateById(String templateId)
    {
        return templateManager.getActiveTemplateById(templateId);
    }

    /**
     * @param templateId
     * @param templateVersion
     * @return
     */
    @Override
    public Optional<CorrespondenceTemplate> getTemplateByIdAndVersion(String templateId, String templateVersion)
    {
        return templateManager.getTemplateByIdAndVersion(templateId, templateVersion);
    }

    /**
     * @param templateId
     * @param templateFilename
     * @return
     */
    @Override
    public Optional<CorrespondenceTemplate> getTemplateByIdAndFilename(String templateId, String templateFilename)
    {
        return templateManager.getTemplateByIdAndFilename(templateId, templateFilename);
    }

    /**
     * @param templateId
     * @param templateVersion
     * @return
     * @throws IOException
     */
    @Override
    public Optional<CorrespondenceTemplate> deleteTemplateByIdAndVersion(String templateId, String templateVersion) throws IOException
    {
        return templateManager.deleteTemplateByIdAndVersion(templateId, templateVersion);
    }

    /**
     * @return
     */
    @Override
    public List<CorrespondenceMergeField> getMergeFields()
    {
        return mergeFieldManager.getMergeFields();
    }

    /**
     * @param objectType
     * @return
     * @throws IOException,
     *             CorrespondenceMergeFieldVersionException
     */
    @Override
    public List<CorrespondenceMergeField> getMergeFieldsByType(String objectType)
    {
        return mergeFieldManager.getMergeFieldsByType(objectType);
    }

    /**
     * @param mergeFieldId
     * @return
     * @throws IOException,
     *             CorrespondenceMergeFieldVersionException
     */
    @Override
    public List<CorrespondenceMergeField> getMergeFieldByMergeFieldId(String mergeFieldId)
    {
        return mergeFieldManager.getMergeFieldByMergeFieldId(mergeFieldId);
    }


    @Override
    public void deleteMergeFields(String mergeFieldId) throws IOException
    {
        mergeFieldManager.deleteMergeFields(mergeFieldId);
    }

    @Override
    public void addMergeField (CorrespondenceMergeField newMergeField) throws IOException
    {
        mergeFieldManager.addMergeField(newMergeField);
    }
    /**
     * @param mergeFields
     * @param auth
     * @return
     * @throws IOException
     */
    @Override
    public List<CorrespondenceMergeField> saveMergeFieldsData(List<CorrespondenceMergeField> mergeFields, Authentication auth)
            throws IOException
    {
        return mergeFieldManager.saveMergeFieldsData(mergeFields, auth);
    }

    /**
     * @param template
     * @throws IOException
     */
    @Override
    public Optional<CorrespondenceTemplate> updateTemplate(CorrespondenceTemplate template) throws IOException
    {
        return templateManager.updateTemplate(template);
    }

    @Override
    public AcmAbstractDao<AcmEntity> getAcmAbstractDao(String objectType)
    {
        if (objectType != null)
        {
            Map<String, AcmAbstractDao> daos = getSpringContextHolder().getAllBeansOfType(AcmAbstractDao.class);

            if (daos != null)
            {
                for (AcmAbstractDao<AcmEntity> dao : daos.values())
                {
                    if (objectType.equals(dao.getSupportedObjectType()))
                    {
                        return dao;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public Map<String, String> listTemplateModelProviders() {
        Collection<TemplateModelProvider> templateModelProviders = getContextHolder().getAllBeansOfType(TemplateModelProvider.class).values();

        Map<String, String> mapTemplateModelProviders = new HashMap<>();
        for (TemplateModelProvider modelProvider : templateModelProviders)
        {
            String[] splittedTemplateProviderClassPath = modelProvider.getClass().getName().split("\\.");
            String templateModelProviderShortName = splittedTemplateProviderClassPath[splittedTemplateProviderClassPath.length-1];

            mapTemplateModelProviders.put(modelProvider.getClass().getName(), templateModelProviderShortName);
        }
        return mapTemplateModelProviders;
    }

    @Override
    public String getTemplateModelProviderDeclaredFields(String classPath) {
        String jsonSchemaProperties = null;
        try
        {
            Class templateModelProviderClass = Class.forName(classPath);
            TemplateModelProvider instance = (TemplateModelProvider) templateModelProviderClass.newInstance();
            Class clazz = instance.getType();

            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();

            JsonSchemaGenerator jsg = new JsonSchemaGenerator(mapper);
            JsonSchema jsonSchema = jsg.generateSchema(clazz);
            jsonSchemaProperties = writer.writeValueAsString(jsonSchema);

        }
        catch (Exception e)
        {
            log.error("The provided classpath is invalid. Because of: {}", e.getMessage());
        }
        return jsonSchemaProperties;
    }

    private void mergeTemplates(List<File> templateFiles, OutputStream dest) throws Docx4JException, JAXBException
    {
        WordprocessingMLPackage target = WordprocessingMLPackage.load(templateFiles.get(0));
        removeHeaderAndFooter(target);

        for (int i = 1; i < templateFiles.size(); i++)
        {
            WordprocessingMLPackage appendDocument = WordprocessingMLPackage.load(templateFiles.get(i));
            mergeDocumentsBodies(target, appendDocument);
            mergeDocumentsImages(target, appendDocument);
        }
        target.save(dest);
    }

    private void mergeDocumentsBodies(WordprocessingMLPackage target, WordprocessingMLPackage appendDocument)
            throws JAXBException, XPathBinderAssociationIsPartialException
    {
        List body = appendDocument.getMainDocumentPart().getJAXBNodesViaXPath("//w:body", false);
        for (Object b : body)
        {
            List bodyContent = ((org.docx4j.wml.Body) b).getContent();
            for (Object content : bodyContent)
            {
                target.getMainDocumentPart().addObject(content);
            }
        }
    }

    private void mergeDocumentsImages(WordprocessingMLPackage target, WordprocessingMLPackage appendDocument)
            throws JAXBException, XPathBinderAssociationIsPartialException, InvalidFormatException
    {
        List<Object> blips = appendDocument.getMainDocumentPart().getJAXBNodesViaXPath("//a:blip", false);
        for (Object el : blips)
        {
            try
            {
                CTBlip blip = (CTBlip) el;
                RelationshipsPart parts = appendDocument.getMainDocumentPart().getRelationshipsPart();
                Relationship rel = parts.getRelationshipByID(blip.getEmbed());
                Part part = parts.getPart(rel);

                Relationship newRel = target.getMainDocumentPart().addTargetPart(part,
                        RelationshipsPart.AddPartBehaviour.RENAME_IF_NAME_EXISTS);
                blip.setEmbed(newRel.getId());
                target.getMainDocumentPart()
                        .addTargetPart(appendDocument.getParts().getParts().get(new PartName("/word/" + rel.getTarget())));
            }
            catch (Exception ex)
            {
                log.error("Could not merge templates images: {}", ex.getMessage());
                throw ex;
            }
        }
    }

    private void removeHeaderAndFooter(WordprocessingMLPackage target)
    {
        List<SectionWrapper> sectionWrappers = target.getDocumentModel().getSections();
        HeaderPart headerPart;
        FooterPart footerPart;

        for (SectionWrapper sectionWrapper : sectionWrappers)
        {
            headerPart = sectionWrapper.getHeaderFooterPolicy().getDefaultHeader();
            footerPart = sectionWrapper.getHeaderFooterPolicy().getDefaultFooter();

            if (Objects.nonNull(headerPart))
            {
                target.getMainDocumentPart().getRelationshipsPart().removeRelationship(headerPart.getPartName());
            }

            if (Objects.nonNull(footerPart))
            {
                target.getMainDocumentPart().getRelationshipsPart().removeRelationship(footerPart.getPartName());
            }

            List<CTRel> rel = sectionWrapper.getSectPr().getEGHdrFtrReferences();
            List<HeaderReference> headerReferencesToBeRemoved = new ArrayList<>();
            List<FooterReference> footerReferencesToBeRemoved = new ArrayList<>();

            for (CTRel ctRel : rel)
            {
                if (ctRel instanceof HeaderReference)
                {
                    HeaderReference hr = (HeaderReference) ctRel;
                    if (hr.getType().equals(HdrFtrRef.DEFAULT))
                    {
                        headerReferencesToBeRemoved.add(hr);
                    }
                }
                else if (ctRel instanceof FooterReference)
                {
                    FooterReference fr = (FooterReference) ctRel;
                    if (fr.getType().equals(HdrFtrRef.DEFAULT))
                    {
                        footerReferencesToBeRemoved.add(fr);
                    }
                }
            }
            if (!headerReferencesToBeRemoved.isEmpty())
            {
                for (int i = 0; i < headerReferencesToBeRemoved.size(); i++)
                {
                    sectionWrapper.getSectPr().getEGHdrFtrReferences().remove(headerReferencesToBeRemoved.get(i));
                }
            }
            if (!footerReferencesToBeRemoved.isEmpty())
            {
                for (int i = 0; i < footerReferencesToBeRemoved.size(); i++)
                {
                    sectionWrapper.getSectPr().getEGHdrFtrReferences().remove(footerReferencesToBeRemoved.get(i));
                }
            }
        }
    }

    public SpringContextHolder getSpringContextHolder()
    {
        return springContextHolder;
    }

    public void setSpringContextHolder(SpringContextHolder springContextHolder)
    {
        this.springContextHolder = springContextHolder;
    }

    public CorrespondenceGenerator getCorrespondenceGenerator()
    {
        return correspondenceGenerator;
    }

    public void setCorrespondenceGenerator(CorrespondenceGenerator correspondenceGenerator)
    {
        this.correspondenceGenerator = correspondenceGenerator;
    }

    public CorrespondenceEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(CorrespondenceEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

    /**
     * @param templateManager
     *            the templateManager to set
     */
    public void setTemplateManager(CorrespondenceTemplateManager templateManager)
    {
        this.templateManager = templateManager;
    }

    /**
     * @param mergeFieldManager
     *            the mergeFieldManager to set
     */
    public void setMergeFieldManager(CorrespondenceMergeFieldManager mergeFieldManager)
    {
        this.mergeFieldManager = mergeFieldManager;
    }

    public SpringContextHolder getContextHolder()
    {
        return contextHolder;
    }

    public void setContextHolder(SpringContextHolder contextHolder)
    {
        this.contextHolder = contextHolder;
    }
}
