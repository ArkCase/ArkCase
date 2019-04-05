angular.module('organizations').controller('Organizations.FaxesModalController', [ '$scope', '$translate', '$modalInstance', 'Object.LookupService', 'params', 'Mentions.Service', function($scope, $translate, $modalInstance, ObjectLookupService, params, MentionsService) {

    ObjectLookupService.getContactMethodTypes().then(function(contactMethodTypes) {
        $scope.faxTypes = _.find(contactMethodTypes, {
            key: 'fax'
        }).subLookup;
        return $scope.faxTypes;
    });

    $scope.fax = params.fax;
    $scope.isEdit = params.isEdit;
    $scope.isDefault = params.isDefault;
    $scope.hideNoField = params.isDefault;

    // --------------  mention --------------
    $scope.params = {
        emailAddresses: [],
        usersMentioned: []
    };

    $scope.onClickCancel = function() {
        $modalInstance.dismiss('Cancel');
    };
    $scope.onClickOk = function() {
        $modalInstance.close({
            fax: $scope.fax,
            isDefault: $scope.isDefault,
            isEdit: $scope.isEdit,
            emailAddresses: $scope.params.emailAddresses,
            usersMentioned: $scope.params.usersMentioned
        });
    };

    $scope.validateInput = function () {
        var regex = /^\d{3}[\-]\d{3}[\-]\d{4}$/;
        var value = $scope.fax.value;
        if (regex.test(value)) {
            $scope.showPhoneError = false;
            $scope.fax.value = value;
        } else {
            $scope.showPhoneError = true;
            $scope.fax.value = null;
        }
    };

} ]);