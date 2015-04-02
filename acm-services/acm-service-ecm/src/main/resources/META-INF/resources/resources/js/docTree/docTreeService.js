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

    ,API_RETRIEVE_FOLDER_LIST_        : "/api/latest/service/ecm/folder/"
    ,API_UPLOAD_FILE                  : "/api/latest/service/ecm/upload"
    ,API_DOWNLOAD_DOCUMENT_           : "/api/v1/plugin/ecm/download/byId/"

    ,retrieveFolderListDeferred: function(objType, objId, folderId, pageId, callbackSuccess) {
        var setting = DocTree.Model.Config.getSetting();
        var url = App.getContextPath() + DocTree.Service.API_RETRIEVE_FOLDER_LIST_ + objType + "/" + objId + "?start=" + pageId;
        url += "&n=" + DocTree.Model.Config.getMaxRows();
        if (Acm.isNotEmpty(setting.sortBy) && Acm.isNotEmpty(setting.sortDirection)) {
            url += "&s=" + setting.sortBy + "&dir=" + setting.sortDirection;
        }

        return Acm.Service.deferredGet(function(data) {
                if (DocTree.Model.validateFolderList(data)) {
                    var fd = data;
                    var setting = DocTree.Model.Config.getSetting();
                    setting.maxRows = Acm.goodValue(fd.maxRows, 0);
                    setting.sortBy = Acm.goodValue(fd.sortBy);
                    setting.sortDirection = Acm.goodValue(fd.sortDirection);

                    var cacheKey = DocTree.Model.getCacheKey(folderId, pageId);
                    DocTree.Model.cacheFolder.put(cacheKey, fd);
                }

                var rc = callbackSuccess(fd);
                return rc;
            }
            ,url
        );

    }

    ,_findFolderNode: function(folderNode, fileId) {
        var found = -1;
        for (var j = folderNode.children.length - 1; 0 <= j; j--) {
            if (folderNode.children[j].data.objectId == fileId) {
                found = j;
                break;
            }
        }
        return found;
    }
    ,_findEmptyNode: function(folderNode) {
        var nodeNotInFd = -1;
        for (var i = folderNode.children.length - 1; 0 <= i; i--) {
            if (Acm.isEmpty(folderNode.children[i].data.objectId)) {
                nodeNotInFd = i;
                break;
            }
        }
        return nodeNotInFd;
    }
    ,checkUploadForm: function(objType, objId, folderId, pageId, folderNode) {
        return DocTree.Service.retrieveFolderListDeferred(objType, objId, folderId, pageId, function(fd) {
//            var mock = {};
//            var i = fd.children.length - 1;
//            mock.objectId   = fd.children[i].objectId + 1000;
//            mock.objectType = fd.children[i].objectType;
//            mock.created    = fd.children[i].created;
//            mock.creator    = fd.children[i].creator;
//            mock.modified   = fd.children[i].modified;
//            mock.modifier   = fd.children[i].modifier;
//            mock.name       = "Mock";
//            mock.type       = fd.children[i].type;
//            mock.status     = fd.children[i].status;
//            mock.version    = fd.children[i].version;
//            mock.category   = fd.children[i].category;
//            fd.children.push(mock);

            var newDoc = null;
            if (Acm.isArray(fd.children) && Acm.isArray(folderNode.children)) {
                var fdNotInNode = -1;
                for (var i = fd.children.length - 1; 0 <= i; i--) {
//                    var found = -1;
//                    for (var j = folderNode.children.length - 1; 0 <= j; j--) {
//                        if (folderNode.children[j].data.objectId == fd.children[i].objectId) {
//                            found = j;
//                            break;
//                        }
//                    }
                    var found = DocTree.Service._findFolderNode(folderNode, fd.children[i].objectId);
                    if (0 > found) { //not found in the tree node, must be newly created
                        fdNotInNode = i;
                        break;
                    }
                }

//                var nodeNotInFd = -1;
//                for (var i = folderNode.children.length - 1; 0 <= i; i--) {
//                    if (Acm.isEmpty(folderNode.children[i].data.objectId)) {
//                        nodeNotInFd = i;
//                        break;
//                    }
//                }
                var nodeNotInFd = DocTree.Service._findEmptyNode(folderNode);

                if (0 <= nodeNotInFd && 0 <= fdNotInNode) {
                    //if ("file" == fd.children[fdNotInNode].objectType && folderNode.children[nodeNotInFd].data.type == fd.children[fdNotInNode].type) { //double check to be sure the new doc is what we expected
                    newDoc = {};
                    var i = fdNotInNode;
                    newDoc.objectId   = Acm.goodValue(fd.children[i].objectId, 0);
                    newDoc.objectType = Acm.goodValue(fd.children[i].objectType, "file");
                    newDoc.created    = Acm.goodValue(fd.children[i].created);
                    newDoc.creator    = Acm.goodValue(fd.children[i].creator);
                    newDoc.modified   = Acm.goodValue(fd.children[i].modified);
                    newDoc.modifier   = Acm.goodValue(fd.children[i].modifier);
                    newDoc.name       = Acm.goodValue(fd.children[i].name);
                    newDoc.type       = Acm.goodValue(fd.children[i].type);
                    newDoc.status     = Acm.goodValue(fd.children[i].status);
                    newDoc.version    = Acm.goodValue(fd.children[i].version);
                    newDoc.category   = Acm.goodValue(fd.children[i].category);
                    //}
                }
            }

            return newDoc;
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
                        var uploadedFile = null;
                        if (DocTree.Model.validateUploadInfo(response)) {
                            var uploadInfo = response;

                            var fd = DocTree.Model.cacheFolder.get(cacheKey);
                            if (DocTree.Model.validateFolderList(fd)) {
                                uploadedFile = {};
                                uploadedFile.category  = Acm.goodValue(uploadInfo[0].category);
                                uploadedFile.objectId   = Acm.goodValue(uploadInfo[0].fileId);
                                uploadedFile.objectType = "file";
                                uploadedFile.created    = Acm.goodValue(uploadInfo[0].created);
                                uploadedFile.creator    = Acm.goodValue(uploadInfo[0].creator);
                                uploadedFile.modified   = Acm.goodValue(uploadInfo[0].modified);
                                uploadedFile.modifier   = Acm.goodValue(uploadInfo[0].modifier);
                                uploadedFile.name       = Acm.goodValue(uploadInfo[0].fileName);
                                uploadedFile.type       = Acm.goodValue(uploadInfo[0].fileType);
                                uploadedFile.status     = Acm.goodValue(uploadInfo[0].status);
                                uploadedFile.version    = Acm.goodValue(uploadInfo[0].activeVersionTag);
//                                uploadedFile.version = "";
//                                if (!Acm.isArrayEmpty(uploadInfo[0].versions)) {
//                                    uploadedFile.version = uploadInfo[0].versions[0].versionTag;
//                                }
                                //uploadInfo.tags
                                fd.children.push(uploadedFile);
                                DocTree.Model.cacheFolder.put(cacheKey, fd);

                                DocTree.Controller.modelUploadedFile(uploadInfo, callerData);
                                $dfd.resolve(uploadedFile);
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

    ,uploadFile0: function(formData, key) {
        var url = App.getContextPath() + this.API_UPLOAD_FILE;
        return Acm.Service.ajax({
            url: url
            ,data: formData
            ,processData: false
            ,contentType: false
            ,type: 'POST'
            ,success: function(response){
                if (response.hasError) {
                    DocTree.Controller.modelUploadedFile(response, key);
                } else {
                    if (DocTree.Model.validateUploadInfo(response)) {
                        var uploadInfo = response;
                        //DocTree.Model.setUploadInfo(uploadInfo);
                        DocTree.Controller.modelUploadedFile(uploadInfo, key);
                    }
                }
            }
        });
    }

    ,testService: function(node, parentId, folder) {
        setTimeout(function(){
            var folder = {id: 123, some: "some", value: "value"};
            DocTree.Controller.modelAddedFolder(node, parentId, folder);
        }, 1000);
    }
    ,testService2: function(node, parentId, folder) {
        setTimeout(function(){
            var folder = {id: 123, some: "some", value: "value"};
            DocTree.Controller.modelAddedDocument(node, parentId, folder);
        }, 1000);
    }

};

