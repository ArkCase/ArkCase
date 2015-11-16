'use strict';
angular.module('profile').service('Profile.UserInfoService', function ($http, $q, Authentication) {
    return({
        getUserInfo: getUserInfo,
        updateUserInfo:updateUserInfo
    });
    function getUserInfo() {
        var deferred = $q.defer();
        Authentication.queryUserInfo({}
            , function (userInfo) {
                var user = userInfo.userId;
                if(user){
                    var request = $http({
                        method: 'GET',
                        cache: true,
                        url: 'proxy/arkcase/api/latest/plugin/profile/get/' + user
                    }).then(
                        function successCallback(response) {
                            deferred.resolve(response.data);
                        },
                        function errorCallback(response) {
                            if (!angular.isObject(response.data) || !response.data.message) {
                                deferred.reject('An unknown error occurred.');
                            }
                            deferred.reject(response.data.message);
                        }
                    );
                }
            }
        );
        return deferred.promise;
    };
    function updateUserInfo(data) {
        var deferred = $q.defer();
        $http({
            method: 'POST',
            processData: false,
            url: 'proxy/arkcase/api/latest/plugin/profile/userOrgInfo/set',
            data: data
        }).then(
            function successCallback(response) {
                deferred.resolve(response.data);
            },
            function errorCallback(response) {
                if (!angular.isObject(response.data) || !response.data.message) {
                    deferred.reject('An unknown error occurred.');
                }
                deferred.reject(response.data.message);
            }
        );
        return deferred.promise;
    };
});
