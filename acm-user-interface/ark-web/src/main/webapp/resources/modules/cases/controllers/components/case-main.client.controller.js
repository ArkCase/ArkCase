'use strict';

angular.module('cases').controller('Cases.MainController', ['$scope', '$stateParams', '$translate', 'UtilService', 'ConfigService'
    , 'Case.InfoService', 'ObjectService', 'Object.CorrespondenceService', 'Object.NoteService', 'Object.TaskService'
    , 'Object.AuditService', 'Object.CostService', 'Object.TimeService', 'dashboard', 'Dashboard.DashboardService'
    , function ($scope, $stateParams, $translate, Util, ConfigService
        , CaseInfoService, ObjectService, ObjectCorrespondenceService, ObjectNoteService, ObjectTaskService
        , ObjectAuditService, ObjectCostService, ObjectTimeService, dashboard, DashboardService) {


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
            $scope.dashboard.model = angular.fromJson(data.dashboardConfig);

            $scope.dashboard.caseModel = widgetFilter($scope.dashboard.model);
            $scope.dashboard.caseModel.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';

            // Set Dashboard custom title
            $scope.dashboard.model.titleTemplateUrl = 'modules/dashboard/views/dashboard-title.client.view.html';
        });

        $scope.$on('adfDashboardChanged', function (event, name, model) {
            //merge main dashboard and case dashboard widgets
            model = mergeWidgets(model, $scope.dashboard.model);

            DashboardService.saveConfig({
                dashboardConfig: angular.toJson(model)
            });
        });

        var getWidgets = function(model) {
            var widgets = [];
            for(var i = 0; i < model.rows.length; i++) {
                //iterate over columns
                for(var j = 0; j < model.rows[i].columns.length; j++) {
                    //iterate over column widgets
                    for(var k = 0; k < model.rows[i].columns[j].widgets.length; k++) {
                        widgets.push( {'widget': model.rows[i].columns[j].widgets[k],
                                        'row' : i,
                                        'col' : j,
                                        'wIndex': k
                        });
                    }
                }
            }
            return widgets;
        };
        var hasWidget = function(name, widgets) {
            for(var i = 0; i < widgets.length; i++) {
                if(widgets[i].widget.type === name) {
                    return true;
                }
            }
            return false;
        }
        var widgetFilter = function(model) {
            var caseModel = model;
            //iterate over rows
            for(var i = 0; i < caseModel.rows.length; i++) {
                //iterate over columns
                for(var j = 0; j < caseModel.rows[i].columns.length; j++) {
                    //iterate over column widgets
                    for(var k = caseModel.rows[i].columns[j].widgets.length; k < 0; k--) {
                        if($scope.allowedWidgets.indexOf(caseModel.rows[i].columns[j].widgets[k].type) < 0) {
                            //remove widget from array
                            caseModel.rows[i].columns[j].widgets.pop();
                        }
                    }
                }
            }
            return caseModel;
        };
        var mergeWidgets = function(caseModel, dashboardModel) {
            //TODO
            /**
             * if caseModel has a widget that is not in dashboardModel,
             * then add widget to dashboard model in the correct row:column
             * if caseModel doesnt have a widget that dashboard has and
             * that widget is a 'case widget'
             */
            // Find all widgets of dashboard model and caseModel
            var dashWidgets = getWidgets(dashboardModel);
            var caseWidgets = getWidgets(caseModel);

            if(caseWidgets.length == 0){
                //remove all 'case widgets' from dashboard
                for(var i =0; i < dashWidgets.length; i++) {
                    var row = dashWidgets[i].row;
                    var col = dashWidgets[i].col;
                    var wIndex = dashWidgets[i].wIndex;

                    if($scope.allowedWidgets.indexOf(dashboardModel.rows[row].columns[col].widget[wIndex].type) > -1) {
                        //remove case widget
                        dashboardModel.rows[row].columns[col].widgets = dashboardModel.rows[row].columns[col].widgets.splice(wIndex,1);
                    }
                }
            }
            else {
                //try to merge changes
                for(var i =0; i < dashWidgets.length; i++) {
                    var row = dashWidgets[i].row;
                    var col = dashWidgets[i].col;
                    var wIndex = dashWidgets[i].wIndex;

                    //if casewidgets doesn't have a certain dashboard widget
                    if(!hasWidget(dashWidgets[i].widget.type, caseWidgets)){
                        //and the widget it doesnt have is a 'case widget'
                        if($scope.allowedWidgets.indexOf(dashWidgets[i].widget.type) > -1) {
                            //then remove that widget from dashboard widgets
                            dashboardModel.rows[row].columns[col].widgets = dashboardModel.rows[row].columns[col].widgets.splice(wIndex,1);
                        }
                    }
                }

                //if casewidgets has a widget that dashboard widgets doesn't have, then add the widget to the dashboard
                for(var i =0; i < caseWidgets; i++) {
                    var row = caseWidgets[i].row;
                    var col = caseWidgets[i].col;
                    var wIndex = caseWidgets[i].wIndex;
                    if(!hasWidget(caseWidgets[i].widget.type, dashWidgets)) {
                        dashboardModel.rows[row].columns[col].widgets = dashboardModel.rows[row].columns[col].widgets.splice(wIndex, 1);
                    }
                }
            }
        return dashboardModel;
        };



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

