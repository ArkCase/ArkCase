'use strict';

/**
 * @ngdoc directive
 * @name global.directive:treeView
 * @restrict E
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/directives/tree-view/tree-view.client.directive.js directives/tree-view/tree-view.client.directive.js}
 *
 * The treeView directive renders simple FansyTree based Tree View
 *
 * @param {expression} treeData Data structure used for tree rendering
 * @param {expression} onSelect Expression to evaluate upon tree item select.
 *
 * @example
    <example>
        <file name="index.html">
            <tree-view treeData="ordersData" onSelect="selectOrder">
            </tree-view>
        </file>
        <file name="app.js">
            angular.module('ngAppDemo', []).controller('ngAppDemoController', function($scope, $log) {
                $scope.ordersData = [
                    {title: 'Order 1', key: '1'},
                    {title: 'Order 2', key: '2', folder: true, children: [
                        {title: 'Order 3', key: '3'},
                        {title: 'Order 4', key: '4'}
                    ]}
                ];

                $scope.selectOrder = function(selectedOrder){
                    $log.debug(selectedOrder)
                };
            });
        </file>
    </example>
 */
angular.module('directives').directive('treeView', ['$q',
    function($q) {
        return {
            restrict: 'E',
            scope: {
                treeData: '=',
                onSelect: '&'
            },

            link: function(scope, element, attrs){
                var treeOptions = {
                    source: [],
                    click: function (event, data) {
                        scope.onSelect()(data.node.data);
                    }
                };
                $(element).fancytree(treeOptions);


                if (scope.treeData) {
                    scope.$watchCollection('treeData', function(newValue, oldValue) {
                        $q.when(newValue).then(function (treeData) {
                            var tree = $(element).fancytree('getTree');
                            tree.reload(treeData);
                        }, true);
                    });
                }
            }
        };
    }
]);
