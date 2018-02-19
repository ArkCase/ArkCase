'use strict';

angular.module('admin').factory('Admin.LdapUserManagementService', [ '$resource', '$http', function($resource, $http) {
    return ({
        queryGroupsByDirectory : queryGroupsByDirectory,
        queryAdhocGroups : queryAdhocGroups,
        addGroupsToUser : addGroupsToUser,
        removeGroupsFromUser : removeGroupsFromUser,
        cloneUser : cloneUser,
        deleteUser : deleteUser,
        getUsersFiltered : getUsersFiltered,
        getNUsers : getNUsers,
        getGroupsFiltered : getGroupsFiltered,
        getGroupsForUser : getGroupsForUser
    });

    function queryGroupsByDirectory(directory, n) {
        return $http({
            method : 'GET',
            url : 'api/latest/users/' + directory + '/groups',
            params : {
                n : (n ? n : 10000)
            }
        });
    }

    function queryAdhocGroups() {
        return $http({
            method : 'GET',
            url : 'api/latest/users/groups/adhoc',
            params : {
                n : 10000
            }
        });
    }

    function addGroupsToUser(user, groups, directory) {
        var url = 'api/latest/ldap/' + directory + '/manage/' + user + '/groups';
        return $http({
            method : 'PUT',
            url : url,
            data : groups
        });
    }

    function removeGroupsFromUser(user, groups, directory) {
        var groupNames = {};
        groupNames['groupNames'] = groups;
        var url = 'api/latest/ldap/' + directory + '/manage/' + user + '/groups';
        return $http({
            method : 'DELETE',
            url : url,
            params : groupNames
        });
    }

    function cloneUser(data) {
        var url = 'api/latest/ldap/' + data.selectedUser.directory + '/users/' + data.selectedUser.key;
        return $http({
            method : 'POST',
            url : url,
            data : data.user
        });
    }

    function deleteUser(user) {
        var url = 'api/latest/ldap/' + user.directory + '/users/' + user.key;
        return $http({
            method : 'DELETE',
            url : url
        });
    }

    /**
     * @ngdoc method
     * @name getUsersFiltered
     * @methodOf services.service:Admin.LdapUserManagementService
     *
     * @description
     * Filtered list of users:
     *      Filtered by: filterWord
     *      Start position: 0
     *      End position: n
     *
     * @returns List of users
     */
    function getUsersFiltered(data) {
        var url = 'api/latest/users/';
        return $http({
            method : 'GET',
            url : url,
            params : {
                fq : data.filterWord,
                n : (data.n ? data.n : 50),
                start : 0
            }
        });
    }

    /**
     * @ngdoc method
     * @name getNUsers
     * @methodOf services.service:Admin.LdapUserManagementService
     *
     * @description
     * List of N users:
     *      Start position: 0
     *      End position: n
     *
     * @returns List of users
     */
    function getNUsers(data) {
        var url = 'api/latest/users/';
        return $http({
            method : 'GET',
            url : url,
            params : {
                start : 0,
                n : (data.n ? data.n : 50)
            }
        });
    }

    /**
     * @ngdoc method
     * @name getGroupsFiltered
     * @methodOf services.service:Admin.LdapUserManagementService
     *
     * @description
     * List of N groups:
     *      Filtered by: filterWord
     *      Start position: 0
     *      Member id key: member_id.key
     *      End position: n
     *      Is the user part of the group: authorized/unauthorized
     *
     * @returns List of filtered authorized/unauthorized groups
     */
    function getGroupsFiltered(data) {
        return $http({
            method : 'GET',
            url : 'api/latest/users/' + data.member_id.key + '/groups/',
            params : {
                n : (data.n ? data.n : 50),
                q : encodeURIComponent(data.member_id.key),
                fq : encodeURIComponent(data.filterWord),
                authorized : data.isAuthorized
            }
        });
    }

    /**
     * @ngdoc method
     * @name getGroupsForUser
     * @methodOf services.service:Admin.LdapUserManagementService
     *
     * @description
     * List of N groups:
     *      Start position: 0
     *      Member id key: member_id.key
     *      End position: n
     *      Is the user part of the group: authorized/unauthorized
     *
     * @returns List of all authorized/unauthorized groups
     */
    function getGroupsForUser(data) {
        return $http({
            method : 'GET',
            url : 'api/latest/users/' + data.member_id.key + '/groups/',
            params : {
                n : (data.n ? data.n : 18),
                authorized : data.isAuthorized
            }
        });
    }
} ]);
