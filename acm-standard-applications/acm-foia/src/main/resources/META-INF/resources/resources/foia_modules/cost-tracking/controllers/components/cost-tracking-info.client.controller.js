'use strict';

angular.module('cost-tracking').controller(
        'CostTracking.InfoController',
        [ '$scope', '$stateParams', 'UtilService', 'ConfigService', 'ObjectService', 'Case.InfoService', 'Complaint.InfoService', 'CostTracking.InfoService', 'Helper.ObjectBrowserService',
                function($scope, $stateParams, Util, ConfigService, ObjectService, CaseInfoService, ComplaintInfoService, CostTrackingInfoService, HelperObjectBrowserService) {

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "cost-tracking",
                        componentId: "info",
                        retrieveObjectInfo: CostTrackingInfoService.getCostsheetInfo,
                        validateObjectInfo: CostTrackingInfoService.validateCostsheet,
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    $scope.parentInfo = {};
                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectInfo = objectInfo;

                        if ($scope.objectInfo.parentType == ObjectService.ObjectTypes.CASE_FILE) {
                            CaseInfoService.getCaseInfo($scope.objectInfo.parentId).then(function(caseInfo) {
                                $scope.parentInfo.title = caseInfo.title;
                                $scope.parentInfo.incidentDate = moment(caseInfo.created).format($scope.config.parentDateFormat);
                                $scope.parentInfo.priortiy = caseInfo.priority;
                                $scope.parentInfo.type = 'Request';
                                $scope.parentInfo.status = caseInfo.status;
                            });

                        } else if ($scope.objectInfo.parentType == ObjectService.ObjectTypes.COMPLAINT) {
                            ComplaintInfoService.getComplaintInfo($scope.objectInfo.parentId).then(function(complaintInfo) {
                                $scope.parentInfo.title = complaintInfo.complaintTitle;
                                $scope.parentInfo.incidentDate = moment(complaintInfo.incidentDate).format($scope.config.parentDateFormat);
                                $scope.parentInfo.priortiy = complaintInfo.priority;
                                $scope.parentInfo.type = complaintInfo.complaintType;
                                $scope.parentInfo.status = complaintInfo.status;
                            });
                        }
                    };

                    $scope.onClickTitle = function() {
                        ObjectService.gotoUrl($scope.objectInfo.parentType, $scope.objectInfo.parentId);
                    }
                } ]);