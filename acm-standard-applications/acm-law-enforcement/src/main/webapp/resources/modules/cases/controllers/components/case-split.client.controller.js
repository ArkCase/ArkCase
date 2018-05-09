'use strict';

angular.module('cases').controller(
        'Cases.SplitController',
        [ '$scope', '$stateParams', '$modal', '$modalInstance', '$q', 'UtilService', 'ConfigService', 'ObjectService', 'Object.LookupService', 'Case.InfoService', 'Helper.ObjectBrowserService',
                function($scope, $stateParams, $modal, $modalInstance, $q, Util, ConfigService, ObjectService, ObjectLookupService, CaseInfoService, HelperObjectBrowserService) {
                    var promiseFormTypes = ObjectLookupService.getFormTypes(ObjectService.ObjectTypes.CASE_FILE);
                    var promiseFileTypes = ObjectLookupService.getFileTypes();
                    ConfigService.getComponentConfig("cases", "documents").then(function(componentConfig) {
                        $scope.config = componentConfig;
                        $scope.treeConfig = $scope.config.docTree;
                        $q.all([ promiseFormTypes, promiseFileTypes ]).then(function(data) {
                            $scope.treeConfig.formTypes = data[0];
                            $scope.treeConfig.fileTypes = data[1];
                        });
                        return componentConfig;
                    });

                    $scope.modalInstance = $modalInstance;
                    $scope.selectedItem = null;
                    $scope.close = function() {
                        $scope.modalInstance.dismiss('cancel');
                    };
                    $scope.splitCase = function() {
                        $scope.selectedItem = HelperObjectBrowserService.getCurrentObjectId();

                        var attachments = [];
                        var selNodes = $scope.treeControl.getSelectedNodes();
                        if (!Util.isArrayEmpty(selNodes)) {
                            for (var i = 0; i < selNodes.length; i++) {
                                if (Util.goodValue(selNodes[i].folder, false)) {
                                    attachments.push({
                                        "id": selNodes[i].data.objectId,
                                        "type": "folder"
                                    });
                                } else { // file node
                                    attachments.push({
                                        "id": selNodes[i].data.objectId,
                                        "type": "document"
                                    });
                                }
                            }
                        }

                        var summary = {};
                        summary.caseFileId = $scope.selectedItem;
                        summary.attachments = attachments;
                        summary.preserveFolderStructure = true;

                        $scope.modalInstance.close(summary);
                    };

                    $scope.objectType = ObjectService.ObjectTypes.CASE_FILE;
                    $scope.objectId = $stateParams.id;

                    var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
                    if (Util.goodPositive(currentObjectId, false)) {
                        CaseInfoService.getCaseInfo(currentObjectId).then(function(caseInfo) {
                            $scope.caseInfo = caseInfo;
                            $scope.objectId = caseInfo.id;
                            return caseInfo;
                        });
                    }

                } ]);