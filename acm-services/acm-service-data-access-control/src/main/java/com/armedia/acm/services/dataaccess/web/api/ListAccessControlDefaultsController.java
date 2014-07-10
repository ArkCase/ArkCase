package com.armedia.acm.services.dataaccess.web.api;

import com.armedia.acm.core.query.QueryResultPageWithTotalCount;
import com.armedia.acm.services.dataaccess.dao.AcmAccessControlDefaultDao;
import com.armedia.acm.services.dataaccess.model.AcmAccessControlDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = { "/api/v1/plugin/dataaccess", "/api/latest/plugin/dataaccess" } )
public class ListAccessControlDefaultsController
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private AcmAccessControlDefaultDao accessControlDefaultDao;

    private String[] defaultSort = { "objectType", "objectState", "accessLevel", "accessorType" };

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/accessControlDefaults",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    QueryResultPageWithTotalCount<AcmAccessControlDefault> accessControlDefaults(
            @RequestParam(value = "s", required = false) String[] sort,
            @RequestParam(value = "start", required = false, defaultValue = "0") int startRow,
            @RequestParam(value = "n", required = false, defaultValue = "10") int maxRows,
            Authentication authentication)
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("Finding default access control; start row: " + startRow + "; max rows: " + maxRows);
        }

        // we support multiple sort params (multiple "s" request params) but Spring will send them as a
        // comma separated list.
        String[] sortOrder = sort == null || sort.length == 0 ?
                getDefaultSort() :
                sort;

        List<AcmAccessControlDefault> resultPage = getAccessControlDefaultDao().findPage(sortOrder, startRow, maxRows);
        int totalCount = getAccessControlDefaultDao().countAll();

        QueryResultPageWithTotalCount<AcmAccessControlDefault> retval = new QueryResultPageWithTotalCount<>();
        retval.setStartRow(startRow);
        retval.setMaxRows(maxRows);
        retval.setTotalCount(totalCount);
        retval.setResultPage(resultPage);

        return retval;


    }


    public AcmAccessControlDefaultDao getAccessControlDefaultDao()
    {
        return accessControlDefaultDao;
    }

    public void setAccessControlDefaultDao(AcmAccessControlDefaultDao accessControlDefaultDao)
    {
        this.accessControlDefaultDao = accessControlDefaultDao;
    }

    public String[] getDefaultSort()
    {
        return defaultSort;
    }

    public void setDefaultSort(String[] defaultSort)
    {
        this.defaultSort = defaultSort;
    }
}
