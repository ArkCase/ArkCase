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

        $scope.$on('req-component-config', onConfigRequest);
        $scope.data = Data.getData();
        $scope.config = ConfigService.getModule({moduleId: 'reports'});

        // Retrieves the properties from the acm-reports-server-config.properties file
        var reportsConfig = LookupService.getConfig({name: 'acm-reports-server-config'});

        // Retrieves the properties from the acm-reports.properties file
        var reports = LookupService.getConfig({name: 'acm-reports'});

        $q.all([reportsConfig.$promise, reports.$promise, $scope.config.$promise])
            .then(function (data) {
                var reportsConfig = data[0].toJSON();
                $scope.data.reports = data[1].toJSON();
                // On some reason reports list contains URL and PORT info
                delete $scope.data.reports.PENTAHO_SERVER_URL;
                delete $scope.data.reports.PENTAHO_SERVER_PORT;
                $scope.data.reportsHost = reportsConfig['PENTAHO_SERVER_URL'];
                $scope.data.reportsPort = reportsConfig['PENTAHO_SERVER_PORT'];
                $scope.data.reportDateFormat = $scope.config.pentahoDateFormat;
                $scope.data.dateFormat = $scope.config.dateFormat;
                $scope.data.reportSelected = null;
            });

        $scope.generateReport = function () {
            $scope.reportUrl = BuildUrl.getUrl($scope.data);
        };

        function onConfigRequest(e, componentId) {
            $scope.config.$promise.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId});
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        }

    }
]);
