'use strict';

/**
 * @ngdoc service
 * @name services:DocTreeExt.Template
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/doc-tree/doc-tree-ext.template.client.service.js directives/doc-tree/doc-tree-ext.template.client.service.js}
 *
 * DocTree extensions for Correspondence Template functions.
 */
angular.module('services').factory('DocTreeExt.Template', ['$q', 'UtilService', 'Object.CorrespondenceService'
    , function ($q, Util, ObjectCorrespondenceService
    ) {

        var Service = {
            /**
             * @ngdoc method
             * @name getColumnRenderers
             * @methodOf services:DocTreeExt.Template
             *
             * @description
             * Return empty list of renderers, since this extension does not customize any renderer.
             * This function is required for a docTree extension.
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
             * @methodOf services:DocTreeExt.Template
             *
             * @description
             * Return list of command handlers this extension provides. This function is required for a docTree extension
             *
             * @param {Object} DocTree  DocTree object defined in doc-tree directive
             *
             */
            ,getCommandHandlers: function(DocTree) {
                Service.DocTree = DocTree;

                return [
                    {
                        name: "template/",
                        getArgs: function (data) {
                            return {templateType: data.cmd.substring(this.name.length), label: data.label};
                        },
                        execute: function (nodes, args) {
                            var names = [args.label];
                            var template = args.templateType;

                            DocTree._addingFileNodes(nodes[0], names, names[0]).then(function (data) {
                                var selectedFolderId = nodes[0].data.objectId;
                                ObjectCorrespondenceService.createCorrespondence(template,
                                    DocTree.getObjType(), DocTree.getObjId(), selectedFolderId).then(function (uploadedFile) {
                                    var cacheKey = DocTree.getCacheKeyByNode(nodes[0]);
                                    var fileNodes = data;
                                    var file = DocTree.fileToSolrData(uploadedFile);
                                    var folderList = DocTree.cacheFolderList.get(cacheKey);

                                    if (DocTree.Validator.validateFolderList(folderList)) {
                                        folderList.children.push(file);
                                        folderList.totalChildren++;
                                        DocTree.cacheFolderList.put(cacheKey, folderList);
                                        if (!Util.isEmpty(uploadedFile) && DocTree.Validator.validateFancyTreeNodes(fileNodes)) {
                                            var type = Util.goodValue(uploadedFile.fileType);
                                            var fileNode = DocTree._matchFileNode(type, type, fileNodes);
                                            if (fileNode) {
                                                DocTree._fileDataToNodeData(file, fileNode);
                                                fileNode.renderTitle();
                                                fileNode.setStatus("ok");
                                            }
                                        }
                                    }
                                })
                            });
                        }
                    }
                ];
            }

            /**
             * @ngdoc method
             * @name onConfigUpdated
             * @methodOf services:DocTreeExt.Template
             *
             * @description
             * This function is called when doc tree configuration is changed. Setup sub menu for Correspondence
             *
             * @param {Object} DocTree  DocTree object defined in doc-tree directive
             *
             */
            , onConfigUpdated: function(DocTree) {
                var correspondenceForms = Util.goodMapValue(DocTree.treeConfig, "correspondenceForms", []);
                DocTree.correspondenceForms = correspondenceForms.data;
                var jqTreeBody = DocTree.jqTree.find("tbody");
                DocTree.Menu.useContextMenu(jqTreeBody);
            }

        };

        return Service;
    }
]);


