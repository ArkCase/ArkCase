'use-strict';

angular.module('audit').controller('Audit.SelectionController', ['$scope', 'UtilService', 'Object.LookupService',
    function ($scope, Util, ObjectLookupService) {

        ObjectLookupService.getLookupByLookupName("auditReportNames").then(function (auditReportNames) {
            $scope.auditReportNames = auditReportNames;
            return auditReportNames;
        });

        $scope.auditReportName = "";
        $scope.$watchGroup(['selectId','auditReportName'], function(){
            if(!Util.isEmpty($scope.auditReportName)){
                $scope.$emit('send-type-id', $scope.auditReportName, $scope.selectId, true);
            } else {
                $scope.$emit('send-type-id', $scope.auditReportName, $scope.selectId, false);
            }
        });

    }
]);