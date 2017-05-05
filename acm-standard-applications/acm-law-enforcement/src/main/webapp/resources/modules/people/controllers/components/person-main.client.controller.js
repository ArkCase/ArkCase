'use strict';

angular.module('people').controller('People.MainController', ['$scope', '$stateParams', '$translate'
    , 'Acm.StoreService', 'UtilService', 'ConfigService', 'Person.InfoService', 'dashboard', 'Dashboard.DashboardService'
    , function ($scope, $stateParams, $translate
        , Store, Util, ConfigService, PersonInfoService, dashboard, DashboardService) {

        /*        //$scope.$emit('main-component-started');

         ConfigService.getModuleConfig("people").then(function (moduleConfig) {
         $scope.components = moduleConfig.components;
         $scope.config = _.find(moduleConfig.components, {id: "main"});

         return moduleConfig;
         });


         _.forEach(dashboard.widgets, function (widget, widgetId) {
         widget.title = $translate.instant('dashboard.widgets.' + widgetId + '.title');
         widget.description = $translate.instant('dashboard.widgets.' + widgetId + '.description');
         });

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
         $scope.dashboard.complaintModel.titleTemplateUrl = 'modules/dashboard/views/module-dashboard-title.client.view.html';
         $scope.$emit("collapsed", data.collapsed);
         });*/
    }
]);