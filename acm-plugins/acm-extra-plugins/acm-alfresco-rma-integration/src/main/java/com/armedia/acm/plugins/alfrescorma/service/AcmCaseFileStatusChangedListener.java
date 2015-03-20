package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.plugins.alfrescorma.model.AcmRecord;
import com.armedia.acm.plugins.casefile.model.CaseEvent;
import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.ecm.dao.EcmFileDao;
import com.armedia.acm.plugins.ecm.model.EcmFile;
import org.mule.api.MuleException;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.Date;
import java.util.List;

public class AcmCaseFileStatusChangedListener implements ApplicationListener<CaseEvent>
{

    private transient Logger LOG = LoggerFactory.getLogger(getClass());
    private EcmFileDao ecmFileDao;

    @Override
    public void onApplicationEvent(CaseEvent event)
    {
        if ("com.armedia.acm.casefile.event.closed".equals(event.getEventType().toLowerCase()))
        {
            CaseFile caseFile = event.getCaseFile();

            if (null != caseFile)
            {
                List<EcmFile> files = getEcmFileDao().findForContainer(caseFile.getContainer().getId());
                for (EcmFile file : files)
                {
                    AcmRecord record = new AcmRecord();

                    record.setEcmFileId(file.getFolder().getCmisFolderId());
                    record.setCategoryFolder("Case Files");
                    record.setOriginatorOrg("Armedia LLC");
                    record.setOriginator(file.getModifier());
                    record.setPublishedDate(new Date());
                    record.setReceivedDate(event.getEventDate());
                    record.setRecordFolder(caseFile.getCaseNumber());

                    try
                    {
                        if (LOG.isTraceEnabled())
                        {
                            LOG.trace("Sending JMS message.");
                        }

                        getMuleClient().dispatch("jms://rmaRecord.in", record, null);

                        if (LOG.isTraceEnabled())
                        {
                            LOG.trace("Done");
                        }

                    } catch (MuleException e)
                    {
                        LOG.error("Could not create RMA folder: " + e.getMessage(), e);
                    }
                }
            }
        }
    }

    public MuleClient getMuleClient()
    {
        return null;  // mule client is returned by Spring method injection
    }

    public EcmFileDao getEcmFileDao()
    {
        return ecmFileDao;
    }

    public void setEcmFileDao(EcmFileDao ecmFileDao)
    {
        this.ecmFileDao = ecmFileDao;
    }

}
