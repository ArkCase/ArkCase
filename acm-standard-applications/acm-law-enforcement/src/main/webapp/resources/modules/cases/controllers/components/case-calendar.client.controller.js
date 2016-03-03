'use strict';

angular.module('cases').controller('Cases.CalendarController', ['$scope', '$stateParams', 'Case.InfoService', 'Helper.ObjectBrowserService'
    , function ($scope, $stateParams, CaseInfoService, HelperObjectBrowserService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cases"
            , componentId: "calendar"
            , retrieveObjectInfo: CaseInfoService.getCaseInfo
            , validateObjectInfo: CaseInfoService.validateCaseInfo
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