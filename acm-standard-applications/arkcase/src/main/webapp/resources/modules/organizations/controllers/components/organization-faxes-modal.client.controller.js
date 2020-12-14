angular.module('organizations').controller('Organizations.FaxesModalController', ['$scope', '$translate', '$modalInstance', 'Object.LookupService', 'params', 'Mentions.Service', 'PhoneValidationService', function ($scope, $translate, $modalInstance, ObjectLookupService, params, MentionsService, PhoneValidationService) {

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

    var regEx = PhoneValidationService.getPhoneRegex().then(function (response) {
        var regExp = new RegExp(response.data);
        regEx = regExp;
    });

    $scope.validateInput = function() {
        var validateObject = PhoneValidationService.validateInput($scope.fax.value, regEx);
        $scope.fax.value = validateObject.inputValue;
        $scope.showPhoneError = validateObject.showPhoneError;

    };

} ]);