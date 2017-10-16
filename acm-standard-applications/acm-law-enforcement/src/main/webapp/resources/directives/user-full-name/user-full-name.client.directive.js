'use strict';

angular.module('directives').directive('userFullName', ['UtilService', 'Profile.UserInfoService',
    function (Util, UserInfoService) {
        return {
            restrict: 'E',
            scope: {
                userid: '=',
                fallback: '@'
            },
            link: function (scope, element, attrs) {
                scope.$watch('userid', function () {
                    UserInfoService.getUserInfoById(scope.userid).then(function (userInfo) {
                        scope.userFullName = userInfo.fullName;
                    }, function (err) {
                        scope.userFullName = scope.userid;
                    })
                });
            }
            , template: "{{userFullName || fallback}}"
        };
    }
]);
