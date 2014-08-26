package com.armedia.acm.services.search.service;

import com.armedia.acm.files.propertymanager.PropertyFileManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by armdev on 6/17/14.
 */
public class AcmQuickSearchJpaSolrGenerator
{


    /**
     * The default run date to use if this generator has never run before (or if the properties file that stores the
     * last run date is missing)
     */
    private static final String DEFAULT_LAST_RUN_DATE = "1970-01-01T00:00:00Z";

    /**
     * The property key to use in the properties file that stores the last run date.
     */
    private static final String SOLR_LAST_RUN_DATE_PROPERTY_KEY = "solr.last.run.date";


    public static final String SOLR_ID_PROPERTY = "id";
    public static final String SOLR_OBJECT_OWNER_PROPERTY = "owner_s";

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * The entity class to query from the database; must be a JPA entity class.
     */
    private Class entityClass;

    /**
     * Location of the properties file that stores the last time this generator was run (the last run date).
     */
    private String lastUpdatedPropertyFileLocation;

    /**
     * Map of quick search field names to entity properties.  The keys must be valid SOLR field names; the values
     * must be Java properties from the entity class.
     * <p/>
     * Keys may include the following values:
     * <ul>
     *     <li>object_id_s: required; the unique id, typically the database primary key</li>
     *     <li>title</li>
     *     <li>last_modified: must be in SOLR_DATE_FORMAT format</li>
     *     <li>create_dt: must be in SOLR_DATE_FORMAT format</li>
     *     <li>name: object name, e.g. complaint number for complaints, or task name for tasks.</li>
     *     <li>status_s: current status, e.g. APPROVED, ACTIVE, CLOSED ...</li>
     *     <li>author: user id of the user who created this object</li>
     *     <li>modifier_s: user id of the user who last modified this object</li>
     *     <li>assignee: user id of the user who is currently responsible for this object, if any (e.g. case agent,
     *     task assgnee</li>
     * </ul>
     * <p/>
     * The corresponding values must be property names from the entity class.  If there is no appropriate property
     * for one of the above fields (e.g. if the entity has no assignee property), do not include the assignee in
     * this map.
     */
    private Map<String, String> quickSearchFieldToEntityPropertyMap;

    /**
     * Number of objects to retrieve in any one database query.  Queries will be executed until no more records are
     * found.
     */
    private int batchSize;

    /**
     * Name of the object type to be queried by this generator; this value becomes the "object_type_s" field in SOLR.
     */
    private String objectType;

    /**
     * Name of the primary key property of the business entity.  Used for sorting in the batch query.
     */
    private String idProperty;

    /**
     * Name of the last modified property of the business entity.  Used in the WHERE clause of the batch query.
     */
    private String lastModifiedProperty;

    /**
     * Name of the property whose value is considered the "owner" of this object.  For complaints, it would be the
     * creator; for case files, the case agent.  Set this to a field containing a user id.  This value must also be
     * a key in the "quickSearchFieldToEntityPropertyMap" map.
     */
    private String ownerProperty;

    /**
     * Internal use only (not specified via Spring).  The values from the quickSearchFieldToEntityPropertyMap.
     */
    private List<String> businessObjectProperties;

    /**
     * Internal use only (not specified via Spring).  The keys from the quickSearchFieldToEntityPropertyMap.
     */
    private List<String> quickSearchFields;

    /**
     * Used to call the Mule flow that submits the business object JSON to SOLR.
     */
    private MuleClient muleClient;

    /**
     * Used to store and load the last run date from the properties file.
     */
    private PropertyFileManager propertyFileManager;

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Convert the map of quick search field names to business object fields into two separate lists.  One is the list
     * of business object properties to query from the database; this list is used by the getBusinesObjects method; this
     * method returns a list of object arrays where each array includes property values in the same order as the
     * business object properties list.  The other is the list of quick search field names; this list is used by
     * the businessObjectToMaps method.
     * <p/>
     * This method is called by Spring when the application context starts.
     */
    public void initBean()
    {
        // we need a list of properties to query from the database, and a list of properties to send to SOLR; both
        // in a guaranteed consistent order that matches the quick search to entity property map.
        businessObjectProperties = new ArrayList<>(getQuickSearchFieldToEntityPropertyMap().size());
        quickSearchFields = new ArrayList<>(getQuickSearchFieldToEntityPropertyMap().size());

        for ( Map.Entry<String, String> qsToEntity : getQuickSearchFieldToEntityPropertyMap().entrySet())
        {
            businessObjectProperties.add(qsToEntity.getValue());
            quickSearchFields.add(qsToEntity.getKey());
        }

    }

    /**
     * Determine when the last time this generator was run (if ever), and start the batch update to send all
     * business entities modified since that date to SOLR.
     */
    @Scheduled(fixedDelay = 60000)
    public void updateSolr()
    {
        try
        {
            // retrieve the last time we sent updates to SOLR.  If we've never sent updates, or the properties
            // file is missing, we use a default last update date (Jan 1 1970).
            String lastRunDate = getPropertyFileManager().load(
                    getLastUpdatedPropertyFileLocation(),
                    SOLR_LAST_RUN_DATE_PROPERTY_KEY,
                    DEFAULT_LAST_RUN_DATE);

            if ( log.isDebugEnabled() )
            {
                log.debug("last run date: " + lastRunDate);
            }

            DateFormat solrDateFormat = new SimpleDateFormat(SearchConstants.SOLR_DATE_FORMAT);
            Date sinceWhen = solrDateFormat.parse(lastRunDate);

            // store the current time as the last run date to use the next time this job runs.  This allows us to
            // scan only for objects updated since this date.
            String solrNow = solrDateFormat.format(new Date());
            getPropertyFileManager().store(SOLR_LAST_RUN_DATE_PROPERTY_KEY, solrNow, getLastUpdatedPropertyFileLocation());

            // now we actually scan for objects created or modified since the last run date, and send them to SOLR.
            batchSolrUpdate(sinceWhen);
        }
        catch (ParseException | MuleException | JsonProcessingException e)
        {
            log.error("Could not send index updates to SOLR: " + e.getMessage(), e);
        }
    }

    /**
     * Run a batch of SOLR updates.  Keep executing the JPA query until no more results are found.  Only entities
     * modified since the sinceWhen parameter are processed.
     * @param sinceWhen Only entities modified since this time will be sent to SOLR.
     * @throws ParseException If the date formatting fails (should never happen).
     * @throws MuleException If Mule throws an exception (e.g. if SOLR is not running)
     * @throws JsonProcessingException If the objects cannot be translated to JSON (should never happen).
     */
    protected void batchSolrUpdate(Date sinceWhen) throws ParseException, MuleException, JsonProcessingException
    {
        int current = 0;
        int batchSize = getBatchSize();

        ObjectMapper mapper = new ObjectMapperFactory().createObjectMapper();

        boolean debug = log.isDebugEnabled();

        // keep retrieving another batch of objects modified since the last update, until we find no more objects.
        List<Object[]> objects;
        do
        {
            objects = getBusinessObjects(sinceWhen, current, batchSize);
            if ( debug )
            {
                log.debug("Number of objects: " + objects.size());
            }

            if ( !objects.isEmpty() )
            {
                current += batchSize;

                List<Map<String, Object>> quickSearchMaps = businessObjectsToMaps(objects);

                String json = mapper.writeValueAsString(quickSearchMaps);

                if ( debug )
                {
                    log.debug("Quick search JSON: " + json);
                }

                MuleMessage fromSolr = getMuleClient().send(SearchConstants.QUICK_SEARCH_JMS_QUEUE_NAME, json, null);

                Object muleResponse = fromSolr.getPayload();

                String solrResponse = extractSolrResponse(muleResponse);
                log.info("SOLR response: " + solrResponse);
            }
        }
        while ( !objects.isEmpty() );
    }

    protected String extractSolrResponse(Object muleResponse)
    {
        if ( muleResponse instanceof List)
        {
            List<?> response = (List<?>) muleResponse;

            for ( Object i : response )
            {
                // solr seems to send back a byte array
                if ( i instanceof byte[] )
                {
                    byte[] bytes = (byte[]) i;
                    String s = new String(bytes);
                    return s;
                }
            }
        }
        return "[could not interpret SOLR response - response type: " + muleResponse.getClass().getName() + "]";
    }

    /**
     * Convert properties retrieved from JPA into quick search objects.
     * @param businessObjects List of object arrays.  Each array in the list must have the same size as all the other
     *                        arrays, and the values must be in the same order as the values in the quickSearchFields
     *                        list.  These conditions will be met for for lists of object arrays from the
     *                        getBusinessObjects method.
     * @return Map of quick search objects.  Each map will have two more than the number of entries in the input object
     *         arrays.  The extra entries hold the object type and the SOLR unique ID.  The SOLR unique ID is the
     *         concatenation of the object_id_s and the object type.  The other map keys are the quick search index field
     *         names (from the quickSearchFields list).
     */
    protected List<Map<String, Object>> businessObjectsToMaps(List<Object[]> businessObjects)
    {
        List<Map<String, Object>> retval = new ArrayList<>(businessObjects.size());

        boolean trace = log.isTraceEnabled();

        for ( Object[] properties : businessObjects )
        {
            // allocate three extra map entries: one for object type, one for ID, one for object owner
            Map<String, Object> objectMap = new HashMap<>(properties.length + 3);
            objectMap.put(SearchConstants.SOLR_OBJECT_TYPE_FIELD_NAME, objectType);


            int fieldPosition = 0;
            for ( String quickSearchKey : quickSearchFields )
            {
                Object value = properties[fieldPosition];
                ++fieldPosition;

                if ( trace )
                {
                    log.trace(quickSearchKey + " = " + value + " [" + (value == null ? "null" : value.getClass().getName()) + "]");
                }

                objectMap.put(quickSearchKey, value);
            }

            objectMap.put(SOLR_ID_PROPERTY, objectMap.get("object_id_s") + "-" + getObjectType());
            objectMap.put(SOLR_OBJECT_OWNER_PROPERTY, objectMap.get(getOwnerProperty()));
            retval.add(objectMap);
        }

        return retval;
    }

    /**
     * Retrieve list of object arrays from the database using a JPA query.  The entityClass property is the
     * "FROM" clause.  The businessObjectProperties property is the column list (the businessObjectProperties property
     * is derived from the quickSearchFieldToEntityPropertyMap property).
     *
     * @param sinceWhen Only entities with a last modified date since this date will be returned.  The
     *                  lastModifiedProperty property is the entity last modified column (e.g., the query filters by
     *                  "WHERE <getLastModifiedProperty() &gt;= sinceWhen").
     * @param firstResult Enables paging.  Since in principle this query could return an arbitrary number of results,
     *                    we need to limit each query to a certain amount of maximum rows.
     * @param maxResults Enables paging.  Only up to this number of results will be returned.
     * @return List of object arrays.  The list includes all entities found by the query.  Each object array includes
     *         the elements specified by the businessObjectProperties property, in the same order as the
     *         businessObjectProperties list.
     */
    @Transactional(readOnly = true)
    protected List<Object[]> getBusinessObjects(Date sinceWhen, int firstResult, int maxResults )
    {
        if ( log.isDebugEnabled() )
        {
            log.debug("getting objects updated since: " + sinceWhen);
        }

        String query = "SELECT ";
        boolean addComma = false;
        for ( String entityProperty : businessObjectProperties )
        {
            if ( addComma )
            {
                query += ", ";
            }
            else
            {
                addComma = true;
            }
            query += "e." + entityProperty;
        }
        query += " FROM " + getEntityClass().getSimpleName() + " e ";
        if ( sinceWhen != null )
        {
            query += " WHERE e." + getLastModifiedProperty() + " >= :lastModified";
        }
        query += " ORDER BY e." + getIdProperty();

        if ( log.isDebugEnabled() )
        {
            log.debug("Quick search query: " + query);
        }


        Query entityQuery = getEntityManager().createQuery(query);
        if ( sinceWhen != null )
        {
            entityQuery.setParameter("lastModified", sinceWhen, TemporalType.TIMESTAMP);
        }
        entityQuery.setFirstResult(firstResult);
        entityQuery.setMaxResults(maxResults);

        return entityQuery.getResultList();
    }


    public EntityManager getEntityManager()
    {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    public Map<String, String> getQuickSearchFieldToEntityPropertyMap()
    {
        return Collections.unmodifiableMap(quickSearchFieldToEntityPropertyMap);
    }

    public void setQuickSearchFieldToEntityPropertyMap(Map<String, String> quickSearchFieldToEntityPropertyMap)
    {
        this.quickSearchFieldToEntityPropertyMap = new HashMap<>(quickSearchFieldToEntityPropertyMap);
    }

    public int getBatchSize()
    {
        return batchSize;
    }

    public void setBatchSize(int batchSize)
    {
        this.batchSize = batchSize;
    }

    public Class getEntityClass()
    {
        return entityClass;
    }

    public void setEntityClass(Class entityClass)
    {
        this.entityClass = entityClass;
    }

    public String getIdProperty()
    {
        return idProperty;
    }

    public void setIdProperty(String idProperty)
    {
        this.idProperty = idProperty;
    }

    public String getLastModifiedProperty()
    {
        return lastModifiedProperty;
    }

    public void setLastModifiedProperty(String lastModifiedProperty)
    {
        this.lastModifiedProperty = lastModifiedProperty;
    }

    public MuleClient getMuleClient()
    {
        return muleClient;
    }

    public void setMuleClient(MuleClient muleClient)
    {
        this.muleClient = muleClient;
    }

    public String getObjectType()
    {
        return objectType;
    }

    public void setObjectType(String objectType)
    {
        this.objectType = objectType;
    }

    public void setLastUpdatedPropertyFileLocation(String lastUpdatedPropertyFileLocation)
    {
        this.lastUpdatedPropertyFileLocation = lastUpdatedPropertyFileLocation;
    }

    public String getLastUpdatedPropertyFileLocation()
    {
        return lastUpdatedPropertyFileLocation;
    }

    public PropertyFileManager getPropertyFileManager()
    {
        return propertyFileManager;
    }

    public void setPropertyFileManager(PropertyFileManager propertyFileManager)
    {
        this.propertyFileManager = propertyFileManager;
    }

    public String getOwnerProperty()
    {
        return ownerProperty;
    }

    public void setOwnerProperty(String ownerProperty)
    {
        this.ownerProperty = ownerProperty;
    }
}
