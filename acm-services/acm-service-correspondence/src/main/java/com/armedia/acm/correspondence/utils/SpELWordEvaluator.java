package com.armedia.acm.correspondence.utils;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Darko.Dimitrievski
 */
public interface SpELWordEvaluator
{
    /**
     * Generating correspondence template, where the merge fields values are mapped by the SPEL expressions generated results
     *
     * @param wordTemplate - the correspondence template
     * @param targetStream
     * @param objectType
     * @param parentObjectId
     *
     * @throws IOException
     */
    void generate(Resource wordTemplate, OutputStream targetStream, String objectType, Long parentObjectId) throws IOException;
}
