'use strict';

/**
 * @ngdoc service
 * @name services:DocTreeExt.Media
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/doc-tree/doc-tree-ext.media.client.service.js directives/doc-tree/doc-tree-ext.media.client.service.js}
 *
 * DocTree extensions for media functions.
 */
angular.module('services').factory('DocTreeExt.Media', ['$q', '$modal', '$translate', '$browser', 'UtilService', 'LookupService'
    , function ($q, $modal, $translate, $browser, Util, LookupService) {

        var Media = {

            /**
             * @ngdoc method
             * @name getColumnRenderers
             * @methodOf services:DocTreeExt.Media
             *
             * @description
             * No renderer is needed; return empty list of renderers.
             *
             * @param {Object} DocTree  DocTree object defined in doc-tree directive
             *
             */
            getColumnRenderers: function (DocTree) {
                return [];
            }

            /**
             * @ngdoc method
             * @name getCommandHandlers
             * @methodOf services:DocTreeExt.Media
             *
             * @description
             * Return list of command handlers this extension provides. This function is required for a docTree extension
             *
             * @param {Object} DocTree  DocTree object defined in doc-tree directive
             *
             */
            , getCommandHandlers: function (DocTree) {
                return [
                    {
                        name: "play",
                        execute: function (nodes, args) {
                            Media.openModal(DocTree, nodes);
                        }
                    }
                ];
            }

            , openModal: function (DocTree, nodes) {
                var params = {
                    nodes: nodes,
                    DocTree: DocTree
                };

                var modalInstance = $modal.open({
                    templateUrl: "directives/doc-tree/doc-tree-ext.media.dialog.html"
                    , controller: 'directives.DocTreeMediaDialogController'
                    , animation: true
                    , size: 'lg'
                    , resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });
            }

            , _extractFileIds: function (nodes) {
                var fileIds = [];
                if (Util.isArray(nodes)) {
                    for (var i = 0; i < nodes.length; i++) {
                        fileIds.push(Util.goodMapValue(nodes[i], "data.objectId"));
                    }
                }
                return fileIds;
            }

        }; // end Media

        return Media;
    }
]);

angular.module('directives').controller('directives.DocTreeMediaDialogController', ['$scope', '$modalInstance'
        , 'UtilService', 'params', 'DocTreeExt.Media', '$modal', '$translate', '$sce'
        , function ($scope, $modalInstance, Util, params, DocTreeExtMedia, $modal, $translate, $sce) {
            $scope.modalInstance = $modalInstance;
            $scope.config = params.config;
            $scope.DocTree = params.DocTree;
            $scope.nodes = _.filter(params.nodes, function (node) {
                return !node.folder;
            });
            
            $scope.selectedFiles = DocTreeExtMedia._extractFileIds($scope.nodes);
            $scope.trustedUrl = $sce.trustAsResourceUrl('api/latest/plugin/ecm/stream/video/' + $scope.selectedFiles[0]);

            $scope.onClickCancel = function () {
                $modalInstance.dismiss();
            };
            
            function VideoControl(videoElement) {
                this.videoElement = videoElement;
            }

            VideoControl.prototype.play = function() {
                this.videoElement.play();
            }

            VideoControl.prototype.pause = function() {
                this.videoElement.pause();
            }
        }
    ]
);