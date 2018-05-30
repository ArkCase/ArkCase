'use strict';

angular.module('cases').controller(
        'Cases.CalendarController',
        [ '$scope', '$stateParams', 'Case.InfoService', 'Helper.ObjectBrowserService', 'ObjectService', 'Admin.CalendarConfigurationService', 'MessageService', 'Object.CalendarService',
                function($scope, $stateParams, CaseInfoService, HelperObjectBrowserService, ObjectService, CalendarConfigurationService, MessageService, CalendarService) {

                    $scope.objectInfoRetrieved = false;

                    new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: "cases",
                        componentId: "calendar",
                        retrieveObjectInfo: CaseInfoService.getCaseInfo,
                        validateObjectInfo: CaseInfoService.validateCaseInfo,
                        onObjectInfoRetrieved: function(objectInfo) {
                            onObjectInfoRetrieved(objectInfo);
                        }
                    });

                    var onObjectInfoRetrieved = function(objectInfo) {
                        $scope.objectType = ObjectService.ObjectTypes.CASE_FILE;
                        $scope.objectId = objectInfo.id;
                        $scope.eventSources = [];
                        CalendarService.isCalendarConfigurationEnabled('CASE_FILE').then(function(data) {
                            $scope.objectInfoRetrieved = data;
                        });
                    };
                } ]);