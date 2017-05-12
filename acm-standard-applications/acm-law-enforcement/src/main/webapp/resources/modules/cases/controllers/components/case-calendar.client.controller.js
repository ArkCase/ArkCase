'use strict';

angular.module('cases').controller('Cases.CalendarController', ['$scope', '$stateParams', 'Case.InfoService', 'Helper.ObjectBrowserService', 'ObjectService'
    , function ($scope, $stateParams, CaseInfoService, HelperObjectBrowserService, ObjectService) {

        new HelperObjectBrowserService.Component({
            scope: $scope
            , stateParams: $stateParams
            , moduleId: "cases"
            , componentId: "calendar"
            , retrieveObjectInfo: CaseInfoService.getCaseInfo
            , validateObjectInfo: CaseInfoService.validateCaseInfo
            , onObjectInfoRetrieved: function (objectInfo) {
                onObjectInfoRetrieved(objectInfo);
            }
        });

        var onObjectInfoRetrieved = function(objectInfo) {
            $scope.objectInfoRetrieved = true;
            $scope.objectType = ObjectService.ObjectTypes.CASE_FILE;
            $scope.objectId = objectInfo.id;
        };
    }
]);