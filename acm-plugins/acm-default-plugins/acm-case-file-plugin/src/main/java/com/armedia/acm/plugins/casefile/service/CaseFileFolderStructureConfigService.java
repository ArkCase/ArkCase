package com.armedia.acm.plugins.casefile.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CaseFileFolderStructureConfigService {
    private String folderStructureAsString;

    public List<String> getFolderStructure (){
        List<String> folderStructure = new ArrayList<>();
        String jsonString = getFolderStructureAsString();
        JSONArray jsonArray = new JSONArray(jsonString);
        for (int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject jObj = jsonArray.getJSONObject(i);
            folderStructure.add(jObj.getString("name").trim());

        }

        return folderStructure;
    }

    public String getFolderStructureAsString() {
        return folderStructureAsString;
    }

    public void setFolderStructureAsString(String folderStructureAsString) {
        this.folderStructureAsString = folderStructureAsString;
    }
}
