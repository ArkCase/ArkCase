'use strict';

/**
 * @ngdoc service
 * @name services:DocTreeExt.Record
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/doc-tree/doc-tree-ext.record.client.service.js directives/doc-tree/doc-tree-ext.record.client.service.js}
 *
 * DocTree extensions for Record functions.
 */
angular.module('services').factory('DocTreeExt.Record', ['UtilService', 'Ecm.RecordService'
    , function (Util, EcmRecordService
    ) {

        var Service = {
            /**
             * @ngdoc method
             * @name getColumnRenderers
             * @methodOf services:DocTreeExt.Record
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
             * @methodOf services:DocTreeExt.Record
             *
             * @description
             * Return list of command handlers this extension provides. This function is required for a docTree extension
             *
             * @param {Object} DocTree  DocTree object defined in doc-tree directive
             *
             */
            ,getCommandHandlers: function(DocTree) {
                return [
                    {
                        name: "declare",
                        execute: function (nodes, args) {
                            var declareAsRecordData = [];
                            var nodesToDeclare = nodes;
                            for (var i = 0; i < nodes.length; i++) {
                                var declareAsRecord = {};
                                declareAsRecord.id = Util.goodValue(nodes[i].data.objectId);
                                declareAsRecord.type = Util.goodValue(nodes[i].data.objectType.toUpperCase());
                                declareAsRecordData.push(declareAsRecord);
                            }

                            if (!Util.isArrayEmpty(declareAsRecordData)) {
                                var objType = DocTree.getObjType();
                                var objId = DocTree.getObjId();
                                EcmRecordService.declareAsRecord(objType, objId, declareAsRecordData).then(function (data) {
                                    for (var j = 0; j < nodes.length; j++) {
                                        if (DocTree.isFolderNode(nodes[j])) {
                                            for (var i = 0; i < nodes[j].children.length; i++) {
                                                if (DocTree.Validator.validateNode(nodes[j].children[i])) {
                                                    nodes[j].children[i].data.status = "RECORD";
                                                    nodes[j].children[i].renderTitle();
                                                }
                                            }
                                        } else if (DocTree.isFileNode(nodes[j])) {
                                            nodes[j].data.status = "RECORD";
                                            nodes[j].renderTitle();
                                        }
                                    }
                                });
                            }
                        }
                    }
                ];
            }

        };

        return Service;
    }
]);


