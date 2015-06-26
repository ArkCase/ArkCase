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

    String PROP_FILE_TYPES = "fileTypes";
    String PROP_TYPE = "type";
    String PROP_FORM = "form";
    String PROP_LABEL = "label";

    String PROP_FORM_NAME_TPL = "%s.name";
    String PROP_FORM_TYPE_TPL = "%s.type";
    String PROP_FORM_MODE_TPL = "%s.mode";

}
