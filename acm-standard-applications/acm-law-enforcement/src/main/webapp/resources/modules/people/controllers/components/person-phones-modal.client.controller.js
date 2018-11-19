angular.module('people').controller('People.PhonesModalController', [ '$scope', '$translate', '$modalInstance', 'Object.LookupService', 'params', 'Mentions.Service', function($scope, $translate, $modalInstance, ObjectLookupService, params, MentionsService) {

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

    // ---------------------   mention   ---------------------------------
    $scope.emailAddresses = [];
    $scope.usersMentioned = [];

    // Obtains a list of all users in ArkCase
    MentionsService.getUsers().then(function (users) {
        $scope.people = users;
    });

    $scope.getMentionedUsers = function (item) {
        $scope.emailAddresses.push(item.email_lcs);
        $scope.usersMentioned.push('@' + item.name);
        return '@' + item.name;
    };
    // -----------------------  end mention   ----------------------------

    $scope.onClickCancel = function() {
        $modalInstance.dismiss('Cancel');
    };
    $scope.onClickOk = function() {
        $modalInstance.close({
            phone: $scope.phone,
            isDefault: $scope.isDefault,
            isEdit: $scope.isEdit,
            emailAddresses: $scope.emailAddresses,
            usersMentioned: $scope.usersMentioned
        });
    };

    $scope.validateInput = function() {
        var regex = /^\d{3}[\-]\d{3}[\-]\d{4}$/;
        var value = $scope.phone.value
        if (regex.test(value)) {
            $scope.showPhoneError = false;
            $scope.phone.value = value;
        } else {
            $scope.showPhoneError = true;
            $scope.phone.value = null;
        }
    };
} ]);