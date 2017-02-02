'use strict';
angular.module('profile').service('Profile.ChangePasswordService', function ($http, $q, $modal) {
    return ({
        changePassword: changePassword,
        changeLdapPassword: changeLdapPassword
    });
    function changePassword(newPassword) {
        var request = $http({
            method: "POST",
            url: "api/v1/plugin/profile/savepassword",
            data: newPassword
        });
        return (request.then(handleSuccess, handleError));
    }

    function handleError(response) {
        if (!angular.isObject(response.data) || !response.data.message) {
            return ($q.reject("An unknown error occurred."));
        }
        var params = response.data;
        $modal.open({
            templateUrl: 'modules/profile/views/components/modalTemplates/profile-modal-password-info.client.view.html',
            size: 'sm',
            controller: ['$scope', 'params', function ($scope, params) {
                $scope.message = params.message;
            }],
            resolve: {
                params: params
            }
        });
        return ($q.reject(response.data.message));
    }

    function handleSuccess(response) {
        $modal.open({
            templateUrl: 'modules/profile/views/components/modalTemplates/profile-modal-password-info.client.view.html',
            size: 'sm',
            controller: ['$scope', function ($scope) {
                $scope.message = 'profile.modal.success';
            }]
        });
        return (response.data);
    }

    function changeLdapPassword(credentials) {
        var request = $http({
            method: "POST",
            url: "api/v1/users/changePassword",
            data: credentials
        });
        return (request.then(handleSuccess, handleError));
    }
});