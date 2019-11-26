angular.module('organizations').controller('Organizations.FaxesModalController', ['$scope', '$translate', '$modalInstance', 'Object.LookupService', 'params', 'Mentions.Service', '$timeout', 'PhoneValidationService', function ($scope, $translate, $modalInstance, ObjectLookupService, params, MentionsService, $timeout, PhoneValidationService) {

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
        PhoneValidationService.getPhoneRegex().then(function (response) {
            $timeout(function () {
                var validateObject = PhoneValidationService.validateInput($scope.fax.value, response.data);
                $scope.fax.value = validateObject.inputValue;
                $scope.showPhoneError = validateObject.showPhoneError;
            }, 0);
        });
    };

} ]);