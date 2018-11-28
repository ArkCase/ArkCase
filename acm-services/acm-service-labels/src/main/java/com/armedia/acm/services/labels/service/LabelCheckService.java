package com.armedia.acm.services.labels.service;

/*-
 * #%L
 * ACM Service: Labels Service
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

import com.armedia.acm.services.labels.exception.AcmLabelManagementException;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Lin on 11/08/18.
 */

public class LabelCheckService
{
    private Logger log = LoggerFactory.getLogger(getClass());
    private String modulesLocation_;
    private String resourcesLocation_;

    public LabelCheckService(String modulesLocation, String resourcesLocation)
    {
        this.modulesLocation_ = modulesLocation;
        this.resourcesLocation_ = resourcesLocation;
    }

    public LabelCheckService(){

    }

    /**
     * find the missing label
     * @return missing label result
     */
    public List<String> checkLabel()
    {

        File[] files = new File(getResourcesLocation()).listFiles();
        List<String> labelList = labelRetrievalProcess();

        Set<JSONObject> translationList = new HashSet<>();
        Set<String> checkAnswer = new HashSet<>();

        for (File file : files)
        {
            translationList.add(loadResource(file.getAbsolutePath()));
        }

        labelList = labelList.stream().distinct().collect(Collectors.toList());
        Collections.sort(labelList);

        boolean isFound = false;
        for (String label : labelList)
        {
            for (JSONObject resource : translationList)
            {
                if (resource.has(label))
                {
                    isFound = true;
                    break;
                }
            }
            if (isFound)
            {
                isFound = false;
            }
            else
            {
                checkAnswer.add(label);
            }
        }
        List<String> resultObject = new ArrayList<>();
        for (String answer : checkAnswer)
        {
            resultObject.add(answer);

        }
        return resultObject;
    }

    /**
     * Load JSON resource file
     *
     * @param name
     * @return
     * @throws AcmLabelManagementException
     */
    private JSONObject loadResource(String name)
    {
        try
        {
            File file = FileUtils.getFile(name);
            String resource = FileUtils.readFileToString(file, "UTF-8");

            return new JSONObject(resource);

        }
        catch (Exception e)
        {
            log.warn("Failed to read file [{}]", name, e);
            return null;
        }
    }

    /**
     * modules foldes "time-tracking", "document-details", and "cost-tracking" are different from those in json file
     * @param path modules file path
     * @return modules list
     */
    private String[] modulesGenerate(String path)
    {
        String[] modules = new File(path).list();
        for (int i = 0; i < modules.length; i++)
        {
            if (modules[i].equals("time-tracking"))
            {
                modules[i] = "timeTracking";
            }
            else if (modules[i].equals("document-details"))
            {
                modules[i] = "documentDetails";
            }
            else if (modules[i].equals("cost-tracking"))
            {
                modules[i] = "costTracking";
            }
        }

        return modules;
    }

    /**
     * retrieval label list from html files and js files
     * @return labelList
     */
    public List<String> labelRetrievalProcess()
    {

        String[] modules = modulesGenerate(getModulesLocation());
        List<File> fileHtmlList = new ArrayList<>();
        List<File> fileJSList = new ArrayList<>();
        findAllFile(getModulesLocation(), fileHtmlList, fileJSList);
        List<String> labelList = jsLabelRetrieve(fileJSList);
        labelList.addAll(htmlLabelRetrieve(fileHtmlList, modules));
        return labelList;
    }

    /**
     * Recursion traversalFolder
     * @param fileHtmlList html file list
     * @param  fileJSList js file list
     */
    public void findAllFile(String path, List<File> fileHtmlList, List<File> fileJSList)
    {
        File file = new File(path);
        for (File subFile : file.listFiles())
        {
            if (subFile.isDirectory())
            {
                findAllFile(subFile.getAbsolutePath(), fileHtmlList, fileJSList);
            }
            else
            {
                if (subFile.getName().endsWith(".html"))
                {
                    fileHtmlList.add(subFile);
                }
                else if (subFile.getName().endsWith(".js"))
                {
                    fileJSList.add(subFile);
                }

            }
        }
    }

    /**
        Retrieve Label from Html files
     * @param htmlFileList html file list
     * @param modules  modules
     */
    private List<String> htmlLabelRetrieve(List<File> htmlFileList, String[] modules)
    {
        StringBuffer regexSBinHtml = new StringBuffer();
        regexSBinHtml.append("ui-sref|translateData|\\{dashboard\\.([a-zA-Z0-9\\-\\_]*\\.)*[a-zA-Z0-9\\-\\_]*\\}|");
        for (String module : modules)
        {
            regexSBinHtml = regexSBinHtml.append(module);
            regexSBinHtml.append("\\.([a-zA-Z0-9\\-\\_]*\\.)*[a-zA-Z0-9\\-\\_]*|");
        }
        String regex = regexSBinHtml.subSequence(0, regexSBinHtml.lastIndexOf("|")).toString();

        List<String> outputMatches = new ArrayList<>();
        for (File file : htmlFileList)
        {
            BufferedReader br = null;
            try
            {
                br = new BufferedReader(new FileReader(file));
                String line = null;
                while ((line = br.readLine()) != null)
                {
                    if (line.trim().startsWith("<!-"))
                    {
                        continue;
                    }
                    Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                    Matcher matcher = pattern.matcher(line);
                    while (matcher.find())
                    {

                        //Filter for dirty data
                        if (matcher.group().startsWith("ui-sref") || matcher.group().startsWith("translateData") || matcher.group()
                                .endsWith("html") || matcher.group()
                                .endsWith(".") || matcher.group().startsWith("{dashboard") || matcher.group().equals("profile.email")
                                || matcher.group().equals("profile.phoneNumber"))
                        {
                            break;
                        }
                        outputMatches.add(matcher.group());
                    }
                }
            }
            catch (FileNotFoundException e)
            {
                log.warn("Can not find file [{}]", file.getName(), e);
            }
            catch (IOException e)
            {
                log.warn("IO Exception [{}]", file.getName(), e);
            }
            finally
            {
                try
                {
                    br.close();
                }
                catch (IOException e)
                {
                    log.warn("IO Exception [{}]", file.getName(), e);
                }
            }
        }
        return outputMatches;
    }

    /**
     *
        Retrieve Label from JS files
     * @param jsFileList Js file list
     * @return label list
     */
    private List<String> jsLabelRetrieve(List<File> jsFileList)
    {
        List<String> outputMatches = new ArrayList<>();
        String regex = "translate.instant\\(\"([a-zA-Z0-9\\-\\_]*\\.)*[a-zA-Z0-9\\-\\_]*\"\\)|translate.instant\\(\\\'([a-zA-Z0-9\\-\\_]*\\.)*[a-zA-Z0-9\\-\\_]*\\\'\\)";
        for (File file : jsFileList)
        {
            BufferedReader br = null;
            try
            {
                br = new BufferedReader(new FileReader(file));
                String line = null;
                while ((line = br.readLine()) != null)
                {
                    Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                    Matcher matcher = pattern.matcher(line);
                    while (matcher.find())
                    {
                        //find the label in translate.instant().
                        String labelLine = matcher.group();
                        labelLine = labelLine.substring(labelLine.lastIndexOf('(') + 1, labelLine.lastIndexOf(')'));
                        labelLine = labelLine.substring(1, labelLine.length() - 1);
                        outputMatches.add(labelLine);
                    }
                }
            }
            catch (FileNotFoundException e)
            {
                log.warn("Can not find file [{}]", file.getName(), e);
            }
            catch (IOException e)
            {
                log.warn("IO Exception [{}]", file.getName(), e);
            }
            finally
            {
                try
                {
                    br.close();
                }
                catch (IOException e)
                {
                    log.warn("IO Exception [{}]", file.getName(), e);
                }
            }
        }
        return outputMatches;
    }

    public String getModulesLocation()
    {
        return modulesLocation_;
    }

    public void setModulesLocation(String modulesLocation)
    {
        this.modulesLocation_ = modulesLocation;
    }

    public String getResourcesLocation()
    {
        return resourcesLocation_;
    }

    public void setResourcesLocation(String resourcesLocation)
    {
        this.resourcesLocation_ = resourcesLocation;
    }
}
