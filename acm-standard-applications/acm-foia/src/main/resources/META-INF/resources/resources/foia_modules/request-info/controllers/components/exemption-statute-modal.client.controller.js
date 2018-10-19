'use strict';

angular.module('request-info').controller('RequestInfo.ExemptionStatuteModalController', [ '$scope', '$modalInstance', 'Object.LookupService', 'params', function($scope, $modalInstance, ObjectLookupService, params) {

    ObjectLookupService.getExemptionStatutes().then(function(exemptionStatute) {
        $scope.exemptionStatutes = exemptionStatute;
    });

    $scope.statute = { value: params.item.exemptionStatute };

    $scope.exemptionStatutesList = params.exemptionStatutesList;

    $scope.save = function() {
        $modalInstance.close({
            exemptionStatute: $scope.statute
        });

    };
    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
} ]);
