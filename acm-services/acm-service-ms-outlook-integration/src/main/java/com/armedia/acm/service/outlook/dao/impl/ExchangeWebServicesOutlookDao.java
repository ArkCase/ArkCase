package com.armedia.acm.service.outlook.dao.impl;

import com.armedia.acm.service.outlook.dao.OutlookDao;
import com.armedia.acm.service.outlook.exception.AcmOutlookConnectionFailedException;
import com.armedia.acm.service.outlook.exception.AcmOutlookFindItemsFailedException;
import com.armedia.acm.service.outlook.model.AcmOutlookUser;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.response.ServiceResponse;
import microsoft.exchange.webservices.data.core.response.ServiceResponseCollection;
import microsoft.exchange.webservices.data.core.service.folder.Folder;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.enumeration.BasePropertySet;
import microsoft.exchange.webservices.data.enumeration.ExchangeVersion;
import microsoft.exchange.webservices.data.enumeration.SortDirection;
import microsoft.exchange.webservices.data.enumeration.WellKnownFolderName;
import microsoft.exchange.webservices.data.property.definition.PropertyDefinition;
import microsoft.exchange.webservices.data.property.definition.PropertyDefinitionBase;
import microsoft.exchange.webservices.data.search.FindItemsResults;
import microsoft.exchange.webservices.data.search.ItemView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.Map;
import java.util.Objects;

/**
 * Created by armdev on 4/20/15.
 */
public class ExchangeWebServicesOutlookDao implements OutlookDao
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private ExchangeVersion exchangeVersion = ExchangeVersion.Exchange2007_SP1;
    private Map<String, PropertyDefinition> sortFields;
    private final PropertySet standardProperties = new PropertySet(
        BasePropertySet.IdOnly,
        ItemSchema.Subject,
        ItemSchema.DateTimeSent,
        ItemSchema.DateTimeCreated,
        ItemSchema.DateTimeReceived,
        ItemSchema.LastModifiedTime,
        ItemSchema.Body,
        ItemSchema.Size);

    @Override
    @Cacheable(value="outlook-connection-cache", key="#user.emailAddress")
    public ExchangeService connect(AcmOutlookUser user) throws AcmOutlookConnectionFailedException
    {
        Objects.requireNonNull(user, "User cannot be null");
        Objects.requireNonNull(user.getOutlookPassword(), "Password cannot be null");
        Objects.requireNonNull(user.getEmailAddress(), "E-mail address cannot be null");

        ExchangeService service = new ExchangeService(getExchangeVersion());
        ExchangeCredentials credentials = new WebCredentials(user.getEmailAddress(), user.getOutlookPassword());
        service.setCredentials(credentials);

        try
        {
            service.autodiscoverUrl(user.getEmailAddress(), redirectionUrl -> true);
            return service;
        }
        catch (Exception e)
        {
            log.error("Could not connect to Exchange: " + e.getMessage(), e);
            throw new AcmOutlookConnectionFailedException(e.getMessage(), e);
        }
    }

    @Override
    @CacheEvict(value="outlook-connection-cache", key="#user.emailAddress")
    public void disconnect(AcmOutlookUser user)
    {
        // EWS apparently has no concept of "logging out" so the whole point of this method is to
        // remove the connection from the connection cache.
        log.info("Exchange session has been removed from session cache");
    }

    @Override
    public FindItemsResults<Item> findItems(
            ExchangeService service,
            WellKnownFolderName wellKnownFolderName,
            PropertySet extraFieldsToRetrieve,
            int start,
            int maxItems,
            String sortProperty,
            boolean sortAscending)
            throws AcmOutlookFindItemsFailedException
    {
        try
        {
            log.debug("finding tasks");

            Folder folder = Folder.bind(service, wellKnownFolderName);

            ItemView view = new ItemView(maxItems, start);

            PropertyDefinition orderBy =
                    sortProperty == null || sortProperty.trim().isEmpty() || !getSortFields().containsKey(sortProperty) ?
                            ItemSchema.DateTimeReceived : getSortFields().get(sortProperty);

            SortDirection sortDirection = sortAscending ? SortDirection.Ascending : SortDirection.Descending;

            view.getOrderBy().add(orderBy, sortDirection);

            FindItemsResults<Item> findResults = service.findItems(folder.getId(), view);

            PropertySet allProperties = new PropertySet();

            allProperties.addRange(standardProperties);

            allProperties.addRange(extraFieldsToRetrieve);

            if ( !findResults.getItems().isEmpty() )
            {
                service.loadPropertiesForItems(findResults.getItems(), allProperties);
            }

            return findResults;
        }
        catch (Exception e)
        {
            log.error("Could not list items: " + e.getMessage(), e);
            throw new AcmOutlookFindItemsFailedException(e.getMessage(), e);
        }

    }

    public Map<String, PropertyDefinition> getSortFields()
    {
        return sortFields;
    }

    public void setSortFields(Map<String, PropertyDefinition> sortFields)
    {
        this.sortFields = sortFields;
    }

    public ExchangeVersion getExchangeVersion()
    {
        return exchangeVersion;
    }

    public void setExchangeVersion(ExchangeVersion exchangeVersion)
    {
        this.exchangeVersion = exchangeVersion;
    }
}
