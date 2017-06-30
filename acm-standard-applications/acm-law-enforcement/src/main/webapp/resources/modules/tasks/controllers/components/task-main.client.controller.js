'use strict';

angular.module('tasks').controller('Tasks.MainController', ['$scope', 'Acm.StoreService', 'UtilService', 'Helper.DashboardService'
    , function ($scope, Store, Util, DashboardHelper) {

        new DashboardHelper.Dashboard({
            scope: $scope
            , moduleId: "tasks"
            , dashboardName: "TASK"
        });

        $scope.shallInclude = function (component) {
            if (component.enabled) {
                var componentsStore = new Store.Variable("TaskComponentsStore");
                var componentsToShow = Util.goodValue(componentsStore.get(), []);
                for (var i = 0; i < componentsToShow.length; i++) {
                    if (componentsToShow[i] == component.id) {
                        return true;
                    }
                }
            }
            return false;
        };

    }
])
;