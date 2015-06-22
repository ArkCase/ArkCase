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
    ,API_LODGE_DOCUMENT               : "/api/latest/service/ecm/createFolderByPath"             //  ?targetObjectType={objType}&targetObjectId={objId}&newPath={fullPath}


//    ,retrieveFolderListDeferred: function(objType, objId, folderId, pageId, callerData, callbackSuccess) {
//        var setting = DocTree.Model.Config.getSetting();
//        var url = DocTree.Service.API_RETRIEVE_FOLDER_LIST_ + objType + "/" + objId;
//        //var url = DocTree.Service.API_RETRIEVE_FOLDER_LIST_ + objType + "/" + objId;
//        if (0 < folderId) {
//            url += "/" + folderId;
//        }
//        url += "?start=" + pageId;
//        url += "&n=" + DocTree.Model.Config.getMaxRows();
//        if (Acm.isNotEmpty(setting.sortBy) && Acm.isNotEmpty(setting.sortDirection)) {
//            url += "&s=" + setting.sortBy + "&dir=" + setting.sortDirection;
//        }
//
//        return Acm.Service.call({type: "GET"
//            ,url: url
//            ,callback: function(data) {
//                var folderList = null;
//                if (DocTree.Model.validateFolderList(data)) {
//                    folderList = data;
//                    var setting = DocTree.Model.Config.getSetting();
//                    setting.maxRows = Acm.goodValue(folderList.maxRows, 0);
//                    setting.sortBy = Acm.goodValue(folderList.sortBy);
//                    setting.sortDirection = Acm.goodValue(folderList.sortDirection);
//
//                    var cacheKey = DocTree.Model.getCacheKey(folderId, pageId);
//                    DocTree.Model.cacheFolderList.put(cacheKey, folderList);
//                }
//
//                var rc = callbackSuccess(folderList);
//                DocTree.Controller.modelRetrievedFolderList(folderList, objType, objId, folderId, pageId, callerData);
//                return rc;
//            }
//        });
//
//    }
//
//
//    //
//    // folderNode is a concept in View, Model should not use any View object.
//    // This is a hack for now until we find way to get Frevvo to pass data back uploaded files to UI
//    //
//    ,checkUploadForm: function(objType, objId, folderId, pageId, folderNode, fileType) {
//        return DocTree.Service.retrieveFolderListDeferred(objType, objId, folderId, pageId, folderNode, function(folderListLatest) {
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
//            mock.category   = folderListLatest.children[i].category;
//            mock.version    = "1.1";
//            mock.versionList  = [{versionTag:"1.0"},{versionTag:"1.1"}];
//            folderListLatest.children.push(mock);
//            folderListLatest.totalChildren++;
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
//            mock.category   = folderListLatest.children[i].category;
//            mock.version    = "1.2";
//            mock.versionList  = [{versionTag:"1.0"}, {versionTag:"1.1"}, {versionTag:"1.2"}];
//            folderListLatest.children.push(mock);
//            folderListLatest.totalChildren++;
//
//            var uploadedFiles = null;
//            if (DocTree.Model.validateFolderList(folderListLatest)) {
//                var newChildren = [];
//                for (var i = folderListLatest.children.length - 1; 0 <= i; i--) {
//                    if (folderListLatest.children[i].type == fileType) {
//                        if (!DocTree.View.findChildNodeById(folderNode, folderListLatest.children[i].objectId)) { //not found in the tree node, must be newly created
//                            newChildren.push(folderListLatest.children[i]);
//                        }
//                    }
//                }
//                if (!Acm.isArrayEmpty(newChildren)) {
//                    var cacheKey = DocTree.Model.getCacheKey(folderId, pageId);
//                    var folderList = DocTree.Model.cacheFolderList.get(cacheKey);
//                    if (DocTree.Model.validateFolderList(folderList)) {
//                        uploadedFiles = [];
//                        for (var i = 0; i < newChildren.length; i++) {
//                            var uploadedFile = DocTree.Model.fileToSolrData(newChildren[i]);
//                            uploadedFiles.push(uploadedFile);
//                            //folderList.children.push(uploadedFile);
//                            //folderList.totalChildren++;
//                        }
//                    } //end if validateFolderList
//                } //end if (!Acm.isArrayEmpty(newChildren))
//            }
//            return uploadedFiles;
//        });
//    }


    ,sendEmail: function(emailNotifications) {
        var url = App.getContextPath() + this.API_SEND_EMAIL_;
        return Acm.Service.call({type: "POST"
            ,url: url
            ,data: JSON.stringify(emailNotifications)
            ,callback: function(response) {
                if(Acm.isArray(response)){
                    var failed;
                    for(var i = 0; i < response.length; i++){
                        if (DocTree.Model.validateSentEmail(response[i])) {
                            if("NOT_SENT" == response[i].state){
                                failed += response[i].userEmail + ";";
                            }
                        }
                    }
                    if(Acm.isNotEmpty(failed)){
                        //jwu: missed it at the code review. We do not want to have UI code in model/service
                        Acm.MessageBoard.show("Email delivery failed to :  ") + failed + "\n" + "Please check provided email addresses and try again";
                    }
                }
            }
        })
    }
};

