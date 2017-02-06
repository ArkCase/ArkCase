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
angular.module('admin').service('Admin.OrganizationalHierarchyService', ['$http', 'UtilService', 'Acm.StoreService', '$resource',
    function ($http, Util, Store, $resource) {
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
                url: 'api/v1/users/ldap/editingEnabled',
                cache: false
            },

            _addMemberToLdapGroup: {
                method: 'POST',
                url: 'api/latest/users/ldap/add'
            },

            _editLdapUser: {
                method: 'POST',
                url: 'api/latest/users/ldap/edit'
            },

            _addExistingMembersToLdapGroup: {
                method: 'POST',
                url: 'api/latest/users/ldap/add/:groupName',
                isArray: true
            }
        });

        Service.CacheNames = {
            INTERNAL_USER_CONFIG: "INTERNAL_USER_CONFIG",
            KEY: "enableEditingLdapUsers"
        };

        return ({
            getGroups: getGroups,
            getGroupsTopLevel: getGroupsTopLevel,
            getSubGroupsForGroup: getSubGroupsForGroup,
            getUsersForGroup: getUsersForGroup,
            addAdHocGroup: addAdHocGroup,
            saveMembers: saveMembers,
            removeMembers: removeMembers,
            removeGroup: removeGroup,
            setSupervisor: setSupervisor,
            getFilteredTopLevelGroups: getFilteredTopLevelGroups,
            isEnabledEditingLdapUsers: isEnabledEditingLdapUsers,
            addMemberToLdapGroup: addMemberToLdapGroup,
            editGroupMember: editGroupMember,
            addExistingMembersToLdapGroup: addExistingMembersToLdapGroup
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
         * @returns {HttpPromise} Future info about groups
         */
        function getGroupsTopLevel(currentPage, pageSize, groupSubtype) {
            //s and n are 0 and 50 by default
            var start = 0, n = 50;
            if (pageSize) {
                if (currentPage)
                    start = (currentPage - 1) * pageSize;
                n = pageSize;
            }
            return $http({
                method: 'GET',
                url: 'api/latest/users/group/get/toplevel?n=' + n + '&start=' + start + '&s=name asc' + '&groupSubtype=' + groupSubtype
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
                url: 'api/latest/users/group/' + group + '/get/subgroups/'
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
         *
         * @returns {HttpPromise} Future info about array of users
         */
        function getUsersForGroup(group) {
            return $http({
                method: 'GET',
                url: 'api/latest/users/by-group/' + group
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
                url += '/' + parent.object_id_s;
            return $http({
                method: 'POST',
                url: url,
                data: angular.toJson(group),
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

            var url = 'api/latest/users/group/' + group.object_id_s + '/members/save';
            return $http({
                method: 'POST',
                url: url,
                data: angular.toJson(members),
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
                var obj = {};
                obj.userId = members[i].object_id_s;
                data.push(obj);
            }
            var url = 'api/latest/users/group/' + group.object_id_s + '/members/remove';
            return $http({
                method: 'POST',
                url: url,
                data: angular.toJson(data),
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
            var url = 'api/latest/users/group/' + group.object_id_s + '/remove';
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

            var url = 'api/latest/users/group/' + group.object_id_s + '/supervisor/save/false';
            return $http({
                method: 'POST',
                url: url,
                data: angular.toJson(member),
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
                service: Service._getFilteredTopLevelGroups
                , param: {
                    n: n,
                    start: start,
                    s: 'name asc',
                    groupSubtype: groupSubtype,
                    roleFilters: roleFilters
                }
                , data: {}
                , onSuccess: function (data) {
                    return data;
                }
            });
        }

        function isEnabledEditingLdapUsers() {
            var cacheConfig = new Store.CacheFifo(Service.CacheNames.INTERNAL_USER_CONFIG);
            var config = cacheConfig.get(Service.CacheNames.KEY);
            return Util.serviceCall({
                service: Service._getInternalUsersConfig
                , result: config
                , onSuccess: function (response) {
                    var config = response.enableEditingLdapUsers;
                    cacheConfig.put(Service.CacheNames.KEY, config);
                    return config;
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
        function addMemberToLdapGroup(ldapUser) {
            return Util.serviceCall({
                service: Service._addMemberToLdapGroup
                , data: ldapUser
                , onSuccess: function (data) {
                    return data;
                }
            });
        }

        function editGroupMember(ldapUser) {
            return Util.serviceCall({
                service: Service._editLdapUser
                , data: ldapUser
                , onSuccess: function (data) {
                    return data;
                }
            });
        }

        function addExistingMembersToLdapGroup(ldapUsers, groupName) {
            return Util.serviceCall({
                service: Service._addExistingMembersToLdapGroup
                , data: ldapUsers
                , param: {
                    "groupName": groupName
                }
                , onSuccess: function (data) {
                    return data;
                }
            });
        }
    }]);
