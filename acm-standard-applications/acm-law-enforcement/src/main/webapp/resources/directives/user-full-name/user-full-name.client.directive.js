'use strict';

angular.module('directives').directive('userFullName', [ 'UtilService', 'Profile.UserInfoService', function(Util, UserInfoService) {
    return {
        restrict: 'E',
        scope: {
            userid: '=',
            fallback: '@'
        },
        link: function(scope, element, attrs) {
            scope.$watch('userid', function() {
                scope.userFullName = scope.userid;
                UserInfoService.queryUserById(scope.userid).then(function (user) {
                    if (user.response.numFound > 0) {
                        scope.userFullName = user.response.docs[0].name;
                    }
                })
            });
        },
        template: "{{userFullName || fallback}}"
    };
} ]);
