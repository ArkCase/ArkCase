'use strict';

angular.module('profile').controller('Profile.GroupController', ['$scope','Profile.UserInfoService',
	function($scope, UserInfoService) {
        $scope.$emit('req-component-config', 'group');
       UserInfoService.getUserInfo().then(function(data) {
            $scope.profileGroups = data.groups;
        });
	}
]);
