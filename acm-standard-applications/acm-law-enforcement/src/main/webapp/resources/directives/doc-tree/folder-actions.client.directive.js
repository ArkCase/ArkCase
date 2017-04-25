'use strict';

/**
 * @ngdoc directive
 * @name global.directive:folderActions
 * @restrict E
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/directives/doc-tree/folder-actions.directive.js directives/doc-tree/folder-actions.directive.js}
 *
 * The "Folder-Actions" directive exposes functionalities like addNewFolder and addDocument buttons enabled when the user selects a folder
 *
 * @example
 <example>
 <file name="index.html">
 <folder-actions></folder-actions>
 </file>
 </example>
 */

angular.module('directives').directive('folderActions', ['ConfigService', '$modal'
    , function (ConfigService, $modal) {
        return {
            restrict: 'E',
            templateUrl: 'directives/doc-tree/folder-actions.html',
            link: function (scope) {
                var context = null;
                var folderActionsConfig = null;

                ConfigService.getModuleConfig("common").then(function (moduleConfig) {
                    folderActionsConfig = _.find(moduleConfig.docTree.menu.basic.root, {cmd: "subMenuFileTypes"});
                    return moduleConfig;
                });

                scope.showFolderActions = false;
                scope.$bus.subscribe('showFolderActionBtns', function (ctx) {
                    context = ctx;
                    scope.$apply(function () {
                        scope.showFolderActions = true;
                    });
                });

                scope.$bus.subscribe('hideFolderActionBtns', function(e){
                    scope.$apply(function () {
                        scope.showFolderActions = false;
                    });
                });

                scope.onAddFile = function () {
                    var fileTypes = folderActionsConfig['___children'];
                    openFileTypeDialog(fileTypes, function (type) {
                        context.command.trigger(type);
                    });
                };

                scope.onAddFolder = function () {
                    context.command.trigger('newFolder');
                };

                function openFileTypeDialog(fileTypes, onSelect) {
                    $modal.open({
                        templateUrl: "directives/doc-tree/doc-tree.file.type.dialog.html"
                        , controller: ['$scope', '$modalInstance', 'fileTypes', function ($scope, $modalInstance) {
                            $scope.modalInstance = $modalInstance;
                            $scope.fileTypes = fileTypes;
                            $scope.fileType = {};
                            $scope.onClickOk = function () {
                                onSelect($scope.fileType.selected);
                                $modalInstance.close();
                            };

                        }]
                        , animation: true
                        , size: 'sm'
                        , resolve: {
                            fileTypes: {data: fileTypes}
                        }
                    });
                }

            }
        };
    }
]);

