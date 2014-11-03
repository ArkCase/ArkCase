/**
 * 
 */
package com.armedia.acm.form.closecase.service;

import com.armedia.acm.frevvo.config.FrevvoFormName;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;

/**
 * @author riste.tutureski
 *
 */
public class CloseCaseService extends FrevvoFormAbstractService {

	/* (non-Javadoc)
	 * @see com.armedia.acm.frevvo.config.FrevvoFormService#init()
	 */
	@Override
	public Object init() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.armedia.acm.frevvo.config.FrevvoFormService#get(java.lang.String)
	 */
	@Override
	public Object get(String action) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.armedia.acm.frevvo.config.FrevvoFormService#save(java.lang.String, org.springframework.util.MultiValueMap)
	 */
	@Override
	public boolean save(String xml,
			MultiValueMap<String, MultipartFile> attachments) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

    @Override
    public String getFormName()
    {
        return FrevvoFormName.CLOSE_CASE;
    }

}
