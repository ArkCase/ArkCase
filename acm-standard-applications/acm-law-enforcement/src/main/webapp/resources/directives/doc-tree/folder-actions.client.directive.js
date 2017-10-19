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

angular.module('directives').directive('folderActions', ['ConfigService', 'Config.LocaleService', 'UtilService', '$modal', '$timeout', 'Object.LookupService', '$translate', 'ObjectService', '$q'
    , function (ConfigService, LocaleService, Util, $modal, $timeout, ObjectLookupService, $translate, ObjectService, $q) {
        return {
            restrict: 'E',
            templateUrl: 'directives/doc-tree/folder-actions.html',
            link: function (scope) {
                var context = null;
                var folderActionsConfig = null;
                var fileLanguages = [];

                ConfigService.getModuleConfig("common").then(function (moduleConfig) {
                    folderActionsConfig = _.find(moduleConfig.docTree.menu.basic.root, {cmd: "subMenuFileTypes"});
                    return moduleConfig;
                });
                
               
                LocaleService.getSettings().then(function(data){
                	fileLanguages = Util.goodMapValue(data, "locales", []);
                });
                
                scope.showFolderActions = false;
                scope.$bus.subscribe('showFolderActionBtns', function (ctx) {
                    context = ctx;
                    $timeout(function () {
                        scope.showFolderActions = true;
                    }, 0);
                });

                scope.$bus.subscribe('hideFolderActionBtns', function(e){
                    $timeout(function () {
                        scope.showFolderActions = false;
                    }, 0);
                });

                scope.onAddFile = function () {

                    var promiseFormTypes = ObjectLookupService.getFormTypes(ObjectService.ObjectTypes.CASE_FILE);
                    var promiseFileTypes = ObjectLookupService.getFileTypes();

                    $q.all([promiseFormTypes, promiseFileTypes]).then(
                        function (data) {
                            var formTypes = data[0];
                            var fileTypes = [];
                            for (var i = 0; i < data[1].length; i++) {
                                fileTypes.push({"key": data[1][i].key, "value": $translate.instant(data[1][i].value)});
                            }

                            for (var i = 0; i < formTypes.length; i++) {
                                fileTypes.push(formTypes[i]);
                            }

                            openFileTypeDialog(makeSubMenu(fileTypes), fileLanguages, function (type, language) {
                                //disabled Modal language support
                                //context.command.trigger(type + "/" + language);
                                context.command.trigger(type);
                            });
                        });
                };

                scope.onAddFolder = function () {
                    context.command.trigger('newFolder');
                };

                function makeSubMenu(subTypes) {
                    var menu = [], item;
                    if (subTypes) {
                        if (Util.isArray(subTypes)) {
                            for (var i = 0; i < subTypes.length; i++) {
                                item = {};
                                item.title = $translate.instant(subTypes[i].value);
                                item.cmd = "file/" + subTypes[i].key;
                                item.data = {};
                                item.data.uploadFile = true;
                                menu.push(item);
                            }
                        }
                    }
                    return menu;
                }

                function openFileTypeDialog(fileTypes, fileLanguages, onSelect) {
                    $modal.open({
                        templateUrl: "directives/doc-tree/doc-tree.file.type.dialog.html"
                        , controller: ['$scope', '$modalInstance', 'fileTypes', function ($scope, $modalInstance) {
                            $scope.modalInstance = $modalInstance;
                            $scope.fileTypes = fileTypes;
                            $scope.fileLanguages = fileLanguages;
                            $scope.fileType = {};
                            $scope.onClickOk = function () {
                            	//disabled Modal language support
                                //onSelect($scope.fileType.selected, $scope.fileLanguage.selected);
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

