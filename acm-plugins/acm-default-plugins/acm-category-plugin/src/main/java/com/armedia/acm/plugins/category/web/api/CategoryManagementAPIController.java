package com.armedia.acm.plugins.category.web.api;

/*-
 * #%L
 * ACM Default Plugin: Categories
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.DESCRIPTION_NO_HTML_TAGS_PARSEABLE;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.PARENT_ID_S;
import static com.armedia.acm.services.search.model.solr.SolrAdditionalPropertiesConstants.STATUS_LCS;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.objectonverter.DateFormats;
import com.armedia.acm.plugins.category.model.Category;
import com.armedia.acm.plugins.category.model.CategoryStatus;
import com.armedia.acm.plugins.category.service.CategoryService;
import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.search.model.solr.GetResponseGenerator;
import com.armedia.acm.services.search.model.solr.PayloadProducer;
import com.armedia.acm.services.search.model.solr.ResponseHeader;
import com.armedia.acm.services.search.model.solr.ResponseHeaderProducer;
import com.armedia.acm.services.search.model.solr.SolrCore;
import com.armedia.acm.services.search.model.solr.SolrSearchResponse;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.fasterxml.jackson.databind.JsonNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Feb 9, 2017
 *
 */
@Controller
@RequestMapping({ "/api/service/category/v1", "/api/service/category/latest" })
public class CategoryManagementAPIController
{
    private Logger log = LogManager.getLogger(getClass());

    private CategoryService categoryService;

    private ExecuteSolrQuery executeSolrQuery;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SolrSearchResponse<ResponseHeader, List<Category>> getCategories(Authentication auth,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "n", required = false, defaultValue = "10") int n,
            @RequestParam(value = "s", required = false, defaultValue = "ASC") String s) throws AcmObjectNotFoundException
    {
        String query = String.format("object_type_s:CATEGORY AND -parent_id_s:*&sort=title_parseable %s", s);
        try
        {
            SolrSearchResponse<ResponseHeader, List<Category>> response = generateGetResponse(auth, query, start, n,
                    Optional.<ResponseHeaderProducer<ResponseHeader>> empty(), this::extractCategories);
            return response;
        }
        catch (IOException | SolrException e)
        {
            log.error("Error while executing Solr query: {}", query, e);
            throw new AcmObjectNotFoundException("Category", null, "Could not retreive the root categories.", e);
        }

    }

    @RequestMapping(value = "/{categoryId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SolrSearchResponse<ResponseHeader, Category> getCategory(Authentication auth,
            @PathVariable(value = "categoryId") Long categoryId) throws AcmObjectNotFoundException
    {
        String query = String.format("object_type_s:CATEGORY AND object_id_s:%d&sort=title_parseable ASC", categoryId);
        try
        {
            SolrSearchResponse<ResponseHeader, Category> response = generateGetResponse(auth, query, 0, 1,
                    Optional.<ResponseHeaderProducer<ResponseHeader>> empty(), node -> {

                        List<Category> result = extractCategories(node);
                        if (result.isEmpty())
                        {
                            throw new AcmObjectNotFoundException("Category", categoryId,
                                    String.format("Category with id %d not found.", categoryId));
                        }
                        return result.get(0);

                    });
            return response;
        }
        catch (IOException | SolrException e)
        {
            log.error("Error while executing Solr query: {}", query, e);
            throw new AcmObjectNotFoundException("Category", categoryId, String.format("Category with id %d not found.", categoryId), e);
        }
    }

    @RequestMapping(value = "/{categoryId}/children", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public SolrSearchResponse<ResponseHeader, List<Category>> getCategoryChildren(Authentication auth,
            @PathVariable(value = "categoryId") Long categoryId,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "n", required = false, defaultValue = "10") int n,
            @RequestParam(value = "s", required = false, defaultValue = "ASC") String s) throws AcmObjectNotFoundException
    {
        String query = String.format("object_type_s:CATEGORY AND parent_id_s:%d&sort=title_parseable %s", categoryId, s);
        try
        {
            SolrSearchResponse<ResponseHeader, List<Category>> response = generateGetResponse(auth, query, start, n,
                    Optional.<ResponseHeaderProducer<ResponseHeader>> empty(), this::extractCategories);
            return response;
        }
        catch (IOException | SolrException e)
        {
            log.error("Error while executing Solr query: {}", query, e);
            throw new AcmObjectNotFoundException("Category", categoryId,
                    String.format("Categories for parent Category with id %d not found.", categoryId), e);
        }

    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Category createCategory(@RequestBody Category category) throws AcmCreateObjectFailedException
    {
        return categoryService.create(category);
    }

    @RequestMapping(value = "/{parentCategoryId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Category createSubcategory(@PathVariable(value = "parentCategoryId") Long parentCategoryId, @RequestBody Category childCategory)
            throws AcmCreateObjectFailedException, AcmObjectNotFoundException
    {
        return categoryService.create(parentCategoryId, childCategory);
    }

    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Category updateCategory(@RequestBody Category category) throws AcmObjectNotFoundException, AcmUpdateObjectFailedException
    {
        return categoryService.update(category);
    }

    @RequestMapping(value = "/{categoryId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCategory(@PathVariable(value = "categoryId") Long categoryId) throws AcmObjectNotFoundException
    {
        categoryService.delete(categoryId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/activate", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Category> activateCategories(@RequestBody List<Long> categoryIds,
            @RequestParam(value = "activateChildren", required = false, defaultValue = "false") boolean activateChildren)
            throws AcmObjectNotFoundException, AcmUpdateObjectFailedException
    {
        return categoryIds.stream().map(categoryId -> {
            try
            {
                categoryService.activate(categoryId, activateChildren);
                return categoryService.get(categoryId);
            }
            catch (AcmObjectNotFoundException e)
            {
                log.warn(String.format("Failed to activate category with id %d.", categoryId), e);
                return null;
            }

        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @RequestMapping(value = "/deactivate", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Category> deactivateCategories(@RequestBody List<Long> categoryIds)
            throws AcmObjectNotFoundException, AcmUpdateObjectFailedException
    {
        return categoryIds.stream().map(categoryId -> {
            try
            {
                categoryService.deactivate(categoryId);
                return categoryService.get(categoryId);
            }
            catch (AcmObjectNotFoundException e)
            {
                log.warn(String.format("Failed to deactivate category with id %d.", categoryId), e);
                return null;
            }

        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @RequestMapping(value = "/{categoryId}/objects", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public Long getObjectCount(Long categoryId)
    {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    private <K extends ResponseHeader, T> SolrSearchResponse<K, T> generateGetResponse(Authentication auth, String query, int start, int n,
            Optional<ResponseHeaderProducer<K>> headerProducer, PayloadProducer<T> payloadProducer)
            throws SolrException, IOException, AcmObjectNotFoundException
    {
        String solrResponse = executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, start, n, "");
        return GetResponseGenerator.generateGetResponse(solrResponse, headerProducer, payloadProducer);
    }

    /**
     * @param solrResponse
     * @return @throws IOException @throws
     */
    private List<Category> extractCategories(JsonNode jsonSolrResponse) throws IOException
    {
        JsonNode docsJson = jsonSolrResponse.get("response").get("docs");
        List<Category> categories = new ArrayList<>();
        SimpleDateFormat dateParser = new SimpleDateFormat(DateFormats.DEFAULT_DATE_FORMAT);
        docsJson.forEach(doc -> {
            try
            {
                Category category = new Category();

                category.setId(doc.get("object_id_s").asLong());
                category.setName(doc.get("name").asText());
                category.setDescription(doc.get(DESCRIPTION_NO_HTML_TAGS_PARSEABLE).asText());
                JsonNode parentIdNode = doc.get(PARENT_ID_S);
                if (parentIdNode != null)
                {
                    Category parent = new Category();
                    parent.setId(parentIdNode.asLong());
                    category.setParent(parent);
                }
                category.setCreator(doc.get("creator_lcs").asText());
                category.setCreated(dateParser.parse(doc.get("create_date_tdt").asText()));
                category.setModifier(doc.get("modifier_lcs").asText());
                category.setModified(dateParser.parse(doc.get("modified_date_tdt").asText()));
                category.setStatus(CategoryStatus.valueOf(doc.get(STATUS_LCS).asText()));

                categories.add(category);
            }
            catch (ParseException e)
            {
                log.error("Error while parsing date: {} or {}", doc.get("create_date_tdt").asText(), doc.get("modified_date_tdt").asText(),
                        e);
            }
        });
        return categories;
    }

    /**
     * @param categoryService
     *            the categoryService to set
     */
    public void setCategoryService(CategoryService categoryService)
    {
        this.categoryService = categoryService;
    }

    /**
     * @param executeSolrQuery
     *            the executeSolrQuery to set
     */
    public void setExecuteSolrQuery(ExecuteSolrQuery executeSolrQuery)
    {
        this.executeSolrQuery = executeSolrQuery;
    }

}
