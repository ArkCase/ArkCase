'use strict';

angular.module('complaints').controller('Complaints.MainController', ['$scope', '$stateParams', '$translate', 'dashboard', 'Dashboard.DashboardService',
    'UtilService', 'ConfigService', 'Complaint.InfoService', 'ObjectService', 'Object.CorrespondenceService', 'Object.NoteService', 'Object.TaskService', 'StoreService'
    , 'Object.AuditService', 'Object.CostService', 'Object.TimeService'
    , function ($scope, $stateParams, $translate, dashboard, DashboardService
        , Util, ConfigService, ComplaintInfoService, ObjectService, ObjectCorrespondenceService, ObjectNoteService, ObjectTaskService, Store
        , ObjectAuditService, ObjectCostService, ObjectTimeService) {

        ConfigService.getModuleConfig("complaints").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            $scope.config = _.find(moduleConfig.components, {id: "main"});
            //$scope.widgets = [];
            //_.each(Util.goodMapValue($scope.config, "widgets", []), function (widget) {
            //    if ("component" == Util.goodValue(widget.type)) {
            //        var item = {};
            //        var found = _.find(moduleConfig.components, {id: widget.id});
            //        //if (found && found.enabled) {
            //        if (found) {
            //            item.title = found.title;
            //            item.id = widget.id;
            //            $scope.widgets.push(item);
            //        }
            //    }
            //});

            $scope.allowedWidgets = ['details'];

            return moduleConfig;
        });

        _.forEach(dashboard.widgets, function (widget, widgetId) {
            widget.title = $translate.instant('dashboard.widgets.' + widgetId + '.title');
            widget.description = $translate.instant('dashboard.widgets.' + widgetId + '.description');
        });

        $scope.dashboard = {
            structure: '6-6',
            collapsible: false,
            maximizable: false,
            model: {
                titleTemplateUrl: 'modules/dashboard/views/dashboard-title.client.view.html'
            }
        };

        DashboardService.getConfig({}, function (data) {
            var cacheDashboardConfig = new Store.CacheFifo(ComplaintInfoService.CacheNames.COMPLAINT_INFO);
            $scope.dashboard.complaintModel = cacheDashboardConfig.get("dashboardConfig");

            if($scope.dashboard.complaintModel) {
                //If cached, use that model
                $scope.dashboard.complaintModel.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';
                $scope.dashboard.model.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';
            } else {
                //Else use dashboard config and filter.
                $scope.dashboard.model = angular.fromJson(data.dashboardConfig);
                $scope.dashboard.complaintModel = Util.filterWidgets($scope.dashboard.model, $scope.allowedWidgets);
                $scope.dashboard.model.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';

                //Cache filtered dashboard model
                cacheDashboardConfig.put("dashboardConfig", $scope.dashboard.complaintModel);
            }
        });

        $scope.$on('adfDashboardChanged', function (event, name, model) {
            //Save dashboard model only to cache
            var cacheDashboardConfig = new Store.CacheFifo(ComplaintInfoService.CacheNames.COMPLAINT_INFO);
            if(cacheDashboardConfig)
                cacheDashboardConfig.put("dashboardConfig", model);
        });

        //var widgetFilter = function(model) {
        //    var complaintModel = model;
        //    //iterate over rows
        //    for(var i = 0; i < complaintModel.rows.length; i++) {
        //        //iterate over columns
        //        for(var j = 0; j < complaintModel.rows[i].columns.length; j++) {
        //            //iterate over column widgets
        //            if(complaintModel.rows[i].columns[j].widgets){
        //                for(var k = complaintModel.rows[i].columns[j].widgets.length; k > 0; k--) {
        //                    // var type = complaintModel.rows[i].columns[j].widgets[k].type;
        //                    var type = complaintModel.rows[i].columns[j].widgets[k-1].type;
        //                    if(!($scope.allowedWidgets.indexOf(type) > -1)) {
        //                        //remove widget from array
        //                        complaintModel.rows[i].columns[j].widgets.splice(k-1, 1);
        //                    }
        //                }
        //            }
        //        }
        //    }
        //    return complaintModel;
        //};

        //$scope.widgetData = {};
        //ComplaintInfoService.getComplaintInfo($stateParams.id).then(function (complaintInfo) {
        //    $scope.complaintInfo = complaintInfo;
        //
        //    $scope.widgetData["details"] = Util.goodMapValue($scope.complaintInfo, "details");
        //
        //    var location = Util.goodMapValue($scope.complaintInfo, "location.streetAddress")
        //            + ", " + Util.goodMapValue($scope.complaintInfo, "location.city")
        //            + ", " + Util.goodMapValue($scope.complaintInfo, "location.state")
        //            + ", " + Util.goodMapValue($scope.complaintInfo, "location.zip")
        //        ;
        //    $scope.widgetData["locations"] = location;
        //
        //    var personAssociations = $scope.complaintInfo.personAssociations;
        //    $scope.widgetData["people"] = personAssociations.length;
        //
        //    var participants = $scope.complaintInfo.participants;
        //    $scope.widgetData["participants"] = participants.length;
        //
        //    var references = Util.goodArray($scope.complaintInfo.references);
        //    $scope.widgetData["references"] = references.length;
        //
        //
        //    return complaintInfo;
        //});
        //
        //ObjectCorrespondenceService.queryCorrespondences(ObjectService.ObjectTypes.COMPLAINT, $stateParams.id, 0, 10, "", "")
        //    .then(function (correspondenceData) {
        //        $scope.widgetData["correspondence"] = Util.goodValue(correspondenceData.totalChildren, 0);
        //    });
        //
        //$scope.widgetData["documents"] = "documents data";
        //
        //ObjectNoteService.queryNotes(ObjectService.ObjectTypes.COMPLAINT, $stateParams.id)
        //    .then(function (notes) {
        //        $scope.widgetData["notes"] = Util.goodValue(notes.length, 0);
        //    });
        //
        //ObjectTaskService.queryChildTasks(ObjectService.ObjectTypes.COMPLAINT, $stateParams.id, 0, 10, "", "")
        //    .then(function (data) {
        //        $scope.widgetData["tasks"] = Util.goodValue(data.response.numFound, 0);
        //    });
        //
        //ObjectAuditService.queryAudit(ObjectService.ObjectTypes.COMPLAINT, $stateParams.id, 0, 10, "", "")
        //    .then(function (auditData) {
        //        $scope.widgetData["history"] = Util.goodValue(auditData.totalCount, 0);
        //    });
        //
        //ObjectCostService.queryCostsheets(ObjectService.ObjectTypes.COMPLAINT, $stateParams.id).then(
        //    function (costsheets) {
        //        $scope.widgetData["cost"] = Util.goodValue(costsheets.length, 0);
        //    });
        //
        //ObjectTimeService.queryTimesheets(ObjectService.ObjectTypes.COMPLAINT, $stateParams.id).then(
        //    function (timesheets) {
        //        $scope.widgetData["time"] = Util.goodValue(timesheets.length, 0);
        //    });
        //
        //$scope.widgetData["calendar"] = "calendar data";
    }
]);