/**
 * ACM serves as namespace for ACM application and also provides some frequent miscellaneous functions
 *
 * @author jwu
 */
var ACM = ACM || {
    initialize : function() {
        ACM.Dispatcher.initialize();
        ACM.Ajax.initialize();
        ACM.Object.initialize();
        ACM.Validation.initialize();
    }

    ,Dispatcher : {}
    ,Ajax : {}
    ,Object : {}
    ,Validation : {}


	,isEmpty: function (val) {
//        if (typeof val == "undefined") {
//            return true;
//        }
	    if (undefined == val) {
	        return true;
	    } else if ("" === val) {
	        return true;
	    } else if (null == val) {
	        return true;
	    } else if ("null" == val) {
	    	return true;
	    }

	    return false;
	}
	,isNotEmpty: function (val) {
	    return !this.isEmpty(val);
	}
	,goodValue: function (val, replacement)  {
	    var replacedWith = (undefined === replacement) ? "" : replacement;
	    return this.isEmpty(val) ? replacedWith : val;
	}

    //append random parameter after a url to avoid undesired cached session variables
    //This function handles input url in following sample cases:
    //  some.com/some/path
    //  some.com/some/path/
    //  some.com/some/path/?var=abc
    ,makeNoneCacheUrl: function(url) {
        var lastChar = url.slice(-1);
        var hasQmark = (-1 !== url.indexOf('?'));

        if (hasQmark) {
            url += '&'
        } else {
            if ('/' !== lastChar) {
                url += '/';
            }
            url += '?';
        }
        url += 'rand=' + Math.floor((Math.random()*10000000000));
        return url;
    }
};


/**
 * initialize ACM
 */
//
// call it externally, from common/ready.js for example
//
//jQuery(document).ready(
//    function() {
//        ACM.initialize();
//    }
//);

