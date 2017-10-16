/**
 * Created by nebojsha on 10/30/2015.
 */

angular.module('admin')
    .controller('Admin.DashboardConfigController', ['$scope', 'Admin.DashboardConfigService', 'ConfigService', '$q', '$translate', 'MessageService'
    , function ($scope, dashboardConfigService, ConfigService, $q, $translate, messageService) {
    	
    	var deferred = $q.defer();
        var tempWidgetsPromise = dashboardConfigService.getRolesByWidgets();
        $scope.widgets = [];
        $scope.widgetsMap = [];
        tempWidgetsPromise.then(function (payload) {
            angular.forEach(payload.data, function (widget) {
                ConfigService.getModuleConfig("dashboard").then(function (data) {
                    var cfg = _.find(data.components, {id: widget.widgetName});

                    var element = new Object;
                    if (cfg != null) {
                        element.name = cfg.title;
                    } else {
                        element.name = widget.name;
                    }
                    element.key = widget.widgetName;
                    $scope.widgets.push(element);
                    $scope.widgetsMap[widget.widgetName] = widget;

                });
            });
        });

        $scope.onObjSelect = function (selectedObject, authorized, notAuthorized) {
            angular.forEach($scope.widgetsMap[selectedObject.key].widgetAuthorizedRoles, function (element) {
                authorized.push(element);
            });
            angular.forEach($scope.widgetsMap[selectedObject.key].widgetNotAuthorizedRoles, function (element) {
                notAuthorized.push(element);
            });
        };

        $scope.onAuthRoleSelected = function (selectedObject, authorized, notAuthorized) {
            $scope.widgetsMap[selectedObject.key].widgetAuthorizedRoles = authorized;
            $scope.widgetsMap[selectedObject.key].widgetNotAuthorizedRoles = notAuthorized;
            dashboardConfigService.authorizeRolesForWidget($scope.widgetsMap[selectedObject.key]).then(function() {
                deferred.resolve();
            }, function(){
                deferred.reject();
            });
            
            return deferred.promise;
        };
    }]);