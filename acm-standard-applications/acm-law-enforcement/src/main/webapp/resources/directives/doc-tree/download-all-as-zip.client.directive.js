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

            var downloadFile = function(data) {
                //TRIGGER DOWNLOAD

                var blob = new Blob([ data ], {
                    type: "application/zip"
                })
                if (window.navigator.msSaveOrOpenBlob) {
                    window.navigator.msSaveOrOpenBlob(blob, "acm-documents.zip");
                    scope.downloadInProgress = false;
                } else {
                    var url = window.URL.createObjectURL(blob);
                    var downloadLink = angular.element('<a></a>');

                    downloadLink.css('display', 'none');
                    downloadLink.attr('href', url);
                    downloadLink.attr('download', "acm-documents.zip");
                    angular.element(document.body).append(downloadLink);
                    downloadLink[0].click();

                    downloadLink.remove();
                    window.URL.revokeObjectURL(url);
                    scope.downloadInProgress = false;
                }
            };

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

                DownloadSelectedAsZip.downloadSelectedFoldersAndFiles(compressNode).then(function(result) {
                    downloadFile(result.data);
                });
            };
        }
    };
} ]);