angular.module('directives').directive('scroll', [ 'UtilService', function(Util) {
    return {
        restrict : 'A',
        scope : {
            panelPagination : "@?"
        },
        link : function(scope, elem, attr) {
            elem.bind("scroll", function() {
                var scrolledpx = elem.context.clientHeight + elem.context.scrollTop;
                if (scrolledpx === elem.context.scrollHeight && !Util.isEmpty(scope.panelPagination)) {
                    scope.$bus.publish(scope.panelPagination + "Scroll");
                }
            });
        }
    }
} ]);