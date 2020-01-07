package com.armedia.acm.userinterface.angular;

/*-
 * #%L
 * ACM UI: Ark Angular Starter
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

import com.armedia.acm.core.AcmSpringActiveProfile;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;
import org.zeroturnaround.exec.stream.slf4j.Slf4jDebugOutputStream;

import javax.servlet.ServletContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Copy all angular resources from the base ArkCase WAR file and also from any ArkCase extension jars, run the
 * Angular front-end build tools, and copy the assembled application into the deployment folder.
 * <p>
 * The ArkCase WAR file should configure the deployment folder in a Tomcat context resources element, such that
 * files in this deployment folder are treated as if they were in the root folder of the war file itself.
 * <p>
 * Yarn and npm (the Node.js Package Manager) must be installed on the deployment host, and npm must be in the
 * system path.
 * <p>
 * The resources to be copied from the war file and extension jars; the front-end commands to be run (e.g. yarn,
 * grunt); and the resources to be copied to the deployment folder are configured in Spring. All resources to
 * be copied from the war file and extension jars must be within a top-level resources folder.
 */
public class AngularResourceCopier implements ServletContextAware
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private String tempFolderPath;
    private String deployFolderPath;
    private String mergeConfigFrontendTask;
    private String yarnInstallCommand;
    private String gruntDefaultCommand;
    private List<String> resourceFoldersToCopyFromArchive;
    private List<String> assembledFoldersToCopyToDeployment;
    private List<String> filesToCopyFromArchive;
    private List<String> assembledFilesToCopyToDeployment;
    private List<String> frontEndCommandsToBeExecuted;
    private List<String> customResourceSourcesToCopyFromArchive;
    private AcmSpringActiveProfile springActiveProfile;

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
            log.debug("modulesRoot: [{}]", modulesRoot);
            String rootPath = modulesRoot.getFile().getCanonicalPath();
            String libFolderPath = tmpDir.getCanonicalPath() + File.separator + "lib";
            log.info("lib folder path: {}", libFolderPath);

            List<String> copiedFiles = new ArrayList<>();

            for (String resourceFolder : getResourceFoldersToCopyFromArchive())
            {
                List<String> copied = copyResources(resolver, rootPath, tmpDir, resourceFolder);
                copiedFiles.addAll(copied);
            }

            for (String resourceFile : getFilesToCopyFromArchive())
            {
                String copied = copyFile(resolver, tmpDir, resourceFile);
                copiedFiles.add(copied);
            }

            // yarn install
            runFrontEndBuildCommand(tmpDir, yarnInstallCommand);
            // add 'customer' as specific profile, so if any customer resources are present will come
            // on top of core and extension resources

            List<String> activeProfiles = springActiveProfile.getExtensionActiveProfile()
                    .map(it -> Arrays.asList(it, "custom"))
                    .orElse(Collections.singletonList("custom"));

            copiedFiles.add(createProfilesJsFileInDir(activeProfiles, tmpDir));

            for (String profile : activeProfiles)
            {
                copyFilesAndExecuteCommands(profile, resolver, rootPath, tmpDir, copiedFiles);
            }

            List<String> tmpFilesFound = findAllFilesInFolder(tmpDir);

            log.debug("Found {} files in tmp folder", tmpFilesFound.size());

            // delete all files that exist in the tmp dir, but we didn't copy them there; such files must have been
            // removed from the project. Exceptions are files managed by yarn and grunt: lib folder, node_modules
            // folder, bower_components folder, yarn.lock
            
            List<File> oldFilesInTmpFolder = tmpFilesFound.stream()
                    .filter(p -> !p.contains("node_modules"))
                    .filter(p -> !p.contains("bower_components"))
                    .filter(p -> !p.endsWith("yarn.lock"))
                    .filter(p -> !p.startsWith(libFolderPath))
                    .filter(p -> !copiedFiles.contains(p))
                    .peek(p -> log.debug("File to be removed: {}", p))
                    .map(File::new)
                    .collect(Collectors.toList());
            log.debug("Found {} files to be removed from tmp folder", oldFilesInTmpFolder.size());
            oldFilesInTmpFolder.stream()
                    .peek(f -> log.debug("Removing tmp file [{}]", f.toPath()))
                    .forEach(File::delete);

            runFrontEndBuildCommand(tmpDir, gruntDefaultCommand);

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

        }
        catch (IOException e)
        {
            log.error("Could not copy Angular resources", e);
            // make sure the webapp does not start... if it did start it wouldn't work right. So better to make sure
            // it doesn't deploy.
            throw new RuntimeException("Could not assemble Angular webapp: " + e.getMessage(), e);
        }
    }

    private void copyFilesAndExecuteCommands(String profile, ServletContextResourcePatternResolver resolver, String rootPath,
            File tmpDir, List<String> copiedFiles)
            throws IOException
    {

        log.debug("Copy resources for specific profile [{}]", profile);
        for (String folder : customResourceSourcesToCopyFromArchive)
        {
            String moduleRoot = String.format("%s_%s", profile, folder);
            copiedFiles.addAll(copyResources(resolver, rootPath, tmpDir, moduleRoot, folder));
        }
        runFrontEndBuildCommand(tmpDir, mergeConfigFrontendTask);
    }

    private String createProfilesJsFileInDir(List<String> profiles, File parentDir) throws IOException
    {
        String exportProfiles = String.format("module.exports = %s", profiles.stream()
                .map(it -> String.format("'%s'", it))
                .collect(Collectors.joining(", ", "{ profiles: [ ", " ] };")));
        File target = new File(parentDir, "profiles.js");
        Files.copy(IOUtils.toInputStream(exportProfiles), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        target.setLastModified(new Date().getTime());
        return target.getCanonicalPath();
    }

    private List<String> findAllFilesInFolder(File folder)
    {
        return FileUtils.listFiles(folder, FileFilterUtils.trueFileFilter(), FileFilterUtils.trueFileFilter())
                .stream()
                .filter(File::isFile)
                .map(File::toPath)
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    public void createFolderStructure(File folder) throws IOException
    {
        if (!folder.exists())
        {
            log.debug("Creating folder [{}]", folder.getCanonicalPath());
            boolean foldersCreated = folder.mkdirs();
            if (!foldersCreated)
            {
                throw new IOException("Could not create folder '" + folder.getCanonicalPath() + "'");
            }
        }
    }

    private String copyFile(ServletContextResourcePatternResolver resolver, File tmpDir, String fileName)
            throws IOException
    {
        Resource r = resolver.getResource(AngularResourceConstants.WAR_ANGULAR_RESOURCE_PATH + "/" + fileName);
        File target = new File(tmpDir, fileName);

        Files.copy(r.getInputStream(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        target.setLastModified(r.lastModified());
        log.debug("Copying file to: {}", target.toPath());
        log.debug("Copying file to: {}", target.getCanonicalPath());
        return target.getCanonicalPath();

    }

    public void copyWebappFile(File sourceFolder, File targetFolder, String filenameToCopy) throws IOException
    {
        FileCopyUtils.copy(new File(sourceFolder, filenameToCopy), new File(targetFolder, filenameToCopy));
    }

    public void copyWebappResources(File tmpDir, File deployFolder, String folderName) throws IOException
    {
        File toFolder = new File(deployFolder, folderName);
        File fromFolder = new File(tmpDir, folderName);

        Collection<File> sourceFiles = FileUtils.listFiles(fromFolder, FileFilterUtils.trueFileFilter(), FileFilterUtils.trueFileFilter());
        List<String> filesToKeep = new ArrayList<>(sourceFiles.size());

        copyFilesAsNeeded(fromFolder, toFolder, sourceFiles, filesToKeep);

        deleteOldFilesFromFolder(toFolder, filesToKeep);
    }

    private void deleteOldFilesFromFolder(File folder, List<String> filesToKeep) throws IOException
    {
        List<String> targetFilesFound = findAllFilesInFolder(folder);
        log.debug("Found {} files in target folder [{}]", targetFilesFound.size(), folder.getCanonicalPath());

        List<File> oldFilesInTargetFolder = targetFilesFound.stream()
                .filter(p -> !filesToKeep.contains(p))
                .map(File::new)
                .collect(Collectors.toList());
        log.debug("Found {} files to be removed from target folder [{}]", oldFilesInTargetFolder.size(), folder.getCanonicalPath());
        oldFilesInTargetFolder.stream().peek(f -> log.debug("Removing custom file [{}]", f.toPath())).forEach(File::delete);
    }

    private void copyFilesAsNeeded(File fromFolder, File toFolder, Collection<File> sourceFiles, List<String> filesToKeep)
            throws IOException
    {
        for (File f : sourceFiles)
        {
            log.trace("Considering [{}]", f.getCanonicalPath());

            long sourceModified = f.lastModified();
            String relativeName = f.getCanonicalPath().replace(fromFolder.getCanonicalPath(), "");
            File targetFile = new File(toFolder, relativeName);

            filesToKeep.add(targetFile.getCanonicalPath());

            log.trace("\tTarget file: [{}]", targetFile.getCanonicalPath());

            if (f.isDirectory())
            {
                createFolderStructure(f);
            }
            else if (targetFile.exists())
            {
                long targetModified = targetFile.lastModified();

                log.trace("\tTarget file exists; modified time is different? {}", targetModified != sourceModified);
                if (targetModified != sourceModified)
                {
                    log.debug("Copying [{}] to [{}]", f.getCanonicalPath(), targetFile.toPath());
                    FileCopyUtils.copy(f, targetFile);
                    targetFile.setLastModified(sourceModified);
                }
            }
            else
            {
                createFolderStructure(targetFile.getParentFile());
                FileCopyUtils.copy(f, targetFile);
                targetFile.setLastModified(sourceModified);
            }
        }
    }

    public void runFrontEndBuildCommand(File tmpDir, String commandLine) throws IOException
    {
        log.debug("About to run [{}]", commandLine);
        CommandLine command = CommandLine.parse(commandLine);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setWorkingDirectory(tmpDir);

        // Slf4jDebugOutputStream is an OutputStream we can send to the DefaultExecutor; the DefaultExecutor will
        // pipe its STDIN and STDOUT to this output stream, which will log such output at DEBUG level to our
        // SLF4j logger.
        try (Slf4jDebugOutputStream debugOutputStream = new Slf4jDebugOutputStream(log))
        {

            executor.setStreamHandler(new PumpStreamHandler(debugOutputStream));
            int exitCode = executor.execute(command);
            log.debug("done with [{}]: exit code {}", commandLine, exitCode);
        }
    }

    public File cleanAndCreateResourceTempFolder() throws IOException
    {
        File tmpDir = new File(getTempFolderPath());
        createFolderStructure(tmpDir);
        return tmpDir;
    }

    public List<String> copyResources(
            ServletContextResourcePatternResolver resolver,
            String rootPath,
            File tmpDir,
            String moduleRoot) throws IOException
    {
        return copyResources(resolver, rootPath, tmpDir, moduleRoot, moduleRoot);
    }

    public List<String> copyResources(
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
        }
        catch (FileNotFoundException fe)
        {
            log.debug("Not copying resources under [{}], since no such resources exist.",
                    AngularResourceConstants.WAR_ANGULAR_RESOURCE_PATH + "/" + moduleRoot);
            return Collections.emptyList();
        }

        return copyFilesFromWebapp(rootPath, tmpDir, resources, moduleRoot, targetRoot);
    }

    public List<String> copyFilesFromWebapp(String rootPath, File tmpDir, Resource[] resources, String moduleRoot, String targetRoot)
            throws IOException
    {
        File targetFile;
        List<String> filepaths = new ArrayList<>(resources.length);

        for (Resource r : resources)
        {

            targetFile = fileFromResource(rootPath, tmpDir, moduleRoot, targetRoot, r);
            long resourceLastModified = r.lastModified();

            if (targetFile != null)
            {
                String canonicalPath = targetFile.getCanonicalPath();
                log.trace("Copying file [{}]", canonicalPath);

                filepaths.add(canonicalPath);
                createFolderStructure(targetFile.getParentFile());

                if (targetFile.exists())
                {
                    Resource targetResource = new FileSystemResource(targetFile);
                    long targetResourceLastModified = targetResource.lastModified();

                    log.trace("[{}] last modified is different from target? {}", canonicalPath,
                            (resourceLastModified != targetResourceLastModified));
                    if (resourceLastModified != targetResourceLastModified)
                    {
                        log.debug("[{}] has been modified - copying it", canonicalPath);
                        FileCopyUtils.copy(r.getInputStream(), new FileOutputStream(targetFile));
                        targetFile.setLastModified(resourceLastModified);
                    }
                }
                else
                {
                    FileCopyUtils.copy(r.getInputStream(), new FileOutputStream(targetFile));
                    targetFile.setLastModified(resourceLastModified);
                }
            }
        }
        return filepaths;
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
        }
        else if (r.getFile().isFile())
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
        log.trace("webapp path for URL [{}] is [{}]: ", url, webappPath);
        return webappPath;
    }

    public File determineTargetFile(String rootPath, File tmpDir, Resource r, String moduleRoot, String targetRoot) throws IOException
    {
        String resourceFullPath = r.getFile().getCanonicalPath();
        String relativePath = resourceFullPath.replace(rootPath, "");

        log.trace("relative path: [{}]", relativePath);

        relativePath = relativePath.replaceFirst(moduleRoot, targetRoot);

        log.trace("new relative path: [{}]", relativePath);

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

    public List<String> getCustomResourceSourcesToCopyFromArchive()
    {
        return customResourceSourcesToCopyFromArchive;
    }

    public void setCustomResourceSourcesToCopyFromArchive(List<String> customResourceSourcesToCopyFromArchive)
    {
        this.customResourceSourcesToCopyFromArchive = customResourceSourcesToCopyFromArchive;
    }

    public AcmSpringActiveProfile getSpringActiveProfile()
    {
        return springActiveProfile;
    }

    public void setSpringActiveProfile(AcmSpringActiveProfile springActiveProfile)
    {
        this.springActiveProfile = springActiveProfile;
    }

    public String getMergeConfigFrontendTask()
    {
        return mergeConfigFrontendTask;
    }

    public void setMergeConfigFrontendTask(String mergeConfigFrontendTask)
    {
        this.mergeConfigFrontendTask = mergeConfigFrontendTask;
    }

    public String getYarnInstallCommand()
    {
        return yarnInstallCommand;
    }

    public void setYarnInstallCommand(String yarnInstallCommand)
    {
        this.yarnInstallCommand = yarnInstallCommand;
    }

    public String getGruntDefaultCommand()
    {
        return gruntDefaultCommand;
    }

    public void setGruntDefaultCommand(String gruntDefaultCommand)
    {
        this.gruntDefaultCommand = gruntDefaultCommand;
    }
}