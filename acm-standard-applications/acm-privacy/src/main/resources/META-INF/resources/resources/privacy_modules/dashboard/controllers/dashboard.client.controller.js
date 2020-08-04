'use strict';

angular.module('dashboard').controller('DashboardController', ['$rootScope', '$scope', 'ConfigService', 'Dashboard.DashboardService', 'Helper.DashboardService', '$modal', '$state', 'PrivacyConfiguration.Service', 'MessageService', function ($rootScope, $scope, ConfigService, DashboardService, DashboardHelper, $modal, $state, PrivacyConfigurationService, MessageService) {

    new DashboardHelper.Dashboard({
        scope: $scope,
        moduleId: "dashboard",
        dashboardName: "DASHBOARD",
        dashboard: {
            structure: '6-6',
            collapsible: false,
            maximizable: false,
            model: {
                titleTemplateUrl: 'modules/dashboard/templates/dashboard-title.html',
                editTemplateUrl: 'modules/dashboard/templates/dashboard-edit.html',
                addTemplateUrl: "modules/dashboard/templates/widget-add.html",
                title: ' '
            }
        },
        onDashboardConfigRetrieved: function(data) {
            onDashboardConfigRetrieved(data);
        }
    });

    var widgetsPerRoles = [];
    var isEditMode = false;

    PrivacyConfigurationService.isDashboardBannerEnabled().then(function (response) {
        $scope.isDashboardBannerEnabled = response.data;
    },function(err){
        MessageService.errorAction();
    });

    var onDashboardConfigRetrieved = function(data) {
        DashboardService.getWidgetsPerRoles(function(widgets) {
            widgetsPerRoles = widgets;
        });
    };

    $scope.widgetFilter = function(widget, type) {
        var result = false;
        angular.forEach(widgetsPerRoles, function(w) {
            if (type === w.widgetName) {
                result = true;
            }
        });
        return result;
    };

    $scope.saveDashboard = function(nextUrl) {

        var params = {
            url: nextUrl.name
        };

        var modalInstance = $modal.open({
            animation: true,
            size: 'md',
            backdrop: 'static',
            resolve: {
                params: function() {
                    return params;
                }
            },
            templateUrl: "modules/dashboard/templates/save-dashboard.html",
            controller: [ '$scope', '$modalInstance', 'params', function($scope, $modalInstance, params) {
                var saveUser = false;
                $scope.onClickSave = function() {
                    saveUser = true;
                    isEditMode = false;
                    $modalInstance.close(saveUser);
                    $state.go(params.url);
                };
                $scope.onClickCancel = function() {
                    $modalInstance.dismiss();
                };
                $scope.onClickDoNotSave = function() {
                    saveUser = false;
                    isEditMode = false;
                    $modalInstance.close(saveUser);
                    $state.go(params.url);
                };

            } ]
        });

        modalInstance.result.then(function(result) {
            if (result) {
                $rootScope.$broadcast('adfDashboardRetrieveChangedModel');
            }
        }, function(error) {

        });
    };

    $scope.$on('adfDashboardChanged', function(event, name, model) {
        isEditMode = false;
        DashboardService.saveConfig({
            dashboardConfig: angular.toJson(model),
            module: "DASHBOARD"
        });
        $scope.dashboard.model = model;
    });

    $scope.$on('adfDashboardEditMode', function() {
        isEditMode = true;
    });

    $scope.$on('adfDashboardEditsCancelled', function() {
        isEditMode = false;
    });

    $scope.$on("$stateChangeStart", function(event, nextUrl, currentUrl) {
        if (isEditMode) {
            event.preventDefault();
            $scope.saveDashboard(nextUrl);
        }
    });
} ]);
