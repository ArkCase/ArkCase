angular.module('organizations').controller('Organizations.PhonesModalController', ['$scope', '$translate', '$modalInstance', 'Object.LookupService', 'params', '$timeout', 'Mentions.Service', 'PhoneValidationService', function ($scope, $translate, $modalInstance, ObjectLookupService, params, $timeout, MentionsService, PhoneValidationService) {

    ObjectLookupService.getContactMethodTypes().then(function(contactMethodTypes) {
        $scope.phoneTypes = _.find(contactMethodTypes, {
            key: 'phone'
        }).subLookup;
        return contactMethodTypes;
    });

    $scope.phone = params.phone;
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
            phone: $scope.phone,
            isDefault: $scope.isDefault,
            isEdit: $scope.isEdit,
            emailAddresses: $scope.params.emailAddresses,
            usersMentioned: $scope.params.usersMentioned
        });
    };

    $scope.validateInput = function () {
        PhoneValidationService.getPhoneRegex().then(function (response) {
            $timeout(function () {
                var validateObject = PhoneValidationService.validateInput($scope.phone.value, response.data);
                $scope.phone.value = validateObject.inputValue;
                $scope.showPhoneError = validateObject.showPhoneError;
            }, 0);
        });
    };
} ]);