angular.module('people').controller('People.EmailsModalController', [ '$scope', '$translate', '$modalInstance', 'Object.LookupService', 'params', 'Mentions.Service', function($scope, $translate, $modalInstance, ObjectLookupService, params, MentionsService) {

    ObjectLookupService.getContactMethodTypes().then(function(contactMethodTypes) {
        $scope.emailTypes = _.find(contactMethodTypes, {
            key: 'email'
        }).subLookup;
        return contactMethodTypes;
    });

    $scope.email = params.email;
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
            email: $scope.email,
            isDefault: $scope.isDefault,
            isEdit: $scope.isEdit,
            emailAddresses: $scope.params.emailAddresses,
            usersMentioned: $scope.params.usersMentioned
        });
    };
} ]);