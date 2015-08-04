/**
 * DocTree.Model
 *
 * @author jwu
 */
DocTree.Model = DocTree.Model || {
    create : function(args) {
        this.cacheTree = new Acm.Model.CacheFifo();
        this.cacheFolderList = new Acm.Model.CacheFifo();

        if (DocTree.Model.Config.create)           {DocTree.Model.Config.create(args);}
        if (DocTree.Model.Key.create)              {DocTree.Model.Key.create(args);}
        if (DocTree.Service.create)                {DocTree.Service.create(args);}
    }
    ,onInitialized: function() {
        if (DocTree.Model.Config.onInitialized)    {DocTree.Model.Config.onInitialized();}
        if (DocTree.Model.Key.onInitialized)       {DocTree.Model.Key.onInitialized();}
        if (DocTree.Service.onInitialized)         {DocTree.Service.onInitialized();}
    }

    ,API_RETRIEVE_FOLDER_LIST_        : "/api/latest/service/ecm/folder/"                        //  {objectType}/{objectId}/{folderId}
    ,API_CREATE_FOLDER_               : "/api/latest/service/ecm/folder/"                        //  {folderId}/{newFolderName}
    ,API_DELETE_FOLDER_               : "/api/latest/service/ecm/folder/"
    ,API_UPLOAD_FILE                  : "/api/latest/service/ecm/upload"
    ,API_REPLACE_FILE_                : "/api/latest/service/ecm/replace/"                       //  {fileToBeReplacedId}
    ,API_DOWNLOAD_DOCUMENT_           : "/api/v1/plugin/ecm/download/byId/"
    ,API_DELETE_FILE_                 : "/api/latest/service/ecm/id/"
    ,API_RENAME_FOLDER_               : "/api/latest/service/ecm/folder/"                        //  {folderId}/{newFolderName}
    ,API_RENAME_FILE_                 : "/api/latest/service/ecm/file/"                          //  {objectId}/{newName}/{extension}
    ,API_MOVE_FILE_                   : "/api/latest/service/ecm/moveToAnotherContainer/"        //  {targetObjectType}/{targetObjectId}
    ,API_COPY_FILE_                   : "/api/latest/service/ecm/copyToAnotherContainer/"        //  {targetObjectType}/{targetObjectId}
    ,API_MOVE_FOLDER_                 : "/api/latest/service/ecm/folder/move/"                   //  {folderToMoveId}/{dstFolderId}
    ,API_COPY_FOLDER_                 : "/api/latest/service/ecm/folder/copy/"                   //  {folderId}/{dstFolderId}/{targetObjectType}/{targetObjectId}
    ,API_SET_ACTIVE_VERSION_          : "/api/latest/service/ecm/file/"                          //  {fileId}?versionTag=x.y"
    ,API_SEND_EMAIL_                  : "/api/latest/service/notification/email"
    ,API_LODGE_DOCUMENT               : "/api/latest/service/ecm/createFolderByPath?isCopy=true&"   // targetObjectType={objType}&targetObjectId={objId}&newPath={fullPath}
    ,API_DECLARE_AS_RECORD            : "/api/latest/service/ecm/declare/"

    ,NODE_TYPE_PREV: "prev"
    ,NODE_TYPE_NEXT: "next"
    ,NODE_TYPE_FILE: "file"
    ,NODE_TYPE_FOLDER: "folder"

    ,_objType: null
    ,getObjType: function() {
        return this._objType;
    }
    ,setObjType: function(objType) {
        this._objType = objType;
    }
    ,_objId: null
    ,getObjId: function() {
        return this._objId;
    }
    ,setObjId: function(objId) {
        this._objId = objId;
    }

    ,getCacheKey: function(folderId, pageId) {
        var setting = DocTree.Model.Config.getSetting();
        var key = this.getObjType() + "." + this.getObjId();
        key += "." + Acm.goodValue(folderId, 0);    //for root folder, folderId is 0 or undefined
        key += "." + Acm.goodValue(pageId, 0);
        key += "." + DocTree.Model.Config.getSortBy();
        key += "." + DocTree.Model.Config.getSortDirection();
        key += "." + DocTree.Model.Config.getMaxRows();
        return key;
    }

    ,findFolderItemIdx: function(objectId, folderList) {
        var found = -1;
        if (DocTree.Model.validateFolderList(folderList)) {
            for (var i = 0; i < folderList.children.length; i++) {
                if (Acm.goodValue(folderList.children[i].objectId) == objectId) {
                    found = i;
                    break;
                }
            }
        }
        return found;
    }

    ,fileToSolrData: function(fileData) {
        var solrData = {};
        solrData.objectType = "file";
        if (!Acm.isEmpty(fileData.fileId, 0)) {
            solrData.objectId       = fileData.fileId;
        } else if (!Acm.isEmpty(fileData.objectId, 0)) {
            solrData.objectId       = fileData.objectId;
        }
        solrData.created    = Acm.goodValue(fileData.created);
        solrData.creator    = Acm.goodValue(fileData.creator);
        solrData.modified   = Acm.goodValue(fileData.modified);
        solrData.modifier   = Acm.goodValue(fileData.modifier);

        if (!Acm.isEmpty(fileData.fileName)) {
            solrData.name       = fileData.fileName;
        } else if (!Acm.isEmpty(fileData.name)) {
            solrData.name       = fileData.name;
        }

        if (!Acm.isEmpty(fileData.fileType)) {
            solrData.type       = fileData.fileType;
        } else if (!Acm.isEmpty(fileData.type)) {
            solrData.type       = fileData.type;
        }
        solrData.status     = Acm.goodValue(fileData.status);
        solrData.category   = Acm.goodValue(fileData.category);
        if (!Acm.isEmpty(fileData.activeVersionTag)) {
            solrData.version    = fileData.activeVersionTag;
        } else if (!Acm.isEmpty(fileData.version)) {
            solrData.version    = fileData.version;
        }

        if (Acm.isArray(fileData.versions)) {
            solrData.versionList = [];
            for (var i = 0; i < fileData.versions.length; i++) {
                var version = {};
                version.versionTag = Acm.goodValue(fileData.versions[i].versionTag);
                solrData.versionList.push(version);
            }
        }
        if (Acm.isArray(fileData.versionList)) {
            solrData.versionList = [];
            for (var i = 0; i < fileData.versionList.length; i++) {
                var version = {};
                version.versionTag = Acm.goodValue(fileData.versionList[i].versionTag);
                solrData.versionList.push(version);
            }
        }
        return solrData;
    }
    ,folderToSolrData: function(folderData) {
        var solrData = {};
        solrData.objectType = "folder";
        if (!Acm.isEmpty(folderData.id, 0)) {
            solrData.objectId       = folderData.id;
        } else if (!Acm.isEmpty(folderData.objectId, 0)) {
            solrData.objectId       = folderData.objectId;
        }
        solrData.created    = Acm.goodValue(folderData.created);
        solrData.creator    = Acm.goodValue(folderData.creator);
        solrData.modified   = Acm.goodValue(folderData.modified);
        solrData.modifier   = Acm.goodValue(folderData.modifier);
        solrData.name       = Acm.goodValue(folderData.name);
        if (!Acm.isEmpty(folderData.parentFolderId, 0)) {
            solrData.folderId   = Acm.goodValue(folderData.parentFolderId, 0);
        } else if (!Acm.isEmpty(folderData.folderId, 0)) {
            solrData.folderId   = Acm.goodValue(folderData.folderId, 0);
        }
        return solrData;
    }

    ,retrieveFolderList: function(pageId, folderId) {
        var objType = DocTree.Model.getObjType();
        var objId = DocTree.Model.getObjId();
        var setting = DocTree.Model.Config.getSetting();
        var url = DocTree.Model.API_RETRIEVE_FOLDER_LIST_ + objType + "/" + objId;
        if (0 < folderId) {
            url += "/" + folderId;
        }
        url += "?start=" + pageId;
        url += "&n=" + DocTree.Model.Config.getMaxRows();
        if (Acm.isNotEmpty(setting.sortBy) && Acm.isNotEmpty(setting.sortDirection)) {
            url += "&s=" + setting.sortBy + "&dir=" + setting.sortDirection;
        }

        return Acm.Service.call({type: "GET"
            ,url: url
            ,callback: function(response) {
                if (!response.hasError) {
                var folderList = null;
                    if (DocTree.Model.validateFolderList(response)) {
                        folderList = response;
                        var setting = DocTree.Model.Config.getSetting();
                        setting.maxRows = Acm.goodValue(folderList.maxRows, 0);
                        setting.sortBy = Acm.goodValue(folderList.sortBy);
                        setting.sortDirection = Acm.goodValue(folderList.sortDirection);

                        var cacheKey = DocTree.Model.getCacheKey(folderId, pageId);
                        DocTree.Model.cacheFolderList.put(cacheKey, folderList);

                    }
                    //DocTree.Controller.modelRetrievedFolderList(folderList, objType, objId, folderId, pageId);
                    //var rc = callbackSuccess(folderList);
                    //return rc;
                    return folderList;
                }
            }
        });

    }
    ,createFolder: function(parentId, folderName, cacheKey) {
        var url = this.API_CREATE_FOLDER_ + parentId + "/" + folderName;
        return Acm.Service.call({type: "PUT"
            ,url: url
            ,callback: function(response) {
                if (!response.hasError) {
                    if (DocTree.Model.validateCreateInfo(response)) {
                        if (response.parentFolderId == parentId) {
                            var createInfo = response;
                            var folderList = DocTree.Model.cacheFolderList.get(cacheKey);
                            if (DocTree.Model.validateFolderList(folderList)) {
                                var createdFolder = DocTree.Model.folderToSolrData(createInfo);
                                folderList.children.push(createdFolder);
                                folderList.totalChildren++;
                                DocTree.Model.cacheFolderList.put(cacheKey, folderList);
                                return createdFolder;
                            }
                        }
                    }
                } //end else
            }
        });
    }
    ,uploadFiles: function(formData, cacheKey) {
        return Acm.Service.call({type: 'POST'
            ,url: DocTree.Model.API_UPLOAD_FILE
            ,data: formData
            ,processData: false
            ,contentType: false
            ,callback: function(response){
                if (!response.hasError) {
                    if (DocTree.Model.validateUploadInfo(response)) {
                        var uploadInfo = response;

                        var folderList = DocTree.Model.cacheFolderList.get(cacheKey);
                        if (DocTree.Model.validateFolderList(folderList)) {
                            var uploadedFiles = [];
                            for (var i = 0; i < uploadInfo.length; i++) {
                                var uploadedFile = DocTree.Model.fileToSolrData(uploadInfo[i]);
                                uploadedFiles.push(uploadedFile);
                                folderList.children.push(uploadedFile);
                                folderList.totalChildren++;
                            }
                            DocTree.Model.cacheFolderList.put(cacheKey, folderList);
                            return uploadedFiles;
                        }
                    } //end validate
                }
            }
        });

    }
    ,replaceFile: function(formData, fileId, cacheKey) {
        var url = this.API_REPLACE_FILE_ + fileId;
        return Acm.Service.call({type: 'POST'
            ,url: url
            ,data: formData
            ,processData: false
            ,contentType: false
            ,callback: function(response){
                if (!response.hasError) {
                    if (DocTree.Model.validateReplaceInfo(response)) {
                        var replaceInfo = response;

                        if (replaceInfo.fileId == fileId) {
                            var folderList = DocTree.Model.cacheFolderList.get(cacheKey);
                            if (DocTree.Model.validateFolderList(folderList)) {
                                var replaced = DocTree.Model.findFolderItemIdx(fileId, folderList);
                                if (0 <= replaced) {
                                    folderList.children[replaced].version = Acm.goodValue(replaceInfo.activeVersionTag);

                                    folderList.children[replaced].versionList = [];
                                    if (Acm.isArray(replaceInfo.versions)) {
                                        for (var i = 0; i < replaceInfo.versions.length; i++) {
                                            var ver = {};
                                            ver.versionTag = replaceInfo.versions[i].versionTag;
                                            folderList.children[replaced].versionList.push(ver);
                                        }
                                    }
                                    DocTree.Model.cacheFolderList.put(cacheKey, folderList);
                                    //DocTree.Controller.modelReplacedFile(replaceInfo, fileId, callerData);
                                    return (folderList.children[replaced]);
                                }
                            }
                        }
                    } //end validate
                }
            }
        });
    }
    ,moveFolder: function(subFolderId, folderId, frCacheKey, toCacheKey) {
        var url = this.API_MOVE_FOLDER_ + subFolderId + "/" + folderId;
        return Acm.Service.call({type: "POST"
            ,url: url
            ,callback: function(response) {
                if (!response.hasError) {
                    if (DocTree.Model.validateMoveFolderInfo(response)) {
                        if (response.id == subFolderId) {
                            var moveFolderInfo = response;

                            var frFolderList = DocTree.Model.cacheFolderList.get(frCacheKey);
                            var toFolderList = DocTree.Model.cacheFolderList.get(toCacheKey);
                            if (DocTree.Model.validateFolderList(frFolderList) && DocTree.Model.validateFolderList(toFolderList)) {
                                var idx = DocTree.Model.findFolderItemIdx(subFolderId, frFolderList);
                                if (0 <= idx) {
                                    toFolderList.children.push(frFolderList.children[idx]);
                                    toFolderList.totalChildren++;
                                    DocTree.Model.cacheFolderList.put(toCacheKey, toFolderList);

                                    frFolderList.children.splice(idx, 1);
                                    frFolderList.totalChildren--;
                                    DocTree.Model.cacheFolderList.put(frCacheKey, frFolderList);
                                }
                            }
                            return moveFolderInfo;
                        }
                    } //end validate
                }
            }
        })
    }
    ,moveFile: function(fileId, folderId, frCacheKey, toCacheKey) {
        var objType = DocTree.Model.getObjType();
        var objId = DocTree.Model.getObjId();
        var url = this.API_MOVE_FILE_ + objType + "/" + objId;
        var data = {"id": fileId, "folderId": folderId};
        return Acm.Service.call({type: "POST"
            ,url: url
            ,data: JSON.stringify(data)
            ,callback: function(response) {
                if (!response.hasError) {
                    if (DocTree.Model.validateMoveFileInfo(response)) {
                        if (response.fileId == fileId) {
                            var moveFileInfo = response;

                            var frFolderList = DocTree.Model.cacheFolderList.get(frCacheKey);
                            var toFolderList = DocTree.Model.cacheFolderList.get(toCacheKey);
                            if (DocTree.Model.validateFolderList(frFolderList) && DocTree.Model.validateFolderList(toFolderList)) {
                                var idx = DocTree.Model.findFolderItemIdx(fileId, frFolderList);
                                if (0 <= idx) {
                                    toFolderList.children.push(frFolderList.children[idx]);
                                    toFolderList.totalChildren++;
                                    DocTree.Model.cacheFolderList.put(toCacheKey, toFolderList);

                                    frFolderList.children.splice(idx, 1);
                                    frFolderList.totalChildren--;
                                    DocTree.Model.cacheFolderList.put(frCacheKey, frFolderList);
                                }
                            }
                            return moveFileInfo;
                        }
                    } //end validate
                }
            }
        })
    }
    ,copyFolder: function(subFolderId, folderId, frCacheKey, toCacheKey) {
        var objType = DocTree.Model.getObjType();
        var objId = DocTree.Model.getObjId();
//        var url = App.getContextPath() + this.API_COPY_FOLDER_ + objType + "/" + objId;
//        var data = {"id": subFolderId, "folderId": folderId};
        var url = this.API_COPY_FOLDER_ + subFolderId + "/" + folderId + "/" + objType + "/" + objId;
        return Acm.Service.call({type: "POST"
            ,url: url
            //,data: JSON.stringify(data)
            ,callback: function(response) {
                if (!response.hasError) {
                    if (DocTree.Model.validateCopyFolderInfo(response)) {
                        var copyFolderInfo = response;
                        if (copyFolderInfo.originalFolderId == subFolderId && copyFolderInfo.newFolder.parentFolderId == folderId) {
                            var frFolderList = DocTree.Model.cacheFolderList.get(frCacheKey);
                            var toFolderList = DocTree.Model.cacheFolderList.get(toCacheKey);
                            if (DocTree.Model.validateFolderList(frFolderList) && DocTree.Model.validateFolderList(toFolderList)) {
                                var idx = DocTree.Model.findFolderItemIdx(subFolderId, frFolderList);
                                if (0 <= idx) {
                                    var folderData = DocTree.Model.folderToSolrData(frFolderList.children[idx]);
                                    folderData.objectId = copyFolderInfo.newFolder.id;
                                    folderData.folderId = copyFolderInfo.newFolder.parentFolderId;
                                    folderData.modified = Acm.goodValue(copyFolderInfo.newFolder.modified);
                                    folderData.modifier = Acm.goodValue(copyFolderInfo.newFolder.modifier);
                                    toFolderList.children.push(folderData);
                                    toFolderList.totalChildren++;
                                    DocTree.Model.cacheFolderList.put(toCacheKey, toFolderList);
                                    return folderData;
                                }
                            }
                        }
                    } //end validate
                }
            }
        })
    }
    ,copyFile: function(fileId, folderId, toCacheKey) {
        var objType = DocTree.Model.getObjType();
        var objId = DocTree.Model.getObjId();
        var url = this.API_COPY_FILE_ + objType + "/" + objId;
        var data = {"id": fileId, "folderId": folderId};
        return Acm.Service.call({type: "POST"
            ,url: url
            ,data: JSON.stringify(data)
            ,callback: function(response) {
                if (!response.hasError) {
                    if (DocTree.Model.validateCopyFileInfo(response)) {
                        var copyFileInfo = response;
                        if (copyFileInfo.originalId == fileId && copyFileInfo.newFile.folder.id == folderId) {
                            var toFolderList = DocTree.Model.cacheFolderList.get(toCacheKey);
                            if (DocTree.Model.validateFolderList(toFolderList)) {
                                var fileData = DocTree.Model.fileToSolrData(copyFileInfo.newFile);
                                toFolderList.children.push(fileData);
                                toFolderList.totalChildren++;
                                DocTree.Model.cacheFolderList.put(toCacheKey, toFolderList);
                                return fileData;
                            }
                        }
                    }
                } //end else
            }
        })
    }
    ,deleteFolder: function(folderId, cacheKey, callerData) {
        var url = this.API_DELETE_FOLDER_ + folderId;
        return Acm.Service.call({type: "DELETE"
            ,url : url
            ,callback: function(response) {
                if (!response.hasError) {
                    if (DocTree.Model.validateDeletedFolder(response)) {
                        if (response.deletedFolderId == folderId) {
                            var folderList = DocTree.Model.cacheFolderList.get(cacheKey);
                            if (DocTree.Model.validateFolderList(folderList)) {
                                var deleted = DocTree.Model.findFolderItemIdx(folderId, folderList);
                                if (0 <= deleted) {
                                    folderList.children.splice(deleted, 1);
                                    folderList.totalChildren--;
                                    DocTree.Model.cacheFolderList.put(cacheKey, folderList);
                                    return response.deletedFolderId;
                                }
                            }
                        }
                    }
                } //end validate
            }
        });
    }
    ,deleteFile: function(fileId, cacheKey) {
        var url = this.API_DELETE_FILE_ + fileId;
        return Acm.Service.call({type: "DELETE"
            ,url: url
            ,callback: function(response) {
                if (!response.hasError) {
                    if (DocTree.Model.validateDeletedFile(response)) {
                        if (response.deletedFileId == fileId) {
                            var folderList = DocTree.Model.cacheFolderList.get(cacheKey);
                            if (DocTree.Model.validateFolderList(folderList)) {
                                var deleted = DocTree.Model.findFolderItemIdx(fileId, folderList);
                                if (0 <= deleted) {
                                    folderList.children.splice(deleted, 1);
                                    folderList.totalChildren--;
                                    DocTree.Model.cacheFolderList.put(cacheKey, folderList);
                                    return response.deletedFileId;
                                }
                            }
                        }
                    } //end validate
                }
            }
        });
    }
    ,renameFolder: function(folderName, folderId, cacheKey) {
        var url = this.API_RENAME_FOLDER_ + folderId + "/" + folderName;
        return Acm.Service.call({type: "POST"
            ,url: url
            ,callback: function(response) {
                if (!response.hasError) {
                    if (DocTree.Model.validateRenamedFolder(response)) {
                        if (response.id == folderId) {
                            var renamedInfo = response;
                            var folderList = DocTree.Model.cacheFolderList.get(cacheKey);
                            var idx = DocTree.Model.findFolderItemIdx(folderId, folderList);
                            if (0 <= idx) {
                                folderList.children[idx].name = Acm.goodValue(renamedInfo.name);
                                DocTree.Model.cacheFolderList.put(cacheKey, folderList);
                                return renamedInfo;
                            }
                        }
                    } //end validate
                }
            }
        });
    }
    ,renameFile: function(fileName, fileId, cacheKey) {
        var name = fileName;
        var ext = "";
        var ar = fileName.split(".");
        if  (Acm.isArray(ar) && 1 < ar.length) {
            ext = ar[ar.length-1];
            name = fileName.substring(0, fileName.length - ext.length - 1);
        }
        var url = App.getContextPath() + this.API_RENAME_FILE_ + fileId + "/" + name + "/" + ext;

        return Acm.Service.call({type: "POST"
            ,url: url
            ,callback: function(response) {
                if (!response.hasError) {
                    if (DocTree.Model.validateRenamedFile(response)) {
                        if (response.fileId == fileId) {
                            var renamedInfo = response;
                            var folderList = DocTree.Model.cacheFolderList.get(cacheKey);
                            var idx = DocTree.Model.findFolderItemIdx(fileId, folderList);
                            if (0 <= idx) {
                                folderList.children[idx].name = Acm.goodValue(renamedInfo.fileName);
                                DocTree.Model.cacheFolderList.put(cacheKey, folderList);
                                return renamedInfo;
                            }
                        }
                    } //end validate
                }
            }
        })
    }
    ,setActiveVersion: function(fileId, version, cacheKey) {
        var url = this.API_SET_ACTIVE_VERSION_ + fileId + "?versionTag=" + version;
        return Acm.Service.call({type: "POST"
            ,url: url
            ,callback: function(response) {
                if (!response.hasError) {
                    if (DocTree.Model.validateActiveVersion(response)) {
                        if (response.fileId == fileId) {
                            var activeVersion = response;
                            var folderList = DocTree.Model.cacheFolderList.get(cacheKey);
                            if (DocTree.Model.validateFolderList(folderList)) {
                                var idx = DocTree.Model.findFolderItemIdx(fileId, folderList);
                                if (0 <= idx) {
                                    folderList.children[idx].activeVersionTag = Acm.goodValue(activeVersion.activeVersionTag);
                                    DocTree.Model.cacheFolderList.put(cacheKey, folderList);
                                    return activeVersion;
                                }
                            }
                        }
                    } //end validate
                }
            }
        })
    }
    ,lodgeDocuments: function(folderNames, docIds) {
        var objType = DocTree.Model.getObjType();
        var objId = DocTree.Model.getObjId();

        //folderNames.shift(); //remove top node

        //make a copy except folderNames[0] - the top Node
        var copyNames = [];
        for (var i = 1; i < folderNames.length; i ++) {
            copyNames.push(folderNames[i]);
        }

        var folderPath = "/" + copyNames.join("/");
        var url = this.API_LODGE_DOCUMENT + "targetObjectType=" + objType + "&targetObjectId=" + objId + "&newPath=" + folderPath;
        if (!Acm.isArrayEmpty(docIds)) {
            url += "&docIds=" + docIds.join();
        }
        return Acm.Service.call({type: "PUT"
            ,url: url
            ,callback: function(response) {
                if (!response.hasError) {
                    if (DocTree.Model.validateCreateInfo(response)) {
                        //if (response.parentFolderId == parentId) {
                        var createInfo = response;
                        var createdFolder = DocTree.Model.folderToSolrData(createInfo);
                        return createdFolder;
//                            var folderList = DocTree.Model.cacheFolderList.get(cacheKey);
//                            if (DocTree.Model.validateFolderList(folderList)) {
//                                var createdFolder = DocTree.Model.folderToSolrData(createInfo);
//                                folderList.children.push(createdFolder);
//                                folderList.totalChildren++;
//                                DocTree.Model.cacheFolderList.put(cacheKey, folderList);
//                                return createdFolder;
//                            }
                        //}
                    } //end validate
                }
            }
        });
    }
    ,sendEmail: function(emailNotifications) {
        var url = this.API_SEND_EMAIL_;
        var failed = "";
        return Acm.Service.call({type: "POST"
            ,url: url
            ,data: JSON.stringify(emailNotifications)
            ,callback: function(response) {
                if (DocTree.Model.validateSentEmails(response)) {
                    for(var i = 0; i < response.length; i++){
                        if("NOT_SENT" == response[i].state){
                            failed += response[i].userEmail + ";";
                        }
                    }
                    if(Acm.isEmpty(failed)){
                        return response;
                    }
                }
            }
            ,invalid: function(response) {
                return failed;
            }
        })
    }

    ,declareAsRecord: function(data) {
        var url = this.API_DECLARE_AS_RECORD + DocTree.Model.getObjType() + "/" + DocTree.Model.getObjId();
        return Acm.Service.call({type: "POST"
            ,url: url
            ,data: JSON.stringify(data)
            ,callback: function(response) {
                return true;
            }
        })
    }


    ,validateFolderList: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isNotArray(data.children)) {
            return false;
        }
        return true;
    }
    ,validateCreateInfo: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.id)) {
            return false;
        }
        if (0 == data.id) {
            return false;
        }
        if (Acm.isEmpty(data.parentFolderId)) {
            return false;
        }
        return true;
    }
    ,validateDeletedFolder: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.deletedFolderId)) {
            return false;
        }
        return true;
    }
    ,validateDeletedFile: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.deletedFileId)) {
            return false;
        }
        return true;
    }
    ,validateRenamedFolder: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.id)) {
            return false;
        }
        if (Acm.isEmpty(data.name)) {
            return false;
        }
        return true;
    }
    ,validateRenamedFile: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.fileId)) {
            return false;
        }
        if (Acm.isEmpty(data.fileName)) {
            return false;
        }
        return true;
    }
    ,validateMoveFileInfo: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.fileId)) {
            return false;
        }
        if (Acm.isEmpty(data.folder)) {
            return false;
        }
        if (Acm.isEmpty(data.folder.id)) {
            return false;
        }
        return true;
    }
    ,validateCopyFileInfo: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.originalId)) {
            return false;
        }
        if (Acm.isEmpty(data.newFile)) {
            return false;
        }
        if (Acm.isEmpty(data.newFile.fileId)) {
            return false;
        }
        if (Acm.isEmpty(data.newFile.folder)) {
            return false;
        }
        if (Acm.isEmpty(data.newFile.folder.id)) {
            return false;
        }
        return true;
    }
    ,validateMoveFolderInfo: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.id)) {
            return false;
        }
        return true;
    }
    ,validateCopyFolderInfo: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.originalFolderId)) {
            return false;
        }
        if (Acm.isEmpty(data.newFolder)) {
            return false;
        }
        if (Acm.isEmpty(data.newFolder.id)) {
            return false;
        }
        if (Acm.isEmpty(data.newFolder.parentFolderId)) {
            return false;
        }
        return true;
    }
    ,validateUploadInfo: function(data) {
        if (Acm.isArrayEmpty(data)) {
            return false;
        }
        for (var i = 0; i < data.length; i++) {
            if (!this.validateUploadInfoItem(data[i])) {
                return false;
            }
        }
        return true;
    }
    ,validateReplaceInfo: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.fileId)) {
            return false;
        }
        return true;
    }
    ,validateUploadInfoItem: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.fileId)) {
            return false;
        }
        if (Acm.isEmpty(data.folder)) {
            return false;
        }
        if (Acm.isNotArray(data.versions)) {
            return false;
        }
        if (Acm.isNotArray(data.tags)) {
            return false;
        }
        return true;
    }
    ,validateActiveVersion: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.fileId)) {
            return false;
        }
        if (Acm.isEmpty(data.activeVersionTag)) {
            return false;
        }
        return true;
    }
    ,validateSentEmails: function(data){
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isNotArray(data)) {
            return false;
        }
        for (var i = 0; i < data.length; i++) {
            if (!this.validateSentEmail(data[i])) {
                return false;
            }
        }
        return true;
    }
    ,validateSentEmail: function(data){
        if (Acm.isEmpty(data.state)) {
            return false;
        }
        if (Acm.isEmpty(data.userEmail)) {
            return false;
        }
        return true;
    }

    ,Key: {
        create: function(args) {
        }
        ,onInitialized: function() {
        }

        ,KEY_SEPARATOR               : "/"
        ,TYPE_ID_SEPARATOR           : "."
        ,NODE_TYPE_PART_PREV_PAGE    : "prevPage"
        ,NODE_TYPE_PART_NEXT_PAGE    : "nextPage"
        ,NODE_TYPE_PART_PAGE         : "p"


        //keyParts format: [{type: "t", id: "123"}, ....]
        //Integer ID works as well: [{type: "t", id: 123}, ....]
        ,makeKey: function(keyParts) {
            var key = "";
            if (Acm.isArray(keyParts)) {
                for (var i = 0; i < keyParts.length; i++) {
                    if (keyParts[i].type) {
                        if (Acm.isNotEmpty(key)) {
                            key += this.KEY_SEPARATOR;
                        }
                        key += keyParts[i].type;

                        if (Acm.isNotEmpty(keyParts[i].id)) {
                            key += this.TYPE_ID_SEPARATOR;
                            key += keyParts[i].id;
                        }
                    }
                } //for i
            }
            return key;
        }


    }

    ,Config: {
        create: function(args) {
        }
        ,onInitialized: function() {
        	var configCategory = "caseFile";
        	App.Model.Config.requestConfig(configCategory).done(function(data) { // retrieves the case_file/roi doc type mapping config (afdp-1249)
       		 var cfg = App.Model.Config.getConfig(configCategory);
                if (Acm.isNotEmpty(cfg)) {
                    DocTree.Model.Config.caseMapping = Acm.goodValue(cfg["casefile.doctype.mapping.case"], "");
                    DocTree.Model.Config.roiMapping = Acm.goodValue(cfg["casefile.doctype.mapping.roi"], "");
                }
       	    });
        }

        ,roiMapping: ""
        ,caseMapping: ""
        ,DEFAULT_MAX_ROWS: 1000
        ,DEFAULT_SORT_BY: "name"
        ,DEFAULT_SORT_DIRECTION: "ASC"
        ,_setting: {
            maxRows: 16
            ,sortBy: null
            ,sortDirection: null
//            ,objType: null
//            ,objId: 0
//            ,category: "Document"
//            ,folderId: 0
//            ,start: 0
//            ,totalChildren: 0
        }
        ,getSetting: function() {
            return this._setting;
        }
        ,getMaxRows: function() {
            return Acm.goodValue(this._setting.maxRows, this.DEFAULT_MAX_ROWS);
        }
        ,getSortBy: function() {
            return Acm.goodValue(this._setting.sortBy, this.DEFAULT_SORT_BY);
        }
        ,getSortDirection: function() {
            return Acm.goodValue(this._setting.sortDirection, this.DEFAULT_SORT_DIRECTION);
        }
    }

};

