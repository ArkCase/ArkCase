'use strict';

angular.module('admin').factory('Admin.LdapUserManagementService', [ '$resource', '$http', function($resource, $http) {
    return ({
        queryGroupsByDirectory : queryGroupsByDirectory,
        queryAdhocGroups : queryAdhocGroups,
        getAuthorizedGroupsFiltered : getAuthorizedGroupsFiltered,
        getAuthorizedGroupsForUser : getAuthorizedGroupsForUser,
        getUnauthorizedGroupsFiltered : getUnauthorizedGroupsFiltered,
        getUnauthorizedGroupsForUser : getUnauthorizedGroupsForUser,
        addGroupsToUser : addGroupsToUser,
        removeGroupsFromUser : removeGroupsFromUser,
        cloneUser : cloneUser,
        deleteUser : deleteUser,
        getUsersFiltered : getUsersFiltered,
        getNUsers : getNUsers
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

    /**
     * @ngdoc method
     * @name getAuthorizedGroupsFiltered
     * @methodOf services.service:Admin.LdapUserManagementService
     *
     * @description
     * List of N groups:
     *      Filtered by: filterWord
     *      Start position: 0
     *      Member id key: member_id.key
     *      End position: n
     *
     * @returns List of filtered authorized objects(groups)
     */
    function getAuthorizedGroupsFiltered(data) {
        return $http({
            method : 'GET',
            url : 'api/latest/users/get/authorized/groups/filtered',
            params : {
                n : (data.n ? data.n : 50),
                q : data.member_id.key,
                fq : data.filterWord
            }
        });
    }

    /**
     * @ngdoc method
     * @name getAuthorizedGroupsForUser
     * @methodOf services.service:Admin.LdapUserManagementService
     *
     * @description
     * List of N groups:
     *      Start position: 0
     *      Member id key: member_id.key
     *      End position: n
     *
     * @returns List of all authorized objects(groups)
     */
    function getAuthorizedGroupsForUser(data) {
        return $http({
            method : 'GET',
            url : 'api/latest/users/get/authorized/groups/foruser',
            params : {
                n : (data.n ? data.n : 50),
                q : data.member_id.key
            }
        });
    }

    /**
     * @ngdoc method
     * @name getUnauthorizedGroupsFiltered
     * @methodOf services.service:Admin.LdapUserManagementService
     *
     * @description
     * List of N groups:
     *      Filtered by: filterWord
     *      Start position: 0
     *      Member id key: member_id.key
     *      End position: n
     *
     * @returns List of filtered unauthorized objects(groups)
     */
    function getUnauthorizedGroupsFiltered(data) {
        return $http({
            method : 'GET',
            url : 'api/latest/users/get/unauthorized/groups/filtered',
            params : {
                n : (data.n ? data.n : 50),
                q : data.member_id.key,
                fq : data.filterWord
            }
        });
    }

    /**
     * @ngdoc method
     * @name getUnauthorizedGroupsForUser
     * @methodOf services.service:Admin.LdapUserManagementService
     *
     * @description
     * List of N groups:
     *      Start position: 0
     *      Member id key: member_id.key
     *      End position: n
     *
     * @returns List of all unauthorized objects(groups)
     */
    function getUnauthorizedGroupsForUser(data) {
        return $http({
            method : 'GET',
            url : 'api/latest/users/get/unauthorized/groups/foruser',
            params : {
                n : (data.n ? data.n : 50),
                q : data.member_id.key
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
     * @returns List of objects(users)
     */
    function getUsersFiltered(data) {
        var url = 'api/latest/ldap/search/users/filtered';
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
     * @returns List of objects(users)
     */
    function getNUsers(data) {
        var url = 'api/latest/ldap/search/n/users';
        return $http({
            method : 'GET',
            url : url,
            params : {
                n : (data.n ? data.n : 50),
                start : 0
            }
        });
    }
} ]);
