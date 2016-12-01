/**
 * Created by riste.tutureski on 11/14/2016.
 */

'use strict';

angular.module('directives').directive('iframeOnload', [function(){
    return {
        scope: {
            callBack: '&iframeOnload'
        },
        link: function(scope, element, attrs){
            element.on('load', function(){
                return scope.callBack();
            })
        }
    }}]);
