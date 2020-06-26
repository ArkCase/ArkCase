package com.armedia.acm.plugins.billing.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.acm.configuration.service.FileConfigurationService;
import com.armedia.acm.core.AcmObjectNumber;
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.pdf.PdfServiceException;
import com.armedia.acm.pdf.service.PdfService;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.plugins.person.model.AcmObjectOriginator;
import com.armedia.acm.plugins.person.model.ExtractPersonInfoUtils;
import com.armedia.acm.services.billing.dao.BillingInvoiceDao;
import com.armedia.acm.services.billing.exception.GetBillingInvoiceException;
import com.armedia.acm.services.billing.model.BillingConstants;
import com.armedia.acm.services.billing.model.BillingInvoice;
import com.armedia.acm.services.billing.model.BillingItem;
import com.armedia.acm.services.billing.service.BillingService;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/**
 * @author sasko.tanaskoski
 *
 */
public class BillingInvoiceDocumentGenerator<T extends AcmContainerEntity & AcmObjectOriginator & AcmObjectNumber>
{

    private static final String PDF_STYLESHEETS_LOCATION = "pdf-stylesheets";

    private T parentObject;

    private BillingService billingService;

    private EcmFileService ecmFileService;

    private EcmFileDao ecmFileDao;

    private BillingInvoiceDao billingInvoiceDao;

    private PdfService pdfService;

    private FileConfigurationService fileConfigurationService;

    private Logger log = LogManager.getLogger(getClass());

    public void generatePdf(String objectType, Long requestId)
    {

        if (parentObject != null)
        {
            String filename = null;
            try
            {
                BillingInvoice billingInvoice = getBillingService().getLatestBillingInvoice(objectType, requestId);

                Document document = buildDocument(parentObject, billingInvoice);
                Source source = new DOMSource(document);

                InputStream xslStream = fileConfigurationService.getInputStreamFromConfiguration(PDF_STYLESHEETS_LOCATION + "/" + BillingConstants.INVOICE_DOCUMENT_STYLESHEET);
                URI baseURI = fileConfigurationService.getLocationUriFromConfiguration(PDF_STYLESHEETS_LOCATION);
                filename = getPdfService().generatePdf(xslStream, baseURI, source);
                log.debug("Created {} document [{}]", BillingConstants.INVOICE_DOCUMENT_TYPE, filename);

                String arkcaseFilename = billingInvoice.getInvoiceNumber();

                String targetFolderId = parentObject.getContainer().getFolder().getCmisFolderId();
                if (!parentObject.getContainer().getFolder().getChildrenFolders().isEmpty())
                {
                    Optional<AcmFolder> invoiceFolder = parentObject.getContainer().getFolder().getChildrenFolders().stream()
                            .filter(acmFolder -> acmFolder.getName().contains("Invoice")).findFirst();
                    if (invoiceFolder.isPresent())
                    {
                        targetFolderId = invoiceFolder.get().getCmisFolderId();
                    }
                }

                Authentication authentication = SecurityContextHolder.getContext() != null
                        ? SecurityContextHolder.getContext().getAuthentication()
                        : null;

                try (InputStream fis = new FileInputStream(filename))
                {
                    EcmFile billingInvoiceEcmFile = ecmFileService.upload(arkcaseFilename, BillingConstants.INVOICE_DOCUMENT_TYPE,
                            "Document", fis,
                            BillingConstants.INVOICE_DOCUMENT_MIME_TYPE_PDF, arkcaseFilename, authentication, targetFolderId,
                            objectType, requestId);

                    billingInvoice.setBillingInvoiceEcmFile(billingInvoiceEcmFile);
                    getBillingInvoiceDao().save(billingInvoice);
                }
            }
            catch (PdfServiceException | AcmCreateObjectFailedException | AcmUserActionFailedException | IOException
                    | URISyntaxException | ParserConfigurationException | GetBillingInvoiceException e)
            {
                log.error("Unable to create {} document for request [{}]",
                        BillingConstants.INVOICE_DOCUMENT_TYPE, requestId, e);
            }
            finally
            {
                FileUtils.deleteQuietly(new File(filename));
            }
        }

    }

    public Document buildDocument(T t, BillingInvoice billingInvoice) throws ParserConfigurationException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        Document document = docBuilder.newDocument();

        // create the root of the document
        Element rootElem = document.createElement("billing-invoice");
        document.appendChild(rootElem);

        addElement(document, rootElem, "invoiceDate",
                billingInvoice.getCreated().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString(),
                true);
        addElement(document, rootElem, "invoiceNumber", billingInvoice.getInvoiceNumber(), true);
        addElement(document, rootElem, "requesterName", ExtractPersonInfoUtils.extractRequestorName(t.getAcmObjectOriginator().getPerson()),true);
        addElement(document, rootElem, "requesterAddress",
                ExtractPersonInfoUtils.extractRequestorAddress(t.getAcmObjectOriginator().getPerson()), true);

        Double totalAmount = 0.0;

        List<BillingItem> billingItems = billingInvoice.getBillingItems();
        if (!billingItems.isEmpty())
        {
            Element itemsElement = document.createElement("billing-items");
            rootElem.appendChild(itemsElement);

            for (BillingItem billingItem : billingItems)
            {
                totalAmount += billingItem.getItemAmount();
                Element itemElement = document.createElement("billing-item");
                itemsElement.appendChild(itemElement);
                addElement(document, itemElement, "itemNumber", billingItem.getItemNumber().toString(), false);
                addElement(document, itemElement, "itemDescription", billingItem.getItemDescription(), false);
                addElement(document, itemElement, "itemAmount",
                        String.format(BillingConstants.BILLING_CURRENCY_FORMAT, billingItem.getItemAmount()), false);
            }
        }

        addElement(document, rootElem, "totalAmount", String.format(BillingConstants.BILLING_CURRENCY_FORMAT, totalAmount), true);
        return document;
    }

    /**
     * A helper method that simplifies this class.
     *
     * @param doc
     *            the DOM Document, used as a factory for
     *            creating Elements.
     * @param parent
     *            the DOM Element to add the child to.
     * @param elemName
     *            the name of the XML element to create.
     * @param elemValue
     *            the text content of the new XML element.
     * @param required
     *            if true, insert 'required="true"' attribute.
     */
    public void addElement(Document doc, Element parent, String elemName,
            String elemValue, boolean required)
    {
        Element elem = doc.createElement(elemName);
        elem.appendChild(doc.createTextNode(elemValue));
        if (required)
        {
            elem.setAttribute("required", "true");
        }
        parent.appendChild(elem);
    }

    public T getParentObject() {
        return parentObject;
    }

    public void setParentObject(T parentObject) {
        this.parentObject = parentObject;
    }

    public BillingService getBillingService()
    {
        return billingService;
    }

    /**
     * @param billingService
     *            the billingService to set
     */
    public void setBillingService(BillingService billingService)
    {
        this.billingService = billingService;
    }

    /**
     * @return the ecmFileService
     */
    public EcmFileService getEcmFileService()
    {
        return ecmFileService;
    }

    /**
     * @param ecmFileService
     *            the ecmFileService to set
     */
    public void setEcmFileService(EcmFileService ecmFileService)
    {
        this.ecmFileService = ecmFileService;
    }

    /**
     * @return the ecmFileDao
     */
    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    /**
     * @param ecmFileDao
     *            the ecmFileDao to set
     */
    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

    public BillingInvoiceDao getBillingInvoiceDao()
    {
        return billingInvoiceDao;
    }

    public void setBillingInvoiceDao(BillingInvoiceDao billingInvoiceDao)
    {
        this.billingInvoiceDao = billingInvoiceDao;
    }

    /**
     * @return the pdfService
     */
    public PdfService getPdfService()
    {
        return pdfService;
    }

    /**
     * @param pdfService
     *            the pdfService to set
     */
    public void setPdfService(PdfService pdfService)
    {
        this.pdfService = pdfService;
    }

    /**
     * @return the fileConfigurationService
     */
    public FileConfigurationService getFileConfigurationService()
    {
        return fileConfigurationService;
    }

    /**
     * @param fileConfigurationService
     *            the fileConfigurationService to set
     */
    public void setFileConfigurationService(FileConfigurationService fileConfigurationService)
    {
        this.fileConfigurationService = fileConfigurationService;
    }

}
