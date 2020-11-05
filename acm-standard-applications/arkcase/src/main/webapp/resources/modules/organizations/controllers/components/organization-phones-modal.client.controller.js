angular.module('organizations').controller('Organizations.PhonesModalController', ['$scope', '$translate', '$modalInstance', 'Object.LookupService', 'params', 'Mentions.Service', 'PhoneValidationService', function ($scope, $translate, $modalInstance, ObjectLookupService, params, MentionsService, PhoneValidationService) {

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

    var regEx = PhoneValidationService.getPhoneRegex().then(function (response) {
        var regExp = new RegExp(response.data);
        regEx = regExp;
    });

    $scope.validateInput = function() {
        var validateObject = PhoneValidationService.validateInput($scope.phone.value, regEx);
        $scope.phone.value = validateObject.inputValue;
        $scope.showPhoneError = validateObject.showPhoneError;

    };
} ]);