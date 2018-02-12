angular.module('directives').directive('scroll', [ 'UtilService', function(Util) {
    return {
        restrict : 'A',
        scope : {
            panelNameAlias : "@?"
        },
        link : function(scope, elem, attr) {
            elem.bind("scroll", function() {
                var scrolledpx = elem.context.clientHeight + elem.context.scrollTop;
                if (scrolledpx === elem.context.scrollHeight && !Util.isEmpty(scope.panelNameAlias)) {
                    scope.$bus.publish(scope.panelNameAlias + "Scroll");
                }
            });
        }
    }
} ]);