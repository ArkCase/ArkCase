package com.armedia.acm.correspondence.utils;

import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * Created by armdev on 12/15/14.
 */
public interface PoiWordGenerator
{
    void generate(Resource wordTemplate, OutputStream targetStream, Map<String, String> substitutions) throws IOException;
}
