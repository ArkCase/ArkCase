'use strict';

angular.module('audit').controller('Audit.SelectionController', [ '$scope', 'UtilService', 'Object.LookupService', function($scope, Util, ObjectLookupService) {

    ObjectLookupService.getLookupByLookupName("auditReportNames").then(function(auditReportNames) {
        $scope.auditReportNames = auditReportNames;
        var defaultAuditReportName = ObjectLookupService.getPrimaryLookup($scope.auditReportNames);
        if (defaultAuditReportName && !$scope.auditReportName) {
            $scope.auditReportName = defaultAuditReportName.key;
        }
        return auditReportNames;
    });

    $scope.auditReportName = "";
    $scope.$watchGroup([ 'selectId', 'auditReportName' ], function() {
        $scope.$emit('send-type-id', $scope.auditReportName, $scope.selectId);
    });

} ]);