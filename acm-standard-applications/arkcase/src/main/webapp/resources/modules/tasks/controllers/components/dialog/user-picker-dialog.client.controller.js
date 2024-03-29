'use strict';

angular.module('tasks').controller('UserPickerDialogController',
        [ '$scope', '$modalInstance', '$q', 'UtilService', 'Admin.OrganizationalHierarchyService', 'cfg', 'parentType', 'showGroupAndUserPicker', function($scope, $modalInstance, $q, Util, organizationalHierarchyService, cfg, parentType, showGroupAndUserPicker) {
            $scope.modalInstance = $modalInstance;
            $scope.data = [];
            $scope.groupsMap = {};
            $scope.groupTypeList = [ 'LDAP_GROUP' ];

            $scope.cfg = cfg;
            $scope.showGroupAndUserPicker = showGroupAndUserPicker;
            if ($scope.cfg && Util.isArray($scope.cfg.topLevelGroupTypes)) {
                $scope.groupTypeList = $scope.cfg.topLevelGroupTypes;
            }

            $scope.onLazyLoad = function(event, groupNode) {
                var parentId = groupNode.object_id_s;
                var dfd = $q.defer();
                var group = $scope.groupsMap[parentId];
                var children = [];

                //find child groups
                var subGroupsPromise = organizationalHierarchyService.getSubGroupsForGroup(parentId);
                subGroupsPromise.then(function(payload) {
                    var tempGroups = [];
                    if (!Util.isArrayEmpty(_.get(payload, 'data.response.docs'))) {
                        tempGroups = _.get(payload, 'data.response.docs');
                    }
                    //create map from groups
                    for (var i = 0; i < tempGroups.length; i++) {
                        var tempGroup = tempGroups[i];
                        $scope.groupsMap[tempGroup.object_id_s] = tempGroup;
                    }
                    createTreeData(tempGroups);

                    if (group && group.child_id_ss) {
                        for (var i = 0; i < group.child_id_ss.length; i++) {
                            var groupId = group.child_id_ss[i];
                            children.push(getGroup(groupId));
                        }
                    }

                    //find child users
                    if (group && group.member_id_ss) {
                        organizationalHierarchyService.getUsersForGroup(group.object_id_s).then(function(payload) {
                            //successfully users received, insert with groups in same array
                            var data = _.get(payload, 'data.response.docs');
                            if (data) {
                                for (var i = 0; i < data.length; i++) {
                                    data[i].title = data[i].name;
                                    data[i].isMember = true;
                                    children.unshift(data[i]);
                                }
                                group.children = children;
                            }
                            dfd.resolve(children);
                        }, function(payload) {
                            //error getting users
                            console.log("Error getting users: " + payload);

                            //be we still need to return sub groups which are included in children, that's why we are not using reject
                            group.children = children;
                            dfd.resolve(children);
                        });
                    } else {
                        dfd.resolve(children);
                    }

                });
                return dfd.promise;
            };

            $scope.onLoadMore = function(currentPage, pageSize) {
                var groupsPromise;
                if ($scope.groupTypeList && Util.isArray($scope.groupTypeList)) {
                    groupsPromise = organizationalHierarchyService.getGroupsTopLevel(currentPage, pageSize, $scope.groupTypeList);
                } else {
                    groupsPromise = organizationalHierarchyService.getGroupsTopLevel(currentPage, pageSize, []);
                }

                groupsPromise.then(function(payload) {
                    var tempGroups = [];
                    if (!Util.isArrayEmpty(_.get(payload, 'data.response.docs'))) {
                        tempGroups = _.get(payload, 'data.response.docs');
                    }
                    $scope.totalGroups = _.get(payload, 'data.response.numFound');
                    //create map from groups
                    for (var i = 0; i < tempGroups.length; i++) {
                        var tempGroup = tempGroups[i];
                        $scope.groupsMap[tempGroup.object_id_s] = tempGroup;
                    }
                    $scope.data = [];
                    createTreeData(tempGroups);
                });
            };

            $scope.onSelect = function(data) {
                if (Util.goodMapValue(data, "node.data.object_type_s") == 'USER') {
                    selectedUser = data.node.data;
                    organization = getOrganization(data);
                    organization.currentParent = getCurrentOrganization(data);
                } else
                // allows users to select nodes
                if (Util.goodMapValue(data, "node.data.object_type_s") == 'GROUP') {
                    selectedUser = null;
                    organization = data.node.data;
                    //organization.currentParent = getCurrentOrganization(data);
                } else {
                    selectedUser = null;
                    organization = null;
                }
            };

            $scope.onClickCancel = function() {
                $scope.modalInstance.dismiss('cancel');
            };

            $scope.onClickOk = function() {
                $scope.modalInstance.close([ selectedUser, organization ]);
            };

            $scope.disableOk = function() {
                // if showGroupAndUserPicker has been passed in and is true, then allow users to submit if they have entered
                // a group or a user. Otherwise, make them select a user (selecting a user currently autofills organization)
                if (Util.goodValue(showGroupAndUserPicker) && showGroupAndUserPicker) {
                    return (Util.isEmpty(selectedUser) && Util.isEmpty(organization));
                } else {
                    return Util.isEmpty(selectedUser) || Util.isEmpty(organization);
                }
            };

            var selectedUser = null, organization = null;

            var createTreeData = function(groups) {
                var source = [];

                //group details
                for (var i = 0; i < groups.length; i++) {
                    var group = groups[i];
                    addToTree(group);
                }
                return source;
            };

            var getGroup = function(id) {
                return $scope.groupsMap[id];
            };

            var addToTree = function(group, top) {
                group.expanded = false;
                group.lazy = true;
                group.folder = true;
                group.title = group.name;
                //init children array
                if (!group.children)
                    group.children = [];
                if (!group.parent_id_s) {
                    //add group to root
                    if (top)
                        $scope.data.unshift(group);
                    else
                        $scope.data.push(group);
                }
            };

            var getOrganization = function(groupNode) {
                var tmpNode = groupNode.node;
                if (parentType === 'SJS' || parentType === 'STAFF') {
                    tmpNode = tmpNode.parent;
                } else {
                    while (tmpNode.parent.title != 'root') {
                        tmpNode = tmpNode.parent;
                    }
                }
                return tmpNode.data;
            };

            var getCurrentOrganization = function(groupNode) {
                return Util.goodMapValue(groupNode.node, "parent.data.object_id_s");
            };

            var getGroupName = function(groupNode) {
                return Util.goodMapValue(groupNode.node, "data.group_name_s");
            }
        } ]);