'use strict';

/**
 * @ngdoc service
 * @name services:DocTreeExt.SearchAndAddDocuments
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/doc-tree/doc-tree-ext.search-and-add-doc.client.service.js }
 *
 * DocTree extensions for searching and adding documents.
 */
angular.module('services').factory('DocTreeExt.SearchAndAddDocuments', [ '$q', '$modal', '$translate', 'UtilService', function($q, $modal, $translate, Util) {

    var Documents = {

        /**
         * @ngdoc method
         * @name getColumnRenderers
         * @methodOf services:DocTreeExt.SearchAndAddDocuments
         *
         * @description
         * No renderer is needed; return empty list of renderers.
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
         * @methodOf services:DocTreeExt.SearchAndAddDocuments
         *
         * @description
         * Return list of command handlers this extension provides. This function is required for a docTree extension
         *
         * @param {Object} DocTree  DocTree object defined in doc-tree directive
         *
         */
        ,
        getCommandHandlers: function(DocTree) {
            return [ {
                name: "searchDocument",
                execute: function() {
                    Documents.openModal(DocTree);
                }
            } ];
        }

        ,
        openModal: function(DocTree) {

            var params = {};
            params.parentType = DocTree._objType;
            params.parentId = DocTree._objId;
            params.folderId = DocTree.objectInfo.container.id;
            params.filter = 'fq="object_type_s": FILE';
            params.header = $translate.instant("common.dialogObjectPicker.addDocument");
            params.config = Util.goodMapValue(DocTree.treeConfig, "dialogObjectPicker");

            var modalInstance = $modal.open({
                templateUrl: "modules/common/views/object-picker-modal.client.view.html",
                controller: 'directives.DocTreeSearchAndAddDocumentsDialogController',
                animation: true,
                size: 'lg',
                backdrop: 'static',
                resolve: {
                    params: function() {
                        return params;
                    }
                }
            });
        }
    };

    return Documents;
} ]);


angular.module('directives').controller('directives.DocTreeSearchAndAddDocumentsDialogController',
    ['$scope', '$modalInstance', 'params', 'UtilService', 'ObjectService', 'EcmService', function($scope, $modalInstance, params, Util, ObjectService, Ecm) {
        $scope.modalInstance = $modalInstance;
        $scope.filter = params.filter;
        $scope.config = params.config;
        $scope.header = params.header;

        $scope.modalInstance.result.then(function(result) {
            var docs = result;
                angular.forEach(docs, function(doc) {
                    var documentId = doc.object_id_s;
                    Util.serviceCall({
                        service: Ecm.copyFile,
                        param: {
                            objType: params.parentType,
                            objId: params.parentId
                        },
                        data: {
                            id : parseInt(documentId),
                            folderId : params.folderId
                        }
                    })
            });
        });
    } ]);
