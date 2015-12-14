'use strict';

angular.module('directives').directive('treeTableView', ['$q', '$compile', '$timeout',
    function ($q, $compile, $timeout) {
        return {
            restrict: 'E',
            scope: {
                treeData: '=',
                columns: '=',
                onSelect: '&',
                onLazyLoad: '=',
                onDeleteMembers: '=',
                onDeleteGroup: '=',
                onAddMembers: '=',
                onAddSubGroup: '=',
                onSetSupervisor: '=',
                config: '='
            },
            link: function (scope, element, attrs) {

                var treeOptions = {
                    source: [],
                    click: function (event, data) {
                        if (scope.onSelect())
                            scope.onSelect()(data.node.data);
                    },
                    extensions: ["table"],
                    checkbox: false,
                    table: {
                        indentation: 16,      // indent 16px per node level
                        nodeColumnIdx: 0,     // render the node title into the 0nd column
                        checkboxColumnIdx: null  // render the checkboxes into the 1st column
                    },
                    lazyLoad: function (event, data) {
                        if (scope.onLazyLoad) {
                            data.result = scope.onLazyLoad(event, data.node.data);
                        } else {
                            data.result = [];
                        }
                    },
                    renderColumns: function (event, data) {
                        var node = data.node,
                            $tdList = $(node.tr).find(">td");

                        if (node.data.object_sub_type_s != null) {
                            $tdList.eq(1).text(node.data.object_sub_type_s);
                        }
                        if (node.data.supervisor) {
                            $tdList.eq(2).text(node.data.supervisor);
                        }
                        if (node.data.object_sub_type_s == "ADHOC_GROUP") {
                            $tdList.eq(3).html($compile("<button class='btn btn-link btn-xs pull-left' type='button' ng-click='addSupervisor($event)' name='addSupervisor' title='Add/Edit Supervisor'><i class='fa fa-edit'></i></button>" +
                                    "<button class='btn btn-link btn-xs' type='button' ng-click='addSubgroup($event)' name='addSubgroup' title='Add Subgroup'><i class='fa fa-users'></i></button>" +
                                    "<button class='btn btn-link btn-xs' type='button' ng-click='pickUsersBtn($event)' name='addMembers' title='Add Members'><i class='fa fa-user'></i></button>" +
                                    "<button class='btn btn-link btn-xs' type='button' ng-click='removeGroupBtn($event)' name='removeGroup' title='Remove Group'><i class='fa fa-trash-o'></i></button>")(scope)
                            );
                        }
                        if (node.data.isMember == true && node.parent.data.object_sub_type_s == "ADHOC_GROUP") {
                            $tdList.eq(3).append($compile("<button class='btn btn-link btn-xs' type='button' ng-click='removeUserBtn($event)' name='removeMember' title='Remove Member'><i class='fa fa-trash-o'></i></button>")(scope));
                        }
                    }
                };


                var $fancytree = $(element).find('table').fancytree(treeOptions);

                if (scope.treeData) {
                    scope.$watchCollection('treeData', function (treeData, oldValue) {
                        if (treeData && treeData.length > 0) {
                            $($fancytree).fancytree("getTree").reload(treeData);
                        }
                    });
                }

                scope.pickUsersBtn = function (event) {
                    var node = $.ui.fancytree.getNode(event);
                    scope.onAddMembers(node.data).then(function () {
                        $timeout(function () {
                            node.load(true).done(function () {
                                node.setExpanded();
                            });
                        }, 1000);
                    });
                };

                scope.addSubgroup = function (event) {
                    var node = $.ui.fancytree.getNode(event);
                    scope.onAddSubGroup(node.data).then(function () {
                        $timeout(function () {
                            node.load(true).done(function () {
                                node.setExpanded();
                            });
                        }, 1000);
                    });
                };

                scope.addSupervisor = function (event) {
                    var node = $.ui.fancytree.getNode(event);
                    scope.onSetSupervisor(node.data).then(function (payload) {
                        node.data.supervisor = payload.supervisor.fullName;
                        node.renderTitle();
                    });
                };

                scope.removeUserBtn = function (event) {
                    var node = $.ui.fancytree.getNode(event);
                    scope.onDeleteMembers(node.parent.data, node.data).then(function () {
                        node.remove();
                    });
                };

                scope.removeGroupBtn = function (event) {
                    var node = $.ui.fancytree.getNode(event);
                    scope.onDeleteGroup(node.data).then(function () {
                        node.remove();
                    });
                };


            },
            templateUrl: 'directives/tree-view/org-hierarchy-tree-table.client.view.html'
        };
    }

]);
