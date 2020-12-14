'use strict';

angular.module('profile').controller('Profile.MfaDeleteFactorModalController', [ '$scope', '$modalInstance', '$translate', 'Profile.MfaService', 'factorInfo', function($scope, $modalInstance, $translate, ProfileMfaService, factorInfo) {
    $scope.modalInstance = $modalInstance;
    $scope.factorInfo = factorInfo;

    $scope.buildConfirmationMessage = function(factorInfo) {
        var messageLabel = $translate.instant('profile.mfa.deleteModal.confirmationMessage');
        var factorType = ProfileMfaService.getFactorType(factorInfo);
        var factorDetails = ProfileMfaService.getFactorDetails(factorInfo);
        return messageLabel + ' [' + factorType + ((factorDetails && factorDetails.length > 0) ? ': ' + factorDetails : '') + ']?';
    };

    $scope.cancel = function() {
        $scope.modalInstance.dismiss('cancel');
    };

    $scope.confirm = function() {
        $scope.modalInstance.close({
            deleteFactorConfirmed: true
        });
    };
} ]);