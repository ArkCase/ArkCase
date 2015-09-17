'use strict';

angular.module('profile').controller('Profile.GroupController', ['$scope','userInfoService',
	function($scope, userInfoService) {
        $scope.$emit('req-component-config', 'group');
       userInfoService.getUserInfo().then(function(data) {
            $scope.profileGroups = data.groups;
        });
	}
]);
