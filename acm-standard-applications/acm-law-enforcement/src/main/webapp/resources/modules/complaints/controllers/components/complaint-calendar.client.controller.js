'use strict';

angular.module('complaints').controller('Complaints.CalendarController', ['$scope', '$stateParams', 'Complaint.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, ComplaintInfoService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cases"
            , componentId: "calendar"
            , retrieveObjectInfo: ComplaintInfoService.getComplaintInfo
            , validateObjectInfo: ComplaintInfoService.validateComplaintInfo
            , onConfigRetrieved: function (componentConfig) {
                return onConfigRetrieved(componentConfig);
            }
        });

        $scope.$watchCollection('objectInfo', function (newValue, oldValue) {
            if (newValue && newValue.container && newValue.container.calendarFolderId) {
                $scope.folderId = newValue.container.calendarFolderId;
            }
        });

        var onConfigRetrieved = function (config) {
            $scope.config = config;
            return config;
        };
    }
]);