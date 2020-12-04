'use strict';

angular.module('cases').controller(
        'Cases.PastApprovalRoutingController',
        [
                '$scope',
                '$stateParams',
                '$q',
                '$translate',
                '$modal',
                'UtilService',
                'Util.DateService',
                'ConfigService',
                'ObjectService',
                'LookupService',
                'Object.LookupService',
                'Case.InfoService',
                'Helper.UiGridService',
                'Helper.ObjectBrowserService',
                'Authentication',
                'PermissionsService',
                'Profile.UserInfoService',
                'Case.FutureApprovalService',
                function($scope, $stateParams, $q, $translate, $modal, Util, UtilDateService, ConfigService, ObjectService, LookupService, ObjectLookupService, CaseInfoService, HelperUiGridService, HelperObjectBrowserService, Authentication, PermissionsService, UserInfoService,
                        CaseFutureApprovalService) {

                    $scope.pastApprovers = {};
                    $scope.gridOptions = $scope.gridOptions || {};
                    $scope.taskInfo = null;

                    var currentUser = '';

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "cases",
                        componentId: "pastapprovals",
                        retrieveObjectInfo: CaseInfoService.getCaseInfo,
                        validateObjectInfo: CaseInfoService.validateCaseInfo,
                        onConfigRetrieved: function(componentConfig) {
                            return onConfigRetrieved(componentConfig);
                        }
                    });

                    var gridHelperPastApprovers = new HelperUiGridService.Grid({
                        scope: $scope
                    });

                    Authentication.queryUserInfo().then(function(data) {
                        currentUser = data.userId;
                    });

                    var onConfigRetrieved = function(config) {
                        $scope.config = config;
                        gridHelperPastApprovers.setColumnDefs(config);
                        gridHelperPastApprovers.setBasicOptions(config);
                        gridHelperPastApprovers.disableGridScrolling(config);
                    };

                    $scope.$bus.subscribe('buckslip-task-object-updated', function(objectInfo) {
                        $scope.taskInfo = objectInfo;

                        //set past approvers info
                        if (!Util.isEmpty(objectInfo.id)) {
                            CaseFutureApprovalService.getBuckslipPastTasksForObject('CASE_FILE', objectInfo.id, true).then(function(result) {
                                if (!Util.isArrayEmpty(result.data)) {
                                    $scope.gridOptions.data = angular.fromJson(result.data);
                                    $scope.gridOptions.noData = false;
                                } else {
                                    $scope.gridOptions.noData = true;
                                }
                            });
                        } else if (objectInfo.buckslipPastApprovers) {
                            $scope.gridOptions.data = angular.fromJson(objectInfo.buckslipPastApprovers);
                            $scope.gridOptions.noData = false;
                        } else {
                            $scope.gridOptions.data = [];
                            $scope.gridOptions.noData = true;
                            $scope.noDataMessage = $translate.instant('cases.comp.approvalRouting.noBuckslipMessage');
                        }
                    });

                    $scope.$bus.publish('buckslip-task-object-updated-subscribe-created', true);

                } ]);