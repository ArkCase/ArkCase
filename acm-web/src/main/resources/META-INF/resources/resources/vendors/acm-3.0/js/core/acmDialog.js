/**
 * Acm.Dialog is used to display Dialog/confirm/error messages to the user
 *
 * Current implementation is to use bootbox
 *
 * @author jwu
 */
Acm.Dialog = {
    create : function() {
    }

    ,DEFAULT_NO_TITLE:    undefined
    ,DEFAULT_NO_CALLBACK: undefined

    //
    // callback example:
    // function() {
    //    alert("callback");
    // }
    //
    ,info: function(msg, callback, title){
        var opt = {
            message: msg
        }
        if(Acm.isNotEmpty(title)) {
            opt.title = title;
        } else {
            opt.title = "Info";
        }
        if (Acm.isNotEmpty(callback)) {
            opt.callback = callback;
        }

        bootbox.alert(opt);
    }
    ,alert: function(msg, callback, title){
        if(Acm.isEmpty(title)) {
            title = "Alert";
        }
        this.info(msg, callback, title);
    }
    ,error: function(msg, callback, title){
        if(Acm.isEmpty(title)) {
            title = "Error";
        }
        this.info(msg, callback, title);
    }

    //
    // Usage example:
    // Acm.Dialog.confirm("Are you sure?"
    //     ,function(result) {
    //         if (result == true) {
    //             alert("Do it");
    //         } else {
    //             alert("Do nothing");
    //         }
    //     }
    //     ,"My Title"
    // }
    //
    ,confirm: function(msg, callback, title){
        if (Acm.isEmpty(callback)) {
            Acm.log("Confirm dialog needs callback");
            return;
        }

        bootbox.confirm(msg, callback);
    }

    //
    // callback example:
    // function(result) {
    //    if (null === result) {
    //        alert("Prompt dismissed");
    //    } else {
    //        alert("Prompt result:" + result);
    //    }
    // }
    //
    ,prompt: function(msg, callback, title){
        if (Acm.isEmpty(callback)) {
            Acm.log("Prompt dialog needs callback");
            return;
        }

        bootbox.prompt(msg, callback);
    }

    ,modal: function($s, onClickBtnPrimary, onClickBtnDefault) {
        if (onClickBtnPrimary) {
            $s.find("button.btn-primary").unbind("click").on("click", function(e){
                onClickBtnPrimary(e, this);
                $s.modal("hide");
            });
        }
        if (onClickBtnDefault) {
            $s.find("button.btn-default").unbind("click").on("click", function(e){onClickBtnDefault(e, this);});
        }

        $s.modal("show");
    }


    ,_popupWindow: null
    ,openWindow2: function(arg) {
        var $dfd = $.Deferred();

        try {
            if (window.focus) {
                this._popupWindow.focus();
            }
        }catch(e) {
            // Do nothing, normal behavior
        }

        try {
            if (Acm.isEmpty(this._popupWindow) || this._popupWindow.closed) {

                if (Acm.isNotEmpty(arg.w) && Acm.isNotEmpty(arg.h)) {
                    var dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : screen.left;
                    var dualScreenTop = window.screenTop != undefined ? window.screenTop : screen.top;

                    var width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
                    var height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;

                    var left = ((width / 2) - (arg.w / 2)) + dualScreenLeft;
                    var top = ((height / 2) - (arg.h / 2)) + dualScreenTop;

                    this._popupWindow = window.open(arg.url, arg.title, 'scrollbars=yes, resizable=1, width=' + arg.w + ', height=' + arg.h + ', top=' + top + ', left=' + left);
                } else if (Acm.isNotEmpty(arg.title)) {
                    this._popupWindow = window.open(arg.url, arg.title);
                } else {
                    this._popupWindow = window.open(arg.url);
                }


                if (window.focus) {
                    this._popupWindow.focus();
                }

                this._checkClosePopup2(this._popupWindow, $dfd);
            } else {
                if (window.focus) {
                    this._popupWindow.focus();
                }
                $dfd.resolve();
            }
        } catch (e) {
            // Do nothing, normal behavior
            $dfd.resolve();
        }

        return $dfd.promise();
    }
    ,_checkClosePopup2: function(newWindow, $dfd){
        var timer = setInterval(function() {
//            var href = null;
//            try{
//                if (newWindow && newWindow.location && newWindow.location.href){
//                    href = newWindow.location.href;
//                }
//            }catch(e){
//            }
//
//            if (href
//                &&
//                href.indexOf('/web/') == -1 &&
//                href.indexOf('/tn/') == -1 &&
//                href.indexOf('/user/') == -1 &&
//                href.indexOf('/app/') == -1 &&
//                href.indexOf('/formtype/') == -1 &&
//                href.indexOf('about:blank') == -1
//            )
//            {
//                newWindow.close();
//            }
            if(newWindow && newWindow.closed) {
                clearInterval(timer);
                $dfd.resolve();
            }
        }, 50);
    }
    ,openWindow: function(url, title, w, h, onDone) {
    	try {
    		if (window.focus) {
    			this._popupWindow.focus();
	        }
    	}catch(e) {
    		// Do nothing, normal behavior
    	}

    	try {
	    	if (this._popupWindow == null || this._popupWindow == 'undefined' || this._popupWindow.closed) {
	    	
		        var dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : screen.left;
		        var dualScreenTop = window.screenTop != undefined ? window.screenTop : screen.top;
		
		        var width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
		        var height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;
		
		        var left = ((width / 2) - (w / 2)) + dualScreenLeft;
		        var top = ((height / 2) - (h / 2)) + dualScreenTop;
		        
		        this._popupWindow = window.open(url, title, 'scrollbars=yes, resizable=1, width=' + w + ', height=' + h + ', top=' + top + ', left=' + left);
		
		        if (window.focus) {
		        	this._popupWindow.focus();
		        }
		
		        this._checkClosePopup(this._popupWindow, onDone);
	    	} else {
	    		if (window.focus) {
	    			this._popupWindow.focus();
		        }
	    	}
    	} catch (e) {
    		// Do nothing, normal behavior
    	}
    }

    ,_checkClosePopup: function(newWindow, onDone){
        var timer = setInterval(function() {
         	var href = null;
         	try{
			     if (newWindow && newWindow.location && newWindow.location.href){
			     	href = newWindow.location.href;
			     }
         	}catch(e){

         	}

         	if (href && 
     			href.indexOf('/web/') == -1 &&
     			href.indexOf('/tn/') == -1 &&
     			href.indexOf('/user/') == -1 &&
     			href.indexOf('/app/') == -1 &&
     			href.indexOf('/formtype/') == -1 &&
     			href.indexOf('about:blank') == -1)
         		{
         			newWindow.close();
         		}
            if(newWindow.closed) {
                clearInterval(timer);
                if (onDone) {
                    onDone();
                }
            }
        }, 50);
    }
}