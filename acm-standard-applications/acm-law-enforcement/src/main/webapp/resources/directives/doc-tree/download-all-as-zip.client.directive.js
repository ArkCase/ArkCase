'use strict';

/**
 * @ngdoc directive
 * @name global.directive:downloadAllAsZip
 * @restrict E
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/doc-tree/download-all-as-zip.directive.js directives/doc-tree/download-all-as-zip.directive.js}
 *
 * The "Download All As Zip" directive contains the method to download all documents of an object into a single compressed folder.
 *
 * @example
 <example>
 <file name="index.html">
 <download-all-as-zip></download-all-as-zip>
 </file>
 </example>
 */

angular.module('directives').directive('downloadAllAsZip', [ 'MessageService', 'UtilService', '$translate', 'DocTreeExt.DownloadSelectedAsZip', '$timeout', function(MessageService, Util, $translate, DownloadSelectedAsZip, $timeout) {
    return {
        restrict: 'E',
        templateUrl: 'directives/doc-tree/download-all-as-zip.html',
        link: function(scope) {
            scope.downloadInProgress = false;

            scope.tmpSelectedNodes = [];
            var updateSelectedNodesList = function() {
                $timeout(function() {
                    scope.tmpSelectedNodes = scope.treeControl.getSelectedNodes();
                }, 0);
            };

            scope.$bus.subscribe('docTreeNodeChecked', function() {
                updateSelectedNodesList();
            });

            scope.$bus.subscribe('toggleAllNodesChecked', function() {
                updateSelectedNodesList();
            });


            scope.downloadAllAsZip = function() {
                scope.downloadInProgress = true;

                var rootFolderId = scope.treeControl.getTopNode().data.objectId;
                scope.tmpSelectedNodes = scope.treeControl.getSelectedNodes();

                var selectedNodes = [];

                for (var i = 0; i < scope.tmpSelectedNodes.length; i++) {

                    var _folder = scope.tmpSelectedNodes[i].folder;
                    var _objectId = scope.tmpSelectedNodes[i].data.objectId;

                    selectedNodes.push({
                        folder: _folder,
                        objectId: _objectId
                    });
                }
                var compressNode = {
                    rootFolderId: rootFolderId,
                    selectedNodes: selectedNodes
                };

                DownloadSelectedAsZip.downloadSelectedFoldersAndFiles(compressNode).then(function () {
                    scope.downloadInProgress = false;
                    MessageService.info($translate.instant("common.directive.downloadAllAsZip.message.start"))
                });
            };
        }
    };
} ]);