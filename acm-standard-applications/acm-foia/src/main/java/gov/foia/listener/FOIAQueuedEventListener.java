/**
 *
 */
package gov.foia.listener;

import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.plugins.casefile.model.QueuedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.time.LocalDateTime;

import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIARequest;

/**
 * @author mario.gjurcheski
 *
 */
public class FOIAQueuedEventListener implements ApplicationListener<QueuedEvent>
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private FOIARequestDao requestDao;

    @Override
    public void onApplicationEvent(QueuedEvent event)
    {
        CaseFile cf = (CaseFile) event.getSource();
        if ((cf.getStatus().equals("Released")))
        {
            try
            {
                FOIARequest request = requestDao.find(cf.getId());
                request.setReleasedDate(LocalDateTime.now());
                log.error("Status of the Request has be changed to Released for object with objectId = [{}]", cf.getId());
            }
            catch (Exception e)
            {
                log.error("Status of the Request has not be changed to Released for object with objectId = [{}]", cf.getId(), e);
            }
        }
    }

    public FOIARequestDao getRequestDao()
    {
        return requestDao;
    }

    public void setRequestDao(FOIARequestDao requestDao)
    {
        this.requestDao = requestDao;
    }
}
