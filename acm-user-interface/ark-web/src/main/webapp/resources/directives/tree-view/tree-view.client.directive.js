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
                if (scope.treeData) {
                    $q.when(scope.treeData).then(function(treeData){
                        $(element).fancytree({
                            source: treeData,
                            click: function(event, data){
                                scope.onSelect()(data.node.data);
                            }
                        });
                    });
                }
            }
        };
    }
]);
