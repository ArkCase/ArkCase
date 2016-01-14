'use strict';

angular.module('dashboard.history', ['adf.provider'])
    .config(function (dashboardProvider) {
        dashboardProvider
            .widget('history', {
                title: 'History',
                description: 'Displays a pie chart showing the number of each history event type',
                controller: 'Dashboard.HistoryController',
                controllerAs: 'history',
                reload: true,
                templateUrl: 'modules/dashboard/views/components/history-widget.client.view.html'
            });
    })
    .controller('Dashboard.HistoryController', ['$scope', 'config', '$state', '$stateParams', '$translate', 'Dashboard.DashboardService', 'Object.AuditService', 'Helper.ObjectBrowserService', 'UtilService',
        function ($scope, config, $state, $stateParams, $translate, DashboardService, ObjectAuditService, HelperObjectBrowserService, Util) {

            var vm = this;

            var promiseQueryAudit;

            var currentObjectId = HelperObjectBrowserService.getCurrentObjectId();
            if (Util.goodPositive(currentObjectId, false)) {
                var promiseQueryAudit = ObjectAuditService.queryAudit($stateParams.type, currentObjectId, Util.goodValue($scope.start, 0), Util.goodValue($scope.pageSize, 10), Util.goodMapValue($scope.sort, "by"), Util.goodMapValue($scope.sort, "dir")).then(function (data) {
                    var results = data.resultPage;
                    var eventsList = [];

                    results.forEach(function (result) {
                        var eventType = result.fullEventType;
                        if (eventType === "") {
                            //Do nothing
                        }
                        else {
                            if (eventsList.length === 0) {
                                eventsList.push({'eventName': eventType, 'count': 1});
                            } else {
                                if (_.find(eventsList, _.matchesProperty('eventName', eventType))) {
                                    var foundEvent = _.find(eventsList, _.matchesProperty('eventName', eventType));
                                    foundEvent.count++;
                                } else {
                                    eventsList.push({'eventName': eventType, 'count': 1});
                                }
                            }
                        }
                    });

                    if (eventsList.length > 0) {
                        var data = [];
                        var labels = [];

                        angular.forEach(eventsList, function (eventIter) {
                            labels.push(eventIter.eventName);
                            data.push(eventIter.count);
                        })
                        vm.showChart = data.length > 0 ? true : false;
                        vm.data = data;
                        vm.labels = labels;
                        vm.chartTitle = "";
                    }
                });
            }

        }
    ]);
