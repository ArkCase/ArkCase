angular.module('people')
    .controller('People.EmailsModalController', ['$scope', '$translate', '$modalInstance', 'Object.LookupService', 'params', 'Mentions.Service', 'Person.InfoService',
        function ($scope, $translate, $modalInstance, ObjectLookupService, params, MentionsService, PersonInfoService) {

            ObjectLookupService.getContactMethodTypes().then(function (contactMethodTypes) {
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

            $scope.checkExistingEmail = function () {
                if ($scope.email.value)
                    PersonInfoService.queryByEmail($scope.email.value).then(function (result) {
                        if (result.data.response.numFound > 0) {
                            $scope.isEmailTaken = true;
                        } else {
                            $scope.isEmailTaken = false;
                        }
                    });
            };

            $scope.onClickCancel = function () {
                $modalInstance.dismiss('Cancel');
            };

            $scope.onClickOk = function () {
                $modalInstance.close({
                    email: $scope.email,
                    isDefault: $scope.isDefault,
                    isEdit: $scope.isEdit,
                    emailAddresses: $scope.params.emailAddresses,
                    usersMentioned: $scope.params.usersMentioned
                });
            };
        }]);