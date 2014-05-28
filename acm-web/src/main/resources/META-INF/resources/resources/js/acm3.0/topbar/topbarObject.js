/**
 * Topbar.Object
 *
 * manages screen objects
 *
 * @author jwu
 */
Topbar.Object = {
    initialize : function() {
        this.$lnkNewComponent      = jQuery("#lnkNewComponent");

        this.$lnkNewComponent.click(function() {Topbar.Event.onClickLnkNewComponent(this);});
    }

};




