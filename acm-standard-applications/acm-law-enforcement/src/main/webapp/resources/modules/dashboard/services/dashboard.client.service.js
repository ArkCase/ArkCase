'use strict';

/**
 *@ngdoc service
 *@name dashboard:Dashboard.DashboardService
 *
 *@description
 *
 *{@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/dashboard/services/dashboard..client.service.js modules/dashboard/services/dashboard.client.service.js}
 *
 *  The ArkCaseDashboard.provider is used to customize Angular Dashboard Framework provider with ArkCase's own templates. It also expose dashboard provider functions to be used.
 */
angular.module('dashboard').factory('Dashboard.DashboardService', ['$resource', '$timeout', '$translate'
    , 'Acm.StoreService', 'UtilService', 'Config.LocaleService', 'ArkCaseDashboard'
    , function ($resource, $timeout, $translate
        , Store, Util, LocaleService, ArkCaseDashboard
    ) {
        var Service = $resource('', {}, {
            getConfig: {
                method: 'GET',
                url: 'api/v1/plugin/dashboard/get',
                params: {
                    moduleName: "@moduleName"
                },
                data: ''
            },
            queryCasesByQueue: {
                method: 'GET',
                url: 'api/v1/plugin/casefile/number/by/queue',
                isArray: false,
                data: ''
            },
            queryCasesByStatus: {
                method: 'GET',
                url: 'api/v1/plugin/casebystatus/:period',
                isArray: true,
                data: ''
            },
            queryNewComplaints: {
                method: 'GET',
                url: 'api/v1/plugin/search/advancedSearch?q=object_type_s\\:COMPLAINT+' +
                'AND+create_date_tdt\\:[NOW-1MONTH TO NOW]',
                isArray: false,
                data: ''
            },
            queryNewCases: {
                method: 'GET',
                url: 'api/v1/plugin/search/advancedSearch?q=object_type_s\\:CASE_FILE+' +
                'AND+NOT+status_lcs\\:DELETED+AND+create_date_tdt\\:[NOW-1MONTH TO NOW]',
                isArray: false
            },
            queryMyTasks: {
                method: 'GET',
                url: 'api/v1/plugin/search/advancedSearch?q=(assignee_id_lcs\\::userId'
                + '+OR+candidate_group_ss\\::userGroupList)'
                + '+AND+object_type_s\\:TASK+'
                + 'AND+status_lcs\:(ACTIVE OR UNCLAIMED)&start=:startWith&n=:pageSize&s=:sortBy :sortDir',
                isArray: false,
                data: ''
            },
            queryWorkflowReport: {
                method: 'GET',
                url: 'api/v1/plugin/task/businessProcessTasks?start=:startWith&n=:pageSize&s=:sortBy :sortDir',
                isArray: false,
                data: ''
            },
            queryMyComplaints: {
                method: 'GET',
                url: 'api/v1/plugin/search/advancedSearch?q=(assignee_id_lcs\\::userId+' +
                'OR+(assignee_id_lcs\\:""+AND+assignee_group_id_lcs\\::userGroupList))+' +
                'AND+object_type_s\\:COMPLAINT+' +
                'AND+NOT+status_lcs\\:CLOSED&start=:startWith&n=:pageSize&s=:sortBy :sortDir',
                isArray: false,
                data: ''
            },
            queryMyCases: {
                method: 'GET',
                url: 'api/v1/plugin/search/advancedSearch?q=(assignee_id_lcs\\::userId+' +
                'OR+(assignee_id_lcs\\:""+AND+assignee_group_id_lcs\\::userGroupList))+' +
                'AND+object_type_s\\:CASE_FILE+' +
                'AND+NOT+status_lcs\\:CLOSED&start=:startWith&n=:pageSize&s=:sortBy :sortDir',
                isArray: false,
                data: ''
            },
            queryTeamWorkload: {
                method: 'GET',
                url: 'api/v1/plugin/task/getListByDueDate/:due',
                isArray: true,
                data: ''
            },
            getWidgetsPerRoles: {
                method: 'GET',
                url: 'api/latest/plugin/dashboard/widgets/get',
                isArray: true,
            },
            saveConfig: {
                method: 'POST',
                url: 'api/v1/plugin/dashboard/set'
            }
        });

        /**
         * @ngdoc method
         * @name localeUseTypical
         * @methodOf dashboard:Object.Dashboard.DashboardService
         *
         * @param {Object} scope, Angular $scope of caller controller
         *
         * @description
         * This function combine the typical usage to set up to use ArkCase Dashboard. It checks and reads any previous
         * cached locale info, and set to correct locale on locale change event.
         */
        Service.localeUseTypical = function(scope) {
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

            scope.$bus.subscribe('$translateChangeSuccess', function (data) {
                var locale = _.find(locales, {code: data.language});
                if (locale) {
                    var iso = Util.goodMapValue(locale, "iso", LocaleService.DEFAULT_ISO);
                    setLocale(iso);
                }
            });
        };


        //make old code compatible. remove fixOldCode_removeLater() after enough time for all users run the new code
        Service.fixOldCode_removeLater = function(moduleName, model) {
            var oldCode = "modules/dashboard/templates/dashboard-title.html" != model.titleTemplateUrl
                && "modules/dashboard/templates/module-dashboard-title.html" != model.titleTemplateUrl;

            if ("DASHBOARD" == moduleName) {
                model.titleTemplateUrl = 'modules/dashboard/templates/dashboard-title.html';
                model.editTemplateUrl = 'modules/dashboard/templates/dashboard-edit.html';
                model.addTemplateUrl = 'modules/dashboard/templates/widget-add.html';
                model.title = "dashboard.title";
            } else {
                model.titleTemplateUrl = 'modules/dashboard/templates/module-dashboard-title.html';
            }

            if ("Dashboard" == model.title) {
                model.title = "dashboard.title";
            }

            if (model.rows) {
                model.rows.forEach(function(row){
                    var columns = row.columns;
                    if (columns) {
                        columns.forEach(function(column){
                            var widgets = column.widgets;
                            if (widgets) {
                                widgets.forEach(function(widget){
                                    if ("modules/dashboard/templates/widget-title.html" != widget.titleTemplateUrl) {
                                        oldCode = true;
                                    }
                                    widget.titleTemplateUrl = "modules/dashboard/templates/widget-title.html";
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

            if (oldCode) {
                $timeout(function () {
                    Service.saveConfig({
                        dashboardConfig: angular.toJson(model),
                        module: moduleName
                    });
                }, 0);

            }
        };
        //TODO: remove fixOldCode_removeLater() and its usage in each module

        return Service;
    }
]);