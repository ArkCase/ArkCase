'use strict';

angular.module('time-tracking').controller('TimeTracking.ActionsController',
    ['$scope', '$state', '$stateParams', '$translate', 'UtilService', 'ConfigService', 'TimeTracking.InfoService', 'Helper.ObjectBrowserService', '$modal', 'FormsType.Service', function ($scope, $state, $stateParams, $translate, Util, ConfigService, TimeTrackingInfoService, HelperObjectBrowserService, $modal, FormsTypeService) {

        new HelperObjectBrowserService.Component({
            scope: $scope,
            stateParams: $stateParams,
            moduleId: "time-tracking",
            componentId: "actions",
            retrieveObjectInfo: TimeTrackingInfoService.getTimesheetInfo,
            validateObjectInfo: TimeTrackingInfoService.validateTimesheet,
            onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var onObjectInfoRetrieved = function (objectInfo) {
            var frevvoDateFormat = $translate.instant("common.frevvo.defaultDateFormat");
            var startDate = moment(objectInfo.startDate).format(frevvoDateFormat);

            $scope.editTimesheetParams = {
                period: startDate
            };
        };

        FormsTypeService.isAngularFormType().then(function (isAngularFormType) {
            $scope.isAngularFormType = isAngularFormType;
        });

        FormsTypeService.isFrevvoFormType().then(function (isFrevvoFormType) {
            $scope.isFrevvoFormType = isFrevvoFormType;
        });

        $scope.newTimesheet = function () {
            var params = {
                isEdit: false
            };
            showModal(params);
        };

        $scope.editTimesheet = function () {
            $scope.editParams = {
                isEdit: true,
                timesheet: $scope.objectInfo
            };
                        showModal($scope.editParams);
        };

        function showModal(params) {
            var modalInstance = $modal.open({
                animation: true,
                templateUrl: 'modules/time-tracking/views/components/time-tracking-new-timesheet-modal.client.view.html',
                controller: 'TimeTracking.NewTimesheetController',
                size: 'lg',
                backdrop: 'static',
                resolve: {
                    modalParams: function () {
                        return params;
                    }
                }
            });

            modalInstance.result.then(function (data) {
                //Do nothing
            });
        }

        $scope.refresh = function () {
            $scope.$emit('report-object-refreshed', $stateParams.id);
        };

        $scope.isVisible = function () {
            return !Util.isEmpty($scope.objectInfo) && $scope.objectInfo.status === 'DRAFT';
        };

    }]);