'use strict';

angular.module('admin').factory('Admin.GroupManagementService', [ '$resource', '$http', 'base64', function($resource, $http, base64) {
    return ({
        getGroups: getGroups,
        getAdHocGroups: getAdHocGroups,
        getGroupsAdhocFiltered: getGroupsAdhocFiltered,
        getAdhocGroupsForAdhocGroup: getAdhocGroupsForAdhocGroup,
        getAdhocGroupsForAdhocGroupFiltered: getAdhocGroupsForAdhocGroupFiltered,
        addGroupSubGroups: addGroupSubGroups,
        deleteGroupSubGroups: deleteGroupSubGroups
    });
    /**
     * @ngdoc method
     * @name getGroups
     * @methodOf services.service:Admin.LdapGroupManagementService
     *
     * @description
     * List of N groups:
     *      Start position: 0
     *      Member id key: member.key
     *      End position: n
     *      Is the user part of the group: authorized/unauthorized
     *
     * @returns List of all authorized/unauthorized groups
     */
    function getGroups(data) {
        return $http({
            method: 'GET',
            url: 'api/latest/users/groups/get',
            params: {
                n: (data.n ? data.n : 50),
                start: (data.start ? data.start : 0)
            }
        });
    }

    /**
     * @ngdoc method
     * @name getAdHocGroups
     * @methodOf services.service:Admin.LdapGroupManagementService
     *
     * @description
     * List of N groups:
     *      Start position: 0
     *      Member id key: member.key
     *      End position: n
     *      Is the user part of the group: authorized/unauthorized
     *
     * @returns List of all authorized/unauthorized groups
     */
    function getAdHocGroups(data) {
        return $http({
            method: 'GET',
            url: 'api/latest/users/groups/adhoc',
            params: {
                n: (data.n ? data.n : 50),
                start: (data.start ? data.start : 0)
            }
        });
    }

    /**
     * @ngdoc method
     * @name getGroupsAdhocFiltered
     * @methodOf services.service:Admin.LdapUserManagementService
     *
     * @description
     * List of groups:
     *      Start position: 0
     *      Member id key: member.key
     *      End position: n
     *      Is the user part of the group: authorized/unauthorized
     *      Filter word: fq
     *
     * @returns List of authorized/unauthorized groups
     */
    function getGroupsAdhocFiltered(data) {
        return $http({
            method: 'GET',
            url: 'api/latest/users/groups/adhoc',
            params: {
                n: (data.n ? data.n : 50),
                start: (data.start ? data.start : 0),
                fq: (data.filterWord ? data.filterWord : "")
            }
        });
    }

    /**
     * @ngdoc method
     * @name getAdhocGroupsForAdhocGroup
     * @methodOf services.service:Admin.LdapUserManagementService
     *
     * @description
     * List of groups:
     *      Start position: 0
     *      Member id key: member.key
     *      End position: n
     *      Is the user part of the group: authorized/unauthorized
     *
     * @returns List of authorized/unauthorized groups
     */
    function getAdhocGroupsForAdhocGroup(data) {
        return $http({
            method: 'GET',
            url: 'api/latest/users/' + encodeURIComponent(data.member.key) + '/groups/adhoc',
            cache: false,
            params: {
                n: (data.n ? data.n : 50),
                start: (data.start ? data.start : 0),
                authorized: data.isAuthorized,
                groupType: (data.member.type ? data.member.type : "")
            }
        });
    }

    /**
     * @ngdoc method
     * @name getAdhocGroupsForAdhocGroupFiltered
     * @methodOf services.service:Admin.LdapUserManagementService
     *
     * @description
     * List of groups:
     *      Start position: 0
     *      Member id key: member.key
     *      End position: n
     *      Is the user part of the group: authorized/unauthorized
     *      Filter word: fq
     *
     * @returns List of authorized/unauthorized groups
     */
    function getAdhocGroupsForAdhocGroupFiltered(data) {
        return $http({
            method: 'GET',
            url: 'api/latest/users/' + data.member.key + '/groups/adhoc',
            params: {
                n: (data.n ? data.n : 50),
                start: (data.start ? data.start : 0),
                authorized: data.isAuthorized,
                fq: (data.filterWord ? data.filterWord : ""),
                groupType: (data.member.type ? data.member.type : "")
            }
        });
    }

    /**
     * @ngdoc method
     * @name addGroupSubGroups
     * @methodOf services.service:Admin.LdapUserManagementService
     *
     * @description
     * Save :
     *      Parent id: parentId
     *      Groups : sub-groups
     *
     * @returns List of groups
     */
    function addGroupSubGroups(data) {
        var parentId = base64.urlencode(data.parentId);
        return $http({
            method: 'POST',
            url: 'api/latest/users/group/' + parentId,
            headers: {
                "Content-Type": "application/json"
            },
            data: data.groups
        });
    }

    /**
     * @ngdoc method
     * @name deleteGroupSubGroups
     * @methodOf services.service:Admin.LdapUserManagementService
     *
     * @description
     * Save :
     *      Parent id: parentId
     *      Groups : sub-groups
     *
     * @returns List of groups
     */
    function deleteGroupSubGroups(data) {
        var parentId = base64.urlencode(data.parentId);
        return $http({
            method: 'DELETE',
            url: 'api/latest/users/groups/' + parentId,
            headers: {
                "Content-Type": "application/json"
            },
            data: data.groups
        });
    }
} ]);
