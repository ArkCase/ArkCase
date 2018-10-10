'use strict';

/**
 * @ngdoc service
 * @name services:Helper.DashboardService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/helper/helper-dashboard.client.service.js services/helper/helper-dashboard.client.service.js}

 * Helper.DashboardService provide basic coding for typical module dashboard in ArkCase
 */
angular.module('services').factory('Helper.DashboardService', [ '$timeout', '$translate', 'UtilService', 'ConfigService', 'ArkCaseDashboard', 'Dashboard.DashboardService', function($timeout, $translate, Util, ConfigService, ArkCaseDashboard, DashboardService) {

    var Service = {
        /**
         * @ngdoc method
         * @name Dashboard Constructor
         * @methodOf services:Helper.DashboardService
         *
         * @param {Object} arg Map arguments
         * @param {Object} arg.scope Angular $scope
         * @param {String} arg.moduleId Module ID
         * @param {String} arg.dashboardName dashboard Name for the module
         * @param {Object} arg.dashboard dashboard definition
         *
         * @description
         * ArkCase module dashboard in different modules has very similar code structure.
         * Helper.DashboardService.Dashboard provides the
         */
        Dashboard: function(arg) {
            var that = this;
            that.scope = arg.scope;
            that.moduleId = arg.moduleId;
            that.dashboardName = arg.dashboardName;
            that.scope.dashboard = Util.goodValue(arg.dashboard, {
                structure: '12',
                collapsible: false,
                maximizable: false,
                model: {
                    titleTemplateUrl: 'modules/dashboard/templates/module-dashboard-title.html'
                }
            });

            ConfigService.getModuleConfig(that.moduleId).then(function(moduleConfig) {
                that.scope.components = moduleConfig.components;
                that.scope.config = _.find(moduleConfig.components, {
                    id: "main"
                });
                return moduleConfig;
            });

            DashboardService.localeUseTypical(that.scope);

            var queryParameters = {
                moduleName: that.dashboardName
            };
            if (that.dashboardName === "DASHBOARD") {
                queryParameters['timestamp'] = new Date().getTime()
            }
            DashboardService.getConfig(queryParameters, function(data) {
                that.scope.dashboard.model = angular.fromJson(data.dashboardConfig);
                DashboardService.fixOldCode_removeLater(that.dashboardName, that.scope.dashboard.model);
                if (arg.onDashboardConfigRetrieved) {
                    arg.onDashboardConfigRetrieved(data);
                } else {
                    that.scope.dashboard.model.titleTemplateUrl = 'modules/dashboard/templates/module-dashboard-title.html';
                    that.scope.$emit("collapsed", data.collapsed);
                }
            });

        }
    };

    /**
     * @ngdoc method
     * @name addLocales
     * @methodOf services:Helper.DashboardService
     *
     * @description
     * addLocales() adds all locale support from dashboard config file.
     */
    Service.addLocales = function() {
        ConfigService.getModuleConfig("dashboard").then(function(moduleConfig) {
            moduleConfig.locals.forEach(function(local) {
                ArkCaseDashboard.addLocale(local.iso, local.translations);
            });
            return moduleConfig;
        });
    };

    return Service;
} ]);
