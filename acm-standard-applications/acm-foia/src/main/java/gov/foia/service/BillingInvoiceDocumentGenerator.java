package gov.foia.service;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.pdf.PdfServiceException;
import com.armedia.acm.pdf.service.PdfService;
import com.armedia.acm.plugins.casefile.dao.CaseFileDao;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.AcmFolder;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.ecm.service.EcmFileService;
import com.armedia.acm.services.billing.dao.BillingInvoiceDao;
import com.armedia.acm.services.billing.exception.GetBillingInvoiceException;
import com.armedia.acm.services.billing.model.BillingConstants;
import com.armedia.acm.services.billing.model.BillingInvoice;
import com.armedia.acm.services.billing.model.BillingItem;
import com.armedia.acm.services.billing.service.BillingService;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import gov.foia.model.FOIARequest;
import gov.foia.model.FOIARequestUtils;

/**
 * @author sasko.tanaskoski
 *
 */
public class BillingInvoiceDocumentGenerator
{

    private CaseFileDao caseFileDao;

    private BillingService billingService;

    private EcmFileService ecmFileService;

    private EcmFileDao ecmFileDao;

    private BillingInvoiceDao billingInvoiceDao;

    private PdfService pdfService;

    private Logger log = LoggerFactory.getLogger(getClass());

    public void generatePdf(String objectType, Long requestId)
    {

        FOIARequest request = (FOIARequest) getCaseFileDao().find(requestId);

        if (request != null)
        {
            String filename = null;
            try
            {
                BillingInvoice billingInvoice = getBillingService().getLatestBillingInvoice(objectType, requestId);

                Document document = buildDocument(request, billingInvoice);
                Source source = new DOMSource(document);

                filename = getPdfService().generatePdf(new File(BillingConstants.INVOICE_DOCUMENT_STYLESHEET), source);
                log.debug("Created {} document [{}]", BillingConstants.INVOICE_DOCUMENT_TYPE, filename);

                String arkcaseFilename = billingInvoice.getInvoiceNumber();

                String targetFolderId = request.getContainer().getFolder().getCmisFolderId();
                if (!request.getContainer().getFolder().getChildrenFolders().isEmpty())
                {
                    Optional<AcmFolder> invoiceFolder = request.getContainer().getFolder().getChildrenFolders().stream()
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
                            objectType, request.getId());

                    billingInvoice.setBillingInvoiceEcmFile(billingInvoiceEcmFile);
                    getBillingInvoiceDao().save(billingInvoice);
                }
            }
            catch (PdfServiceException | AcmCreateObjectFailedException | AcmUserActionFailedException | IOException
                    | ParserConfigurationException | GetBillingInvoiceException e)
            {
                log.error("Unable to create {} document for request [{}]",
                        BillingConstants.INVOICE_DOCUMENT_TYPE, request.getId(), e);
            }
            finally
            {
                FileUtils.deleteQuietly(new File(filename));
            }
        }

    }

    public Document buildDocument(FOIARequest foiaRequest, BillingInvoice billingInvoice) throws ParserConfigurationException
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
        addElement(document, rootElem, "requesterName", FOIARequestUtils.extractRequestorName(foiaRequest.getOriginator().getPerson()),
                true);

        addElement(document, rootElem, "requesterAddress",
                FOIARequestUtils.extractRequestorAddress(foiaRequest.getOriginator().getPerson()), true);

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
    private void addElement(Document doc, Element parent, String elemName,
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

    /**
     * @return the caseFileDao
     */
    public CaseFileDao getCaseFileDao()
    {
        return caseFileDao;
    }

    /**
     * @param caseFileDao
     *            the caseFileDao to set
     */
    public void setCaseFileDao(CaseFileDao caseFileDao)
    {
        this.caseFileDao = caseFileDao;
    }

    /**
     * @return the billingService
     */
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

}
