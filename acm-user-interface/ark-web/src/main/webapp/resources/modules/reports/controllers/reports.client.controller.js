'use strict';

/**
 * @ngdoc controller
 * @name reports.controller:ReportsController
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/tree/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/reports/controllers/reports.client.controller.js modules/reports/controllers/reports.client.controller.js}
 *
 * The Reports module main controller
 */

angular.module('reports').controller('ReportsController', ['$scope', 'ConfigService', 'LookupService', 'Reports.BuildUrl', '$q', 'Reports.Data',
    function ($scope, ConfigService, LookupService, BuildUrl, $q, Data) {

        $scope.config = ConfigService.getModule({moduleId: 'reports'});
        $scope.$on('req-component-config', onConfigRequest);

        $scope.data = Data.getData();


        function onConfigRequest(e, componentId) {
            $scope.config.$promise.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId})
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        }


        // Retrieves the properties from the acm-reports-server-config.properties file
        var reportsConfig = LookupService.getConfig({name: 'acm-reports-server-config'});

        // Retrieves the properties from the acm-reports.properties file
        var reports = LookupService.getConfig({name: 'acm-reports'});

        $q.all([reportsConfig.$promise, reports.$promise])
            .then(function (data) {
                $scope.reportsConfig = data[0].toJSON();
                $scope.reports = data[1].toJSON();
                $scope.reportsHost = $scope.reportsConfig['PENTAHO_SERVER_URL'];
                $scope.reportsPort = $scope.reportsConfig['PENTAHO_SERVER_PORT'];
                $scope.$broadcast('available-reports', $scope.reports);
                $scope.$broadcast('reports-data-retrieved', $scope.data);
            });

        $scope.generateReport = function () {
            $scope.reportUrl = BuildUrl.getUrl(
                $scope.reportsHost,
                $scope.reportsPort,
                $scope.reports[$scope.data.reportSelected],
                $scope.data.caseStateSelected,
                moment($scope.data.startDate).format($scope.config.dateFormat),
                moment($scope.data.endDate).format($scope.config.dateFormat),
                $scope.config.pentahoDateFormat);
        }
    }
]);