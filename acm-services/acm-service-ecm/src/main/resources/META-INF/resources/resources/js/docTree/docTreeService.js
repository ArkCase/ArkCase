/**
 * DocTree.Service
 *
 * manages all service call to application server
 *
 * @author jwu
 */
DocTree.Service = {
    create : function(args) {
    }
    ,onInitialized: function() {
    }

    ,API_RETRIEVE_FOLDER_LIST_        : "/api/latest/service/ecm/folder/"                        //  {objectType}/{objectId}/{folderId}
    ,API_CREATE_FOLDER_               : "/api/latest/service/ecm/folder/"                        //  {folderId}/{newFolderName}
    ,API_DELETE_FOLDER_               : "/api/latest/service/ecm/folder/"
    ,API_UPLOAD_FILE                  : "/api/latest/service/ecm/upload"
    ,API_DOWNLOAD_DOCUMENT_           : "/api/v1/plugin/ecm/download/byId/"
    ,API_DELETE_FILE_                 : "/api/latest/service/ecm/id/"
    ,API_RENAME_FOLDER_               : "/api/latest/service/ecm/folder/"                        //  {folderId}/{newFolderName}
    ,API_RENAME_FILE_                 : "/api/latest/service/ecm/file/"                          //  {objectId}/{newName}/{extension}
    ,API_MOVE_FILE_                   : "/api/latest/service/ecm/moveToAnotherContainer/"        //  {targetObjectType}/{targetObjectId}
    ,API_COPY_FILE_                   : "/api/latest/service/ecm/copyToAnotherContainer/"        //  {targetObjectType}/{targetObjectId}
    ,API_MOVE_FOLDER_                 : "/api/latest/service/folder/move/"                       //  {folderToMoveId}/{dstFolderId}
    ,API_COPY_FILE_                   : "/api/latest/service/ecm/copyToAnotherContainer/"        //  {targetObjectType}/{targetObjectId}
    ,API_SET_ACTIVE_VERSION_          : "to be defined soon"

    ,retrieveFolderListDeferred: function(objType, objId, folderId, pageId, callerData, callbackSuccess) {
        var setting = DocTree.Model.Config.getSetting();
        var url = App.getContextPath() + DocTree.Service.API_RETRIEVE_FOLDER_LIST_ + objType + "/" + objId;
        if (0 < folderId) {
            url += "/" + folderId;
        }
        url += "?start=" + pageId;
        url += "&n=" + DocTree.Model.Config.getMaxRows();
        if (Acm.isNotEmpty(setting.sortBy) && Acm.isNotEmpty(setting.sortDirection)) {
            url += "&s=" + setting.sortBy + "&dir=" + setting.sortDirection;
        }

        return Acm.Service.deferredGet(function(data) {
                var folderList = null;
                if (DocTree.Model.validateFolderList(data)) {
                    folderList = data;
                    var setting = DocTree.Model.Config.getSetting();
                    setting.maxRows = Acm.goodValue(folderList.maxRows, 0);
                    setting.sortBy = Acm.goodValue(folderList.sortBy);
                    setting.sortDirection = Acm.goodValue(folderList.sortDirection);

                    var cacheKey = DocTree.Model.getCacheKey(folderId, pageId);
                    DocTree.Model.cacheFolderList.put(cacheKey, folderList);
                }

                var rc = callbackSuccess(folderList);
                DocTree.Controller.modelRetrievedFolderList(folderList, objType, objId, folderId, pageId, callerData);
                return rc;
            }
            ,url
        );

    }

    ,_findFolderNode: function(folderNode, fileId) {
        var node = null;
        for (var j = folderNode.children.length - 1; 0 <= j; j--) {
            if (folderNode.children[j].data.objectId == fileId) {
                node = folderNode.children[j];
                break;
            }
        }
        return node;
    }

    //
    // folderNode is a concept in View, Model should not use any View object.
    // This is a hack for now until we find way to get Frevvo to pass data back uploaded files to UI
    //
    ,checkUploadForm: function(objType, objId, folderId, pageId, folderNode, fileType) {
        return DocTree.Service.retrieveFolderListDeferred(objType, objId, folderId, pageId, folderNode, function(folderListLatest) {
//            var mock = {};
//            var i = folderListLatest.children.length - 1;
//            mock.objectId   = folderListLatest.children[i].objectId + 1001;
//            mock.objectType = folderListLatest.children[i].objectType;
//            mock.created    = folderListLatest.children[i].created;
//            mock.creator    = folderListLatest.children[i].creator;
//            mock.modified   = folderListLatest.children[i].modified;
//            mock.modifier   = folderListLatest.children[i].modifier;
//            mock.name       = "Mock";
//            mock.type       = fileType;
//            mock.status     = folderListLatest.children[i].status;
//            mock.version    = folderListLatest.children[i].version;
//            mock.category   = folderListLatest.children[i].category;
//            folderListLatest.children.push(mock);
//            mock = {};
//            i = folderListLatest.children.length - 1;
//            mock.objectId   = folderListLatest.children[i].objectId + 1002;
//            mock.objectType = folderListLatest.children[i].objectType;
//            mock.created    = folderListLatest.children[i].created;
//            mock.creator    = folderListLatest.children[i].creator;
//            mock.modified   = folderListLatest.children[i].modified;
//            mock.modifier   = folderListLatest.children[i].modifier;
//            mock.name       = "Mock2";
//            mock.type       = fileType;
//            mock.status     = folderListLatest.children[i].status;
//            mock.version    = folderListLatest.children[i].version;
//            mock.category   = folderListLatest.children[i].category;
//            folderListLatest.children.push(mock);

            var uploadedFiles = null;
            if (DocTree.Model.validateFolderList(folderListLatest)) {
                var newChildren = [];
                for (var i = folderListLatest.children.length - 1; 0 <= i; i--) {
                    if (folderListLatest.children[i].type == fileType) {
                        if (!DocTree.Service._findFolderNode(folderNode, folderListLatest.children[i].objectId)) { //not found in the tree node, must be newly created
                            newChildren.push(folderListLatest.children[i]);
                        }
                    }
                }
                if (!Acm.isArrayEmpty(newChildren)) {
                    var cacheKey = DocTree.Model.getCacheKey(folderId, pageId);
                    var folderList = DocTree.Model.cacheFolderList.get(cacheKey);
                    if (DocTree.Model.validateFolderList(folderList)) {
                        uploadedFiles = [];
                        for (var i = 0; i < newChildren.length; i++) {
                            var uploadedFile = {};
                            uploadedFile.objectId   = Acm.goodValue(newChildren[i].objectId, 0);
                            uploadedFile.objectType = Acm.goodValue(newChildren[i].objectType, "file");
                            uploadedFile.created    = Acm.goodValue(newChildren[i].created);
                            uploadedFile.creator    = Acm.goodValue(newChildren[i].creator);
                            uploadedFile.modified   = Acm.goodValue(newChildren[i].modified);
                            uploadedFile.modifier   = Acm.goodValue(newChildren[i].modifier);
                            uploadedFile.name       = Acm.goodValue(newChildren[i].name);
                            uploadedFile.type       = Acm.goodValue(newChildren[i].type);
                            uploadedFile.status     = Acm.goodValue(newChildren[i].status);
                            uploadedFile.version    = Acm.goodValue(newChildren[i].version);
                            uploadedFile.category   = Acm.goodValue(newChildren[i].category);
                            uploadedFiles.push(uploadedFile);
                            folderList.children.push(uploadedFile);
                            folderList.totalChildren++;
                        }
                    } //end if validateFolderList
                } //end if (!Acm.isArrayEmpty(newChildren))
            }
            return uploadedFiles;
        });
    }


    ,uploadFile: function(formData, cacheKey, callerData) {
        return $.Deferred(function($dfd){
            var url = App.getContextPath() + DocTree.Service.API_UPLOAD_FILE;
            Acm.Service.ajax({
                url: url
                ,data: formData
                ,processData: false
                ,contentType: false
                ,type: 'POST'
                ,success: function(response){
                    if (response.hasError) {
                        DocTree.Controller.modelUploadedFile(response, callerData);
                        $dfd.reject();
                    } else {
                        var uploadedFiles = null;
                        if (DocTree.Model.validateUploadInfo(response)) {
                            var uploadInfo = response;

                            var folderList = DocTree.Model.cacheFolderList.get(cacheKey);
                            if (DocTree.Model.validateFolderList(folderList)) {
                                uploadedFiles = [];
                                for (var i = 0; i < uploadInfo.length; i++) {
                                    var uploadedFile = DocTree.Service.fileCreateToFileData(uploadInfo[i]);
//                                    var uploadedFile = {};
//                                    uploadedFile.objectId   = Acm.goodValue(uploadInfo[i].fileId);
//                                    uploadedFile.objectType = "file";
//                                    uploadedFile.created    = Acm.goodValue(uploadInfo[i].created);
//                                    uploadedFile.creator    = Acm.goodValue(uploadInfo[i].creator);
//                                    uploadedFile.modified   = Acm.goodValue(uploadInfo[i].modified);
//                                    uploadedFile.modifier   = Acm.goodValue(uploadInfo[i].modifier);
//                                    uploadedFile.name       = Acm.goodValue(uploadInfo[i].fileName);
//                                    uploadedFile.type       = Acm.goodValue(uploadInfo[i].fileType);
//                                    uploadedFile.status     = Acm.goodValue(uploadInfo[i].status);
//                                    uploadedFile.version    = Acm.goodValue(uploadInfo[i].activeVersionTag);
//                                    uploadedFile.category   = Acm.goodValue(uploadInfo[i].category);
                                    uploadedFiles.push(uploadedFile);
                                    folderList.children.push(uploadedFile);
                                    folderList.totalChildren++;
                                }
                                DocTree.Model.cacheFolderList.put(cacheKey, folderList);

                                DocTree.Controller.modelUploadedFile(uploadInfo, callerData);
                                $dfd.resolve(uploadedFiles);
                            }
                        }

                        if (!uploadedFile) {
                            $dfd.reject();
                        }
                    }
                }
            });
        });
    }
    ,createFolder: function(parentId, folderName, cacheKey, callerData) {
        var url = App.getContextPath() + this.API_CREATE_FOLDER_ + parentId + "/" + folderName;
        Acm.Service.call({type: "PUT"
            ,url: url
            ,callback: function(response) {
                if (response.hasError) {
                    DocTree.Controller.modelCreatedFolder(response, parentId, folderName, cacheKey, callerData);

                } else {
                    if (DocTree.Model.validateCreateInfo(response)) {
                        if (response.parentFolderId == parentId) {
                            var createInfo = response;

                            var folderList = DocTree.Model.cacheFolderList.get(cacheKey);
                            if (DocTree.Model.validateFolderList(folderList)) {
                                var createdFolder = DocTree.Service.folderCreateToFileData(createInfo);
    //                                var createdFolder = {};
    //                                createdFolder.objectId   = Acm.goodValue(createInfo.id, 0);
    //                                createdFolder.objectType = "folder";
    //                                createdFolder.created    = Acm.goodValue(createInfo.created);
    //                                createdFolder.creator    = Acm.goodValue(createInfo.creator);
    //                                createdFolder.modified   = Acm.goodValue(createInfo.modified);
    //                                createdFolder.modifier   = Acm.goodValue(createInfo.modifier);
    //                                createdFolder.name       = Acm.goodValue(createInfo.name);
    //                                createdFolder.folderId   = Acm.goodValue(createInfo.parentFolderId, 0);
                                folderList.children.push(createdFolder);
                                folderList.totalChildren++;
                                DocTree.Model.cacheFolderList.put(cacheKey, folderList);
                                DocTree.Controller.modelCreatedFolder(createdFolder, parentId, folderName, cacheKey, callerData);
                                return true;
                            }
                        }
                    }
                } //end else
            }
        });
    }
    ,deleteFolder: function(folderId, cacheKey, callerData) {
        var url = App.getContextPath() + this.API_DELETE_FOLDER_ + folderId;
        Acm.Service.asyncDelete(
            function(response) {
                if (response.hasError) {
                    DocTree.Controller.modelDeletedFolder(response, folderId, cacheKey, callerData);

                } else {
                    if (DocTree.Model.validateDeletedFolder(response)) {
                        if (DocTree.Model.validateDeleteFolder(response)) {
                            if (response.deletedFolderId == folderId) {
                                var folderList = DocTree.Model.cacheFolderList.get(cacheKey);
                                if (DocTree.Model.validateFolderList(folderList)) {
//                                    var deleted = -1;
//                                    for (var i = 0; i < folderList.children.length; i++) {
//                                        if (folderList.children[i].objectId == folderId) {
//                                            var deleted = i;
//                                            break;
//                                        }
//                                    }
                                    var deleted = DocTree.Model.findFolderItemIdx(folderId, folderList);
                                    if (0 <= deleted) {
                                        folderList.children.splice(deleted, 1);
                                        folderList.totalChildren--;
                                        DocTree.Model.cacheFolderList.put(cacheKey, folderList);
                                        DocTree.Controller.modelDeletedFolder(response, folderId, cacheKey, callerData);
                                    }
                                }
                            }
                        }
                    }
                } //end else
            }
            ,url
        )
    }
    ,deleteFile: function(fileId, cacheKey, callerData) {
        var url = App.getContextPath() + this.API_DELETE_FILE_ + fileId;
        Acm.Service.asyncDelete(
            function(response) {
                if (response.hasError) {
                    DocTree.Controller.modelDeletedFile(response, fileId, cacheKey, callerData);

                } else {
                    if (DocTree.Model.validateDeletedFile(response)) {
                        if (response.deletedFileId == fileId) {
                            var folderList = DocTree.Model.cacheFolderList.get(cacheKey);
                            if (DocTree.Model.validateFolderList(folderList)) {
//                                var deleted = -1;
//                                for (var i = 0; i < folderList.children.length; i++) {
//                                    if (folderList.children[i].objectId == fileId) {
//                                        var deleted = i;
//                                        break;
//                                    }
//                                }
                                var deleted = DocTree.Model.findFolderItemIdx(fileId, folderList);
                                if (0 <= deleted) {
                                    folderList.children.splice(deleted, 1);
                                    folderList.totalChildren--;
                                    DocTree.Model.cacheFolderList.put(cacheKey, folderList);
                                    DocTree.Controller.modelDeletedFile(response, fileId, cacheKey, callerData);
                                }
                            }
                        }
                    }
                } //end else
            }
            ,url
        )
    }
    ,renameFolder: function(folderName, folderId, cacheKey, callerData) {
        var url = App.getContextPath() + this.API_RENAME_FOLDER_ + folderId + "/" + folderName;
        Acm.Service.asyncPost(
            function(response) {
                if (response.hasError) {
                    DocTree.Controller.modelRenamedFolder(response, folderName, folderId, cacheKey, callerData);

                } else {
                    if (DocTree.Model.validateRenamedFolder(response)) {
                        DocTree.Controller.modelRenamedFolder({hasError: true, errorMsg: "Rename Folder service available now; Ask UI to use it"}, folderName, folderId, cacheKey, callerData);
//                        if (response.parentFolderId == parentId) {
//                            var renamedInfo = response;
//                            var folderList = DocTree.Model.cacheFolderList.get(cacheKey);
//                            var idx = DocTree.Model.findFolderItemIdx(folderId, folderList);
//                            if (0 <= idx) {
//                                folderList[idx].name = Acm.goodValue(renamedInfo.name);
//                                DocTree.Model.cacheFolderList.put(cacheKey, folderList);
//                                DocTree.Controller.modelRenamedFolder(renamedInfo, folderName, folderId, cacheKey, callerData);
//                            }
//                        }
                    }
                } //end else
            }
            ,url
        )
    }
    ,renameFile: function(fileName, fileId, cacheKey, callerData) {
        var name = fileName;
        var ext = "";
        var ar = fileName.split(".");
        if  (Acm.isArray(ar) && 1 < ar.length) {
            ext = ar[ar.length-1];
            name = fileName.substring(0, fileName.length - ext.length - 1);
        }
        var url = App.getContextPath() + this.API_RENAME_FILE_ + fileId + "/" + name + "/" + ext;

        Acm.Service.asyncPost(
            function(response) {
                if (response.hasError) {
                    DocTree.Controller.modelRenamedFile(response, fileName, fileId, cacheKey, callerData);

                } else {
                    if (DocTree.Model.validateRenamedFile(response)) {
                        if (response.fileId == fileId) {
                            var renamedInfo = response;
                            var folderList = DocTree.Model.cacheFolderList.get(cacheKey);
                            var idx = DocTree.Model.findFolderItemIdx(fileId, folderList);
                            if (0 <= idx) {
                                folderList.children[idx].name = Acm.goodValue(renamedInfo.fileName);
                                DocTree.Model.cacheFolderList.put(cacheKey, folderList);
                                DocTree.Controller.modelRenamedFile(renamedInfo, fileName, fileId, cacheKey, callerData);
                            }
                        }
                    }
                } //end else
            }
            ,url
        )
    }
    ,moveFile: function(objType, objId, folderId, fileId, frCacheKey, toCacheKey, callerData) {
        var url = App.getContextPath() + this.API_MOVE_FILE_ + objType + "/" + objId;
        var data = {"id": fileId, "folderId": folderId};
        Acm.Service.call({type: "POST"
            ,url: url
            ,data: JSON.stringify(data)
            ,callback: function(response) {
                if (response.hasError) {
                    DocTree.Controller.modelMovedFile(response, objType, objId, folderId, fileId, frCacheKey, toCacheKey, callerData);

                } else {
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

                            DocTree.Controller.modelMovedFile(moveFileInfo, objType, objId, folderId, fileId, frCacheKey, toCacheKey, callerData);
                            return true;
                        }
                    }
                } //end else
            }
        })
    }
    ,copyFile: function(objType, objId, folderId, fileId, toCacheKey, callerData) {
        var url = App.getContextPath() + this.API_COPY_FILE_ + objType + "/" + objId;
        var data = {"id": fileId, "folderId": folderId};
        Acm.Service.call({type: "POST"
            ,url: url
            ,data: JSON.stringify(data)
            ,callback: function(response) {
                if (response.hasError) {
                    DocTree.Controller.modelCopiedFile(response, objType, objId, folderId, fileId, toCacheKey, callerData);

                } else {
                    if (DocTree.Model.validateCopyFileInfo(response)) {
                        if (response.folder.id == folderId) {
                            var copyFileInfo = response;

                            var toFolderList = DocTree.Model.cacheFolderList.get(toCacheKey);
                            if (DocTree.Model.validateFolderList(toFolderList)) {
                                var fileData = DocTree.Service.fileCreateToFileData(copyFileInfo);
                                toFolderList.children.push(fileData);
                                toFolderList.totalChildren++;
                                DocTree.Model.cacheFolderList.put(toCacheKey, toFolderList);

                                DocTree.Controller.modelCopiedFile(fileData, objType, objId, folderId, fileId, toCacheKey, callerData);
                                return true;
                            }

                        }
                    }
                } //end else
            }
        })
    }
    ,moveFolder: function(subFolderId, folderId, frCacheKey, toCacheKey, callerData) {
        var url = App.getContextPath() + this.API_MOVE_FOLDER_ + subFolderId + "/" + folderId;
        Acm.Service.call({type: "POST"
            ,url: url
            ,callback: function(response) {
                if (response.hasError) {
                    DocTree.Controller.modelMovedFolder(response, subFolderId, folderId, frCacheKey, toCacheKey, callerData);

                } else {
                    if (DocTree.Model.validateMoveFolderInfo(response)) {
                        if (response.fileId == subFolderId) {
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

                            DocTree.Controller.modelMovedFolder(moveFolderInfo, subFolderId, folderId, frCacheKey, toCacheKey, callerData);
                            return true;
                        }
                    }
                } //end else
            }
        })
    }
    ,copyFolder: function(objType, objId, folderId, subFolderId, toCacheKey, callerData) {
        return;

        var url = App.getContextPath() + this.API_COPY_FILE_ + objType + "/" + objId;
        var data = {"id": subFolderId, "folderId": folderId};
        Acm.Service.call({type: "POST"
            ,url: url
            ,data: JSON.stringify(data)
            ,callback: function(response) {
                if (response.hasError) {
                    DocTree.Controller.modelCopiedFolder(response, objType, objId, folderId, subFolderId, toCacheKey, callerData);

                } else {
                    if (DocTree.Model.validateCopyFolderInfo(response)) {
                        if (response.folder.id == folderId) {
                            var copyFolderInfo = response;

                            var toFolderList = DocTree.Model.cacheFolderList.get(toCacheKey);
                            if (DocTree.Model.validateFolderList(toFolderList)) {
                                var fileData = DocTree.Service.fileCreateToFileData(copyFolderInfo);
                                toFolderList.children.push(fileData);
                                toFolderList.totalChildren++;
                                DocTree.Model.cacheFolderList.put(toCacheKey, toFolderList);

                                DocTree.Controller.modelCopiedFolder(fileData, objType, objId, folderId, subFolderId, toCacheKey, callerData);
                                return true;
                            }
                        }
                    }
                } //end else
            }
        })
    }

    ,fileCreateToFileData: function(createInfo) {
        var fileData = {};
        fileData.objectId   = Acm.goodValue(createInfo.fileId);
        fileData.objectType = "file";
        fileData.created    = Acm.goodValue(createInfo.created);
        fileData.creator    = Acm.goodValue(createInfo.creator);
        fileData.modified   = Acm.goodValue(createInfo.modified);
        fileData.modifier   = Acm.goodValue(createInfo.modifier);
        fileData.name       = Acm.goodValue(createInfo.fileName);
        fileData.type       = Acm.goodValue(createInfo.fileType);
        fileData.status     = Acm.goodValue(createInfo.status);
        fileData.version    = Acm.goodValue(createInfo.activeVersionTag);
        fileData.category   = Acm.goodValue(createInfo.category);
        return fileData;
    }
    ,folderCreateToFileData: function(createInfo) {
        var fileData = {};
        fileData.objectId   = Acm.goodValue(createInfo.id, 0);
        fileData.objectType = "folder";
        fileData.created    = Acm.goodValue(createInfo.created);
        fileData.creator    = Acm.goodValue(createInfo.creator);
        fileData.modified   = Acm.goodValue(createInfo.modified);
        fileData.modifier   = Acm.goodValue(createInfo.modifier);
        fileData.name       = Acm.goodValue(createInfo.name);
        fileData.folderId   = Acm.goodValue(createInfo.parentFolderId, 0);
        return fileData;
    }

    ,setActiveVersion: function(fileId, version, cacheKey, callerData) {
        return;

        var url = App.getContextPath() + this.API_SET_ACTIVE_VERSION_ + fileId + "/" + version;
        //var data = {"id": subFolderId, "folderId": folderId};
        Acm.Service.asyncPost2({url: url
            //,data: JSON.stringify(data)
            ,callback: function(response) {
                if (response.hasError) {
                    DocTree.Controller.modelSetActiveVersion(response, fileId, cacheKey, callerData);

                } else {
                    if (DocTree.Model.validateCopyFolderInfo(response)) {
                        if (response.folder.id == folderId) {
                            var copyFolderInfo = response;

                            var folderList = DocTree.Model.cacheFolderList.get(cacheKey);
                            if (DocTree.Model.validateFolderList(folderList)) {
                                var idx = DocTree.Model.findFolderItemIdx(fileId, folderList);
                                if (0 <= idx) {
                                    folderList.children[idx].activeVersionTag = "xxxxxxxxxxxxxxxxx"; //response.activeVersionTag;
                                    DocTree.Model.cacheFolderList.put(cacheKey, folderList);
                                    DocTree.Controller.modelSetActiveVersion(version, fileId, cacheKey, callerData);
                                    return true;
                                }
                            }
                        }
                    }
                } //end else
            }
        })
    }


    ,testService2: function(node, parentId, folder) {
        setTimeout(function(){
            var folder = {id: 123, some: "some", value: "value"};
            DocTree.Controller.modelAddedDocument(node, parentId, folder);
        }, 1000);
    }

};

