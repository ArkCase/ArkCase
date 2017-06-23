'use strict';

angular.module('dashboard').controller('DashboardController', ['$rootScope', '$scope', '$translate', 'dashboard', '$timeout'
    , 'Acm.StoreService', 'UtilService', 'Config.LocaleService', 'ConfigService', 'Dashboard.DashboardService', 'ArkCaseDashboard'
    , function ($rootScope, $scope, $translate, dashboard, $timeout
        , Store, Util, LocaleService, ConfigService, DashboardService, ArkCaseDashboard
    ) {
		var promiseConfig = ConfigService.getModuleConfig("dashboard").then(function (moduleConfig) {
			$scope.config = moduleConfig;
            return moduleConfig;
        });

		//Phased out code for backward compatibility.
        //Keep it for now just in case there are still widgets using 'req-component-config'
        $scope.$on('req-component-config', onConfigRequest);
        function onConfigRequest(e, componentId) {
            promiseConfig.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId});
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        }
        //TODO: remove above phased out block, after long enough time for all users to update the code changes

        // //Update all dashboard widget titles and descriptions
        // _.forEach(dashboard.widgets, function (widget, widgetId) {
        //     widget.title = $translate.instant('dashboard.widgets.' + widgetId + '.title');
        //     widget.description = $translate.instant('dashboard.widgets.' + widgetId + '.description');
        // });

        var widgetsPerRoles;

        $scope.dashboard = {
            structure: '6-6',
            collapsible: false,
            maximizable: false,
            model: {
                titleTemplateUrl: 'modules/dashboard/templates/widget-title.html',
                editTemplateUrl: 'modules/dashboard/templates/dashboard-edit.html',
                addTemplateUrl : "modules/dashboard/templates/widget-add.html",
                title: ' '
            }
        };

        //make old code compatible. remove fixOldCode_removeLater() after enough time for all users run the new code
        var fixOldCode_removeLater = function() {
            var m = $scope.dashboard.model;
            if ("modules/dashboard/views/dashboard-title.client.view.html" == m.titleTemplateUrl) {
                m.titleTemplateUrl = "";
                if ("Dashboard" == m.title) {
                    m.title = "dashboard.title";
                }
                if (m.rows) {
                    m.rows.forEach(function(row){
                        var columns = row.columns;
                        if (columns) {
                            columns.forEach(function(column){
                               var widgets = column.widgets;
                               if (widgets) {
                                   widgets.forEach(function(widget){
                                       widget.titleTemplateUrl = "";
                                       if ("My Tasks" == widget.title) {
                                           widget.title = "dashboard.widgets.myTasks.title";
                                       } else if ("My Complaints" == widget.title) {
                                           widget.title = "dashboard.widgets.myComplaints.title";
                                       } else if ("New Complaints" == widget.title) {
                                           widget.title = "dashboard.widgets.newComplaints.title";
                                       } else if ("Active Case Files by Queue" == widget.title) {
                                           widget.title = "dashboard.widgets.casesByQueue.title";
                                       } else if ("Cases By Status" == widget.title) {
                                           widget.title = "dashboard.widgets.casesByStatus.title";
                                       } else if ("My Cases" == widget.title) {
                                           widget.title = "dashboard.widgets.myCases.title";
                                       } else if ("New Cases" == widget.title) {
                                           widget.title = "dashboard.widgets.newCases.title";
                                       } else if ("Team Workload" == widget.title) {
                                           widget.title = "dashboard.widgets.teamWorkload.title";
                                       } else if ("Displays weather" == widget.title) {
                                           widget.title = "dashboard.widgets.weather.title";
                                       } else if ("News" == widget.title) {
                                           widget.title = "dashboard.widgets.news.title";
                                       } else if ("Details" == widget.title) {
                                           widget.title = "dashboard.widgets.details.title";
                                       } else if ("People" == widget.title) {
                                           widget.title = "dashboard.widgets.people.title";
                                       } else if ("Documents" == widget.title) {
                                           widget.title = "dashboard.widgets.documents.title";
                                       } else if ("Locations" == widget.title) {
                                           widget.title = "dashboard.widgets.locations.title";
                                       } else if ("Tasks" == widget.title) {
                                           widget.title = "dashboard.widgets.tasks.title";
                                       } else if ("Participants" == widget.title) {
                                           widget.title = "dashboard.widgets.participants.title";
                                       } else if ("References" == widget.title) {
                                           widget.title = "dashboard.widgets.references.title";
                                       } else if ("History" == widget.title) {
                                           widget.title = "dashboard.widgets.history.title";
                                       } else if ("Notes" == widget.title) {
                                           widget.title = "dashboard.widgets.notes.title";
                                       } else if ("Time" == widget.title) {
                                           widget.title = "dashboard.widgets.time.title";
                                       } else if ("Cost" == widget.title) {
                                           widget.title = "dashboard.widgets.cost.title";
                                       } else if ("Calendar" == widget.title) {
                                           widget.title = "dashboard.widgets.calendar.title";
                                       } else if ("Rework Details" == widget.title) {
                                           widget.title = "dashboard.widgets.reworkDetails.title";
                                       } else if ("Documents Under Review" == widget.title) {
                                           widget.title = "dashboard.widgets.docReview.title";
                                       } else if ("Workflow Overview" == widget.title) {
                                           widget.title = "dashboard.widgets.workflow.title";
                                       } else if ("eSignature" == widget.title) {
                                           widget.title = "dashboard.widgets.signature.title";
                                       } else if ("Person" == widget.title) {
                                           widget.title = "dashboard.widgets.person.title";
                                       } else if ("Hours Summary" == widget.title) {
                                           widget.title = "dashboard.widgets.hoursSummary.title";
                                       } else if ("Expenses" == widget.title) {
                                           widget.title = "dashboard.widgets.expenses.title";
                                       } else if ("Organizations" == widget.title) {
                                           widget.title = "dashboard.widgets.organizations.title";
                                       } else if ("Tags" == widget.title) {
                                       widget.title = "dashboard.widgets.tags.title";
                                       } else if ("Approval routing" == widget.title) {
                                           widget.title = "dashboard.widgets.approvalRouting.title";
                                       }
                                   });
                               }
                            });
                        }
                    });
                }
            }
        };
        //TODO: remove above fixOldCode_removeLater()

        DashboardService.getConfig({moduleName: "DASHBOARD"}, function (data) {
            $scope.dashboard.model = angular.fromJson(data.dashboardConfig);
            fixOldCode_removeLater();
            $scope.dashboard.model.titleTemplateUrl = 'modules/dashboard/templates/dashboard-title.html';
            $scope.dashboard.model.editTemplateUrl = 'modules/dashboard/templates/dashboard-edit.html';
            $scope.dashboard.model.addTemplateUrl = 'modules/dashboard/templates/widget-add.html';

            DashboardService.getWidgetsPerRoles(function (widgets) {
                widgetsPerRoles = widgets;
            });

            $scope.widgetFilter = function (widget, type) {
                var result = false;
                angular.forEach(widgetsPerRoles, function (w) {
                    if (type === w.widgetName) {
                        result = true;
                    }
                });
                return result;
            };

        });

        var setLocale = function(iso) {
            $timeout(function () {
                ArkCaseDashboard.setLocale(iso);
            }, 0);
        };

        var cacheLocale = new Store.LocalData({name: "AcmLocale", noOwner: true, noRegistry: true});
        var lastLocale = cacheLocale.get();
        if (Util.isEmpty(lastLocale)) {
            lastLocale = {};
            lastLocale.locales = LocaleService.DEFAULT_LOCALES;
            lastLocale.code = LocaleService.DEFAULT_CODE;
            lastLocale.iso = LocaleService.DEFAULT_ISO;
            cacheLocale.set(lastLocale);
        }
        var locales = Util.goodMapValue(lastLocale, "locales", LocaleService.DEFAULT_LOCALES);
        var localeCode = Util.goodMapValue(lastLocale, "code", LocaleService.DEFAULT_CODE);
        var localeIso = Util.goodMapValue(lastLocale, "iso", LocaleService.DEFAULT_ISO);
        $translate.use(localeCode);
        setLocale(localeIso);

        $scope.$bus.subscribe('$translateChangeSuccess', function (data) {
            var locale = _.find(locales, {code: data.language});
            if (locale) {
                var iso = Util.goodMapValue(locale, "iso", LocaleService.DEFAULT_ISO);
                setLocale(iso);
            }
        });

        $scope.$on('adfDashboardChanged', function (event, name, model) {
            DashboardService.saveConfig({
                dashboardConfig: angular.toJson(model),
                module: "DASHBOARD"
            });
            $scope.dashboard.model = model;
        });

    }
]);
