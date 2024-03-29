'use strict';

angular.module('request-info').controller('RequestInfo.ExemptionController',
    ['$scope', '$stateParams', '$q', 'Case.InfoService', 'Profile.UserInfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'ConfigService', 'ExemptionService', '$modal', 'Object.LookupService', '$state', 'Case.ExemptionService', 'UtilService', 'MessageService', 'Admin.ZylabIntegrationService',
        function ($scope, $stateParams, $q, CaseInfoService, UserInfoService, HelperUiGridService, HelperObjectBrowserService, ConfigService, ExemptionService, $modal, ObjectLookupService, $state, CaseExemptionService, Util, MessageService, ZylabIntegrationService) {

            $scope.isDisabled = false;
            $scope.statuteGridOptions = {};

            var componentHelper = new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "request-info",
                componentId: "exemption",
                retrieveObjectInfo: CaseInfoService.getCaseInfo,
                validateObjectInfo: CaseInfoService.validateCaseInfo,
                onConfigRetrieved: function (componentConfig) {
                    return onConfigRetrieved(componentConfig);
                },
                onObjectInfoRetrieved: function (objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
            });

            var gridHelper = new HelperUiGridService.Grid({
                scope: $scope
            });
            var promiseUsers = gridHelper.getUsers();

            var onConfigRetrieved = function (config) {
                ZylabIntegrationService.getConfiguration().then(function (response) {
                    $scope.documentReviewEnabled = response.data["zylabIntegration.enabled"];
                    if ($scope.documentReviewEnabled) {
                        config.columnDefs.unshift({
                            "name": "exemptionCodeNumber",
                            "displayName": "cases.comp.exemption.table.columns.exemptionCodeNumber",
                            "headerCellFilter": "translate"
                        });
                    }

                    gridHelper.setColumnDefs(config);
                    gridHelper.setBasicOptions(config);
                    gridHelper.disableGridScrolling(config);
                    gridHelper.setUserNameFilterToConfig(promiseUsers);
                    gridHelper.addButton(config, "delete", null, null, "isDeleteDisabled");
                    retrieveGridData($stateParams.id, $stateParams.fileId);
                });
            };

            var onObjectInfoRetrieved = function (objectInfo) {
                $scope.objectInfo = objectInfo;
                if (!Util.isEmpty($scope.objectInfo.dispositionClosedDate)) {
                    $scope.isDisabled = true;
                }
            };

            $scope.isDeleteDisabled = function (rowEntity) {
                if ($scope.isDisabled) {
                    return true;
                } else {
                    if (rowEntity.exemptionStatus != "MANUAL") {
                        return true;
                    }
                }
            };

            $scope.deleteRow = function (rowEntity) {
                var id = Util.goodMapValue(rowEntity, "id", 0);
                if (0 < id) { //do not need to call service when deleting a new row with id==0
                    if (rowEntity.exemptionCode) {
                        CaseExemptionService.deleteExemptionCode(id).then(function () {
                            _.remove($scope.gridOptions.data, function (row) {
                                return row === rowEntity;
                            });
                            MessageService.succsessAction();
                        }, function () {
                            MessageService.errorAction();
                        });
                    } else {
                        CaseExemptionService.deleteExemptionStatute(id).then(function () {
                            _.remove($scope.statuteGridOptions.data, function (row) {
                                return row === rowEntity;
                            });
                            MessageService.succsessAction();
                        }, function () {
                            MessageService.errorAction();
                        });
                    }
                }
            };

            ObjectLookupService.getExemptionStatutes().then(function(exemptionStatute) {
                $scope.exemptionStatutes = exemptionStatute;
            });

            ObjectLookupService.getAnnotationTags().then(function(annotationTags) {
                $scope.annotationTags = annotationTags;
            });

            $scope.refresh = function() {
                    retrieveGridData($stateParams.id, $stateParams.fileId);
            };

            $scope.addNew = function() {
                var params = {};
                params.annotationTags = $scope.annotationTags;
                params.existingAnnotationTags = $scope.gridOptions.data;
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'modules/document-details/views/components/annotation-tags-modal.client.view.html',
                    controller: 'RequestInfo.AnnotationTagsManuallyModalController',
                    windowClass: 'modal-width-80',
                    backdrop: 'static',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function () {
                    retrieveGridData($stateParams.id, $stateParams.fileId);
                }, function (error) {
                    // Do nothing
                });
            };


             function retrieveGridData(caseId, fileId) {
                var params = {};
                params.caseId = caseId;
                params.fileId = fileId;
                 var promiseQueryCodes = ExemptionService.getDocumentExemptionCodes(params.caseId, params.fileId);
                $q.all([ promiseQueryCodes ]).then(function(data) {
                    $scope.codes = data[0];
                    $scope.gridOptions = $scope.gridOptions || {};
                    $scope.gridOptions.data = $scope.codes.data;
                    $scope.gridOptions.totalItems = $scope.codes.data.length;
                });
            }

            $scope.$bus.subscribe('reload-exemption-code-grid', function(data) {
                // reload the exemption code table without reloading the page
                $state.transitionTo("request-info", { id: data.id, fileId: data.fileId }, {notify: false});
                retrieveGridData(data.id, data.fileId);
                retrieveStatuteGridData(data.id, data.fileId);
            });

            $scope.checkCodesDescription = function(){
                $modal.open({
                    size: 'lg',
                    templateUrl: 'modules/cases/views/components/case-exemption-codes-description-modal.client.view.html',
                    controller: 'Cases.ExemptionCodesDescriptionModalController',
                    windowClass: 'modal-width-80',
                    backdrop: 'static'
                })
            };

            // Exemption statutes

            $scope.exemptionData = {
                exemptionStatute : {},
                parentObjectType : "FILE"
            };

            ConfigService.getComponentConfig("request-info", "exemptionStatute").then(function(compConfig) {
                $scope.statuteConfig = compConfig;
                $scope.statuteGridOptions = {
                    columnDefs: $scope.statuteConfig.columnDefs,
                    enableColumnResizing: true,
                    enableRowSelection: true,
                    multiSelect: false,
                    noUnselect: false,
                    paginationPageSizes: $scope.statuteConfig.paginationPageSizes,
                    paginationPageSize: $scope.statuteConfig.paginationPageSize,
                    totalItems: 0,
                    data: []
                };
                gridHelper.setUserNameFilterToConfig(promiseUsers, compConfig);
                gridHelper.addButton(compConfig, "delete", null, null, "isDeleteDisabled");
                retrieveStatuteGridData($stateParams.fileId);
            });

            $scope.refreshStatute = function() {
                retrieveStatuteGridData($stateParams.fileId);
            };

            $scope.addNewStatute = function() {
                var params = {};
                params.config = $scope.config;
                params.exemptionStatutes = $scope.exemptionStatutes;
                var modalInstance = $modal.open({
                    animation: true,
                    size: 'md',
                    backdrop: 'static',
                    templateUrl: 'modules/request-info/views/components/exemption-statute-modal.client.view.html',
                    controller: 'RequestInfo.ExemptionStatuteModalController',
                    resolve: {
                        params: function() {
                            return params;
                        }
                    }
                });
                modalInstance.result.then(function(data) {
                    $scope.exemptionData.exemptionStatute = data.exemptionStatute;
                    ExemptionService.saveDocumentExemptionStatute($stateParams.fileId, $scope.exemptionData).then(function (value) {
                        MessageService.succsessAction();
                        retrieveStatuteGridData($stateParams.fileId)
                    }, function () {
                        MessageService.errorAction();
                    });
                });
            };

            function retrieveStatuteGridData(fileId) {
                var promiseQueryStatutes = ExemptionService.getDocumentExemptionStatutes(fileId);
                $q.all([ promiseQueryStatutes ]).then(function(data) {
                    $scope.statutes = data[0];
                    $scope.statuteGridOptions = $scope.statuteGridOptions || {};
                    $scope.statuteGridOptions.data = $scope.statutes.data;
                    $scope.statuteGridOptions.totalItems = $scope.statutes.data.length;
                });
            }

        } ]);
