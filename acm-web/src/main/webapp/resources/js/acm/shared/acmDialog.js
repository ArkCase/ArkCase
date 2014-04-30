/**
 * Acm.Dialog is used to display Dialog/confirm/error messages to the user
 *
 * Capabilities:
 *
 *   + showDialogDialog - show an Dialog message to the user
 *   + showConfirmDialog - show an Dialog message to the user and get back confirmation
 *   + showErrorDialog - show an error message to the user
 *
 * @author dmcclure
 */
Acm.Dialog = {
    initialize : function() {
    }

    ,show: function(msgTop, msgBottom, callback, title){
        if(undefined === title) {
            title = 'Dialog'
        }
        if(undefined === msgBottom) {
            msgBottom = '';
        }
        jDialog("<strong>" + msgTop + "</strong>\n\n" + msgBottom, title, callback);
    }

    ,showNotice: function(msgTop, msgBottom, callback, title){
        if(undefined === title) {
            title = 'Notice'
        }
        if(undefined === msgBottom) {
            msgBottom = '';
        }
        jNotify("<strong>" + msgTop + "</strong>\n\n" + msgBottom, title, callback);
    }

    ,showConfirm: function(msgTop, msgBottom, callback, title){
        if(undefined === title) {
            title = 'Confirm';
        }
        if(undefined === msgBottom) {
            msgBottom = '';
        }
        jConfirm("<strong>" + msgTop + "</strong>\n\n" + msgBottom, title, callback);
    }

    ,showError: function(msgTop, msgBottom, title){
        if(undefined === title) {
            title = 'Error';
        }
        if(undefined === msgBottom) {
            msgBottom = '';
        }
        //jDialog("<strong>" + msgTop + "</strong>\n\n" + msgBottom, title);
        alert("<strong>" + msgTop + "</strong>\n\n" + msgBottom, title);
    }

    ,showErrorPositioned: function(msgTop, msgBottom, title, pos){
        if(undefined === title) {
            title = 'Error';
        }
        if(undefined === msgBottom) {
            msgBottom = '';
        }
        jPosDialog("<strong>" + msgTop + "</strong>\n\n" + msgBottom, title, pos);
    }

}