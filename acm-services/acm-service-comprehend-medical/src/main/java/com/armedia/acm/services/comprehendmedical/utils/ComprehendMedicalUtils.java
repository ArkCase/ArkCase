package com.armedia.acm.services.comprehendmedical.utils;

/*-
 * #%L
 * ACM Service: Comprehend Medical
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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

import com.armedia.acm.services.comprehendmedical.model.ComprehendMedicalEntity;
import com.armedia.acm.services.comprehendmedical.model.ComprehendMedicalEntityAttribute;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 05/12/2020
 */
public class ComprehendMedicalUtils
{
    private static Logger LOG = LoggerFactory.getLogger(ComprehendMedicalUtils.class);

    public static <T> Predicate<T> distinctByProperty(Function<? super T, ?> propertyExtractor)
    {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(propertyExtractor.apply(t));
    }

    public static List<ComprehendMedicalEntity> getEntitiesFromOutput(String output)
    {
        List<ComprehendMedicalEntity> result = new ArrayList<>();

        if (StringUtils.isEmpty(output)) return result;

        try
        {
            JSONObject object = new JSONObject(output);
            if (object != null && object.has("Entities")) {
                JSONArray entities = object.getJSONArray("Entities");
                if (entities != null && entities.length() > 0) {
                    for (int i = 0; i < entities.length(); i++) {
                        JSONObject entity = entities.getJSONObject(i);
                        ComprehendMedicalEntity comprehendMedicalEntity = new ComprehendMedicalEntity();
                        comprehendMedicalEntity.setText(entity.has("Text") ? entity.getString("Text") : "");
                        comprehendMedicalEntity.setCategory(entity.has("Category") ? entity.getString("Category") : "");
                        comprehendMedicalEntity.setType(entity.has("Type") ? entity.getString("Type") : "");
                        if (entity.has("Attributes")) {
                            JSONArray attributes = entity.getJSONArray("Attributes");
                            List<ComprehendMedicalEntityAttribute> entityAttributes = new ArrayList<>();
                            if (attributes.length() > 0) {
                                for (int j = 0; j < attributes.length(); j++) {
                                    JSONObject attribute = attributes.getJSONObject(j);
                                    ComprehendMedicalEntityAttribute comprehendMedicalEntityAttribute = new ComprehendMedicalEntityAttribute();
                                    comprehendMedicalEntityAttribute.setText(attribute.has("Text") ? attribute.getString("Text") : "");
                                    comprehendMedicalEntityAttribute.setType(attribute.has("Type") ? attribute.getString("Type") : "");

                                    entityAttributes.add(comprehendMedicalEntityAttribute);
                                }
                            }

                            comprehendMedicalEntity.setAttributes(entityAttributes);
                        }

                        result.add(comprehendMedicalEntity);
                    }
                }
            }
        }
        catch (Exception e)
        {
            LOG.warn("Cannot parse JSON output=[{}]", output);
        }

        return result;
    }
}
