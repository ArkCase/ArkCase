/**
 * Created by nick.ferguson on 1/8/2016.
 */
'use strict';

angular.module('dashboard.calendar', [ 'adf.provider' ]).config(function(dashboardProvider) {
    dashboardProvider.widget('calendar', {
        title: 'preference.overviewWidgets.calendar.title',
        description: 'dashboard.widgets.calendar.description',
        controller: 'Dashboard.CalendarController',
        controllerAs: 'calendar',
        reload: true,
        templateUrl: 'modules/dashboard/views/components/calendar-widget.client.view.html',
        commonName: 'calendar'
    });
}).controller(
        'Dashboard.CalendarController',
        [ '$scope', '$stateParams', '$translate', 'Case.InfoService', 'Complaint.InfoService', 'Helper.ObjectBrowserService', 'Object.CalendarService', 'ObjectService', 'Admin.CalendarConfigurationService', 'MessageService',
                function($scope, $stateParams, $translate, CaseInfoService, ComplaintInfoService, HelperObjectBrowserService, CalendarService, ObjectService, CalendarConfigurationService, MessageService) {

                    var vm = this;

                    $scope.hideInnerCalendarTitle = true;

                    var modules = [ {
                        name: "CASE_FILE",
                        configName: "cases",
                        getInfo: CaseInfoService.getCaseInfo,
                        objectType: ObjectService.ObjectTypes.CASE_FILE,
                        objectIdPropertyName: "id",
                        validateInfo: CaseInfoService.validateCaseInfo
                    }, {
                        name: "COMPLAINT",
                        configName: "complaints",
                        getInfo: ComplaintInfoService.getComplaintInfo,
                        objectType: ObjectService.ObjectTypes.COMPLAINT,
                        objectIdPropertyName: "complaintId",
                        validateInfo: ComplaintInfoService.validateComplaintInfo
                    } ];

                    var module = _.find(modules, function(module) {
                        return module.name == $stateParams.type;
                    });

                    var componentHelper = new HelperObjectBrowserService.Component({
                        scope: $scope,
                        stateParams: $stateParams,
                        moduleId: module.configName,
                        componentId: "main",
                        retrieveObjectInfo: module.getInfo,
                        validateObjectInfo: module.validateInfo,
                        onObjectInfoRetrieved: function() {
                            onObjectInfoRetrieved();
                        }
                    });

                    var onObjectInfoRetrieved = function() {
                        $scope.objectType = HelperObjectBrowserService.getCurrentObjectType();
                        $scope.objectId = componentHelper.currentObjectId;
                        $scope.eventSources = [];
                        CalendarService.isCalendarConfigurationEnabled($scope.objectType).then(function(data) {
                            $scope.objectInfoRetrieved = data;
                        });
                    };
                } ]);