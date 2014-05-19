/**
 * Topbar.Event
 *
 * event handlers for objects
 *
 * @author jwu
 */
Topbar.Event = {
    initialize : function() {
    }

    ,onClickLnkNewComponent : function(chkSelectAll) {
        Acm.Service.gotoComplaintWizard();
    }

    ,onPostInit: function() {
    }
};
