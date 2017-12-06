'use-strict';

angular.module('audit').controller('Audit.SelectionController', ['$scope', 'Object.LookupService',
    function ($scope, ObjectLookupService) {

        ObjectLookupService.getLookupByLookupName("auditReportNames").then(function (auditReportNames) {
            $scope.auditReportNames = auditReportNames;
            return auditReportNames;
        });

        $scope.auditReportName = "";
        $scope.$watchGroup(['selectId','auditReportName'], function(){
            $scope.$emit('send-type-id', $scope.auditReportName, $scope.selectId);
        });

    }
]);