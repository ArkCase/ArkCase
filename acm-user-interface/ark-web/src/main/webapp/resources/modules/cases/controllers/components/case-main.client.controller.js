'use strict';

angular.module('cases').controller('Cases.MainController', ['$scope', '$stateParams', '$translate', 'UtilService', 'ConfigService'
    , 'Case.InfoService', 'ObjectService', 'Object.CorrespondenceService', 'Object.NoteService', 'Object.TaskService'
    , 'Object.AuditService', 'Object.CostService', 'Object.TimeService', 'dashboard', 'Dashboard.DashboardService', 'StoreService'
    , function ($scope, $stateParams, $translate, Util, ConfigService
        , CaseInfoService, ObjectService, ObjectCorrespondenceService, ObjectNoteService, ObjectTaskService
        , ObjectAuditService, ObjectCostService, ObjectTimeService, dashboard, DashboardService, Store) {

        var promiseConfig = ConfigService.getModuleConfig("cases").then(function (moduleConfig) {
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
            var cacheDashboardConfig = new Store.CacheFifo(CaseInfoService.CacheNames.CASE_INFO);
            $scope.dashboard.caseModel = cacheDashboardConfig.get("dashboardConfig");

            if($scope.dashboard.caseModel) {
                //If cached, use that model
                $scope.dashboard.caseModel.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';
                $scope.dashboard.model.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';
            } else {
                //Else use dashboard config and filter.
                $scope.dashboard.model = angular.fromJson(data.dashboardConfig);
                $scope.dashboard.caseModel = Util.filterWidgets($scope.dashboard.model, $scope.allowedWidgets);
                $scope.dashboard.model.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';

                //Cache filtered dashboard model
                cacheDashboardConfig.put("dashboardConfig", $scope.dashboard.caseModel);
            }
        });

        $scope.$on('adfDashboardChanged', function (event, name, model) {
            //Save dashboard model only to cache
            var cacheDashboardConfig = new Store.CacheFifo(CaseInfoService.CacheNames.CASE_INFO);
            if(cacheDashboardConfig)
                cacheDashboardConfig.put("dashboardConfig", model);
        });

        //var widgetFilter = function(model) {
        //    var caseModel = model;
        //    //iterate over rows
        //    for(var i = 0; i < caseModel.rows.length; i++) {
        //        //iterate over columns
        //        for(var j = 0; j < caseModel.rows[i].columns.length; j++) {
        //            //iterate over column widgets
        //            if(caseModel.rows[i].columns[j].widgets){
        //                for(var k = caseModel.rows[i].columns[j].widgets.length; k > 0; k--) {
        //                    // var type = caseModel.rows[i].columns[j].widgets[k].type;
        //                    var type = caseModel.rows[i].columns[j].widgets[k-1].type;
        //                    if(!($scope.allowedWidgets.indexOf(type) > -1)) {
        //                        //remove widget from array
        //                        caseModel.rows[i].columns[j].widgets.splice(k-1, 1);
        //                    }
        //                }
        //            }
        //        }
        //    }
        //    return caseModel;
        //};

        ////$scope.widgetData = {};
        ////$scope.$on('case-updated', function (e, data) {
        ////    if (!CaseInfoService.validateCaseInfo(data)) {
        ////        return;
        ////    }
        ////
        ////    $scope.caseInfo = data;
        ////    $scope.widgetData["details"] = $scope.caseInfo.details;
        ////
        ////    var personAssociations = $scope.caseInfo.personAssociations;
        ////    $scope.widgetData["people"] = personAssociations.length;
        ////
        ////    var participants = $scope.caseInfo.participants;
        ////    $scope.widgetData["participants"] = participants.length;
        ////});
        //$scope.widgetData = {};
        //CaseInfoService.getCaseInfo($stateParams.id).then(function (caseInfo) {
        //    $scope.caseInfo = caseInfo;
        //
        //    $scope.widgetData["details"] = Util.goodMapValue($scope.caseInfo, "details");
        //
        //    var personAssociations = $scope.caseInfo.personAssociations;
        //    $scope.widgetData["people"] = personAssociations.length;
        //
        //    var participants = $scope.caseInfo.participants;
        //    $scope.widgetData["participants"] = participants.length;
        //
        //    var references = $scope.caseInfo.references;
        //    $scope.widgetData["references"] = references.length;
        //
        //
        //    return caseInfo;
        //});
        //
        //ObjectCorrespondenceService.queryCorrespondences(ObjectService.ObjectTypes.CASE_FILE, $stateParams.id, 0, 10, "", "")
        //    .then(function (correspondenceData) {
        //        $scope.widgetData["correspondence"] = Util.goodValue(correspondenceData.totalChildren, 0);
        //    });
        //
        //$scope.widgetData["documents"] = "documents data";
        //
        //ObjectNoteService.queryNotes(ObjectService.ObjectTypes.CASE_FILE, $stateParams.id)
        //    .then(function (notes) {
        //        $scope.widgetData["notes"] = Util.goodValue(notes.length, 0);
        //    });
        //
        //ObjectTaskService.queryChildTasks(ObjectService.ObjectTypes.CASE_FILE, $stateParams.id, 0, 10, "", "")
        //    .then(function (data) {
        //        $scope.widgetData["tasks"] = Util.goodValue(data.response.numFound, 0);
        //    });
        //
        //ObjectAuditService.queryAudit(ObjectService.ObjectTypes.CASE_FILE, $stateParams.id, 0, 10, "", "")
        //    .then(function (auditData) {
        //        $scope.widgetData["history"] = Util.goodValue(auditData.totalCount, 0);
        //    });
        //
        //ObjectCostService.queryCostsheets(ObjectService.ObjectTypes.CASE_FILE, $stateParams.id).then(
        //    function (costsheets) {
        //        $scope.widgetData["cost"] = Util.goodValue(costsheets.length, 0);
        //    });
        //
        //ObjectTimeService.queryTimesheets(ObjectService.ObjectTypes.CASE_FILE, $stateParams.id).then(
        //    function (timesheets) {
        //        $scope.widgetData["time"] = Util.goodValue(timesheets.length, 0);
        //    });
        //
        //$scope.widgetData["calendar"] = "calendar data";
    }
]);