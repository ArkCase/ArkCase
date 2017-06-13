'use strict';

angular.module('admin').factory('Admin.LdapUserManagementService', ['$resource', '$http', '$q',
    function ($resource, $http, $q) {
        return ({
            queryUsers: queryUsers,
            addGroupsToUser: addGroupsToUser,
            removeGroupsFromUser: removeGroupsFromUser
        });

        function queryUsers() {
            return $http({
                method: 'GET',
                url: 'api/latest/plugin/admin/user/management/users'
            });
        };

        function addGroupsToUser(user, groups) {
            var url = 'api/latest/plugin/admin/' + user + '/management/'+ groups.join();
            return $http({
                method: 'POST',
                url: url
            });
        };

        function removeGroupsFromUser(user, groups) {
            var url = 'api/latest/plugin/admin/' + user + '/management/'+ groups.join();
            return $http({
                method: 'DELETE',
                url: url
            });
        };
    }
]);
