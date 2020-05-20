'use strict';

angular.module('admin').controller('Admin.ReportsConfigController', [ '$scope', 'Admin.ReportsConfigService', 'LookupService', '$q', '$sce', 'MessageService',

function($scope, ReportsConfigService, LookupService, $q, $sce, MessageService) {
    var deferred = $q.defer();

    $scope.fillList = fillList;
    $scope.fillListReport = fillListReport;
    $scope.retrieveDataScroll = retrieveDataScroll;
    //Filter
    $scope.chooseReportsFilter = chooseReportsFilter;
    $scope.reportsUnauthorizedFilter = reportsUnauthorizedFilter;
    $scope.reportsAuthorizedFilter = reportsAuthorizedFilter;
    //Scroll
    $scope.reportsScroll = reportsScroll;
    $scope.reportsUnauthorizedScroll = reportsUnauthorizedScroll;
    $scope.reportsAuthorizedScroll = reportsAuthorizedScroll;

    $scope.reportsData = {
        "chooseObject": [],
        "selectedNotAuthorized": [],
        "selectedAuthorized": []
    };
    $scope.filterData = {
        "objectsFilter": $scope.chooseReportsFilter,
        "unauthorizedFilter": $scope.reportsUnauthorizedFilter,
        "authorizedFilter": $scope.reportsAuthorizedFilter
    };
    $scope.scrollLoadData = {
        "loadObjectsScroll": $scope.reportsScroll,
        "loadUnauthorizedScroll": $scope.reportsUnauthorizedScroll,
        "loadAuthorizedScroll": $scope.reportsAuthorizedScroll
    };
    var currentAuthRoles = [];
    $scope.reportsData.chooseObject = [];
    $scope.reportsMap = [];
    $scope.reportsConfig = null;
    $scope.reportDesignerUrl = null;

    function chooseReportsFilter(data) {
        ReportsConfigService.getReportsByMatchingName(data).then(function(response) {
            $scope.reportsData.chooseObject = [];
            fillListReport($scope.reportsMap, response.data, $scope.reportsData.chooseObject);
            if (_.isArray($scope.reportsData.chooseObject) && $scope.reportsData.chooseObject.length > 0) {
                $scope.onObjSelect($scope.reportsData.chooseObject[0]);
            }
        });
    }

    function reportsUnauthorizedFilter(data) {
        data.isAuthorized = false;
        data.report = $scope.lastSelectedReport;
        ReportsConfigService.getRolesForReportByName(data).then(function(response) {
            $scope.reportsData.selectedNotAuthorized = [];
            fillList($scope.reportsData.selectedNotAuthorized, response.data);
        });
    }

    function reportsAuthorizedFilter(data) {
        data.isAuthorized = true;
        data.report = $scope.lastSelectedReport;
        ReportsConfigService.getRolesForReportByName(data).then(function(response) {
            $scope.reportsData.selectedAuthorized = [];
            fillList($scope.reportsData.selectedAuthorized, response.data);
        });
    }

    function reportsScroll() {
        var data = {
            report: $scope.lastSelectedReport,
            start: $scope.reportsData.chooseObject.length
        };
        ReportsConfigService.getReportsPaged(data).then(function(response) {
            fillListReport($scope.reportsMap, response.data, $scope.reportsData.chooseObject);
        });
    }

    function reportsUnauthorizedScroll() {
        var data = {
            report: $scope.lastSelectedReport,
            start: $scope.reportsData.selectedNotAuthorized.length,
            isAuthorized: false
        };
        $scope.retrieveDataScroll(data, "getRolesForReport", "selectedNotAuthorized");
    }

    function reportsAuthorizedScroll() {
        var data = {
            report: $scope.lastSelectedReport,
            start: $scope.reportsData.selectedAuthorized.length,
            isAuthorized: true
        };
        $scope.retrieveDataScroll(data, "getRolesForReport", "selectedAuthorized");
    }

    function retrieveDataScroll(data, methodName, panelName) {
        ReportsConfigService[methodName](data).then(function(response) {
            $scope.fillList($scope.reportsData[panelName], response.data);
            if (panelName === "selectedAuthorized") {
                currentAuthRoles = [];
                _.forEach($scope.reportsData[panelName], function(obj) {
                    currentAuthRoles.push(obj.key);
                });
            }
        }, function() {
            $log.error('Error during calling the method ' + methodName);
        });
    }

    $scope.execute = function() {
        var tempReportsPentahoPromise = ReportsConfigService.getReportsPaged({});
        var promiseServerConfig = LookupService.getConfig("acm-reports-server-config");
        var tempReportsRolesPromise = ReportsConfigService.getReportsRoles();

        //wait all promises to resolve
        $q.all([ tempReportsPentahoPromise, promiseServerConfig, tempReportsRolesPromise ]).then(function(payload) {
            $scope.reportsData.chooseObject = [];
            //get all reports
            fillListReport($scope.reportsMap, payload[0].data, $scope.reportsData.chooseObject);

            $scope.reportsConfig = payload[1];

            $scope.reportsRoles = payload[2].data;

            var url = $scope.reportsConfig['report.plugin.PENTAHO_SERVER_URL'] + '/pentaho';
            $scope.reportDesignerUrl = $sce.trustAsResourceUrl(url);
            $scope.onObjSelect($scope.reportsData.chooseObject[0]);
        });
    };

    $scope.execute();

    function fillList(listToFill, data) {
        _.forEach(data, function(obj) {
            var element = {};
            element.name = obj;
            element.key = obj;
            listToFill.push(element);
        });
    }

    function fillListReport(mapToFill, data, listToFill) {
        angular.forEach(data, function(report) {
            if (listToFill) {
                var element = new Object;
                element.name = report["title"];
                element.key = report["propertyName"];
                listToFill.push(element);
            }
            mapToFill[report["propertyName"]] = report;
        });
    }

    //callback function when report is selected
    $scope.onObjSelect = function(selectedObject) {
        $scope.reportsData.selectedAuthorized = [];
        $scope.reportsData.selectedNotAuthorized = [];

        var data = {};
        data.report = selectedObject;
        $scope.lastSelectedReport = selectedObject;
        data.isAuthorized = false;
        var unAuthorizedRolesForReport = ReportsConfigService.getRolesForReport(data);
        data.isAuthorized = true;
        var authorizedRolesForReport = ReportsConfigService.getRolesForReport(data);
        $q.all([ authorizedRolesForReport, unAuthorizedRolesForReport ]).then(function(result) {
            currentAuthRoles = [];
            //set authorized roles
            _.forEach(result[0].data, function(obj) {
                var element = {};
                element.name = obj;
                element.key = obj;
                $scope.reportsData.selectedAuthorized.push(element);
                currentAuthRoles.push(element.key);
            });

            //set not authorized roles.
            fillList($scope.reportsData.selectedNotAuthorized, result[1].data);
        });
    };

    //callback function when roles are moved
    $scope.onAuthRoleSelected = function(selectedObject, authorized, notAuthorized) {
        var toBeAdded = [];
        var toBeRemoved = [];

        //get roles which needs to be added
        _.forEach(authorized, function(role) {
            if (currentAuthRoles.indexOf(role.key) === -1) {
                toBeAdded.push(role.key);
            }
        });
        _.forEach(notAuthorized, function(role) {
            if (currentAuthRoles.indexOf(role.key) !== -1) {
                toBeRemoved.push(role.key);
            }
        });
        //perform adding on server
        if (toBeAdded.length > 0) {
            currentAuthRoles = currentAuthRoles.concat(toBeAdded);

            ReportsConfigService.addRolesToReport(selectedObject.key, toBeAdded).then(function(data) {
                MessageService.succsessAction();
            }, function() {
                //error adding role
                MessageService.errorAction();
            });
            return deferred.promise;
        }

        if (toBeRemoved.length > 0) {
            _.forEach(toBeRemoved, function(element) {
                currentAuthRoles.splice(currentAuthRoles.indexOf(element), 1);
            });

            ReportsConfigService.removeRolesFromReport(selectedObject.key, toBeRemoved).then(function(data) {
                MessageService.succsessAction();
            }, function() {
                //error adding role
                MessageService.errorAction();
            });
            return deferred.promise;
        }

    };

    $scope.reCreateReports = function(selectedObject, authorized) {
        $scope.reportsRoles[selectedObject.key] = [];
        angular.forEach(authorized, function(element) {
            $scope.reportsRoles[selectedObject.key].push(element);
        });
        ReportsConfigService.saveReportsRoles($scope.reportsRoles);

        //recreate reports array
        var reports = [];
        for ( var key in $scope.reportsMap) {
            if ($scope.reportsRoles && $scope.reportsRoles[key]) {
                var injected = $scope.reportsRoles[key].length === 0 ? false : true;
                $scope.reportsMap[key].injected = injected;
            }

            reports.push($scope.reportsMap[key]);
        }
        ReportsConfigService.saveReports(reports).then(function() {
            deferred.resolve();
        }, function() {
            deferred.reject();
        });

        return deferred.promise;
    };

    $scope.openPentaho = function() {
        if ($scope.reportDesignerUrl) {
            window.open($scope.reportDesignerUrl, '_blank');
        }
    };

    $scope.syncReports = function() {
        ReportsConfigService.syncReports().then(function() {
            $scope.execute();
            deferred.resolve();
        }, function() {
            deferred.reject();
        });
    }
    
    // sync reports once the view is open
    $scope.syncReports();
} ]);
