package com.armedia.acm.frevvo.config.service;

import com.armedia.acm.frevvo.config.FrevvoFormAbstractService;

import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by armdev on 11/3/14.
 */
public class FrevvoTestService extends FrevvoFormAbstractService
{
    @Override
    public Object init()
    {
        return null;
    }

    @Override
    public Object get(String action)
    {
        return null;
    }

    @Override
    public boolean save(String xml, MultiValueMap<String, MultipartFile> attachments) throws Exception
    {
        return false;
    }

    @Override
    public String getFormName()
    {
        return "test";
    }

    @Override
    public Class<?> getFormClass()
    {
        // Implementation no needed so far
        return null;
    }

    @Override
	public Object convertToFrevvoForm(Object obj, Object form) {
		// Implementation no needed so far
		return null;
	}
}
