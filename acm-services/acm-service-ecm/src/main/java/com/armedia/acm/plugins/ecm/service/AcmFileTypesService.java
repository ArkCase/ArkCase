package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.plugins.ecm.exception.AcmFileTypesException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * Created by admin on 6/12/15.
 */
public interface AcmFileTypesService {

    Set<String> getFileTypes() throws AcmFileTypesException;
    Set<String> getForms() throws AcmFileTypesException;
}
