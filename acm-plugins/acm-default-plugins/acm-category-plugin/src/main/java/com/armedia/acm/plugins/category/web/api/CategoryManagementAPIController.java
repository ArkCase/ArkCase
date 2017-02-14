package com.armedia.acm.plugins.category.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUpdateObjectFailedException;
import com.armedia.acm.plugins.category.model.Category;
import com.armedia.acm.plugins.category.service.CategoryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Category> getCategories()
    {
        return categoryService.getRoot();
    }

    @RequestMapping(value = "/{categoryId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Category getCategory(@PathVariable(value = "categoryId") Long categoryId,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "n", required = false, defaultValue = "10") int n,
            @RequestParam(value = "s", required = false, defaultValue = "DESC") String s) throws AcmObjectNotFoundException
    {
        return categoryService.get(categoryId);
    }

    @RequestMapping(value = "/{categoryId}/children", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Category> getCategoryChildren(@PathVariable(value = "categoryId") Long categoryId,
            @RequestParam(value = "start", required = false, defaultValue = "0") int start,
            @RequestParam(value = "n", required = false, defaultValue = "10") int n,
            @RequestParam(value = "s", required = false, defaultValue = "DESC") String s) throws AcmObjectNotFoundException
    {
        return categoryService.getChildren(categoryId);
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
     * @param categoryService
     *            the categoryService to set
     */
    public void setCategoryService(CategoryService categoryService)
    {
        this.categoryService = categoryService;
    }

}
