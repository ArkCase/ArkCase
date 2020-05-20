'use strict';

angular.module('cases').controller('Cases.ExemptionController',
    ['$scope', '$stateParams', '$q', 'Case.InfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'Case.ExemptionService', '$modal', 'Object.LookupService', 'Profile.UserInfoService', 'UtilService', 'MessageService',
        function ($scope, $stateParams, $q, CaseInfoService, HelperUiGridService, HelperObjectBrowserService, CaseExemptionService, $modal, ObjectLookupService, UserInfoService, Util, MessageService) {

            $scope.isDisabled = false;

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
                gridHelper.addButton(config, "edit", null, null, "isEditDisabled");
                gridHelper.addButton(config, "delete", null, null, "isDeleteDisabled");
                retrieveGridData();
            };

            ObjectLookupService.getExemptionStatutes().then(function (exemptionStatute) {
                $scope.exemptionStatutes = exemptionStatute;
            });

            ObjectLookupService.getAnnotationTags().then(function (annotationTags) {
                $scope.annotationTags = annotationTags;
            });

            $scope.isEditDisabled = function (rowEntity) {
                if ($scope.isDisabled) {
                    return true;
                } else {
                    if (rowEntity.exemptionCode != 'Ex.3') {
                        return true;
                    }
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
                    CaseExemptionService.deleteExemptionCode(id).then(function () {
                        _.remove($scope.gridOptions.data, function (row) {
                            return row === rowEntity;
                        });
                        MessageService.succsessAction();
                    }, function () {
                        MessageService.errorAction();
                    });
                }
            };

            $scope.editRow = function(rowEntity) {
                $scope.entry = rowEntity;
                var item = {
                   exemptionStatute: rowEntity.exemptionStatute
                };
                showModal(item);
            };

            function showModal(item) {
                var params = {};
                params.item = item || {};
                params.config = $scope.config;
                params.exemptionStatutesList = $scope.exemptionStatutes;
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
                     $scope.entry.exemptionStatute = data.exemptionStatute.key;
                     saveExemptionRule();
                });
            }

            function saveExemptionRule() {
                var exemptionData = $scope.entry;
                CaseExemptionService.saveExemptionStatute(exemptionData).then(function (value) {
                    MessageService.succsessAction();
                }, function () {
                    MessageService.errorAction();
                });
            }

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

                    var userInfoPromises = [];
                    for(var i = 0; i<$scope.codes.data.length; i++) {
                        userInfoPromises.push(UserInfoService.getUserInfoById($scope.codes.data[i].creator));
                    }

                    $q.all(userInfoPromises).then(function (userInfo) {
                        for(var j = 0; j < $scope.codes.data.length; j++) {
                            for(var k = 0; k< userInfo.length; k++) {
                                if($scope.codes.data[j].creator === userInfo[k].userId) {
                                    //change creator user id with the user full name
                                    $scope.codes.data[j].creator = userInfo[k].fullName;
                                    break;
                                }
                            }
                        }
                        $scope.gridOptions = $scope.gridOptions || {};
                        $scope.gridOptions.data = $scope.codes.data;
                        $scope.gridOptions.totalItems = $scope.codes.data.length;
                    });
                });
            }

            $scope.checkCodesDescription = function () {
                $modal.open({
                    size: 'lg',
                    templateUrl: 'modules/cases/views/components/case-exemption-codes-description-modal.client.view.html',
                    controller: 'Cases.ExemptionCodesDescriptionModalController',
                    backdrop: 'static'
                })
            }
        } ]);
