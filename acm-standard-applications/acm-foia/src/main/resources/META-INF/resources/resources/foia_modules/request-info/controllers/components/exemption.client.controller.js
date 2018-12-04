'use strict';

angular.module('request-info').controller('RequestInfo.ExemptionController',
        [ '$scope', '$stateParams', '$q', 'Case.InfoService', 'Profile.UserInfoService', 'Helper.UiGridService', 'Helper.ObjectBrowserService', 'ExemptionService', '$modal', 'Object.LookupService',
                function($scope, $stateParams, $q, CaseInfoService, UserInfoService, HelperUiGridService, HelperObjectBrowserService, ExemptionService, $modal, ObjectLookupService) {

            var componentHelper = new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "request-info",
                componentId: "exemption",
                retrieveObjectInfo: CaseInfoService.getCaseInfo,
                validateObjectInfo: CaseInfoService.validateCaseInfo,
                onConfigRetrieved: function(componentConfig) {
                    return onConfigRetrieved(componentConfig);
                }
            });

            var gridHelper = new HelperUiGridService.Grid({
                scope: $scope
            });
            var promiseUsers = gridHelper.getUsers();

            var onConfigRetrieved = function(config) {
                gridHelper.setColumnDefs(config);
                gridHelper.setBasicOptions(config);
                gridHelper.disableGridScrolling(config);
                gridHelper.setUserNameFilterToConfig(promiseUsers);
                gridHelper.addButton(config, 'edit', null, null, "isEditDisabled");
                retrieveGridData();
            };

            $scope.isEditDisabled = function(rowEntity) {
                if (rowEntity.exemptionCode != 'X3') {
                    return true;
                }
            };

            ObjectLookupService.getExemptionStatutes().then(function(exemptionStatute) {
                $scope.exemptionStatutes = exemptionStatute;
            });

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
                     ExemptionService.saveExemptionStatutes(exemptionData);
            }

            $scope.refresh = function() {
                    retrieveGridData();
            };

             function retrieveGridData() {
                var params = {};
                params.caseId = $stateParams.id;
                var promiseQueryCodes = ExemptionService.getExemptionCodes(params.caseId);
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

            $scope.checkCodesDescription = function(){
                $modal.open({
                    size: 'lg',
                    templateUrl: 'modules/cases/views/components/case-exemption-codes-description-modal.client.view.html',
                    controller: 'Cases.ExemptionCodesDescriptionModalController'
                })
            }
        } ]);