'use strict';

angular.module('admin').controller('AdminListController', ['$scope', '$state', '$stateParams', '$translate', '$timeout'
    , 'UtilService', 'ConfigService'
    , function ($scope, $state, $stateParams, $translate, $timeout
        , Util, ConfigService) {

        ConfigService.getModuleConfig("admin").then(function (config) {
            $scope.treeConfig = config.tree;

            $scope.treeData = {docs: [], total: 0};
            var count = 0;
            _.each(Util.goodMapValue(config.tree, "nodeTypes", []), function(typeDef){
                var tokens = Util.goodMapValue(typeDef, "type").split("/");
                if (2 === tokens.length) {
                    count++;
                    $scope.treeData.docs.push({
                        nodeId: count
                        , nodeType: tokens[1]
                        , nodeTitleLabel: Util.goodMapValue(typeDef, "label")
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

        $scope.onLoad = function(start, n, sort, filters){
            return $scope.treeData.docs;
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

    }
]);