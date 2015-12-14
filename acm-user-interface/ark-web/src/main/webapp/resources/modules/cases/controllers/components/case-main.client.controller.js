'use strict';

angular.module('cases').controller('Cases.MainController', ['$scope', '$stateParams', 'UtilService', 'ConfigService'
    , 'Case.InfoService', 'ObjectService', 'Object.CorrespondenceService', 'Object.NoteService', 'Object.TaskService'
    , 'Object.AuditService', 'Object.CostService', 'Object.TimeService', 'dashboard', 'Dashboard.DashboardService'
    , function ($scope, $stateParams, Util, ConfigService
        , CaseInfoService, ObjectService, ObjectCorrespondenceService, ObjectNoteService, ObjectTaskService
        , ObjectAuditService, ObjectCostService, ObjectTimeService, dashboard, DashboardService) {


        var promiseConfig = ConfigService.getModuleConfig("cases").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            $scope.config = _.find(moduleConfig.components, {id: "main"});
            $scope.widgets = [];
            _.each(Util.goodMapValue($scope.config, "widgets", []), function (widget) {
                if ("component" == Util.goodValue(widget.type)) {
                    var item = {};
                    var found = _.find(moduleConfig.components, {id: widget.id});
                    //if (found && found.enabled) {
                    if (found) {
                        item.title = found.title;
                        item.id = widget.id;
                        $scope.widgets.push(item);
                    }
                }
            });

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
            $scope.dashboard.model = angular.fromJson(data.dashboardConfig);

            // Set Dashboard custom title
            $scope.dashboard.model.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';
        });

        $scope.$on('adfDashboardChanged', function (event, name, model) {
            DashboardService.saveConfig({
                dashboardConfig: angular.toJson(model)
            });
        });



        //$scope.widgetData = {};
        //$scope.$on('case-updated', function (e, data) {
        //    if (!CaseInfoService.validateCaseInfo(data)) {
        //        return;
        //    }
        //
        //    $scope.caseInfo = data;
        //    $scope.widgetData["details"] = $scope.caseInfo.details;
        //
        //    var personAssociations = $scope.caseInfo.personAssociations;
        //    $scope.widgetData["people"] = personAssociations.length;
        //
        //    var participants = $scope.caseInfo.participants;
        //    $scope.widgetData["participants"] = participants.length;
        //});
        $scope.widgetData = {};
        CaseInfoService.getCaseInfo($stateParams.id).then(function (caseInfo) {
            $scope.caseInfo = caseInfo;

            $scope.widgetData["details"] = Util.goodMapValue($scope.caseInfo, "details");

            var personAssociations = $scope.caseInfo.personAssociations;
            $scope.widgetData["people"] = personAssociations.length;

            var participants = $scope.caseInfo.participants;
            $scope.widgetData["participants"] = participants.length;

            var references = $scope.caseInfo.references;
            $scope.widgetData["references"] = references.length;


            return caseInfo;
        });

        ObjectCorrespondenceService.queryCorrespondences(ObjectService.ObjectTypes.CASE_FILE, $stateParams.id, 0, 10, "", "")
            .then(function (correspondenceData) {
                $scope.widgetData["correspondence"] = Util.goodValue(correspondenceData.totalChildren, 0);
            });

        $scope.widgetData["documents"] = "documents data";

        ObjectNoteService.queryNotes(ObjectService.ObjectTypes.CASE_FILE, $stateParams.id)
            .then(function (notes) {
                $scope.widgetData["notes"] = Util.goodValue(notes.length, 0);
            });

        ObjectTaskService.queryChildTasks(ObjectService.ObjectTypes.CASE_FILE, $stateParams.id, 0, 10, "", "")
            .then(function (data) {
                $scope.widgetData["tasks"] = Util.goodValue(data.response.numFound, 0);
            });

        ObjectAuditService.queryAudit(ObjectService.ObjectTypes.CASE_FILE, $stateParams.id, 0, 10, "", "")
            .then(function (auditData) {
                $scope.widgetData["history"] = Util.goodValue(auditData.totalCount, 0);
            });

        ObjectCostService.queryCostsheets(ObjectService.ObjectTypes.CASE_FILE, $stateParams.id).then(
            function (costsheets) {
                $scope.widgetData["cost"] = Util.goodValue(costsheets.length, 0);
            });

        ObjectTimeService.queryTimesheets(ObjectService.ObjectTypes.CASE_FILE, $stateParams.id).then(
            function (timesheets) {
                $scope.widgetData["time"] = Util.goodValue(timesheets.length, 0);
            });

        $scope.widgetData["calendar"] = "calendar data";
    }
]);

