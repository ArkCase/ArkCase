/**
 *
 */
package com.armedia.acm.plugins.category.service;

import static com.armedia.acm.plugins.category.model.CategoryStatus.DEACTIVATED;

import com.armedia.acm.plugins.category.model.Category;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.junit.Test;

import java.util.Date;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Feb 10, 2017
 *
 */
public class CategoryTest
{

    @Test
    public void test() throws Exception
    {
        Date created = new Date();
        Category parent = new Category();
        parent.setName("parent");
        parent.setDescription("parent");
        parent.setCreator("creator");
        parent.setCreated(created);
        parent.setModifier("creator");
        parent.setModified(created);
        parent.setStatus(DEACTIVATED);

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String categoryAsString = mapper.writeValueAsString(parent);
        System.out.println(categoryAsString);
    }

}
