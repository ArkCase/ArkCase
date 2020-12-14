'use strict';

angular.module('adhoc-reports').controller('AdhocReportsController', ['$scope', '$q', '$sce', 'Acm.LoginService', 'ConfigService', 'LookupService'
    , function ($scope, $q, $sce, AcmLoginService, ConfigService, LookupService) {

        var promiseModuleConfig = ConfigService.getModuleConfig("adhoc-reports");
        var promiseServerConfig = LookupService.getConfig("acm-reports-server-config");

        $q.all([promiseModuleConfig, promiseServerConfig]).then(function (data) {
            $scope.config = data[0];
            var reportServerConfig = data[1];

            var host = reportServerConfig['report.plugin.PENTAHO_SERVER_URL'];
            var port = reportServerConfig['report.plugin.PENTAHO_SERVER_PORT'];
            var context = reportServerConfig['report.plugin.PENTAHO_ADHOC_REPORTS_CONTEXT'];
            var finalUrl = host + (port ? (":" + port) : "") + context;
            $scope.adhocReportsUrl = $sce.trustAsResourceUrl(finalUrl);
            AcmLoginService.setIsLoggedIntoPentaho(true);
        });
    }
]);