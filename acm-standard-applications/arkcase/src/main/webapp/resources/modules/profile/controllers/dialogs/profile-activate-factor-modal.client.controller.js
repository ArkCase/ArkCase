'use strict';

angular.module('profile').controller('Profile.MfaActivateFactorModalController', [ '$scope', '$modalInstance', '$translate', 'UtilService', 'Profile.MfaService', 'factorInfo', function($scope, $modalInstance, $translate, Util, ProfileMfaService, factorInfo) {
    $scope.modalInstance = $modalInstance;
    $scope.factorTypeLabel = ProfileMfaService.getFactorType(factorInfo);
    $scope.factorInfo = factorInfo;
    $scope.activateInfo = {
        factorId: factorInfo.id
    };

    $scope.cancel = function() {
        $scope.modalInstance.dismiss('cancel');
    };

    $scope.confirm = function() {
        $scope.modalInstance.close($scope.activateInfo);
    };

    $scope.disableConfirm = function() {
        return Util.isEmpty($scope.activateInfo) || Util.isEmpty($scope.activateInfo.factorId) || Util.isEmpty($scope.activateInfo.activationCode);
    };
} ]);