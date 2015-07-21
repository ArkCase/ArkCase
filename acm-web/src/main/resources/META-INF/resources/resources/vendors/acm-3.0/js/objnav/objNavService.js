/**
 * ObjNav.Service
 *
 * manages service calls to application server
 *
 * @author jwu
 */
ObjNav.Service = {
    create : function(args) {
        if (ObjNav.Service.List.create)   {ObjNav.Service.List.create(args);}
        if (ObjNav.Service.Detail.create) {ObjNav.Service.Detail.create(args);}
    }
    ,onInitialized: function() {
        if (ObjNav.Service.List.onInitialized)   {ObjNav.Service.List.onInitialized();}
        if (ObjNav.Service.Detail.onInitialized) {ObjNav.Service.Detail.onInitialized();}
    }


    ,List: {
        create : function(args) {
        }
        ,onInitialized: function() {
        }


        ,retrieveObjectList: function(treeInfo){
            var start  = treeInfo.start;
            var n      = treeInfo.n;
            var sort   = treeInfo.sort;
            var filter = treeInfo.filter;
            var searchQuery = treeInfo.searchQuery;

            var url = ObjNav.Model.interface.apiListObjects();
            if (0 <= treeInfo.start) {
                url += "?start=" + treeInfo.start;
            }
            if (0 < treeInfo.n) {
                url += "&n=" + treeInfo.n;
            }
            if (Acm.isNotEmpty(treeInfo.sort)) {
                url += "&s=" + treeInfo.sort;
            }
            if (Acm.isNotEmpty(treeInfo.filter)) {
                url += "&filters=" + treeInfo.filter;
            }
            if (Acm.isNotEmpty(searchQuery)) {
                url += "&searchQuery=" + searchQuery;
            }

            return Acm.Service.call({type: "GET"
                ,url: url
                ,callback: function(response) {
                    if (response.hasError) {
                        ObjNav.Controller.modelRetrievedObjectListError(response);

                    } else {
                        var key = null;
                        if (Acm.Validator.validateSolrData(response)) {
                            var treeInfo = ObjNav.Model.Tree.Config.getTreeInfo();
                            treeInfo.total = response.response.numFound;

                            var objList = response.response.docs;
                            var pageId = ObjNav.Model.Tree.Config.getPageId();
                            ObjNav.Model.List.cachePage.put(pageId, objList);

                            var subKey = treeInfo.key;

                            var objId = 0;
                            var objType = "";
                            if (null == subKey) {
                                if (0 < objList.length) {
                                    objId = ObjNav.Model.interface.nodeId(objList[0]);
                                    objType = ObjNav.Model.interface.nodeType(objList[0]);
                                    if (Acm.isNotEmpty(objId) && Acm.isNotEmpty(objType)) {
                                        key = ObjNav.Model.Tree.Key.getKeyByObjWithPage(pageId, objType, objId);
                                    }
                                }
                            } else {
                                key = ObjNav.Model.Tree.Key.getKeyBySubWithPage(pageId, subKey);
                                objId = ObjNav.Model.Tree.Key.getObjIdByKey(key);
                                objType = ObjNav.Model.Tree.Key.getObjTypeByKey(key);
                                treeInfo.key = null;  //use it once; reset after use
                            }

                            if (Acm.isNotEmpty(objId) && Acm.isNotEmpty(objType)) {
                                ObjNav.Model.setObjectId(objId);
                                ObjNav.Model.setObjectType(objType);
                            }
                            
                            ObjNav.Controller.modelRetrievedObjectList(key);
                        }
                        return response;
                    }
                }
            });
        }
    }

    ,Detail: {
        create : function(args) {
        }
        ,onInitialized: function() {
        }

        ,retrieveObject : function(objType, objId) {
            var url = ObjNav.Model.interface.apiRetrieveObject(objType, objId);
            return Acm.Service.call({type: "GET"
                ,url: url
                ,callback: function(response) {
                    if (response.hasError) {
                        ObjNav.Controller.modelRetrievedObjectError(response);

                    } else {
                        if (ObjNav.Model.interface.validateObjData(response)) {
                            var objData = response;
//                            var caseFileId = ObjNav.Model.getCaseFileId();
//                            if (caseFileId != caseFile.id) {
//                                return;         //user clicks another caseFile before callback, do nothing
//                            }

//                            ObjNav.Model.Detail.putCacheObject(objType, objId, objData);
                            var solr = ObjNav.Model.interface.objToSolr(objData);
                            var nodeId = ObjNav.Model.interface.nodeId(solr);
                            var nodeType = ObjNav.Model.interface.nodeType(solr);
                            ObjNav.Model.Detail.putCacheObject(nodeType, nodeId, objData);

                            var treeInfo = ObjNav.Model.Tree.Config.getTreeInfo();
                            if (0 < treeInfo.objId) {      //handle single object situation
                                treeInfo.total = 1;
                                treeInfo.start = 0;

                                var objList = [solr];
                                ObjNav.Model.List.cachePage.put(treeInfo.start, objList);

                                ObjNav.Model.setObjectId(nodeId);
                                ObjNav.Model.setObjectType(nodeType);
                                var key = ObjNav.Model.Tree.Key.getKeyByObjWithPage(treeInfo.start, nodeType, nodeId);
                                ObjNav.Controller.modelRetrievedObjectList(key);
                                return key;

                            } else {
                                ObjNav.Controller.modelRetrievedObject(objData);
                                return objData;
                            }
                        }
                    }
                }
            });
        }

        ,retrieveObjectDeferred : function(objType, objId, callbackSuccess) {
            var url = App.getContextPath() + ObjNav.Model.interface.apiRetrieveObject(objType, objId);
            return Acm.Service.deferredGet(
                function(response) {
                    if (!response.hasError) {
                        if (ObjNav.Model.interface.validateObjData(response)) {
                            var objData = response;
                            ObjNav.Model.Detail.putCacheObject(objType, objId, objData);
                            return callbackSuccess(response);
                        }
                    }
                }
                ,url
            );
        }

        ,saveObject : function(objType, objId, objData, handler) {
            var url = ObjNav.Model.interface.apiSaveObject(objType, objId);
            return Acm.Service.call({type: "POST"
                ,url: url
                ,data:JSON.stringify(objData)
                ,callback: function(response) {
                    if (response.hasError) {
                        if (handler) {
                            handler(response);
                        } else {
                            ObjNav.Controller.modelSavedObjectError(response);
                        }

                    } else {
                        if (ObjNav.Model.interface.validateObjData(response)) {
                            ObjNav.Model.Detail.putCacheObject(objType, objId, response);
                            if (handler) {
                                handler(response);
                            } else {
                                ObjNav.Controller.modelSavedObject(objType, objId, response);
                            }
                            return response;
                        }
                    }
                }
            });
        }
    }
};

