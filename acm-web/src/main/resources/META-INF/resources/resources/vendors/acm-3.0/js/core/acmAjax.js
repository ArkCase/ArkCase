/**
 * Acm.Ajax
 *
 * Make Ajax call with typical settings
 *
 * @author jwu
 */
Acm.Ajax = {
    initialize: function() {
    }

    ,deferredGet : function(url, data) {
        var arg = {type: 'GET'
            ,url: url
            ,dataType: 'json'
        };
        if (data) {
            arg.data = data;
        }
        return $.Deferred(function ($dfd) {
            arg.success = function (response) {
                $dfd.resolve(response);
            };
            arg.error = function () {
                $dfd.reject();
            }
            jQuery.ajax(arg);
        });
    }

    ,asyncGetWithData : function(url, callback, data) {
        jQuery.ajax({type: 'GET'
            ,url: url
            ,async: true
            ,data: data
            ,dataType: 'json'
            ,success: function(response) {
                Acm.Dispatcher.triggerEvent(callback, response);
            }
            ,error: function(xhr, status, error) {
                Acm.Dispatcher.triggerEvent(callback, {hasError:true,errorMsg:xhr.responseText});
            }
            //,complete: function(xhr, status) {
            //}
        });
    }

    //,asyncGet : function(url, callback, data) {
	,asyncGet : function(url, callback) {
        var arg = {type: 'GET'
            ,url: url
            ,async: true
            ,dataType: 'json'
            ,success: function(response) {
                Acm.Dispatcher.triggerEvent(callback, response);
            }
            ,error: function(xhr, status, error) {
                Acm.Dispatcher.triggerEvent(callback, {hasError:true,errorMsg:xhr.responseText});
            }
        };
//        if (data) {
//            arg.data = data;
//        }
	    jQuery.ajax(arg);
	}

    ,syncGet : function(url, callback) {
        jQuery.ajax({type: 'GET'
            ,url: url
            ,async: false
            ,dataType: 'json'
            ,success: function(response) {
                Acm.Dispatcher.triggerEvent(callback, response);
            }
            ,error: function(xhr, status, error) {
                Acm.Dispatcher.triggerEvent(callback, {hasError:true,errorMsg:xhr.responseText});
            }
        });
    }

    ,asyncPost : function(url, param, callback) {
        jQuery.ajax({type: 'POST'
            ,url: url
            ,async: true
            ,data: param
            ,dataType: 'json'
            ,contentType: "application/json; charset=utf-8"
            ,beforeSend: function(x) {
                if (x && x.overrideMimeType) {
                    x.overrideMimeType("application/json;charset=UTF-8");
                }
            }
            ,success: function(response) {
                Acm.Dispatcher.triggerEvent(callback, response);
            }
            ,error: function(xhr, status, error) {
                Acm.Dispatcher.triggerEvent(callback, {hasError:true,errorMsg:xhr.responseText});
            }
        });
    }
    
    /*
     * This is an ajax form data submit, not a <form> with a form submit button type of submit.
     */
    ,asyncPostForm : function(url, form, callback) {
	    var postData = $(form).serializeArray();
    
        jQuery.ajax({type: 'POST'
            ,url: url
            ,async: true
            ,data : postData
            ,contentType: "application/x-www-form-urlencoded; charset=UTF-8"
            ,success: function(response) {
                Acm.Dispatcher.triggerEvent(callback, response);
            }
            ,error: function(xhr, status, error) {
                Acm.Dispatcher.triggerEvent(callback, {hasError:true,errorMsg:xhr.responseText});
            }
        });
    }

	,asyncPut : function(url, param, callback) {
	    jQuery.ajax({type: 'PUT'
	        ,url: url
	        ,async: true
	        ,data: param
	        ,dataType: 'json'
	        ,beforeSend: function(x) {
	            if (x && x.overrideMimeType) {
	                x.overrideMimeType("application/json;charset=UTF-8");
	            }
	        }
	        ,success: function(response) {
	            Acm.Dispatcher.triggerEvent(callback, response);
	        }
	        ,error: function(xhr, status, error) {
	            Acm.Dispatcher.triggerEvent(callback, {hasError:true,errorMsg:xhr.responseText});
	        }
	    });
	}

};