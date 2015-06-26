/**
 * Acm.Service
 *
 * @author jwu
 */
Acm.Service = {
    create: function() {
    }


    ,_contextPath: null
    ,getContextPath: function() {
        return this._contextPath;
    }
    ,setContextPath: function(contextPath) {
        this._contextPath = contextPath;
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

        if (!arg.nonAcmUrl) {
            var contextPath = this.getContextPath();
            if (contextPath) {
                if (0 != arg.url.indexOf(contextPath)) {
                    arg.url = contextPath + arg.url;
                }
            }
        }

        arg.url = Acm.Url.makeNoneCacheUrl(arg.url);

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

        if (arg.error) {
            arg.errorCallback = arg.error;
        } else {
            arg.errorCallback = function(xhr, status, error) {
                if (arg.success) {
                    arg.success({hasError:true, errorMsg:xhr.responseText, errorCode:xhr.status});
                }
            };
        }
        arg.error = function(xhr, status, error) {
            var errorCount = App.Model.Login.getErrorCount();
            App.Model.Login.setErrorCount(errorCount + 1);

            arg.errorCallback(xhr, status, error);
        }
        return jQuery.ajax(arg);
    }

    ,call : function(arg) {
        var $dfd = jQuery.Deferred();

        if (arg.success) {
            arg.successCallback = arg.success;
        }
        arg.success = function(response) {
            if (!response.hasError) {
                App.Model.Login.setErrorCount(0);
            }

            if (arg.successCallback) {
                arg.successCallback(response);
                $dfd.resolve(response);

            } else if (arg.callback) {
                var rc = arg.callback(response);
                if (response.hasError) {
                    $dfd.reject(response);
                } else if (true === rc) {
                    $dfd.resolve(response);
                } else if (Acm.isNotEmpty(rc)) {
                    $dfd.resolve(rc);
                } else {
                    if (arg.invalid) {
                        rc = arg.invalid(response);
                    } else {
                        rc = arg.callback({hasError: true, errorMsg: "Invalid response from service " + arg.url, errorCode: 0});
                    }
                    if (Acm.isNotEmpty(rc)) {
                        $dfd.reject(rc);
                    } else {
                        $dfd.reject(response);
                    }
                }
            }
        };
        this.ajax(arg);
        return $dfd.promise();
    }

    ///////////////////////////////////////////////////

    ,promise : function(arg) {
        Acm.log("Acm.Service.promise() is phasing out. Please use Acm.Service.call() instead");
        return this.call(arg);
    }

    ,_process2: function($dfd, response, arg) {
        if (arg.successCallback) {
            arg.successCallback(response);
            $dfd.resolve(response);

        } else if (arg.callback) {
            var rc = arg.callback(response);
            if (response.hasError) {
                $dfd.reject(response);
            } else if (true === rc) {
                $dfd.resolve(response);
            } else if (Acm.isNotEmpty(rc)) {
                $dfd.resolve(rc);
            } else {
                if (arg.invalid) {
                    rc = arg.invalid(response);
                } else {
                    rc = arg.callback({hasError: true, errorMsg: "Invalid response from service " + arg.url, errorCode: 0});
                }
                if (Acm.isNotEmpty(rc)) {
                    $dfd.reject(rc);
                } else {
                    $dfd.reject(response);
                }
            }
        }
    }

    ,call_save : function(arg) {
        return this.ajax({type: arg.type
            ,url: arg.url
            ,data: arg.data
            ,success: function(response) {
                if (!response.hasError) {
                    App.Model.Login.setErrorCount(0);
                }

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
                    arg.callback({hasError: true, errorMsg: "Invalid response from service " + arg.url, errorCode: 0});
                }
            }
        }
    }


    ,asyncGet : function(callback, url, param) {
        //Acm.log("Acm.Service.asyncGet() is phasing out. Please use Acm.Service.call() instead");
        return this.ajax({url: url
            ,data: param
            ,success: function(response) {
                callback(response);
            }
        });
    }

    ,asyncPost : function(callback, url, param) {
        //Acm.log("Acm.Service.asyncPost() is phasing out. Please use Acm.Service.call() instead");
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

    ,asyncPostFormData : function(callback, url, formData) {
        //Acm.log("Acm.Service.asyncPost() is phasing out. Please use Acm.Service.call() instead");
        return this.ajax({
            type: 'POST'
            ,url: url
            ,cache: false
            ,contentType: false
            ,processData: false
            ,data: formData
            ,success: function(response) {
                callback(response);
            }
        });
    }

    /*
     * This is an ajax form data submit, not a <form> with a form submit button type of submit.
     */
    ,asyncPostForm : function(callback, url, form) {
        Acm.log("Acm.Service.asyncPostForm() is phasing out. Please use Acm.Service.call() instead");
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
        Acm.log("Acm.Service.asyncPut() is phasing out. Please use Acm.Service.call() instead");
        return this.ajax({type: 'PUT'
            ,url: url
            ,data: param
            ,success: function(response) {
                callback(response);
            }
        });
    }

    ,asyncDelete : function(callback, url) {
        Acm.log("Acm.Service.asyncDelete() is phasing out. Please use Acm.Service.call() instead");
        return this.ajax({type: 'DELETE'
            ,url: url
            ,success: function(response) {
                callback(response);
            }
        });
    }


    ,deferredGet: function(callbackSuccess, url, param) {
        Acm.log("Acm.Service.deferredGet() is phasing out. Please use Acm.Service.call() instead");
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