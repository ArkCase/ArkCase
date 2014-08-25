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

    ,onClickBtnSearch : function(e) {
        var term = Topbar.Object.getValueEdtSearch();
        Topbar.Object.setActionFormSearch(term);
        //e.preventDefault();
    }

    ,onSubmitFormSearch : function(e) {
        var term = Topbar.Object.getValueEdtSearch();
        Topbar.Object.setQuickSearchTerm(term);
        return false;
    }

    ,onPostInit: function() {
        //Topbar.Service.getTypeAheadTerms();
    }


// jw: not working code commented out. will comeback later
//    ,onClickBtnTest1 : function(e) {
//        var msg = '<div class="media list-group-item">'+
//            '<span class="pull-left thumb-sm text-center">'+
//            '<i class="fa fa-file fa-2x text-success"></i>'+
//            '</span>'+
//            '<span class="media-body block m-b-none">'+
//            '1David Miller assigned a document to you.<br>'+
//            '<small class="text-muted">1 minutes ago</small>'+
//            '</span>'+
//            '</div';
//        var $msg = $(msg);
//
//        var msg2 = '<div class="media list-group-item">'+
//            '<span class="pull-left thumb-sm text-center">'+
//            '<i class="fa fa-file fa-2x text-success"></i>'+
//            '</span>'+
//            '<span class="media-body block m-b-none">'+
//            '2David Miller assigned a document to you.<br>'+
//            '<small class="text-muted">1 minutes ago</small>'+
//            '</span>'+
//            '</div>';
//        var $msg2 = $(msg2);
//        var $el = $('.nav-user');
//
//        this.$divAsnList = $el.find('.list-group');
//        this.$divAsnList.empty();
//        this.$divAsnList.prev().hide();
//        this.$divAsnList.next().hide();
//        $msg.hide().prependTo(this.$divAsnList).slideDown()
//            //.css('display','block')
//        ;
//        $msg2.hide().prependTo(this.$divAsnList).slideDown()
//            //.css('display','block')
//        ;
//
//        this.$divAsnList.closest("section.dropdown-menu")
//            //.slideToggle()
//            .fadeIn()
//        ;
//    }
//    ,onClickBtnTest2 : function(e) {
//        var msg3 = '<a href="#" class="media list-group-item">'+
//            '<span class="pull-left thumb-sm text-center">'+
//            '<i class="fa fa-file fa-2x text-success"></i>'+
//            '</span>'+
//            '<span class="media-body block m-b-none">'+
//            '2David Miller assigned a document to you.<br>'+
//            '<small class="text-muted">1 minutes ago</small>'+
//            '</span>'+
//            '</a>';
//        var $msg3 = $(msg3);
//        var $el = $('.nav-user');
//
//        this.$divAsnList = $el.find('.list-group');
//        this.$divAsnList.empty();
//        this.$divAsnList.prev().hide();
//        this.$divAsnList.next().hide();
//
//
//
//        this.$divAsnList.closest("section.dropdown-menu")
//            //.slideToggle()
//            .fadeOut()
//        ;
//    }
//
//    ,onClickBtnTest3 : function(e) {
//        var msg = '<a href="#" class="media list-group-item">'+
//            '<span class="pull-left thumb-sm text-center">'+
//            '<i class="fa fa-file fa-2x text-success"></i>'+
//            '</span>'+
//            '<span class="media-body block m-b-none">'+
//            '1David Miller assigned a document to you.<br>'+
//            '<small class="text-muted">1 minutes ago</small>'+
//            '</span>'+
//            '</a>';
//        var $msg = $(msg);
//        var $el = $('.nav-user');
//
//        this.$divAsnList = $el.find('.list-group');
//        $msg.hide().prependTo(this.$divAsnList).slideDown()
//            //.css('display','block')
//        ;
//    }
//    ,onClickBtnTest4 : function(e) {
//        var $el = $('.nav-user');
//
//        this.$divAsnList = $el.find('.list-group');
//        var child =  this.$divAsnList.children().eq(1);
//        if (child) {
//            child.remove();
//        }
//    }
//    ,onClickLnkAsn: function(e) {
//        var msg4 = '<a href="#" class="media list-group-item">'+
//            '<span class="pull-left thumb-sm text-center">'+
//            '<i class="fa fa-file fa-2x text-success"></i>'+
//            '</span>'+
//            '<span class="media-body block m-b-none">'+
//            '2David Miller assigned a document to you.<br>'+
//            '<small class="text-muted">1 minutes ago</small>'+
//            '</span>'+
//            '</a>';
//        var $msg4 = $(msg4);
//        var $el = $('.nav-user');
//
//        this.$divAsnList = $el.find('.list-group');
//        this.$divAsnList.empty();
//        this.$divAsnList.prev().show();
//        this.$divAsnList.next().show();
//        $msg4.hide().prependTo(this.$divAsnList)
//            .slideDown()
//            //.fadeToggle()
//            //.css('display','block')
//        ;
//
//        this.$divAsnList.closest("section.dropdown-menu")
//            //.slideToggle()
//            .toggle()
//        ;
//    }

};
