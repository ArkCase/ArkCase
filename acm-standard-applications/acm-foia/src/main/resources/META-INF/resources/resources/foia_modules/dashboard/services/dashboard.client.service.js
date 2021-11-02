'use strict';

/**
 *@ngdoc service
 *@name dashboard:Dashboard.DashboardService
 *
 *@description
 *
 *{@link /acm-standard-applications/arkcase/src/main/webapp/resources/modules/dashboard/services/dashboard..client.service.js modules/dashboard/services/dashboard.client.service.js}
 *
 *  The ArkCaseDashboard.provider is used to customize Angular Dashboard Framework provider with ArkCase's own templates. It also expose dashboard provider functions to be used.
 */
angular.module('dashboard').factory('Dashboard.DashboardService',
        [ '$resource', '$timeout', '$translate', '$q', 'Acm.StoreService', 'UtilService', 'Config.LocaleService', 'ArkCaseDashboard', 'Authentication', function($resource, $timeout, $translate, $q, Store, Util, LocaleService, ArkCaseDashboard, Authentication) {
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
                    url: 'api/v1/plugin/search/advancedSearch?q=object_type_s\\:COMPLAINT+' + 'AND+create_date_tdt\\:%5BNOW/DAY-1MONTH TO NOW%252B1DAY%5D&n=100&s=create_date_tdt desc',
                    isArray: false,
                    data: ''
                },
                queryNewCases: {
                    method: 'GET',
                    url: 'api/v1/plugin/search/advancedSearch?q=object_type_s\\:CASE_FILE+' + 'AND+NOT status_lcs\\:DELETED AND create_date_tdt\\:%5BNOW/DAY-1MONTH TO NOW%252B1DAY%5D&n=100&s=create_date_tdt desc',
                    isArray: false
                },
                queryMyTasks: {
                    method: 'GET',
                    url: 'api/v1/plugin/search/advancedSearch?q=object_type_s\\:TASK' + '+AND+((assignee_id_lcs\\::userId AND status_lcs\\:ACTIVE)' + '+OR+(candidate_group_ss\\::userGroupList AND status_lcs\\:UNCLAIMED))&start=:startWith&n=:pageSize&s=:sortBy :sortDir',
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
                    url: 'api/v1/plugin/search/advancedSearch?q=(assignee_id_lcs\\::userId+' + 'OR+(assignee_id_lcs\\:""+AND+assignee_group_id_lcs\\::userGroupList))+' + 'AND+object_type_s\\:COMPLAINT+' + 'AND+NOT+status_lcs\\:CLOSED&start=:startWith&n=:pageSize&s=:sortBy :sortDir',
                    isArray: false,
                    data: ''
                },
                queryMyCases: {
                    method: 'GET',
                    url: 'api/v1/plugin/search/advancedSearch?q=(assignee_id_lcs\\::userId+' + 'OR+(assignee_id_lcs\\:""+AND+assignee_group_id_lcs\\::userGroupList))+' + 'AND+object_type_s\\:CASE_FILE+' + 'AND+NOT+status_lcs\\:CLOSED+AND+NOT+status_lcs\\:DENIED+AND+NOT+status_lcs\\:DELETED&start=:startWith&n=:pageSize&s=:sortBy :sortDir',
                    isArray: false,
                    data: ''
                },
                queryMyOverdueRequests: {
                    method: 'GET',
                    url: 'api/v1/plugin/search/advancedSearch?q=object_type_s\\:CASE_FILE+' + 'AND+dueDate_tdt\\:%5B*+TO+NOW%5D' + '+AND+assignee_id_lcs\\::userId+' + 'AND+NOT+status_lcs\:CLOSED+AND+NOT+status_lcs\:DENIED+AND+NOT+status_lcs\:DELETED' + 'AND+' + '-queue_name_s\\::queue&n=:pageSize&start=:startWith&s=:sortBy :sortDir',
                    isArray: false,
                    data: ''
                },
                queryMyConsultations: {
                    method: 'GET',
                    url: 'api/v1/plugin/search/advancedSearch?q=(assignee_id_lcs\\::userId+' + 'OR+(assignee_id_lcs\\:""+AND+assignee_group_id_lcs\\::userGroupList))+' + 'AND+object_type_s\\:CONSULTATION+' + 'AND+NOT+status_lcs\\:CLOSED+AND+NOT+status_lcs\\:DELETED&start=:startWith&n=:pageSize&s=:sortBy :sortDir',
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
                    isArray: true
                },
                saveConfig: {
                    method: 'POST',
                    url: 'api/v1/plugin/dashboard/set'
                },
                queryUserWebsites: {
                    method: 'GET',
                    url: 'api/v1/plugin/dashboard/widgets/site?user=:userId',
                    cache: false,
                    isArray: true
                },
                saveUserWebsites: {
                    method: 'POST',
                    url: 'api/v1/plugin/dashboard/widgets/site',
                    cache: false
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
                    $timeout(function() {
                        ArkCaseDashboard.setLocale(iso);
                    }, 0);
                };

                $q.all([ Authentication.queryUserInfo(), LocaleService.getSettings() ]).then(function(result) {
                    var userInfo = result[0];
                    var userLocale = LocaleService.requestLocale(userInfo.langCode);
                    setLocale(userLocale.iso);
                });

                scope.$bus.subscribe('$translateChangeSuccess', function(data) {
                    var userLocale = LocaleService.requestLocale(data.language);
                    setLocale(userLocale.iso);
                });

            };

            //make old code compatible. remove fixOldCode_removeLater() after enough time for all users run the new code
            Service.fixOldCode_removeLater = function(moduleName, model) {
                var oldCode = "modules/dashboard/templates/dashboard-title.html" != model.titleTemplateUrl && "modules/dashboard/templates/module-dashboard-title.html" != model.titleTemplateUrl;

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
                    model.rows.forEach(function(row) {
                        var columns = row.columns;
                        if (columns) {
                            columns.forEach(function(column) {
                                var widgets = column.widgets;
                                if (widgets) {
                                    widgets.forEach(function(widget) {
                                        if ("modules/dashboard/templates/widget-title.html" != widget.titleTemplateUrl) {
                                            oldCode = true;
                                        }
                                        widget.titleTemplateUrl = "modules/dashboard/templates/widget-title.html";
                                    });
                                }
                            });
                        }
                    });
                }

                if (oldCode) {
                    $timeout(function() {
                        Service.saveConfig({
                            dashboardConfig: angular.toJson(model),
                            module: moduleName
                        });
                    }, 0);
                }
            };
            //TODO: remove fixOldCode_removeLater() and its usage in each module

            return Service;
        } ]);