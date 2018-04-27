'use strict';

angular.module('admin').controller('Admin.ReportsConfigController',
        [ '$scope', 'Admin.ReportsConfigService', 'LookupService', '$q', '$sce', 'MessageService',

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
                "chooseObject" : [],
                "selectedNotAuthorized" : [],
                "selectedAuthorized" : []
            };
            $scope.filterData = {
                "objectsFilter" : $scope.chooseReportsFilter,
                "unauthorizedFilter" : $scope.reportsUnauthorizedFilter,
                "authorizedFilter" : $scope.reportsAuthorizedFilter
            };
            $scope.scrollLoadData = {
                "loadObjectsScroll" : $scope.reportsScroll,
                "loadUnauthorizedScroll" : $scope.reportsUnauthorizedScroll,
                "loadAuthorizedScroll" : $scope.reportsAuthorizedScroll
            };
            var currentAuthGroups = [];
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
                ReportsConfigService.getGroupsForReportByName(data).then(function(response) {
                    $scope.reportsData.selectedNotAuthorized = [];
                    fillList($scope.reportsData.selectedNotAuthorized, response.data.response.docs);
                });
            }

            function reportsAuthorizedFilter(data) {
                data.isAuthorized = true;
                data.report = $scope.lastSelectedReport;
                ReportsConfigService.getGroupsForReportByName(data).then(function(response) {
                    $scope.reportsData.selectedAuthorized = [];
                    fillList($scope.reportsData.selectedAuthorized, response.data.response.docs);
                });
            }

            function reportsScroll() {
                var data = {
                    report : $scope.lastSelectedReport,
                    start : $scope.reportsData.chooseObject.length
                };
                ReportsConfigService.getReportsPaged(data).then(function(response) {
                    fillListReport($scope.reportsMap, response.data, $scope.reportsData.chooseObject);
                });
            }

            function reportsUnauthorizedScroll() {
                var data = {
                    report : $scope.lastSelectedReport,
                    start : $scope.reportsData.selectedNotAuthorized.length,
                    isAuthorized : false
                };
                $scope.retrieveDataScroll(data, "getGroupsForReport", "selectedNotAuthorized");
            }

            function reportsAuthorizedScroll() {
                var data = {
                    report : $scope.lastSelectedReport,
                    start : $scope.reportsData.selectedAuthorized.length,
                    isAuthorized : true
                };
                $scope.retrieveDataScroll(data, "getGroupsForReport", "selectedAuthorized");
            }

            function retrieveDataScroll(data, methodName, panelName) {
                ReportsConfigService[methodName](data).then(function(response) {
                    if (_.isArray(response.data)) {
                        $scope.fillList($scope.reportsData[panelName], response.data);
                    } else {
                        $scope.fillList($scope.reportsData[panelName], response.data.response.docs);
                    }
                    if (panelName === "selectedAuthorized") {
                        currentAuthGroups = [];
                        _.forEach($scope.reportsData[panelName], function(obj) {
                            currentAuthGroups.push(obj.key);
                        });
                    }
                }, function() {
                    $log.error('Error during calling the method ' + methodName);
                });
            }

            $scope.execute = function() {
                var tempReportsPentahoPromise = ReportsConfigService.getReportsPaged({});
                var promiseServerConfig = LookupService.getConfig("acm-reports-server-config");
                var tempReportsUserGroupsPromise = ReportsConfigService.getReportsUserGroups();
                //wait all promises to resolve
                $q.all([ tempReportsPentahoPromise, promiseServerConfig, tempReportsUserGroupsPromise ]).then(function(payload) {
                    $scope.reportsData.chooseObject = [];
                    //get all reports
                    fillListReport($scope.reportsMap, payload[0].data, $scope.reportsData.chooseObject);

                    $scope.reportsConfig = payload[2];

                    $scope.reportsUserGroups = payload[3].data;

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
                $scope.lastSelectedReport = [];
                $scope.lastSelectedReport = selectedObject;
                data.isAuthorized = false;
                var unAuthorizedGroupsForReport = ReportsConfigService.getGroupsForReport(data);
                data.isAuthorized = true;
                var authorizedGroupsForReport = ReportsConfigService.getGroupsForReport(data);
                $q.all([ authorizedGroupsForReport, unAuthorizedGroupsForReport ]).then(function(result) {
                    currentAuthGroups = [];
                    //set authorized groups
                    _.forEach(result[0].data.response.docs, function(obj) {
                        var element = {};
                        element.name = obj.object_id_s;
                        element.key = obj.object_id_s;
                        $scope.reportsData.selectedAuthorized.push(element);
                        currentAuthGroups.push(element.key);
                    });

                    //set not authorized groups.
                    fillList($scope.reportsData.selectedNotAuthorized, result[1].data.response.docs);
                });
            };

            //callback function when groups are moved
            $scope.onAuthRoleSelected = function(selectedObject, authorized, notAuthorized) {
                var toBeAdded = [];
                var toBeRemoved = [];

                //get roles which needs to be added
                _.forEach(authorized, function(group) {
                    if (currentAuthGroups.indexOf(group.key) === -1) {
                        toBeAdded.push(group.key);
                    }
                });
                _.forEach(notAuthorized, function(group) {
                    if (currentAuthGroups.indexOf(group.key) !== -1) {
                        toBeRemoved.push(group.key);
                    }
                });
                //perform adding on server
                if (toBeAdded.length > 0) {
                    currentAuthGroups = currentAuthGroups.concat(toBeAdded);

                    ReportsConfigService.addGroupsToReport(selectedObject.key, toBeAdded).then(function(data) {
                        $scope.reCreateReports(selectedObject, data.data);
                        MessageService.succsessAction();
                    }, function() {
                        //error adding group
                        MessageService.errorAction();
                    });
                    return deferred.promise;
                }

                if (toBeRemoved.length > 0) {
                    _.forEach(toBeRemoved, function(element) {
                        currentAuthGroups.splice(currentAuthGroups.indexOf(element), 1);
                    });

                    ReportsConfigService.removeGroupsFromReport(selectedObject.key, toBeRemoved).then(function(data) {
                        $scope.reCreateReports(selectedObject, data.data);
                        MessageService.succsessAction();
                    }, function() {
                        //error adding group
                        MessageService.errorAction();
                    });
                    return deferred.promise;
                }

            };

            $scope.reCreateReports = function(selectedObject, authorized) {
                $scope.reportsUserGroups[selectedObject.key] = [];
                angular.forEach(authorized, function(element) {
                    $scope.reportsUserGroups[selectedObject.key].push(element);
                });
                ReportsConfigService.saveReportsUserGroups($scope.reportsUserGroups);

                //recreate reports array
                var reports = [];
                for ( var key in $scope.reportsMap) {
                    if ($scope.reportsUserGroups && $scope.reportsUserGroups[key]) {
                        var injected = $scope.reportsUserGroups[key].length === 0 ? false : true;
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

            //Raboti
            $scope.syncReports = function() {
                ReportsConfigService.syncReports().then(function() {
                    $scope.execute();
                    deferred.resolve();
                }, function() {
                    deferred.reject();
                });
            }
        } ]);