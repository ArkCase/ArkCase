'use strict';

/**
 * @ngdoc service
 * @name services:DocTreeExt.Lodge
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/doc-tree/doc-tree-ext.lodge.client.service.js directives/doc-tree/doc-tree-ext.lodge.client.service.js}
 *
 * DocTree extensions for lodge document functions.
 */
angular.module('services').factory('DocTreeExt.Lodge', ['$q', '$modal', '$translate', 'UtilService', 'LookupService'
    , function ($q, $modal, $translate, Util, LookupService
    ) {

        var Service = {
            /**
             * @ngdoc method
             * @name getColumnRenderers
             * @methodOf services:DocTreeExt.Lodge
             *
             * @description
             * Return list of renderers this extension provides. This function is required for a docTree extension
             *
             * @param {Object} DocTree  DocTree object defined in doc-tree directive
             *
             */
            getColumnRenderers: function(DocTree) {
                return [];
            }

            /**
             * @ngdoc method
             * @name getCommandHandlers
             * @methodOf services:DocTreeExt.Lodge
             *
             * @description
             * Return list of command handlers this extension provides. This function is required for a docTree extension
             *
             * @param {Object} DocTree  DocTree object defined in doc-tree directive
             *
             */
            ,getCommandHandlers: function(DocTree) {
                return [];
            }

//                , lodgeDocuments: function (folderNames, docIds, frFolderNode) {
//                    var dfd = $.Deferred();
//
//                    //make a copy
//                    var findNames = [];
//                    for (var i = 0; i < folderNames.length; i++) {
//                        findNames.push(folderNames[i]);
//                    }
//
//
//                    var node = DocTree.findNodeByPathNames(findNames);
//                    if (Validator.validateNode(node)) {
//                        DocTree.markNodePending(node);
//                    }
//
//                    DocTree.lodgeDocuments(folderNames, docIds)
//                        .done(function (createdFolder) {
//
////                    //
////                    // remove files from original folder cache
////                    //
////                    var frCacheKey = DocTree.getCacheKeyByNode(frFolderNode);
////                    var frFolderList = DocTree.cacheFolderList.get(frCacheKey);
////                    for (var i = 0; i < docIds.length; i++) {
////                        var idx = DocTree.findFolderItemIdx(docIds[i], frFolderList);
////                        if (0 <= idx) {
////                            frFolderList.children.splice(idx, 1);
////                            frFolderList.totalChildren--;
////                        }
////                    }
////                    DocTree.cacheFolderList.put(frCacheKey, frFolderList);
//
//                            //
//                            // fix target folders
//                            //
//                            var node = DocTree.findNodeByPathNames(findNames);
//                            if (Validator.validateNode(node)) {
//                                var cacheKey = DocTree.getCacheKeyByNode(node);
//                                DocTree.cacheFolderList.remove(cacheKey);
//                                node.setExpanded(false);
//                                node.resetLazy();
//                                DocTree.markNodeOk(node);
//                            }
//
//                            while (2 < findNames.length) {
//                                node = DocTree.findNodeByPathNames(findNames);
//                                if (Validator.validateNode(node)) {
//                                    var parent = node.parent;
//                                    var cacheKey = DocTree.getCacheKeyByNode(parent);
//                                    var folderList = DocTree.cacheFolderList.get(cacheKey);
//                                    var idx = DocTree.findFolderItemIdx(node.data.objectId, folderList);
//                                    if (0 > idx) {
//                                        //not found, this must be newly created folder, no folder info available for now, so we can only close parent
//                                        DocTree.cacheFolderList.remove(cacheKey);
//                                        parent.setExpanded(false);
//                                        parent.resetLazy();
//                                    }
//                                }
//                                findNames.pop();
//                            }
//
//
//                            dfd.resolve(createdFolder.objectId);
//                        })
//                        .fail(function (response) {
//                            dfd.reject(response);
//                        })
//                    ;
//                    return dfd.promise();
//                }
        };

        return Service;
    }
]);


