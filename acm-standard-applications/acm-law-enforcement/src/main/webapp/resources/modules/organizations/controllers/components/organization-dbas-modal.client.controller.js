angular.module('organizations').controller('Organizations.DBAsModalController', [ '$scope', '$translate', '$modalInstance', 'Object.LookupService', 'params', 'Mentions.Service', function($scope, $translate, $modalInstance, ObjectLookupService, params, MentionsService) {
    ObjectLookupService.getDBAsTypes().then(function (response) {
        $scope.dbasTypes = response;
        $scope.defaultDbasType = ObjectLookupService.getPrimaryLookup($scope.dbasTypes);
    });

    $scope.dba = params.dba;
    $scope.isEdit = params.isEdit;
    $scope.isDefault = params.isDefault;
    $scope.hideNoField = params.isDefault;

    if ($scope.defaultDbasType != null && $scope.dba == null) {
        $scope.dba.type = $scope.defaultAlias.key;
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
            dba: $scope.dba,
            isDefault: $scope.isDefault,
            isEdit: $scope.isEdit,
            emailAddresses: $scope.params.emailAddresses,
            usersMentioned: $scope.params.usersMentioned
        });
    };
} ]);