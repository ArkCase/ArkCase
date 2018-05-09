'use strict';

angular.module('admin').controller('Admin.ReportsConfigController', [ '$scope', 'Admin.ReportsConfigService', 'LookupService', '$q', '$sce',

function($scope, reportsConfigService, LookupService, $q, $sce) {
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
    $scope.reportsData.chooseObject = [];
    $scope.reportsMap = [];
    $scope.reportsConfig = null;
    $scope.reportDesignerUrl = null;

    function chooseReportsFilter(data) {
        reportsConfigService.getReportsByMatchingName(data).then(function(response) {
            $scope.reportsData.chooseObject = [];
            fillListReport($scope.reportsData.chooseObject, $scope.reportsMap, response.data);
            $scope.onObjSelect($scope.reportsData.chooseObject[0]);
        });
    }

    function reportsUnauthorizedFilter(data) {
        data.isAuthorized = false;
        data.report = $scope.lastSelectedReport;
        reportsConfigService.getGroupsForReportByName(data).then(function(response) {
            $scope.reportsData.selectedNotAuthorized = [];
            fillList($scope.reportsData.selectedNotAuthorized, response.data.response.docs);
        });
    }

    function reportsAuthorizedFilter(data) {
        data.isAuthorized = true;
        data.report = $scope.lastSelectedReport;
        reportsConfigService.getGroupsForReportByName(data).then(function(response) {
            $scope.reportsData.selectedAuthorized = [];
            fillList($scope.reportsData.selectedAuthorized, response.data.response.docs);
        });
    }

    function reportsScroll() {
        var data = {};
        data.report = $scope.lastSelectedReport;
        data.start = $scope.reportsData.chooseObject.length;
        reportsConfigService.getReportsPaged(data).then(function(response) {
            fillListReport($scope.reportsData.chooseObject, $scope.reportsMap, response.data);
        });
    }

    function reportsUnauthorizedScroll() {
        var data = {};
        data.report = $scope.lastSelectedReport;
        data.start = $scope.reportsData.selectedNotAuthorized.length;
        data.isAuthorized = false;
        $scope.retrieveDataScroll(data, "getGroupsForReport", "selectedNotAuthorized");
    }

    function reportsAuthorizedScroll() {
        var data = {};
        data.report = $scope.lastSelectedReport;
        data.start = $scope.reportsData.selectedAuthorized.length;
        data.isAuthorized = true;
        $scope.retrieveDataScroll(data, "getGroupsForReport", "selectedAuthorized");
    }

    function retrieveDataScroll(data, methodName, panelName) {
        reportsConfigService[methodName](data).then(function(response) {
            if (_.isArray(response.data)) {
                $scope.fillList($scope.reportsData[panelName], response.data);
            } else {
                $scope.fillList($scope.reportsData[panelName], response.data.response.docs);
            }
        }, function() {
            $log.error('Error during calling the method ' + methodName);
        });
    }

    $scope.execute = function() {
        var tempReportsPromise = reportsConfigService.getReportsPaged({});
        var promiseServerConfig = LookupService.getConfig("acm-reports-server-config");
        var tempReportsUserGroupsPromise = reportsConfigService.getReportsUserGroups();
        //wait all promises to resolve
        $q.all([ tempReportsPromise, promiseServerConfig, tempReportsUserGroupsPromise ]).then(function(payload) {
            $scope.reportsData.chooseObject = [];
            //get all reports
            fillListReport($scope.reportsData.chooseObject, $scope.reportsMap, payload[0].data);

            $scope.reportsConfig = payload[1];

            $scope.reportsUserGroups = payload[2].data;

            var url = $scope.reportsConfig['PENTAHO_SERVER_URL'] + '/pentaho';
            $scope.reportDesignerUrl = $sce.trustAsResourceUrl(url);
            $scope.onObjSelect($scope.reportsData.chooseObject[0]);
        });
    };

    $scope.execute();

    function fillList(listToFill, data) {
        _.forEach(data, function(obj) {
            var element = {};
            element.name = obj.object_id_s;
            element.key = obj.object_id_s;
            listToFill.push(element);
        });
    }

    function fillListReport(listToFill, mapToFill, data) {
        angular.forEach(data, function(report) {
            var element = new Object;
            element.name = report["title"];
            element.key = report["propertyName"];
            listToFill.push(element);
            mapToFill[report["propertyName"]] = report;
        });
    }

    //callback function when report is selected
    $scope.onObjSelect = function(selectedObject) {
        $scope.reportsData.selectedAuthorized = [];
        $scope.reportsData.selectedNotAuthorized = [];

        var data = {};
        data.report = selectedObject;
        $scope.lastSelectedReport = [];
        $scope.lastSelectedReport = selectedObject;
        data.isAuthorized = false;
        var unAuthorizedGroupsForReport = reportsConfigService.getGroupsForReport(data);
        data.isAuthorized = true;
        var authorizedGroupsForReport = reportsConfigService.getGroupsForReport(data);
        $q.all([ authorizedGroupsForReport, unAuthorizedGroupsForReport ]).then(function(result) {
            //set authorized groups
            fillList($scope.reportsData.selectedAuthorized, result[0].data.response.docs);

            //set not authorized groups.
            fillList($scope.reportsData.selectedNotAuthorized, result[1].data.response.docs);
        });
    };

    //callback function when groups are moved
    $scope.onAuthRoleSelected = function(selectedObject, authorized, notAuthorized) {
        //get authorized user groups for selected report and save all reports user groups
        $scope.reportsUserGroups[selectedObject.key] = [];
        angular.forEach(authorized, function(element) {
            $scope.reportsUserGroups[selectedObject.key].push(element.key);
        });
        reportsConfigService.saveReportsUserGroups($scope.reportsUserGroups);

        //recreate reports array
        var reports = [];
        for ( var key in $scope.reportsMap) {
            if ($scope.reportsUserGroups && $scope.reportsUserGroups[key]) {
                var injected = $scope.reportsUserGroups[key].length === 0 ? false : true;
                $scope.reportsMap[key].injected = injected;
            }

            reports.push($scope.reportsMap[key]);
        }
        reportsConfigService.saveReports(reports).then(function() {
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

    //Raboti
    $scope.syncReports = function() {
        reportsConfigService.syncReports().then(function() {
            $scope.execute();
            deferred.resolve();
        }, function() {
            deferred.reject();
        });
    }
} ]);