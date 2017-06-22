'use strict';

angular.module('complaints').controller('Complaints.MainController', ['$scope', '$stateParams', '$translate', '$timeout'
    , 'Acm.StoreService', 'UtilService', 'ConfigService', 'Complaint.InfoService', 'dashboard'
    , 'Config.LocaleService', 'Dashboard.DashboardService', 'ArkCaseDashboard'
    , function ($scope, $stateParams, $translate, $timeout
        , Store, Util, ConfigService, ComplaintInfoService, dashboard
        , LocaleService, DashboardService, ArkCaseDashboard
    ) {
        ConfigService.getModuleConfig("complaints").then(function (moduleConfig) {
            $scope.components = moduleConfig.components;
            $scope.config = _.find(moduleConfig.components, {id: "main"});

            return moduleConfig;
        });



        //make old code compatible. remove fixOldCode_removeLater() after enough time for all users run the new code
        var fixOldCode_removeLater = function(m) {
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

        // _.forEach(dashboard.widgets, function (widget, widgetId) {
        //     widget.title = $translate.instant('dashboard.widgets.' + widgetId + '.title');
        //     widget.description = $translate.instant('dashboard.widgets.' + widgetId + '.description');
        // });

        $scope.dashboard = {
            structure: '12',
            collapsible: false,
            maximizable: false,
            complaintModel: {
                titleTemplateUrl: 'modules/dashboard/views/module-dashboard-title.client.view.html'
            }
        };

        DashboardService.getConfig({moduleName: "COMPLAINT"}, function (data) {
            $scope.dashboard.complaintModel = angular.fromJson(data.dashboardConfig);
            fixOldCode_removeLater($scope.dashboard.complaintModel);
            $scope.dashboard.complaintModel.titleTemplateUrl = 'modules/dashboard/views/module-dashboard-title.client.view.html';
            $scope.$emit("collapsed", data.collapsed);
        });
    }
]);