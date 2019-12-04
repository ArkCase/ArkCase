package gov.foia.dao;

import com.armedia.acm.data.AcmAbstractDao;

import gov.foia.model.FOIAFile;

/**
 * @author sasko.tanaskoski
 *
 */
public class FOIAFileDao extends AcmAbstractDao<FOIAFile>
{

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.data.AcmAbstractDao#getPersistenceClass()
     */
    @Override
    protected Class<FOIAFile> getPersistenceClass()
    {
        return FOIAFile.class;
    }

}
