angular.module('directives').directive('scroll', [ 'UtilService', function(Util) {
    return {
        restrict : 'A',
        scope : {
            panelPagination : "@?"
        },
        link : function(scope, elem, attr) {
            var scrollElement = angular.element(document.querySelector(".scroll"));

            scrollElement.bind("scroll", function() {
                var scrolledpx = scrollElement.context.clientHeight + scrollElement.context.scrollTop;
                if (scrolledpx === scrollElement.context.scrollHeight && !Util.isEmpty(scope.panelPagination)) {
                    scope.$bus.publish(scope.panelPagination + "Scroll");
                }
            });
        }
    }
} ]);