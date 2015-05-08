/**
 * Acm.Service
 *
 * @author jwu
 */
Acm.Service = {
    create: function() {
    }

    ,ajax: function(arg) {
        if (!arg.type) {
            arg.type = 'GET';
        }
        if (!arg.async) {
            arg.async = true;
        }
        if (!arg.dataType) {
            arg.dataType = 'json';
        }
        if (Acm.isNotEmpty(arg.data)) {
            if (Acm.isEmpty(arg.contentType)) {
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
                //for compatible with v1.0, until refactor to v2.0
//                if (arg.callback) {
//                    arg.callback({hasError:true, errorMsg:xhr.responseText});
//                } else
                if (arg.success) {
                    arg.success({hasError:true, errorMsg:xhr.responseText});
                }

                //v2.0, after refactor v1.0 to v2.0, remove above and uncomment below
//                if (arg.callback) {
//                    arg.callback({hasError:true, errorMsg:xhr.responseText});
//                }
            };
        }
        return jQuery.ajax(arg);
    }

    ,call : function(arg) {
        return this.ajax({type: arg.type
            ,url: arg.url
            ,data: arg.data
            ,success: function(response) {
                Acm.Service._process(response, arg);
            }
        });
    }

    ,asyncGet : function(callback, url, param) {
        return this.ajax({url: url
            ,data: param
            ,success: function(response) {
                callback(response);
            }
        });
    }

    ,asyncPost : function(callback, url, param) {
        return this.ajax({type: 'POST'
            ,url: url
            ,data: param
            ,success: function(response) {
                callback(response);
            }
        });
    }
    ,asyncPost2 : function(arg) {
        return this.ajax({type: 'POST'
            ,url: arg.url
            ,data: arg.data
            ,success: function(response) {
                Acm.Service._process(response, arg);
            }
        });
    }
    ,_process: function(response, arg) {
        if (arg.success) {
            arg.success(response);

        } else if (arg.callback) {
            var happy = arg.callback(response);
            if (!response.hasError && !happy) {
                if (arg.invalid) {
                    arg.invalid(response);
                } else {
                    arg.callback({hasError: true, errorMsg: "Invalid response from service " + arg.url});
                }
            }
        }
    }
    
    /*
     * This is an ajax form data submit, not a <form> with a form submit button type of submit.
     */
    ,asyncPostForm : function(callback, url, form) {
	    var postData = $(form).serializeArray();
        return this.ajax({type: 'POST'
            ,url: url
            ,data : postData
            ,contentType: "application/x-www-form-urlencoded; charset=UTF-8"
            ,success: function(response) {
                callback(response);
            }
        });
    }

    ,asyncPut : function(callback, url, param) {
        return this.ajax({type: 'PUT'
            ,url: url
            ,data: param
            ,success: function(response) {
                callback(response);
            }
        });
    }

    ,asyncDelete : function(callback, url) {
        return this.ajax({type: 'DELETE'
            ,url: url
            ,success: function(response) {
                callback(response);
            }
        });
    }


    ,deferredGet: function(callbackSuccess, url, param) {
        return $.Deferred(function ($dfd) {
            var arg = {
                url: url
                ,type: 'GET'
                ,dataType: 'json'
                ,success: function (data) {
                    var rc = null;
                    if (data) {
                        rc = callbackSuccess(data);
                    }

                    if (rc) {
                        $dfd.resolve(rc);
                    } else {
                        $dfd.reject();
                    }
                }
                ,error: function () {
                    $dfd.reject();
                }
            };
            if (param) {
                arg.data = param;
            }
            $.ajax(arg);
        });
    }


    ,responseWrapper: function(response, data) {
        if (response.hasError) {
            return response;
        } else if (data) {
            return data;
        } else {
            return {hasError:true, errorMsg:"Null data"};
        }
    }
};