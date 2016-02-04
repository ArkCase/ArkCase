'use strict';

angular.module('cost-tracking').controller('CostTracking.InfoController', ['$scope', '$stateParams', 'UtilService'
    , 'ConfigService', 'ObjectService', 'Case.InfoService', 'Complaint.InfoService', 'CostTracking.InfoService'
    , 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, Util
        , ConfigService, ObjectService, CaseInfoService, ComplaintInfoService, CostTrackingInfoService
        , HelperObjectBrowserService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cost-tracking"
            , componentId: "info"
            , retrieveObjectInfo: CostTrackingInfoService.getCostsheetInfo
            , validateObjectInfo: CostTrackingInfoService.validateCostsheet
            , onObjectInfoRetrieved: function (costsheetInfo) {
                onObjectInfoRetrieved(costsheetInfo);
            }
        });


        $scope.parentInfo = {};
        var onObjectInfoRetrieved = function (costsheetInfo) {
            $scope.costsheetInfo = costsheetInfo;

            if ($scope.costsheetInfo.parentType == ObjectService.ObjectTypes.CASE_FILE) {
                CaseInfoService.getCaseInfo($scope.costsheetInfo.parentId).then(function (caseInfo) {
                    $scope.parentInfo.title = caseInfo.title;
                    $scope.parentInfo.incidentDate = moment(caseInfo.created).format($scope.config.parentDateFormat);
                    $scope.parentInfo.priortiy = caseInfo.priority;
                    $scope.parentInfo.type = caseInfo.caseType;
                    $scope.parentInfo.status = caseInfo.status;
                });

            } else if ($scope.costsheetInfo.parentType == ObjectService.ObjectTypes.COMPLAINT) {
                ComplaintInfoService.getComplaintInfo($scope.costsheetInfo.parentId).then(function (complaintInfo) {
                    $scope.parentInfo.title = complaintInfo.complaintTitle;
                    $scope.parentInfo.incidentDate = moment(complaintInfo.incidentDate).format($scope.config.parentDateFormat);
                    $scope.parentInfo.priortiy = complaintInfo.priority;
                    $scope.parentInfo.type = complaintInfo.complaintType;
                    $scope.parentInfo.status = complaintInfo.status;
                });
            }
        };

        $scope.onClickTitle = function () {
            ObjectService.gotoUrl($scope.costsheetInfo.parentType, $scope.costsheetInfo.parentId);
        }
    }
]);