'use strict';

angular.module('admin').factory('Admin.LdapUserManagementService', ['$resource', '$http', '$q', 'Acm.StoreService',
    function ($resource, $http, $q, Store) {
        return ({
            queryGroupsByDirectory: queryGroupsByDirectory,
            addGroupsToUser: addGroupsToUser,
            removeGroupsFromUser: removeGroupsFromUser,
            cloneUser: cloneUser
        });

        function queryGroupsByDirectory(directory) {
            var cacheGroups = new Store.SessionData('groups' + direct);
            var groups = cacheGroups.get();
            return $http({
                method: 'GET',
                url: 'api/latest/users/directory/groups?directory=' + directory
            }).success(function (data) {

                if(!groups){
                    cacheGroups.set(data);
                    return data;
                }else {
                    if(groups.response.docs[0].directory !== data.response.docs[0].directory){
                        cacheGroups.clearCache('string', groups);
                        cacheGroups.set(data);
                        return data;
                    }else {
                        return groups;
                    }
                }
            });
        };

        function addGroupsToUser(user, groups, directory) {
            var url = 'api/latest/ldap/' + directory + '/manage/' + user +'/groups';
            return $http({
                method: 'PUT',
                url: url,
                data: groups
            });
        };

        function removeGroupsFromUser(user, groups, directory) {
            var url = 'api/latest/ldap/' + directory + '/manage/' + user +'/groups';
            return $http({
                method: 'DELETE',
                url: url,
                data: groups
            });
        };

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
        };
    }
]);
