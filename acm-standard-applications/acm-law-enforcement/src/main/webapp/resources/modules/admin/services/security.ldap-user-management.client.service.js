'use strict';

angular.module('admin').factory('Admin.LdapUserManagementService', ['$resource', '$http'
    , function ($resource, $http) {
        return ({
            queryGroupsByDirectory: queryGroupsByDirectory,
            queryAdhocGroups: queryAdhocGroups,
            addGroupsToUser: addGroupsToUser,
            removeGroupsFromUser: removeGroupsFromUser,
            cloneUser: cloneUser,
            deleteUser: deleteUser
        });

        function queryGroupsByDirectory(directory) {
            return $http({
                method: 'GET',
                url: 'api/latest/users/' + directory + '/groups',
                params: {
                    n: 10000
                }
            });
        }

        function queryAdhocGroups() {
            return $http({
                method: 'GET',
                url: 'api/latest/users/groups/adhoc',
                params: {
                    n: 10000
                }
            });
        }

        function addGroupsToUser(user, groups, directory) {
            var url = 'api/latest/ldap/' + directory + '/manage/' + user + '/groups';
            return $http({
                method: 'PUT',
                url: url,
                data: groups
            });
        }

        function removeGroupsFromUser(user, groups, directory) {
            var groupNames = {};
            groupNames['groupNames'] = groups;
            var url = 'api/latest/ldap/' + directory + '/manage/' + user + '/groups';
            return $http({
                method: 'DELETE',
                url: url,
                params: groupNames
            });
        }

        function cloneUser(user) {
            var url = 'api/latest/ldap/' + user.selectedUser.directory + '/users/' + user.selectedUser.key;
            return $http({
                method: 'POST',
                url: url,
                data: {
                    acmUser: user.acmUser,
                    password: user.password
                }
            });
        }

        function deleteUser(user) {
            var url = 'api/latest/ldap/' + user.directory + '/users/' + user.key;
            return $http({
                method: 'DELETE',
                url: url
            });
        }
    }
]);
