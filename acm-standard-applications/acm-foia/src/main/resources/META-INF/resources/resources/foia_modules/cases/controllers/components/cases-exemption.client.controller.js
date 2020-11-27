'use strict';

angular.module('cases').controller('Cases.ExemptionController',
    ['$scope', '$stateParams', '$q', 'Case.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'ConfigService', 'Case.ExemptionService', '$modal', 'Object.LookupService', 'Profile.UserInfoService', 'UtilService', 'MessageService',
        function ($scope, $stateParams, $q, CaseInfoService, HelperUiGridService, HelperObjectBrowserService, ConfigService, CaseExemptionService, $modal, ObjectLookupService, UserInfoService, Util, MessageService) {

            $scope.isDisabled = false;
            $scope.statuteGridOptions = {};
            $scope.exemptionData = {
                exemptionStatute : {},
                parentObjectId : $stateParams.id,
                parentObjectType : "CASE_FILE"
            };

            var componentHelper = new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "cases",
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

            var onObjectInfoRetrieved = function (objectInfo) {
                $scope.objectInfo = objectInfo;
                if (!Util.isEmpty($scope.objectInfo.dispositionClosedDate)) {
                    $scope.isDisabled = true;
                }
            };

            var gridHelper = new HelperUiGridService.Grid({
                scope: $scope
            });
            var promiseUsers = gridHelper.getUsers();

            var onConfigRetrieved = function (config) {
                gridHelper.setColumnDefs(config);
                gridHelper.setBasicOptions(config);
                gridHelper.disableGridScrolling(config);
                gridHelper.setUserNameFilterToConfig(promiseUsers, config);
                gridHelper.addButton(config, "delete", null, null, "isDeleteDisabled");
                retrieveGridData();
            };

            ObjectLookupService.getExemptionStatutes().then(function (exemptionStatute) {
                $scope.exemptionStatutes = exemptionStatute;
            });

            ObjectLookupService.getAnnotationTags().then(function (annotationTags) {
                $scope.annotationTags = annotationTags;
            });

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

            $scope.refresh = function() {
                retrieveGridData();
            };

            $scope.addNew = function () {
                var params = {};
                params.annotationTags = $scope.annotationTags;
                params.existingAnnotationTags = $scope.gridOptions.data;
                var modalInstance = $modal.open({
                    animation: true,
                    templateUrl: 'modules/document-details/views/components/annotation-tags-modal.client.view.html',
                    controller: 'Case.AnnotationTagsModalController',
                    backdrop: 'static',
                    windowClass: 'modal-width-80',
                    resolve: {
                        params: function () {
                            return params;
                        }
                    }
                });

                modalInstance.result.then(function () {
                    retrieveGridData();
                }, function (error) {
                    // Do nothing
                });
            };

            function retrieveGridData() {
                var params = {};
                params.caseId = $stateParams.id;
                var promiseQueryCodes = CaseExemptionService.getExemptionCode(params.caseId, 'CASE_FILE');
                $q.all([ promiseQueryCodes ]).then(function(data) {
                    $scope.codes = data[0];
                    $scope.gridOptions = $scope.gridOptions || {};
                    $scope.gridOptions.data = $scope.codes.data;
                    $scope.gridOptions.totalItems = $scope.codes.data.length;
                });
            }

            $scope.checkCodesDescription = function () {
                $modal.open({
                    size: 'lg',
                    templateUrl: 'modules/cases/views/components/case-exemption-codes-description-modal.client.view.html',
                    controller: 'Cases.ExemptionCodesDescriptionModalController',
                    windowClass: 'modal-width-80',
                    backdrop: 'static'
                })
            };

            // Exemption statutes

            ConfigService.getComponentConfig("cases", "exemptionStatute").then(function(compConfig) {
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
                retrieveStatuteGridData();
            });


            $scope.refreshStatute = function() {
                retrieveStatuteGridData();
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
                    CaseExemptionService.saveExemptionStatute($scope.exemptionData).then(function (value) {
                        MessageService.succsessAction();
                        retrieveStatuteGridData()
                    }, function () {
                        MessageService.errorAction();
                    });
                });
            };

            function retrieveStatuteGridData() {
                var params = {};
                params.caseId = $stateParams.id;
                var promiseQueryStatutes = CaseExemptionService.getExemptionStatute(params.caseId, 'CASE_FILE');
                $q.all([ promiseQueryStatutes ]).then(function(data) {
                    $scope.statutes = data[0];
                    $scope.statuteGridOptions = $scope.statuteGridOptions || {};
                    $scope.statuteGridOptions.data = $scope.statutes.data;
                    $scope.statuteGridOptions.totalItems = $scope.statutes.data.length;
                });
            }
        } ]);
