package com.armedia.acm.userinterface.angular;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;
import org.zeroturnaround.exec.stream.slf4j.Slf4jDebugOutputStream;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Copy all angular resources from the base ArkCase WAR file and also from any ArkCase extension jars, run the
 * Angular front-end build tools, and copy the assembled application into the deployment folder.  Before copying any
 * files to a deployment folder, remove current contents of that folder.
 * <p>
 * The ArkCase WAR file should configure the deployment folder in a Tomcat context resources element, such that
 * files in this deployment folder are treated as if they were in the root folder of the war file itself.
 * <p>
 * Node.js and npm (the Node.js Package Manager) must be installed on the deployment host, and npm must be in the
 * system path.
 * <p>
 * The resources to be copied from the war file and extension jars; the front-end commands to be run (e.g. npm,
 * bower, grunt); and the resources to be copied to the deployment folder are configured in Spring.  All resources to
 * be copied from the war file and extension jars must be within a top-level resources folder.
 */
public class AngularResourceCopier implements ServletContextAware
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private String tempFolderPath;
    private String deployFolderPath;
    private List<String> resourceFoldersToCopyFromArchive;
    private List<String> assembledFoldersToCopyToDeployment;
    private List<String> filesToCopyFromArchive;
    private List<String> assembledFilesToCopyToDeployment;
    private List<String> frontEndCommandsToBeExecuted;
    private List<String> oldDeployFoldersToBeRemovedBeforeBuild;


    @Override
    public void setServletContext(ServletContext servletContext)
    {
        copyAngularResources(servletContext);
    }

    public void copyAngularResources(ServletContext servletContext)
    {
        try
        {
            File tmpDir = cleanAndCreateResourceTempFolder();

            ServletContextResourcePatternResolver resolver = new ServletContextResourcePatternResolver(servletContext);

            Resource modulesRoot = resolver.getResource(AngularResourceConstants.WAR_ANGULAR_RESOURCE_PATH);
            log.debug("modulesRoot: {}", modulesRoot);
            String rootPath = modulesRoot.getFile().getCanonicalPath();

            for (String oldDeployFolder : getOldDeployFoldersToBeRemovedBeforeBuild())
            {
                File oldFolder = new File(tmpDir.getCanonicalPath() + oldDeployFolder);
                log.debug("Old folder path: {}", oldFolder.getCanonicalPath());
                if (oldFolder.exists())
                {
                    log.debug("Removing old folder {}", oldFolder.getCanonicalPath());
                    FileSystemUtils.deleteRecursively(oldFolder);
                }
            }
            for (String resourceFolder : getResourceFoldersToCopyFromArchive())
            {
                copyResources(resolver, rootPath, tmpDir, resourceFolder);
            }
            for (String resourceFile : getFilesToCopyFromArchive())
            {
                copyFile(resolver, tmpDir, resourceFile);
            }

            // custom_modules is copied specially since we have to squash it into modules folder
            copyResources(resolver, rootPath, tmpDir, "custom_modules", "modules");
            copyResources(resolver, rootPath, tmpDir, "custom_assets", "assets");
            copyResources(resolver, rootPath, tmpDir, "custom_config", "config");
            copyResources(resolver, rootPath, tmpDir, "custom_directives", "directives");
            copyResources(resolver, rootPath, tmpDir, "custom_services", "services");

            for (String frontEndCommand : getFrontEndCommandsToBeExecuted())
            {
                runFrontEndBuildCommand(tmpDir, frontEndCommand);
            }

            File deployFolder = new File(getDeployFolderPath());
            createFolderStructure(deployFolder);

            for (String assembledFolder : getAssembledFoldersToCopyToDeployment())
            {
                copyWebappResources(tmpDir, deployFolder, assembledFolder);
            }
            for (String assembledFile : getAssembledFilesToCopyToDeployment())
            {
                copyWebappFile(tmpDir, deployFolder, assembledFile);
            }


        } catch (IOException e)
        {
            log.error("Could not copy Angular resources", e);
            // make sure the webapp does not start... if it did start it wouldn't work right.  So better to make sure
            // it doesn't deploy.
            throw new RuntimeException("Could not assemble Angular webapp: " + e.getMessage(), e);
        }
    }

    public void createFolderStructure(File folder) throws IOException
    {
        log.debug("Creating folder {}", folder.getCanonicalPath());
        if (!folder.exists())
        {
            boolean foldersCreated = folder.mkdirs();
            if (!foldersCreated)
            {
                throw new IOException("Could not create folder '" + folder.getCanonicalPath() + "'");
            }
        }
    }

    private void copyFile(ServletContextResourcePatternResolver resolver, File tmpDir, String fileName)
            throws IOException
    {
        Resource r = resolver.getResource(AngularResourceConstants.WAR_ANGULAR_RESOURCE_PATH + "/" + fileName);
        File target = new File(tmpDir, fileName);

        FileCopyUtils.copy(r.getInputStream(), new FileOutputStream(target));
    }

    public void copyWebappFile(File sourceFolder, File targetFolder, String filenameToCopy) throws IOException
    {
        FileCopyUtils.copy(new File(sourceFolder, filenameToCopy), new File(targetFolder, filenameToCopy));
    }

    public void copyWebappResources(File tmpDir, File deployFolder, String folderName) throws IOException
    {
        File toFolder = new File(deployFolder, folderName);
        File fromFolder = new File(tmpDir, folderName);

        log.debug("Removing current contents of folder {}", folderName);
        FileSystemUtils.deleteRecursively(toFolder);

        log.debug("Copying folder {} to webapp folder", folderName);
        FileSystemUtils.copyRecursively(fromFolder, toFolder);
        log.debug("Done copying folder {} to webapp folder", folderName);
    }

    public void runFrontEndBuildCommand(File tmpDir, String commandLine) throws IOException
    {
        log.debug("About to run {}", commandLine);
        CommandLine command = CommandLine.parse(commandLine);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setWorkingDirectory(tmpDir);

        // Slf4jDebugOutputStream is an OutputStream we can send to the DefaultExecutor; the DefaultExecutor will
        // pipe its STDIN and STDOUT to this output stream, which will log such output at DEBUG level to our
        // SLF4j logger.
        Slf4jDebugOutputStream debugOutputStream = null;
        try
        {
            debugOutputStream = new Slf4jDebugOutputStream(log);
            executor.setStreamHandler(new PumpStreamHandler(debugOutputStream));
            int exitCode = executor.execute(command);
            log.debug("done with {}: exit code {}", commandLine, exitCode);
        } finally
        {
            if (debugOutputStream != null)
            {
                debugOutputStream.close();
            }
        }
    }

    public File cleanAndCreateResourceTempFolder() throws IOException
    {
        File tmpDir = new File(getTempFolderPath());

        // probably safer to start fresh every time, but npm takes minutes to run :-(
        //FileSystemUtils.deleteRecursively(tmpDir);
        createFolderStructure(tmpDir);
        return tmpDir;
    }

    public void copyResources(
            ServletContextResourcePatternResolver resolver,
            String rootPath,
            File tmpDir,
            String moduleRoot) throws IOException
    {
        copyResources(resolver, rootPath, tmpDir, moduleRoot, moduleRoot);
    }


    public void copyResources(
            ServletContextResourcePatternResolver resolver,
            String rootPath,
            File tmpDir,
            String moduleRoot,
            String targetRoot) throws IOException
    {
        Resource[] resources;
        try
        {
            resources = resolver.getResources(AngularResourceConstants.WAR_ANGULAR_RESOURCE_PATH + "/" + moduleRoot + "/**");
        } catch (FileNotFoundException fe)
        {
            log.debug("Not copying resources under {}, since no such resources exist.",
                    AngularResourceConstants.WAR_ANGULAR_RESOURCE_PATH + "/" + moduleRoot);
            return;
        }

        copyFilesFromWebapp(rootPath, tmpDir, resources, moduleRoot, targetRoot);
    }

    public void copyFilesFromWebapp(String rootPath, File tmpDir, Resource[] resources, String moduleRoot, String targetRoot) throws IOException
    {
        File targetFile;

        for (Resource r : resources)
        {

            targetFile = fileFromResource(rootPath, tmpDir, moduleRoot, targetRoot, r);

            if (targetFile != null)
            {
                log.trace("Copying file {}", targetFile.getCanonicalPath());
                createFolderStructure(targetFile.getParentFile());
                FileCopyUtils.copy(r.getInputStream(), new FileOutputStream(targetFile));
            }

        }
    }

    public File fileFromResource(String rootPath, File tmpDir, String moduleRoot, String targetRoot, Resource r) throws IOException
    {
        File targetFile = null;

        URL url = r.getURL();
        if ("jar".equals(url.getProtocol()))
        {
            String webappPath = logicalPathFromJarPath(url);
            webappPath = webappPath.replaceFirst(moduleRoot, targetRoot);

            if (!webappPath.endsWith("/"))
            {
                targetFile = new File(tmpDir, webappPath);
            }
        } else if (r.getFile().isFile())
        {
            targetFile = determineTargetFile(rootPath, tmpDir, r, moduleRoot, targetRoot);
        }

        return targetFile;
    }

    public String logicalPathFromJarPath(URL url)
    {
        // should have come from META-INF/resources/resources folder
        String path = url.getFile();

        int metaInfPortionLength = AngularResourceConstants.JAR_PATH_META_INF_PORTION.length();

        String webappPath = path.substring(path.indexOf(AngularResourceConstants.JAR_PATH_META_INF_PORTION) + metaInfPortionLength + 1);
        log.trace("webapp path for URL {} is {}: ", url, webappPath);
        return webappPath;
    }

    public File determineTargetFile(String rootPath, File tmpDir, Resource r, String moduleRoot, String targetRoot) throws IOException
    {
        String resourceFullPath = r.getFile().getCanonicalPath();
        String relativePath = resourceFullPath.replace(rootPath, "");

        log.trace("relative path: {}", relativePath);

        relativePath = relativePath.replaceFirst(moduleRoot, targetRoot);

        log.trace("new relative path: {}", relativePath);

        return new File(tmpDir, relativePath);
    }

    public String getTempFolderPath()
    {
        return tempFolderPath;
    }

    public void setTempFolderPath(String tempFolderPath)
    {
        this.tempFolderPath = tempFolderPath;
    }

    public String getDeployFolderPath()
    {
        return deployFolderPath;
    }

    public void setDeployFolderPath(String deployFolderPath)
    {
        this.deployFolderPath = deployFolderPath;
    }

    public List<String> getResourceFoldersToCopyFromArchive()
    {
        return resourceFoldersToCopyFromArchive;
    }

    public void setResourceFoldersToCopyFromArchive(List<String> resourceFoldersToCopyFromArchive)
    {
        this.resourceFoldersToCopyFromArchive = resourceFoldersToCopyFromArchive;
    }

    public List<String> getAssembledFoldersToCopyToDeployment()
    {
        return assembledFoldersToCopyToDeployment;
    }

    public void setAssembledFoldersToCopyToDeployment(List<String> assembledFoldersToCopyToDeployment)
    {
        this.assembledFoldersToCopyToDeployment = assembledFoldersToCopyToDeployment;
    }

    public List<String> getFilesToCopyFromArchive()
    {
        return filesToCopyFromArchive;
    }

    public void setFilesToCopyFromArchive(List<String> filesToCopyFromArchive)
    {
        this.filesToCopyFromArchive = filesToCopyFromArchive;
    }

    public List<String> getAssembledFilesToCopyToDeployment()
    {
        return assembledFilesToCopyToDeployment;
    }

    public void setAssembledFilesToCopyToDeployment(List<String> assembledFilesToCopyToDeployment)
    {
        this.assembledFilesToCopyToDeployment = assembledFilesToCopyToDeployment;
    }

    public List<String> getFrontEndCommandsToBeExecuted()
    {
        return frontEndCommandsToBeExecuted;
    }

    public void setFrontEndCommandsToBeExecuted(List<String> frontEndCommandsToBeExecuted)
    {
        this.frontEndCommandsToBeExecuted = frontEndCommandsToBeExecuted;
    }

    public List<String> getOldDeployFoldersToBeRemovedBeforeBuild()
    {
        return oldDeployFoldersToBeRemovedBeforeBuild;
    }

    public void setOldDeployFoldersToBeRemovedBeforeBuild(List<String> oldDeployFoldersToBeRemovedBeforeBuild)
    {
        this.oldDeployFoldersToBeRemovedBeforeBuild = oldDeployFoldersToBeRemovedBeforeBuild;
    }
}
