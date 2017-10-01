'use strict';

angular.module('admin').controller('AdminListController', ['$scope', '$state', '$stateParams', '$translate'
    , 'UtilService', 'ConfigService'
    , 'ObjectService', 'Case.ListService', 'Case.InfoService', 'Helper.ObjectBrowserService'
    , 'ServCommService', 'MessageService'
    , function ($scope, $state, $stateParams, $translate
        , Util, ConfigService
        , ObjectService, CaseListService, CaseInfoService, HelperObjectBrowserService
        , ServCommService, MessageService) {

        ConfigService.getModuleConfig("admin").then(function (config) {
            $scope.treeConfig = config.tree;

            $scope.treeData = {docs: [], total: 0};
            var count = 0;
            _.each(Util.goodMapValue(config.tree, "nodeTypes", []), function(typeDef){
                var tokens = Util.goodMapValue(typeDef, "type").split("/");
                if (2 === tokens.length) {
                    count++;
                    var nodeType = tokens[1];
                    var nodeTitleLabel = Util.goodMapValue(typeDef, "label");
                    //var nodeTitle = $translate.instant(Util.goodMapValue(typeDef, "label"));
                    $scope.treeData.docs.push({
                        nodeId: count
                        , nodeType: tokens[1]
                        , nodeTitleLabel: nodeTitleLabel
                        //, nodeTitle: nodeTitle
                    });
                }
            });
            $scope.treeData.total = count;

            $timeout(function() {
                var expandTree = Util.goodMapValue($scope.treeControl, "expandAll", false);
                if (expandTree) {
                    expandTree();
                }
            }, 0);

            return config;
        });

        $scope.treeControl = {};

        $scope.onLoad = function(start, n, sort, filters){
            return $scope.treeData.docs;
            //return Util.goodMapValue($scope, "treeData.docs", []);
        };

        $scope.onSelect = function(selectedObject){
            console.log(selectedObject);
            console.log(selectedObject.components);
            var comp = Util.goodMapValue(selectedObject, "components[0]", false);
            if (comp) {
                $state.go('admin.view-node', {
                    nodeName: comp
                }, {
                    inherit: false,
                    location: true
                });
            }
        };

/*
        $scope.treeConfig = {
            "pageSize": 3,
            "filters": [],
            "sorters": [],
            "nodeTypes": [
                {
                    "type": "prev",
                    "icon": "fa fa-arrow-up"
                },
                {
                    "type": "next",
                    "icon": "fa fa-arrow-down"
                },
                {
                    "type": "p/security",
                    "label": "admin.security.title",
                    "icon": "fa fa-wrench",
                    "components": []
                },
                {
                    "type": "p/security/access",
                    "label": "admin.security.functionalAccessControl",
                    "components": [
                        "security.functional-access-control"
                    ]
                },
                {
                    "type": "p/security/ldap",
                    "label": "admin.security.ldapConfig",
                    "components": [
                        "security.ldap-config"
                    ]
                },
                {
                    "type": "p/security/org",
                    "label": "admin.security.organizationalHierarchy.title",
                    "components": [
                        "security.organizational-hierarchy"
                    ]
                },
                {
                    "type": "p/dashboard",
                    "label": "admin.dashboard.title",
                    "icon": "fa fa-wrench",
                    "components": []
                },
                {
                    "type": "p/dashboard/cfg",
                    "label": "admin.dashboard.config",
                    "components": [
                        "dashboard.config"
                    ]
                },
                {
                    "type": "p/docMan",
                    "label": "admin.documentManagement.title",
                    "icon": "fa fa-wrench",
                    "components": []
                },
                {
                    "type": "p/docMan/cmis",
                    "label": "admin.documentManagement.cmisConfiguration",
                    "components": [
                        "cmis-configuration"
                    ]
                }
            ]
        };

        var page1 = [
            {nodeId: 101, nodeType: "security", nodeTitle: 'security 101'},
            {nodeId: 102, nodeType: "dashboard", nodeTitle: 'dashboard 102', nodeToolTip: ''},
            {nodeId: 103, nodeType: "docMan", nodeTitle: 'docMan 103', nodeToolTip: 'docMan 103'}
        ];
        var page2 = [
            {nodeId: 201, nodeType: "security", nodeTitle: 'Case 201', nodeToolTip: 'Case 201'},
            {nodeId: 202, nodeType: "security", nodeTitle: 'Case 202', nodeToolTip: 'Case 202'},
            {nodeId: 202, nodeType: "security", nodeTitle: 'Case 202', nodeToolTip: 'Case 202'}
        ];
        var page3 = [
            {nodeId: 301, nodeType: "security", nodeTitle: 'Case 301', nodeToolTip: 'Case 301'}
        ];

        $scope.treeData = {};
        $scope.treeData.docs = page1;
        $scope.treeData.total = 3;

        $scope.onLoad = function(start, n, sort, filters){
            //query list of objects according to the parameters. Only consider 'start' here:
            page1[0].nodeTitle = "xxxx";
            if (0 == start) {
                return page1;
            } else if (2 == start) {
                return page2;
            } else if (4 == start) {
                return page3;
            } else {
                return [];
            }
        };
*/

    }
]);