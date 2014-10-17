/**
 * Acm.Service
 *
 * @author jwu
 */
Acm.Service = {
    initialize: function() {
    }

    ,ajax: function(arg) {
        if (!arg) {
            return;
        }
        if (!arg.type) {
            arg.type = 'GET';
        }
        if (!arg.async) {
            arg.async = true;
        }
        if (!arg.dataType) {
            arg.dataType = 'json';
        }
        if (arg.data) {
            if (!arg.contentType) {
                arg.contentType = "application/json; charset=utf-8";
            }
            if (!arg.beforeSend) {
                arg.beforeSend = function(x) {
                    if (x && x.overrideMimeType) {
                        x.overrideMimeType(arg.contentType);
                    }
                };
            }
        }
        if (!arg.error) {
            arg.error = function(xhr, status, error) {
                arg.success({hasError:true,errorMsg:xhr.responseText});
            };
        }
        jQuery.ajax(arg);
    }


    ,asyncGet : function(callback, url, param) {
        this.ajax({url: url
            ,data: param
            ,success: function(response) {
                callback(response);
            }
        });
    }

    ,asyncPost : function(callback, url, param) {
        this.ajax({type: 'POST'
            ,url: url
            ,data: param
            ,success: function(response) {
                callback(response);
            }
        });
    }
    
    /*
     * This is an ajax form data submit, not a <form> with a form submit button type of submit.
     */
    ,asyncPostForm : function(callback, url, form) {
	    var postData = $(form).serializeArray();
        this.ajax({type: 'POST'
            ,url: url
            ,data : postData
            ,contentType: "application/x-www-form-urlencoded; charset=UTF-8"
            ,success: function(response) {
                callback(response);
            }
        });
    }

	,asyncPut : function(callback, url, param) {
	    jQuery.ajax({type: 'PUT'
	        ,url: url
	        ,data: param
	        ,success: function(response) {
                callback(response);
	        }
	    });
	}

};