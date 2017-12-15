'use strict';

/**
 * @ngdoc directive
 * @name global.directive:downloadAllAsZip
 * @restrict E
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/doc-tree/download-all-as-zip.directive.js directives/doc-tree/download-all-as-zip.directive.js}
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

angular.module('directives').directive('downloadAllAsZip', ['MessageService', 'UtilService', '$translate', 'DocTreeExt.DownloadSelectedAsZip'
    , function (MessageService, Util, $translate, DownloadSelectedAsZip) {
        return {
            restrict: 'E',
            templateUrl: 'directives/doc-tree/download-all-as-zip.html',
            link: function (scope) {
                scope.downloadInProgress = false;

                scope.downloadAllAsZip = function () {
                    scope.downloadInProgress = true;

                    var folderId = Util.goodMapValue(scope.objectInfo, 'container.folder.id', false);
                    var selectedNodes =  [];
                    var tmpSelectedNodes = scope.treeControl.getSelectedNodes();
                    for(var i = 0; i < tmpSelectedNodes.length; i++ ){

                        var _folder =  tmpSelectedNodes[i].folder;
                        var _objectId = tmpSelectedNodes[i].data.objectId;

                        selectedNodes.push({
                            folder: _folder,
                            objectId: _objectId
                        });
                    }
                    var zipNodes = {
                      rootFolder: folderId,
                      selectedNodes: selectedNodes
                    };

                    DownloadSelectedAsZip.downloadSelectedFoldersAndFiles(zipNodes)
                        .then(function (result){
                            debugger;
                        });


                };
            }
        };
    }
]);