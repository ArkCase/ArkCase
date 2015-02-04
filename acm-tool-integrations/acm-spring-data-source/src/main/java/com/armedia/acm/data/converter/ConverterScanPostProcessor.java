package com.armedia.acm.data.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import javax.persistence.Converter;
import javax.persistence.PersistenceException;
import java.io.IOException;
import java.net.URL;

/**
 * Most of this code copied from DefaultPersistenceUnitManager; the goal is to scan for and register converter
 * classes.  DefaultPersistenceUnitManager only scans for Entity, Embeddable, and MappedSuperclass, and there is no
 * way to configure it to scan for other JPA types.  So this class uses the same strategy to scan for @Converter.
 */
public class ConverterScanPostProcessor implements PersistenceUnitPostProcessor
{
    private String[] packagesToScan;

    private static final String ENTITY_CLASS_RESOURCE_PATTERN = "/**/*.class";

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private final TypeFilter converterFilter = new AnnotationTypeFilter(Converter.class, false);

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui)
    {
        log.debug("Scanning for converter classes");
        if (this.packagesToScan != null)
        {
            for (String pkg : this.packagesToScan)
            {
                log.debug("Checking for converters in package '" + pkg + "'");
                try
                {
                    String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                            ClassUtils.convertClassNameToResourcePath(pkg) + ENTITY_CLASS_RESOURCE_PATTERN;
                    Resource[] resources = this.resourcePatternResolver.getResources(pattern);
                    MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);
                    for (Resource resource : resources)
                    {
                        if (resource.isReadable())
                        {
                            MetadataReader reader = readerFactory.getMetadataReader(resource);
                            String className = reader.getClassMetadata().getClassName();
                            if (matchesFilter(reader, readerFactory))
                            {
                                pui.addManagedClassName(className);
                                log.debug("Added converter class '" + className + "'");
                                if (pui.getPersistenceUnitRootUrl() == null)
                                {
                                    URL url = resource.getURL();
                                    if (ResourceUtils.isJarURL(url))
                                    {
                                        pui.setPersistenceUnitRootUrl(ResourceUtils.extractJarFileURL(url));
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException ex)
                {
                    throw new PersistenceException("Failed to scan classpath for unlisted entity classes", ex);
                }
            }
        }
    }

    /**
     * Check whether any of the configured entity type filters matches
     * the current class descriptor contained in the metadata reader.
     */
    private boolean matchesFilter(MetadataReader reader, MetadataReaderFactory readerFactory) throws IOException
    {
        return converterFilter.match(reader, readerFactory);
    }

    public void setPackagesToScan(String... packagesToScan)
    {
        this.packagesToScan = packagesToScan;
    }
}
