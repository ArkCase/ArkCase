/**
 * Created by nebojsha on 12/02/2015.
 */

'use strict';

/**
 * @ngdoc service
 * @name admin.service:Admin.OrganizationalHierarchyService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/admin/services/security.organizational-hierarchy.client.service.js modules/admin/services/security.organizational-hierarchy.client.service.js}
 *
 * The Admin.OrganizationalHierarchyService provides Organizational Hierarchy REST calls functionality
 */
angular.module('admin').service('Admin.OrganizationalHierarchyService', [ '$http', 'UtilService', 'Acm.StoreService', '$resource', 'base64', function($http, Util, Store, $resource, base64) {
    var Service = $resource('api/latest/users/group', {}, {
        /**
         * @ngdoc method
         * @name _getFilteredTopLevelGroups
         * @methodOf services:Admin.OrganizationalHierarchyService
         *
         * @description
         * Query for list of filtered top level groups from SOLR.
         *
         * @param {Object} params Map of input parameter.
         * @param {Number} params.start  Zero based index of result starts from
         * @param {Number} params.n max Number of list to return
         * @param {String} params.sort  Sort value. Allowed choice is based on backend specification
         * @param {String} params.roleFilters  Roles to filter groups returned
         * @param {String} params.groupSubtype  Type of groups to search for
         * @param {Function} onSuccess (Optional)Callback function of success query
         * @param {Function} onError (Optional) Callback function when fail
         *
         * @returns {Object} Object returned by $resource
         */
        _getFilteredTopLevelGroups: {
            method: 'GET',
            url: 'api/v1/service/functionalaccess/groups/toplevel',
            cache: false
        },

        _getInternalUsersConfig: {
            method: 'GET',
            url: 'api/latest/ldap/:directoryName/editingEnabled',
            cache: false
        },

        _addMemberToLdapGroup: {
            method: 'POST',
            url: 'api/latest/ldap/:directoryName/users'
        },

        _editLdapUser: {
            method: 'PUT',
            url: 'api/latest/ldap/:directoryName/users/:userId'
        },

        _cloneLdapUser: {
            method: 'POST',
            url: 'api/latest/ldap/:directoryName/users/:userId'
        },

        _deleteLdapUser: {
            method: 'DELETE',
            url: 'api/latest/ldap/:directoryName/users/:userId'
        },

        _deleteLdapUserMember: {
            method: 'DELETE',
            url: 'api/latest/ldap/:directoryName/manage/:userId/groups'
        },

        _addExistingMembersToLdapGroup: {
            method: 'POST',
            url: 'api/latest/ldap/:directoryName/groups/:groupName/users',
            isArray: true
        },

        _createLdapGroup: {
            method: 'POST',
            url: 'api/latest/ldap/:directoryName/groups'
        },

        _createLdapSubgroup: {
            method: 'POST',
            url: 'api/latest/ldap/:directoryName/groups/:parentGroupName'
        },

        _deleteLdapGroup: {
            method: 'DELETE',
            url: 'api/latest/ldap/:directoryName/groups/:groupName'
        },

        _removeLdapGroupMembership: {
            method: 'DELETE',
            url: 'api/latest/ldap/:directoryName/groups/:groupName/parent/:parentName'
        },

        _removeGroupMembership: {
            method: 'DELETE',
            url: 'api/latest/users/group/:groupName/parent/:parentName'
        }
    });

    Service.CacheNames = {
        INTERNAL_USER_CONFIG: "INTERNAL_USER_CONFIG",
        KEY: "enableEditingLdapUsers"
    };

    return ({
        getGroups: getGroups,
        getGroupsTopLevel: getGroupsTopLevel,
        getGroupsByName: getGroupsByName,
        getSubGroupsForGroup: getSubGroupsForGroup,
        getUsersForGroup: getUsersForGroup,
        addAdHocGroup: addAdHocGroup,
        addExistingAdHocSubGroup: addExistingAdHocSubGroup,
        addExistingAdHocSubGroups: addExistingAdHocSubGroups,
        saveMembers: saveMembers,
        removeMembers: removeMembers,
        removeGroup: removeGroup,
        removeGroupMembership: removeGroupMembership,
        setSupervisor: setSupervisor,
        getFilteredTopLevelGroups: getFilteredTopLevelGroups,
        isEnabledEditingLdapUsers: isEnabledEditingLdapUsers,
        addMemberToLdapGroup: addMemberToLdapGroup,
        editGroupMember: editGroupMember,
        addExistingMembersToLdapGroup: addExistingMembersToLdapGroup,
        createLdapGroup: createLdapGroup,
        createLdapSubgroup: createLdapSubgroup,
        deleteLdapUserMember: deleteLdapUserMember,
        deleteLdapGroup: deleteLdapGroup,
        removeLdapGroupMembership: removeLdapGroupMembership,
        getControlGroup: getControlGroup
    });

    /**
     * @ngdoc method
     * @name getGroups
     * @methodOf admin.service:Admin.OrganizationalHierarchyService
     *
     * @description
     * Performs retrieving all groups
     *
     * @returns {HttpPromise} Future info about groups
     */
    function getGroups() {
        return $http({
            method: 'GET',
            url: 'api/latest/users/groups/get?n=50&s=create_date_tdt desc'
        });
    }

    /**
     * @ngdoc method
     * @name getGroupsTopLevel
     * @methodOf admin.service:Admin.OrganizationalHierarchyService
     *
     * @description
     * Performs retrieving all groups
     *
     * param {string} currentPage
     * param {string} pageSize
     * param {string} groupSubtype
     * param {string} directoryName
     * @returns {HttpPromise} Future info about groups
     */
    function getGroupsTopLevel(currentPage, pageSize, groupSubtype, directoryName) {
        //s and n are 0 and 50 by default
        var start = 0, n = 50;
        if (pageSize) {
            if (currentPage)
                start = (currentPage - 1) * pageSize;
            n = pageSize;
        }
        return $http({
            method: 'GET',
            url: 'api/latest/users/group/get/toplevel?n=' + n + '&start=' + start + '&s=name asc' + '&groupSubtype=' + groupSubtype + '&directoryName=' + directoryName
        });
    }

    /**
     * @ngdoc method
     * @name getGroupsByName
     * @methodOf admin.service:Admin.OrganizationalHierarchyService
     *
     * param {params} object for request params
     * {n: page size
     * start: start row
     * nameFq: search value for name filter
     * }
     *
     *
     * @description
     * Retrieve all groups by matching name
     *
     * @returns {HttpPromise} Future info about groups
     */
    function getGroupsByName(params) {
        return $http({
            method: 'GET',
            url: 'api/latest/groups',
            params: params
        });
    }

    /**
     * @ngdoc method
     * @name getSubGroupsForGroup
     * @methodOf admin.service:Admin.OrganizationalHierarchyService
     *
     * @description
     * Performs retrieving users for provided group
     *
     * param {string} group id
     *
     * @returns {HttpPromise} Future info about array of users
     */
    function getSubGroupsForGroup(group) {
        return $http({
            method: 'GET',
            url: 'api/latest/users/group/' + base64.urlencode(group) + '/get/subgroups/',
            params: {
                n: 10000
            }
        });
    }

    /**
     * @ngdoc method
     * @name getUsersForGroup
     * @methodOf admin.service:Admin.OrganizationalHierarchyService
     *
     * @description
     * Performs retrieving users for provided group
     *
     * param {string} group id
     * param {string} user status
     *
     * @returns {HttpPromise} Future info about array of users
     */
    function getUsersForGroup(group, status) {
        var params = {};

        if (status) {
            params.status = status;
        }
        return $http({
            method: 'GET',
            url: 'api/latest/users/by-group/' + base64.urlencode(group),
            params: params
        });
    }

    /**
     * @ngdoc method
     * @name addAdHocGroup
     * @methodOf admin.service:Admin.OrganizationalHierarchyService
     *
     * @description
     * Performs create ad hoc group
     *
     * param {object} group object to be created
     *
     * param {string} parent parent id of the group
     *
     * @returns {HttpPromise} Future info about create ad hoc group
     */
    function addAdHocGroup(group, parent) {
        var url = 'api/latest/users/group/save';
        if (parent)
            url += '/' + base64.urlencode(parent.object_id_s);
        return $http({
            method: 'POST',
            url: url,
            data: group,
            headers: {
                "Content-Type": "application/json"
            }
        });
    }

    /**
     * @ngdoc method
     * @name addExistingAdHocSubGroup
     * @methodOf admin.service:Admin.OrganizationalHierarchyService
     *
     * @description
     * Adds an existing Ad Hoc group as member to another Ad Hoc group
     *
     * param {String} groupId member group
     *
     * param {String} parentId parent group
     *
     * @returns {HttpPromise} Future info about ad hoc subgroup
     */
    function addExistingAdHocSubGroup(groupId, parentId) {
        groupId = base64.urlencode(groupId);
        parentId = base64.urlencode(parentId);
        var url = 'api/latest/users/group/save/' + groupId + '/' + parentId;
        return $http({
            method: 'POST',
            url: url,
            headers: {
                "Content-Type": "application/json"
            }
        });
    }

    /**
     * @ngdoc method
     * @name addExistingAdHocSubGroups
     * @methodOf admin.service:Admin.OrganizationalHierarchyService
     *
     * @description
     * Adds an existing Ad Hoc groups as members to another Ad Hoc group
     *
     * param {String} parentId parent group
     *
     * param {array} memberIds array of member group names
     *
     * @returns {HttpPromise} Future info about list of ad hoc subgroups
     */
    function addExistingAdHocSubGroups(parentId, memberIds) {
        parentId = base64.urlencode(parentId);
        var url = 'api/latest/users/group/' + parentId;
        return $http({
            method: 'POST',
            url: url,
            data: memberIds,
            headers: {
                "Content-Type": "application/json"
            }
        });
    }

    /**
     * @ngdoc method
     * @name saveMembers
     * @methodOf admin.service:Admin.OrganizationalHierarchyService
     *
     * @description
     * Performs save members
     *
     * param {string} group  group in which members should be saved
     *
     * param {array} members array of users(members) to be saved
     *
     * @returns {HttpPromise} Future info about save members
     */
    function saveMembers(group, members) {

        var url = 'api/latest/users/group/' + base64.urlencode(group.object_id_s) + '/members/save';
        return $http({
            method: 'POST',
            url: url,
            data: members,
            headers: {
                "Content-Type": "application/json"
            }
        });
    }

    /**
     * @ngdoc method
     * @name saveMembers
     * @methodOf admin.service:Admin.OrganizationalHierarchyService
     *
     * @description
     * Performs remove members
     *
     * param {string} group  group in which member should be remove
     *
     * param {array} members array of users(member) to be remove
     *
     * @returns {HttpPromise} Future info about remove members
     */
    function removeMembers(group, members) {
        var data = [];
        for (var i = 0; i < members.length; i++) {
            data.push(members[i].object_id_s);
        }
        var url = 'api/latest/users/group/' + base64.urlencode(group.object_id_s) + '/members/remove';
        return $http({
            method: 'POST',
            url: url,
            data: data,
            headers: {
                "Content-Type": "application/json"
            }
        });
    }

    /**
     * @ngdoc method
     * @name removeGroup
     * @methodOf admin.service:Admin.OrganizationalHierarchyService
     *
     * @description
     * Performs remove group
     *
     * param {string} group  group which should be removed
     *
     *
     * @returns {HttpPromise} Future info about remove group
     */
    function removeGroup(group) {
        var url = 'api/latest/users/group/' + base64.urlencode(group.object_id_s) + '/remove';
        return $http({
            method: 'DELETE',
            url: url,
            headers: {
                "Content-Type": "application/json"
            }
        });
    }

    /**
     * @ngdoc method
     * @name setSupervisor
     * @methodOf admin.service:Admin.OrganizationalHierarchyService
     *
     * @description
     * Performs set supervisor
     *
     * param {string} group  group in which supervisor should be set
     *
     * param {object} member member to be set as supervisor
     *
     * @returns {HttpPromise} Future info about set supervisor
     */
    function setSupervisor(group, member) {

        var url = 'api/latest/users/group/' + base64.urlencode(group.object_id_s) + '/supervisor/save/false';
        return $http({
            method: 'POST',
            url: url,
            data: member,
            headers: {
                "Content-Type": "application/json"
            }
        });
    }

    /**
     * @ngdoc method
     * @name getFilteredTopLevelGroups
     * @methodOf services:Admin.OrganizationalHierarchyService
     *
     * @description
     * Get filtered top level groups
     *
     * @param {Number} currentPage
     * @param {Number} pageSize
     * @param {String} groupSubtype
     * @param {String} roleFilters  roles to filter groups returned
     *
     * @returns {Object} Promise
     */
    function getFilteredTopLevelGroups(currentPage, pageSize, groupSubtype, roleFilters) {
        var n = 50, start = 0;
        if (pageSize) {
            if (currentPage) {
                start = (currentPage - 1) * pageSize;
            }
            n = pageSize;
        }

        return Util.serviceCall({
            service: Service._getFilteredTopLevelGroups,
            param: {
                n: n,
                start: start,
                s: 'name asc',
                groupSubtype: groupSubtype,
                roleFilters: roleFilters
            },
            data: {},
            onSuccess: function(data) {
                return data;
            }
        });
    }

    function isEnabledEditingLdapUsers(directoryName) {
        return Util.serviceCall({
            service: Service._getInternalUsersConfig,
            param: {
                directoryName: directoryName
            },
            onSuccess: function(response) {
                return response.enableEditingLdapUsers;
            }
        });
    }

    /**
     * @ngdoc method
     * @name addMembersToLdapGroup
     * @methodOf admin.service:Admin.OrganizationalHierarchyService
     *
     * @description
     * Performs adding members to ldap group
     *
     * ldapUser {object} Ldap AcmUser object to be created
     *
     * @returns {HttpPromise} Future info about add members to ldap group
     */
    function addMemberToLdapGroup(ldapUser, directoryName) {
        return Util.serviceCall({
            service: Service._addMemberToLdapGroup,
            param: {
                directoryName: directoryName
            },
            data: ldapUser,
            onSuccess: function(data) {
                return data;
            }
        });
    }

    function editGroupMember(ldapUser) {
        return Util.serviceCall({
            service: Service._editLdapUser,
            param: {
                directoryName: ldapUser.userDirectoryName,
                userId: ldapUser.userId
            },
            data: ldapUser,
            onSuccess: function(data) {
                return data;
            }
        });
    }

    function deleteLdapUserMember(params) {
        return Util.serviceCall({
            service: Service._deleteLdapUserMember,
            param: {
                directoryName: params.user.directory_name_s,
                userId: params.user.object_id_s,
                groupNames: params.groups
            },
            onSuccess: function(data) {
                return data;
            }
        });
    }

    function addExistingMembersToLdapGroup(ldapUserIds, groupName, directoryName) {
        return Util.serviceCall({
            service: Service._addExistingMembersToLdapGroup,
            data: ldapUserIds,
            param: {
                groupName: base64.urlencode(groupName),
                directoryName: directoryName
            },
            onSuccess: function(data) {
                return data;
            }
        });
    }

    /**
     * @ngdoc method
     * @name createLdapGroup
     * @methodOf admin.service:Admin.OrganizationalHierarchyService
     *
     * @description
     * Performs create ldap group
     *
     * param {object} group object to be created
     *
     * @returns {HttpPromise} Future info about create ldap group
     */
    function createLdapGroup(group, directoryName) {
        return Util.serviceCall({
            service: Service._createLdapGroup,
            param: {
                directoryName: directoryName
            },
            data: group,
            onSuccess: function(data) {
                return data;
            }
        });
    }

    /**
     * @ngdoc method
     * @name createLdapSubGroup
     * @methodOf admin.service:Admin.OrganizationalHierarchyService
     *
     * @description
     * Performs create ldap subgroup
     *
     * param {object} group object to be created
     *
     * @returns {HttpPromise} Future info about create ldap subgroup
     */
    function createLdapSubgroup(group, parentGroupName, directoryName) {
        return Util.serviceCall({
            service: Service._createLdapSubgroup,
            data: group,
            param: {
                parentGroupName: base64.urlencode(parentGroupName),
                directoryName: directoryName
            },
            onSuccess: function(data) {
                return data;
            }
        });
    }

    /**
     * @ngdoc method
     * @name deleteLdapGroup
     * @methodOf admin.service:Admin.OrganizationalHierarchyService
     *
     * @description
     * Performs delete of a ldap group
     *
     * param {object} group object to be deleted
     *
     * @returns {HttpPromise} Future info about create ldap subgroup
     */
    function deleteLdapGroup(ldapGroup) {
        return Util.serviceCall({
            service: Service._deleteLdapGroup,
            param: {
                directoryName: ldapGroup.directory_name_s,
                groupName: base64.urlencode(ldapGroup.name)
            },
            onSuccess: function(data) {
                return data;
            }
        });
    }

    function removeLdapGroupMembership(group, parenGroup) {
        return Util.serviceCall({
            service: Service._removeLdapGroupMembership,
            param: {
                directoryName: group.directory_name_s,
                groupName: base64.urlencode(group.name),
                parentName: base64.urlencode(parenGroup.name)
            },
            onSuccess: function(data) {
                return data;
            }
        });
    }

    function removeGroupMembership(groupName, parentName) {
        return Util.serviceCall({
            service: Service._removeGroupMembership,
            param: {
                groupName: base64.urlencode(groupName),
                parentName: base64.urlencode(parentName)
            },
            onSuccess: function(data) {
                return data;
            }
        });
    }

    function getControlGroup(directoryName) {
        return $http({
            method: 'GET',
            url: 'api/latest/ldap/' + base64.urlencode(directoryName)
        });
    }

} ]);
