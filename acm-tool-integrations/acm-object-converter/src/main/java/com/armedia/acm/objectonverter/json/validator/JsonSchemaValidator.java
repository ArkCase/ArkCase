package com.armedia.acm.objectonverter.json.validator;

/*-
 * #%L
 * Tool Integrations: Object Converter
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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.core.exceptions.InvalidSchemaException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.load.RefResolver;
import com.github.fge.jsonschema.core.load.SchemaLoader;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.processing.CachingProcessor;
import com.github.fge.jsonschema.core.processing.Processor;
import com.github.fge.jsonschema.core.processing.ProcessorMap;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.core.report.ListReportProvider;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.report.ReportProvider;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.tree.SimpleJsonTree;
import com.github.fge.jsonschema.library.Library;
import com.github.fge.jsonschema.processors.data.FullData;
import com.github.fge.jsonschema.processors.data.SchemaContext;
import com.github.fge.jsonschema.processors.data.ValidatorList;
import com.github.fge.jsonschema.processors.validation.SchemaContextEquivalence;
import com.github.fge.jsonschema.processors.validation.ValidationChain;
import com.google.common.base.Function;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Most of this class is cobbled together from the com.github.fge Json Schema Validator. The validator code is meant to
 * validate actual JSON documents against a schema; but this test is to verify the actual schema complies with the
 * schema specification.
 */
public class JsonSchemaValidator

{
    private static final Function<SchemaContext, JsonRef> FUNCTION = new Function<SchemaContext, JsonRef>()
    {
        @Override
        public JsonRef apply(final SchemaContext input)
        {
            return input.getSchema().getDollarSchema();
        }
    };

    public ProcessingReport validate(File schema) throws IOException, ProcessingException
    {
        JsonFactory jsonFactory = new JsonFactory();
        JsonParser jsonParser = jsonFactory.createParser(schema);
        jsonParser.enable(JsonParser.Feature.ALLOW_COMMENTS);

        ObjectMapper om = new ObjectMapper();
        JsonNode schemaNode = om.readTree(jsonParser);

        SchemaLoader loader = new SchemaLoader(LoadingConfiguration.byDefault());
        final SchemaTree tree = loader.load(schemaNode).setPointer(JsonPointer.empty());

        final FullData data = new FullData(tree, new SimpleJsonTree(schemaNode), false);
        ReportProvider reportProvider = new ListReportProvider(LogLevel.INFO, LogLevel.FATAL);
        final ProcessingReport report = reportProvider.newReport();

        final SchemaContext context = new SchemaContext(data);

        ValidationConfiguration validationConfiguration = ValidationConfiguration.byDefault();

        Processor<SchemaContext, ValidatorList> processor = buildProcessor(validationConfiguration, loader);

        final ValidatorList fullContext = processor.process(report, context);

        if (fullContext == null)
        {
            final ProcessingMessage message = collectSyntaxErrors(report, validationConfiguration);
            throw new InvalidSchemaException(message);
        }

        return report;
    }

    private Processor<SchemaContext, ValidatorList> buildProcessor(ValidationConfiguration validationCfg, SchemaLoader loader)
    {
        final RefResolver resolver = new RefResolver(loader);

        final Map<JsonRef, Library> libraries = validationCfg.getLibraries();
        final Library defaultLibrary = validationCfg.getDefaultLibrary();
        final ValidationChain defaultChain = new ValidationChain(resolver, defaultLibrary, validationCfg);
        final ProcessorMap<JsonRef, SchemaContext, ValidatorList> map = new ProcessorMap<>(FUNCTION);
        map.setDefaultProcessor(defaultChain);

        libraries.forEach((ref, value) -> map.addEntry(ref, new ValidationChain(resolver, value, validationCfg)));

        final Processor<SchemaContext, ValidatorList> processor = map.getProcessor();
        return new CachingProcessor<>(processor, SchemaContextEquivalence.getInstance());
    }

    private ProcessingMessage collectSyntaxErrors(final ProcessingReport report, ValidationConfiguration validationConfiguration)
    {
        final String msg = validationConfiguration.getSyntaxMessages().getMessage("core.invalidSchema");
        final ArrayNode arrayNode = JacksonUtils.nodeFactory().arrayNode();
        JsonNode node;
        for (final ProcessingMessage message : report)
        {
            node = message.asJson();
            if ("syntax".equals(node.path("domain").asText()))
            {
                arrayNode.add(node);
            }
        }
        return new ProcessingMessage().setMessage(msg + "\nSyntax errors:\n" + JacksonUtils.prettyPrint(arrayNode));
    }
}
