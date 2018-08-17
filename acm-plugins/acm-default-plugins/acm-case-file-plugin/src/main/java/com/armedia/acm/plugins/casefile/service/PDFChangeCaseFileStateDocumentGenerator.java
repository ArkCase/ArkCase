package com.armedia.acm.plugins.casefile.service;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStateContants;
import com.armedia.acm.plugins.casefile.model.ChangeCaseStatus;
import com.armedia.acm.plugins.casefile.pipeline.CaseFilePipelineContext;
import com.armedia.acm.plugins.ecm.service.PDFDocumentGenerator;
import com.armedia.acm.services.pipeline.AbstractPipelineContext;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class PDFChangeCaseFileStateDocumentGenerator<D extends AcmAbstractDao, T extends CaseFile> extends PDFDocumentGenerator
{
    private D dao;

    public void generatePdf(String objectType, Long caseFileId, CaseFilePipelineContext ctx)
            throws PipelineProcessException
    {
        try
        {
            CaseFile caseFile = (CaseFile) getDao().find(caseFileId);
            generatePdf(objectType, caseFileId, ctx, ctx.getAuthentication(), caseFile, caseFile.getContainer(),
                    ChangeCaseStateContants.CHANGE_CASE_STATUS_STYLESHEET,
                    ChangeCaseStateContants.CHANGE_CASE_STATUS_DOCUMENT, ChangeCaseStateContants.CHANGE_CASE_STATUS_FILENAMEFORMAT);
        }
        catch (ParserConfigurationException e)
        {
            throw new PipelineProcessException(e);
        }
    }

    @Override
    public Document buildXmlForPdfDocument(Object businessObject, AbstractPipelineContext ctx) throws ParserConfigurationException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        Document document = docBuilder.newDocument();

        Element rootElem = document.createElement("changeCaseFileState");
        document.appendChild(rootElem);

        ChangeCaseStatus caseFilePipelineContext = ((CaseFilePipelineContext) ctx).getChangeCaseStatus();
        CaseFile caseFile = (CaseFile) businessObject;

        addElement(document, rootElem, "caseTitle", "Dimee",
                true);

        return document;
    }

    public D getDao()
    {
        return dao;
    }

    public void setDao(D dao)
    {
        this.dao = dao;
    }
}
