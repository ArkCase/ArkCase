package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.plugins.ecm.exception.AcmFileTypesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by admin on 6/12/15.
 */
public interface AcmFileTypesService {

    List<String> getFileTypes() throws AcmFileTypesException;
    List<String> getForms() throws AcmFileTypesException;
}
