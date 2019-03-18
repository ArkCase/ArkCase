'use strict';

angular.module('admin').controller('AdminListController', [ '$scope', '$state', '$stateParams', '$translate', '$timeout', 'UtilService', 'ConfigService', function($scope, $state, $stateParams, $translate, $timeout, Util, ConfigService) {

    ConfigService.getModuleConfig("admin").then(function(config) {
        $scope.treeConfig = config.tree;

        $scope.treeData = {
            docs: [],
            total: 0
        };
        var count = 0;
        _.each(Util.goodMapValue(config.tree, "nodeTypes", []), function(typeDef) {
            var tokens = Util.goodMapValue(typeDef, "type").split("/");
            if (2 === tokens.length) {
                count++;
                $scope.treeData.docs.push({
                    nodeId: count,
                    nodeType: tokens[1],
                    nodeTitleLabel: Util.goodMapValue(typeDef, "label")
                });
            }
        });
        $scope.treeData.total = count;
        return config;
    }).then(function() {
        expandTree();
    });

    var expandTree = function() {
        $timeout(function() {
            var expandAll = Util.goodMapValue($scope.treeControl, "expandAll", false);
            if (expandAll) {
                expandAll();
            } else {
                expandTree();
            }
        }, 100);
    };

    $scope.onLoad = function(start, n, sort, filters) {
        return $scope.treeData.docs;
    };

    $scope.onSelect = function(selectedObject) {
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

} ]);