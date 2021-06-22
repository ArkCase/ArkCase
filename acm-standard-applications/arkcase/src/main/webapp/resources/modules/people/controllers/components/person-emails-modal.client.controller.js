angular.module('people')
    .controller('People.EmailsModalController', ['$scope', '$translate', '$modalInstance', 'Object.LookupService', 'params', 'Mentions.Service', 'Person.InfoService',
        function ($scope, $translate, $modalInstance, ObjectLookupService, params, MentionsService, PersonInfoService) {

            ObjectLookupService.getContactMethodTypes().then(function (contactMethodTypes) {
                $scope.emailTypes = _.find(contactMethodTypes, {
                    key: 'email'
                }).subLookup;
                return contactMethodTypes;
            });

            $scope.model = params;
            $scope.model.emailAddress = [];
            $scope.model.usersMentioned = [];
            $scope.hideNoField = params.isDefault;

            $scope.checkExistingEmail = function () {
                if ($scope.model.email.value)
                    PersonInfoService.queryByEmail($scope.model.email.value).then(function (result) {
                        $scope.isEmailTaken = result.data.response.numFound > 0;
                    });
            };

            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };

            $scope.onClickOk = function () {
                $modalInstance.close($scope.model);
            };
        }]);
