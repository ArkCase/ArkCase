'use strict';

angular.module('profile').controller('ProfileController', ['$scope', 'ConfigService', 'passwordService', '$modal',
    function ($scope, ConfigService, passwordService, $modal) {
        $scope.config = ConfigService.getModule({moduleId: 'profile'});
        $scope.$on('req-component-config', onConfigRequest);

        function onConfigRequest(e, componentId) {
            $scope.config.$promise.then(function (config) {
                var componentConfig = _.find(config.components, {id: componentId})
                $scope.$broadcast('component-config', componentId, componentConfig);
            });
        }
        $scope.changePassword = function () {
            var newPassword = $("#newpass").val();
            var newPasswordAgain = $("#newpassagain").val();
            if (newPassword === '') {
                $modal.open({
                    template: 'New Password Can not be Empty',
                    size: 'sm'
                });
            }
            else if (newPasswordAgain === '') {
                $modal.open({
                    template: 'Please Re-Enter Your Password',
                    size: 'sm'
                });
            }
            else if (newPassword !== newPasswordAgain) {
                $modal.open({
                    template: 'Two Passwords Do Not Match, Enter Again',
                    size: 'sm'
                });
                $("#newpassagain").val('');
                $("#newpass").val('');
            }
            else {
                var data = '{"outlookPassword":' + '"' + newPassword + '"}';
                passwordService.changePassword(data);
                $("#changePassword").modal("hide");
                $("#newpassagain").val('');
                $("#newpass").val('');
            }

        };
    }
]);
angular.module('profile').run(function (editableOptions, editableThemes) {
    editableThemes.bs3.inputClass = 'input-sm';
    editableThemes.bs3.buttonsClass = 'btn-sm';
    editableOptions.theme = 'bs3';
});