'use strict';

angular.module('cases').controller('Cases.CalendarController', ['$scope', '$stateParams', 'Case.InfoService'
    , 'Helper.ObjectBrowserService', 'ObjectService', 'Object.CalendarService', 'MessageService'
    , function ($scope, $stateParams, CaseInfoService, HelperObjectBrowserService, ObjectService, CalendarService, MessageService) {

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
            CalendarService.getCalendarIntegration('CASE_FILE').then(function (calendarAdminConfigRes) {
                $scope.objectType = ObjectService.ObjectTypes.CASE_FILE;
                $scope.objectId = objectInfo.id;
                if(calendarAdminConfigRes.data === true){
                    $scope.objectInfoRetrieved = true;
                }else{
                    MessageService.info('Calendar Integration Configuration Not Enabled');
                    $scope.objectInfoRetrieved = false;

                }
            });

        };
    }
]);