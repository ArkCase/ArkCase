angular.module('people').controller('Person.AliasesModalController', [ '$scope', '$translate', '$modalInstance', 'Object.LookupService', 'params', 'Mentions.Service', function($scope, $translate, $modalInstance, ObjectLookupService, params, MentionsService) {
    ObjectLookupService.getAliasTypes().then(function (response) {
        $scope.aliasTypes = response;
        $scope.defaultAlias = ObjectLookupService.getPrimaryLookup($scope.aliasTypes);

    });

    $scope.alias = params.alias;
    $scope.isEdit = params.isEdit;
    $scope.isDefault = params.isDefault;
    $scope.hideNoField = params.isDefault;

    if ($scope.defaultAlias != null && $scope.alias == null) {
        $scope.alias.aliasType = $scope.defaultAlias.key;
    }

    // --------------  mention --------------
    $scope.params = {
        emailAddresses: [],
        usersMentioned: []
    };

    $scope.onClickCancel = function () {
        $modalInstance.dismiss('Cancel');
    };
    $scope.onClickOk = function() {
        $modalInstance.close({
            alias: $scope.alias,
            isDefault: $scope.isDefault,
            isEdit: $scope.isEdit,
            emailAddresses: $scope.params.emailAddresses,
            usersMentioned: $scope.params.usersMentioned
        });
    };
} ]);