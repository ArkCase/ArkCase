/**
 * Created by nebojsha on 10/30/2015.
 */

angular.module('admin').controller('Admin.DashboardConfigController', [ '$scope', 'Admin.DashboardConfigService', 'ConfigService', '$q', 'UtilService', '$translate', 'MessageService', function($scope, DashboardConfigService, ConfigService, $q, Util, $translate, MessageService) {

    var tempWidgetsPromise = DashboardConfigService.getRolesByWidgets();
    var tempDashboardModulePromise = ConfigService.getModuleConfig("dashboard");

    $scope.filterArrayByProperty = filterArrayByProperty;
    $scope.fillList = fillList;
    //filter functions
    $scope.chooseAppRoleFilter = chooseAppRoleFilter;
    $scope.appRoleUnauthorizedFilter = appRoleUnauthorizedFilter;
    $scope.appRoleAuthorizedFilter = appRoleAuthorizedFilter;
    //scroll functions
    $scope.unauthorizedScroll = unauthorizedScroll;
    $scope.authorizedScroll = authorizedScroll;
    $scope.retrieveDataScroll = retrieveDataScroll;

    // Loaded data after the initialization
    var initWidgetsData = {
        "chooseObject": [],
        "selectedNotAuthorized": [],
        "selectedAuthorized": []
    };
    // Data to be displayed
    $scope.widgetsData = {
        "chooseObject": [],
        "selectedNotAuthorized": [],
        "selectedAuthorized": []
    };
    $scope.filterData = {
        "objectsFilter": $scope.chooseAppRoleFilter,
        "unauthorizedFilter": $scope.appRoleUnauthorizedFilter,
        "authorizedFilter": $scope.appRoleAuthorizedFilter
    };
    $scope.scrollLoadData = {
        "loadUnauthorizedScroll": $scope.unauthorizedScroll,
        "loadAuthorizedScroll": $scope.authorizedScroll
    };
    $scope.widgetsMap = [];
    var currentAuthRolesGroups = [];

    $q.all([ tempWidgetsPromise, tempDashboardModulePromise ]).then(function(payload) {
        angular.forEach(payload[0].data, function(widget) {
            var cfg = _.find(payload[1].components, {
                id: widget.widgetName
            });

            if (!Util.isEmpty(cfg)) {
                var element = new Object;
                element.name = cfg.title;
                element.key = widget.widgetName;

                initWidgetsData.chooseObject.push(element);
                $scope.widgetsData.chooseObject.push(element);
                $scope.widgetsMap[widget.widgetName] = widget;
            }
        });
        $scope.onObjSelect($scope.widgetsData.chooseObject[0]);
    });

    function fillList(listToFill, data) {
        _.forEach(data, function(item) {
            var element = {};
            element.key = item.name;
            element.name = item.name;
            listToFill.push(element);
        });
    }

    function filterArrayByProperty(filterWord, arrayFrom, property) {
        var result = [];
        if (!Util.isEmpty(filterWord)) {
            result = _.filter(arrayFrom, function(item) {
                return ($translate.instant(item[property]).toLowerCase().indexOf(filterWord.toLowerCase()) >= 0);
            });
        } else {
            result = angular.copy(arrayFrom);
        }
        return result;
    }

    function chooseAppRoleFilter(searchData) {
        $scope.widgetsData.chooseObject = filterArrayByProperty(searchData.filterWord, initWidgetsData.chooseObject, "name");
        if (_.isArray($scope.widgetsData.chooseObject) && $scope.widgetsData.chooseObject.length > 0) {
            $scope.onObjSelect($scope.widgetsData.chooseObject[0]);
        }
    }

    function appRoleUnauthorizedFilter(searchData) {
        var data = {
            isAuthorized: false,
            widget: $scope.lastSelectedWidget,
            filterWord: searchData.filterWord
        };
        DashboardConfigService.getRolesGroupsByName(data).then(function(response) {
            $scope.widgetsData.selectedNotAuthorized = [];
            $scope.fillList($scope.widgetsData.selectedNotAuthorized, response.data);
        });
    }

    function appRoleAuthorizedFilter(searchData) {
        var data = {
            isAuthorized: true,
            widget: $scope.lastSelectedWidget,
            filterWord: searchData.filterWord
        };
        DashboardConfigService.getRolesGroupsByName(data).then(function(response) {
            $scope.widgetsData.selectedAuthorized = [];
            $scope.fillList($scope.widgetsData.selectedAuthorized, response.data);
        });
    }

    function retrieveDataScroll(data, methodName, panelName) {
        DashboardConfigService[methodName](data).then(function(response) {
            $scope.fillList($scope.widgetsData[panelName], response.data);

            if (panelName === "selectedAuthorized") {
                currentAuthRolesGroups = [];
                _.forEach($scope.widgetsData[panelName], function(obj) {
                    currentAuthRolesGroups.push(obj.key);
                });
            }
        }, function() {
            $log.error('Error during calling the method ' + methodName);
        });
    }

    function authorizedScroll() {
        var data = {};
        data.widget = $scope.lastSelectedWidget;
        data.start = $scope.widgetsData.selectedAuthorized.length;
        data.isAuthorized = true;
        $scope.retrieveDataScroll(data, "getRolesGroupsByName", "selectedAuthorized");
    }

    function unauthorizedScroll() {
        var data = {};
        data.widget = $scope.lastSelectedWidget;
        data.start = $scope.widgetsData.selectedNotAuthorized.length;
        data.isAuthorized = false;
        $scope.retrieveDataScroll(data, "getRolesGroupsByName", "selectedNotAuthorized");
    }

    $scope.onObjSelect = function(selectedObject) {
        $scope.lastSelectedWidget = selectedObject;

        var data = {};
        data.role = selectedObject;
        data.isAuthorized = true;
        var unAuthorizedRolesGroupsForWidgetPromise = DashboardConfigService.getRolesGroups(data);
        data.isAuthorized = false;
        var authorizedRolesGroupsForWidgetPromise = DashboardConfigService.getRolesGroups(data);

        //wait all promises to resolve
        $q.all([ unAuthorizedRolesGroupsForWidgetPromise, authorizedRolesGroupsForWidgetPromise ]).then(function(payload) {
            $scope.widgetsData.selectedAuthorized = [];
            $scope.widgetsData.selectedNotAuthorized = [];
            currentAuthRolesGroups = [];
            _.forEach(payload[0].data, function(item) {
                var element = {};
                element.key = item.name;
                element.name = item.name;
                $scope.widgetsData.selectedAuthorized.push(element);
                currentAuthRolesGroups.push(element.key);
            });

            fillList($scope.widgetsData.selectedNotAuthorized, payload[1].data);
        });
    };

    $scope.onAuthRoleSelected = function(selectedObject, authorized, notAuthorized) {
        var toBeAdded = [];
        var toBeRemoved = [];
        var deferred = $q.defer();

        //get roles which needs to be added
        _.forEach(authorized, function(group) {
            if (currentAuthRolesGroups.indexOf(group.key) === -1) {
                toBeAdded.push(group.key);
            }
        });
        _.forEach(notAuthorized, function(group) {
            if (currentAuthRolesGroups.indexOf(group.key) !== -1) {
                toBeRemoved.push(group.key);
            }
        });

        //perform adding on server
        if (toBeAdded.length > 0) {
            currentAuthRolesGroups = currentAuthRolesGroups.concat(toBeAdded);

            DashboardConfigService.addRoleGroupToWidget(selectedObject.key, toBeAdded, true).then(function(data) {
                MessageService.succsessAction();
            }, function() {
                //error adding group
                MessageService.errorAction();
            });
            return deferred.promise;
        }

        if (toBeRemoved.length > 0) {
            _.forEach(toBeRemoved, function(element) {
                currentAuthRolesGroups.splice(currentAuthRolesGroups.indexOf(element), 1);
            });

            DashboardConfigService.removeRoleGroupToWidget(selectedObject.key, toBeRemoved, false).then(function(data) {
                MessageService.succsessAction();
            }, function() {
                //error adding group
                MessageService.errorAction();
            });
            return deferred.promise;
        }
    };
} ]);