angular.module('directives').directive('paginationOnScroll', [ 'UtilService', function(Util) {
    return {
        restrict : 'A',
        scope : {
            loadMore : "&"
        },
        link : function(scope, elem, attr) {
            elem.bind("scroll", function() {
                var scrolledpx = elem.context.clientHeight + elem.context.scrollTop;
                if (scrolledpx === elem.context.scrollHeight) {
                    scope.loadMore();
                }
            });
        }
    }
} ]);