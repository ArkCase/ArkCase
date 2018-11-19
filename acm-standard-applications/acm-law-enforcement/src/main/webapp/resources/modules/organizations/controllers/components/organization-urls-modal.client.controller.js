angular.module('organizations').controller('Organizations.UrlsModalController', [ '$scope', '$translate', '$modalInstance', 'Object.LookupService', 'params', 'Mentions.Service', function($scope, $translate, $modalInstance, ObjectLookupService, params, MentionsService) {

    ObjectLookupService.getContactMethodTypes().then(function(contactMethodTypes) {
        $scope.urlTypes = _.find(contactMethodTypes, {
            key: 'url'
        }).subLookup;
        return contactMethodTypes;
    });

    $scope.url = params.url;
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
            url: $scope.url,
            isDefault: $scope.isDefault,
            isEdit: $scope.isEdit,
            emailAddresses: $scope.emailAddresses,
            usersMentioned: $scope.usersMentioned
        });
    };
} ]);