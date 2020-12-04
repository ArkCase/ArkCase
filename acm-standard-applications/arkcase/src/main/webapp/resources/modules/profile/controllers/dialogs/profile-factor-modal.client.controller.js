'use strict';

angular.module('profile').controller('Profile.MfaFactorModalController', [ '$scope', '$modalInstance', 'UtilService', 'Profile.MfaService', 'factorInfo', 'isEdit', 'availableFactors', function($scope, $modalInstance, Util, ProfileMfaService, factorInfo, isEdit, availableFactors) {
    $scope.modalInstance = $modalInstance;
    $scope.factorTypeLabel = ProfileMfaService.getFactorType(factorInfo);
    $scope.factorInfo = factorInfo;
    $scope.isEdit = isEdit;
    $scope.availableFactors = availableFactors;

    $scope.cancel = function() {
        $scope.modalInstance.dismiss('cancel');
    };

    $scope.confirm = function() {
        $scope.modalInstance.close($scope.factorInfo);
    };

    $scope.disableConfirm = function() {
        if (Util.isEmpty($scope.factorInfo) || Util.isEmpty($scope.factorInfo.factorType)) {
            return true;
        }

        // User must select email, sms, or token:software:totp factor type and provide associated metadata
        if (Util.compare('email', $scope.factorInfo.factorType)) {
            return Util.isEmpty($scope.factorInfo.profile) || Util.isEmpty($scope.factorInfo.profile.email);
        } else if (Util.compare('sms', $scope.factorInfo.factorType)) {
            return Util.isEmpty($scope.factorInfo.profile) || Util.isEmpty($scope.factorInfo.profile.phoneNumber) || $scope.factorInfo.profile.phoneNumber.length < 7;
        } else if (Util.compare('token:software:totp', $scope.factorInfo.factorType)) {
            return false;
        }

        return true;
    };
} ]);