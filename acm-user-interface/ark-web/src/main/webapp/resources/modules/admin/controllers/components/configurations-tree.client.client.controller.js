'use strict';

angular.module('admin').controller('Admin.ConfigurationsTreeController',
    ['$scope', '$state', '$stateParams', '$log', 'ConfigService', '$q', '$translate',
        function ($scope, $state, $stateParams, $log, ConfigService, $q, $translate) {
            function setTitle(treeNode) {
                if (treeNode.children !== undefined && treeNode.children.length > 0) {
                    angular.forEach(treeNode.children, function (child) {
                        setTitle(child);
                    });
                }
                //translate provided title key
                treeNode["title"] = $translate.instant(treeNode.title);

            };

            //data for the tree view
            $scope.treeData = [];
            ConfigService.getModule({moduleId: 'admin'}).$promise.then(function (config) {
                if (config.adminTree) {
                    var treeData = config.adminTree;
                    angular.forEach(treeData, function (node) {
                        setTitle(node)
                    });
                    $scope.treeData = treeData;
                }
            });

            $scope.selectData = function (selectedData) {
                if (selectedData.templateName !== undefined) {
                    $state.go('admin.view-node',
                        {
                            nodeName: selectedData.templateName
                        },
                        {
                            inherit: false,
                            location: true
                        });
                }
            };
        }
    ]);