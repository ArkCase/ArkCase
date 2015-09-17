'use strict';

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
