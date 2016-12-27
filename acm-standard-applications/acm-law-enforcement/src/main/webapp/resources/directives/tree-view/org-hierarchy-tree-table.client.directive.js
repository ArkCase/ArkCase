'use strict';

angular.module('directives').directive('treeTableView', ['$q', '$compile', 'MessageService',
    function ($q, $compile, messageService) {
        return {
            restrict: 'E',
            scope: {
                treeData: '=',
                onSelect: '&',
                onLazyLoad: '=',
                onLoadMore: '=',
                onDeleteMembers: '=',
                onDeleteGroup: '=',
                onAddMembers: '=',
                onAddSubGroup: '=',
                onSetSupervisor: '=',
                config: '=',
                totalGroups: '=',
                showActions: '=',
                showSupervisor: '=',
                showType: '='
            },
            link: function (scope, element, attrs) {
                var $tbl = $("#org");
                var treeOptions = {
                    source: [],
                    click: function (event, data) {
                        if (scope.onSelect())
                            scope.onSelect()(data);
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

                        if (!scope.showType) {
                            hideColumn(1, "#type", $tdList)
                        } else if (node.data.object_sub_type_s != null) {
                            $tdList.eq(1).text(node.data.object_sub_type_s);
                        }
                        if (!scope.showSupervisor) {
                            hideColumn(2, "#supervisorName", $tdList)
                        } else if (node.data.supervisor) {
                            $tdList.eq(2).text(node.data.supervisor);
                        }

                        if (!scope.showActions) {
                            hideColumn(3, "#actions", $tdList)
                        } else {
                            if (!node.data.isMember && node.data.object_sub_type_s != "LDAP_GROUP") {
                                $tdList.eq(3).html($compile("<button class='btn btn-link btn-xs' type='button' ng-click='addSubgroup($event)' name='addSubgroup' title='Add Subgroup'><i class='fa fa-users'></i></button>" +
                                    "<button class='btn btn-link btn-xs' type='button' ng-click='pickUsersBtn($event)' name='addMembers' title='Add Members'><i class='fa fa-user'></i></button>" +
                                    "<button class='btn btn-link btn-xs' type='button' ng-click='removeGroupBtn($event)' name='removeGroup' title='Remove Group'><i class='fa fa-trash-o'></i></button>")(scope));
                                if (scope.showSupervisor) {
                                    $tdList.eq(3).append($compile("<button class='btn btn-link btn-xs pull-left' type='button' ng-click='addSupervisor($event)' name='addSupervisor' title='Add/Edit Supervisor'><i class='fa fa-edit'></i></button>")(scope));
                                }
                            }
                            if (node.data.isMember && node.parent.data.object_sub_type_s != "LDAP_GROUP") {
                                $tdList.eq(3).append($compile("<button class='btn btn-link btn-xs' type='button' ng-click='removeUserBtn($event)' name='removeMember' title='Remove Member'><i class='fa fa-trash-o'></i></button>")(scope));
                            }
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

                //some default values if it is not set in config file
                scope.pagerData = {
                    pageSizes: [10, 20, 30, 40, 50],
                    pageSize: 50,
                    totalItems: scope.totalGroups
                };
                scope.$watchCollection('config', function (config, oldValue) {
                    if (config) {
                        if (config.paginationPageSizes)
                            scope.pagerData.pageSizes = config.paginationPageSizes;
                        if (config.paginationPageSize)
                            scope.pagerData.pageSize = config.paginationPageSize;
                        if (!scope.treeData || scope.treeData.length == 0)
                            scope.onLoadMore(scope.pagerData.currentPage, scope.pagerData.pageSize);
                    }
                });

                scope.$watchCollection('totalGroups', function (totalGroups, oldValue) {
                    if (totalGroups && totalGroups != oldValue) {
                        scope.pagerData.totalItems = scope.totalGroups;
                    }
                });

                scope.pickUsersBtn = function (event) {
                    var node = $.ui.fancytree.getNode(event);
                    scope.onAddMembers(node.data).then(function (members) {
                    	//success
                    	angular.forEach(members, function (member) {
                            node.addChildren(member);
                        });
                        node.setExpanded();
                        messageService.succsessAction();
                    }, function () {
                        //error
                    	messageService.errorAction();
                    });
                };

                scope.addSubgroup = function (event) {
                    var node = $.ui.fancytree.getNode(event);
                    scope.onAddSubGroup(node.data).then(function (subGroup) {
                    	//success
                    	node.addNode(subGroup, 'firstChild');
                        node.setExpanded();
                    	messageService.succsessAction();
                    }, function () {
                        //error
                    	messageService.errorAction();
                    });
                };

                scope.addSupervisor = function (event) {
                    var node = $.ui.fancytree.getNode(event);
                    scope.onSetSupervisor(node.data).then(function (payload) {
                    	//success
                    	node.data.supervisor = payload.supervisor.fullName;
                        node.renderTitle();
                        messageService.succsessAction();
                    }, function () {
                        //error
                    	messageService.errorAction();
                    });
                };

                scope.removeUserBtn = function (event) {
                    var node = $.ui.fancytree.getNode(event);
                    scope.onDeleteMembers(node.parent.data, node.data).then(function () {
                    	//success
                    	node.remove();
                        messageService.succsessAction();
                    }, function () {
                        //error
                    	messageService.errorAction();
                    });
                };

                scope.removeGroupBtn = function (event) {
                    var node = $.ui.fancytree.getNode(event);
                    scope.onDeleteGroup(node.data).then(function () {
                    	//success
                    	node.remove();
                        messageService.succsessAction();
                    }, function () {
                        //error
                    	messageService.errorAction();
                    });
                };

                var hideColumn = function (index, $id, $tdList) {
                    var colToHide = $tbl.find($id)
                    colToHide.hide();
                    $tdList.eq(index).hide();
                }
            },
            templateUrl: 'directives/tree-view/org-hierarchy-tree-table.client.view.html'
        };
    }

]);
