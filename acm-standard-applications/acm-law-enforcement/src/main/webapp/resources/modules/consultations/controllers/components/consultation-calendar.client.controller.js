'use strict';

angular.module('consultations').controller(
    'Consultations.CalendarController',
    [ '$scope', '$stateParams', 'Consultation.InfoService', 'Helper.ObjectBrowserService', 'ObjectService', 'Admin.CalendarConfigurationService', 'MessageService', 'Object.CalendarService',
        function($scope, $stateParams, ConsultationInfoService, HelperObjectBrowserService, ObjectService, CalendarConfigurationService, MessageService, CalendarService) {

            $scope.objectInfoRetrieved = false;

            new HelperObjectBrowserService.Component({
                scope: $scope,
                stateParams: $stateParams,
                moduleId: "consultations",
                componentId: "calendar",
                retrieveObjectInfo: ConsultationInfoService.getConsultationInfo,
                validateObjectInfo: ConsultationInfoService.validateConsultationInfo,
                onObjectInfoRetrieved: function(objectInfo) {
                    onObjectInfoRetrieved(objectInfo);
                }
            });

            var onObjectInfoRetrieved = function(objectInfo) {
                $scope.objectType = ObjectService.ObjectTypes.CONSULTATION;
                $scope.objectId = objectInfo.id;
                $scope.eventSources = [];
                CalendarService.isCalendarConfigurationEnabled('CONSULTATION').then(function(data) {
                    $scope.objectInfoRetrieved = data;
                });
            };
        } ]);