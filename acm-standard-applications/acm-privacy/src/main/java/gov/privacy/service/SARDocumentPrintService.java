package gov.privacy.service;

/*-
 * #%L
 * ACM Privacy: Subject Access Request
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import static com.armedia.acm.plugins.ecm.model.EcmFileConstants.OBJECT_FILE_TYPE;
import static com.armedia.acm.plugins.ecm.model.EcmFileConstants.OBJECT_FOLDER_TYPE;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.objectdataprocessing.DocumentConverter;
import com.armedia.acm.objectdataprocessing.ObjectDataExtractingProcessor;
import com.armedia.acm.objectdataprocessing.UnconvertableSourceException;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.casefile.model.CaseFileConstants;
import com.armedia.acm.plugins.ecm.dao.AcmContainerDao;
import com.armedia.acm.plugins.ecm.model.AcmContainer;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.AcmFolderService;
import com.armedia.acm.plugins.ecm.service.EcmFileService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import gov.privacy.model.SubjectAccessRequest;

/**
 * <code>DocumentPrintService</code> implementation capable of processing <code>SubjectAccessRequest</code>s.
 *
 * @author Vladimir Cherepnalkovski <vladimir.cherepnalkovski@armedia.com> on Jun, 2020
 *
 */
public class SARDocumentPrintService implements ObjectDataExtractingProcessor<SubjectAccessRequest, SARPrintDocument>
{

    /**
     * Logger instance.
     */
    private Logger log = LogManager.getLogger(getClass());

    private AcmContainerDao acmContainerDao;

    private AcmFolderService folderService;

    private EcmFileService fileService;

    private DocumentConverter documentConverter;

    /**
     * Designates if subfolders should be checked against the predicates in case parent folder matched.
     */
    private boolean testSubfolders = false;

    /**
     * Predicates are initialized to empty, so by default all folders are processed.
     */
    private Optional<Predicate<AcmFolder>> predicates = Optional.empty();

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.printdocuments.DocumentPrintService#createPrintDocument(java.util.List)
     */
    @Override
    public SARPrintDocument processObjects(List<Long> caseFileIds) throws IOException
    {
        SARPrintDocument printDocument = new SARPrintDocument();

        List<AcmContainer> requestContainers = acmContainerDao.findFoldersByObjectTypeAndIds(CaseFileConstants.OBJECT_TYPE, caseFileIds);

        printDocument.setRequestContainers(requestContainers);
        processSARs(printDocument);

        log.debug("Creating PrintDocument for Case File IDs [{}]", caseFileIds);

        return printDocument;
    }

    /**
     * @param printDocument
     *            associated with the <code>Subject Access Request</code>s that are being processed, and that will
     *            contain the resulting binary data of the merged PDF document.
     * @throws IOException
     *             if there was an error thrown while setting content of the print document.
     * @see SARPrintDocument#setContent(PDFMergerUtility)
     */
    private void processSARs(SARPrintDocument printDocument) throws IOException
    {
        PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
        for (AcmContainer container : printDocument.getRequestContainers())
        {
            AcmFolder folder = container.getFolder();
            processSAR(pdfMergerUtility, folder, false);
        }
        printDocument.setContent(pdfMergerUtility);
    }

    /**
     * Processes an instance of <code>SubjectAccessRequest</code> by recursively processing its folders, starting with the
     * <code>ROOT</code> folder.
     *
     * @param pdfMergerUtility
     *            used to merge files that are supposed to be printed.
     * @param folder
     *            being processed.
     * @param parentMatched
     *            parentMatched if a parent is matched, and <code>testSubfolders</code> is set to
     *            <code>false</code> subfolders are processed without checking the predicates.
     */
    private void processSAR(PDFMergerUtility pdfMergerUtility, AcmFolder folder, boolean parentMatched)
    {

        try
        {
            Map<String, List<AcmObject>> map = folderService.getFolderChildren(folder.getId()).stream()
                    .filter(obj -> obj.getObjectType() != null).collect(Collectors.groupingBy(obj -> obj.getObjectType().toUpperCase()));

            boolean processFolder = shouldProcessFolderFiles(folder, parentMatched);
            if (processFolder)
            {
                map.getOrDefault(OBJECT_FILE_TYPE, Collections.emptyList()).stream().map(acmObject -> EcmFile.class.cast(acmObject))
                        .filter(ecmFile -> isLastVersion(ecmFile)).forEach(ecmFile -> {
                            try
                            {
                                InputStream fileByteStream = documentConverter.convertStream(
                                        fileService.downloadAsInputStream(ecmFile.getId()), ecmFile.getFileActiveVersionMimeType());
                                pdfMergerUtility.addSource(fileByteStream);
                            }
                            catch (AcmUserActionFailedException | UnconvertableSourceException e)
                            {
                                if (UnconvertableSourceException.class.isInstance(e))
                                {
                                    log.error("File with ID [{}] cannot be converted to pdf format.", ecmFile.getId(), e);
                                }
                                else
                                {
                                    log.error("Could not retieve the input stream for file with ID [{}].", ecmFile.getId(), e);
                                }
                            }
                        });
            }

            map.getOrDefault(OBJECT_FOLDER_TYPE, Collections.emptyList()).stream().map(acmObject -> AcmFolder.class.cast(acmObject))
                    .forEach(ecmFolder -> processSAR(pdfMergerUtility, ecmFolder, processFolder));

        }
        catch (AcmUserActionFailedException | AcmObjectNotFoundException fe)
        {
            log.error("Could not get folder children for folder with ID [{}].", folder.getId(), fe);
            return;
        }

    }

    /**
     * Checks if a folder should be processed based on the rules set in the predicates. If there are no predicates set,
     * all folders are processed.
     *
     * @param folder
     *            folder being checked.
     * @param parentMatched
     *            if a parent is matched, and <code>testSubfolders</code> is set to <code>false</code>
     *            subfolders are processed without checking the predicates.
     * @return <code>true</code> if the folder should be processed <code>false</code> otherwise.
     * @see #testSubfolders
     */
    private boolean shouldProcessFolderFiles(AcmFolder folder, boolean parentMatched)
    {
        if (parentMatched && !testSubfolders)
        {
            return true;
        }
        else
        {
            return predicates.orElse(f -> true).test(folder);
        }
    }

    /**
     * Checks if this is the last version of the file, by comparing its active version tag with latest version tag.
     *
     * @param ecmFile
     *            the file being checked.
     * @return <code>true</code> if the file is latest version <code>false</code> otherwise.
     */
    private boolean isLastVersion(EcmFile ecmFile)
    {
        Optional<String> lastVersion = ecmFile.getVersions().stream().map(fileVersion -> fileVersion.getVersionTag())
                .sorted(Comparator.reverseOrder()).findFirst();
        if (lastVersion.isPresent())
        {
            return lastVersion.get().equals(ecmFile.getActiveVersionTag());
        }
        else
        {
            return true;
        }
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.printdocuments.DocumentPrintService#postProcessPrintDocument(com.armedia.acm.printdocuments.
     * PrintDocument)
     */
    @Override
    public void postProcessDataProvider(SARPrintDocument printDocument)
    {
        // nothing to do here, we are not performing any kind of post processing.
    }

    /**
     * @param caseFileDao
     *            the caseFileDao to set
     */
    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
    }

    /**
     * @param acmContainerDao
     *            the acmContainerDao to set
     */
    public void setAcmContainerDao(AcmContainerDao acmContainerDao)
    {
        this.acmContainerDao = acmContainerDao;
    }

    /**
     * @param folderService
     *            the folderService to set
     */
    public void setFolderService(AcmFolderService folderService)
    {
        this.folderService = folderService;
    }

    /**
     * @param fileService
     *            the fileService to set
     */
    public void setFileService(EcmFileService fileService)
    {
        this.fileService = fileService;
    }

    /**
     * @param documentConverter
     *            the pdfConverter to set
     */
    public void setDocumentConverter(DocumentConverter documentConverter)
    {
        this.documentConverter = documentConverter;
    }

    /**
     * @param predicates
     *            the predicates to set
     */
    public void setPredicates(List<Predicate<AcmFolder>> predicates)
    {
        Predicate<AcmFolder> predicate = predicates.stream().reduce(Predicate::and).orElse(p -> false);
        this.predicates = Optional.of(predicate);
    }

    /**
     * @param testSubfolders
     *            the testSubfolders to set
     */
    public void setTestSubfolders(boolean testSubfolders)
    {
        this.testSubfolders = testSubfolders;
    }

}
