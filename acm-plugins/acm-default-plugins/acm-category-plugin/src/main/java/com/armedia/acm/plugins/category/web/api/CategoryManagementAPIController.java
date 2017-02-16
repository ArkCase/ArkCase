package com.armedia.acm.plugins.category.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.plugins.category.model.Category;
import com.armedia.acm.plugins.category.model.CategoryStatus;
import com.armedia.acm.plugins.category.service.CategoryService;
import com.armedia.acm.services.search.model.SolrCore;
import com.armedia.acm.services.search.service.ExecuteSolrQuery;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.stream.Collectors;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Feb 9, 2017
 *
 */
@Controller
@RequestMapping({ "/api/service/category/v1", "/api/service/category/latest" })
public class CategoryManagementAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private CategoryService categoryService;

    private ExecuteSolrQuery executeSolrQuery;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Category> getCategories(Authentication auth, @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "n", required = false, defaultValue = "10") int n,
            @RequestParam(value = "s", required = false, defaultValue = "ASC") String s) throws AcmObjectNotFoundException
    {
        String query = String.format("object_type_s:CATEGORY AND -parent_id_s:*&sort=queue_order_s %s", s);
        try
        {
            String solrResponse = executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, start, n, "");
            return extractCategories(solrResponse);
        } catch (MuleException | IOException e)
        {
            log.error("Error while executing Solr query: {}", query, e);
            throw new AcmObjectNotFoundException("Category", null, "Could not retreive the root categories.", e);
        }

    }

    @RequestMapping(value = "/{categoryId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Category getCategory(Authentication auth, @PathVariable(value = "categoryId") Long categoryId) throws AcmObjectNotFoundException
    {
        String query = String.format("object_type_s:CATEGORY AND object_id_s:%d&sort=queue_order_s ASC", categoryId);
        try
        {
            String solrResponse = executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, 0, 1, "");
            List<Category> result = extractCategories(solrResponse);
            if (result.isEmpty())
            {
                throw new AcmObjectNotFoundException("Category", categoryId, String.format("Category with id %d not found.", categoryId));
            }
            return result.get(0);
        } catch (MuleException | IOException e)
        {
            log.error("Error while executing Solr query: {}", query, e);
            throw new AcmObjectNotFoundException("Category", categoryId, String.format("Category with id %d not found.", categoryId), e);
        }
    }

    @RequestMapping(value = "/{categoryId}/children", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Category> getCategoryChildren(Authentication auth, @PathVariable(value = "categoryId") Long categoryId,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "n", required = false, defaultValue = "10") int n,
            @RequestParam(value = "s", required = false, defaultValue = "ASC") String s) throws AcmObjectNotFoundException
    {
        String query = String.format("object_type_s:CATEGORY AND parent_id_s:%d&sort=queue_order_s %s", categoryId, s);
        try
        {
            String solrResponse = executeSolrQuery.getResultsByPredefinedQuery(auth, SolrCore.ADVANCED_SEARCH, query, start, n, "");
            return extractCategories(solrResponse);
        } catch (MuleException | IOException e)
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
            } catch (AcmObjectNotFoundException e)
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
            } catch (AcmObjectNotFoundException e)
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

    /**
     * @param solrResponse
     * @return @throws IOException @throws
     */
    private List<Category> extractCategories(String solrResponse) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(solrResponse);
        JsonNode docsJson = json.get("response").get("docs");
        List<Category> categories = new ArrayList<>();
        SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        docsJson.forEach(doc -> {
            try
            {
                Category category = new Category();

                category.setId(doc.get("object_id_s").asLong());
                category.setName(doc.get("name").asText());
                category.setDescription(doc.get("description_no_html_tags_parseable").asText());
                // setParent
                category.setCreator(doc.get("creator_lcs").asText());
                category.setCreated(dateParser.parse(doc.get("create_date_tdt").asText()));
                category.setModifier(doc.get("modifier_lcs").asText());
                category.setModified(dateParser.parse(doc.get("modified_date_" + "tdt").asText()));
                category.setStatus(CategoryStatus.valueOf(doc.get("status_lcs").asText()));

                categories.add(category);
            } catch (ParseException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
