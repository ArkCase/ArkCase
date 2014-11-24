/**
 * Topbar.View
 *
 * @author jwu
 */
Topbar.View = {
    create : function() {
        if (Topbar.View.QuickSearch.create) {Topbar.View.QuickSearch.create();}
        if (Topbar.View.Suggestion.create)  {Topbar.View.Suggestion.create();}
        if (Topbar.View.Asn.create)         {Topbar.View.Asn.create();}
    }
    ,initialize: function() {
        if (Topbar.View.QuickSearch.initialize) {Topbar.View.QuickSearch.initialize();}
        if (Topbar.View.Suggestion.initialize)  {Topbar.View.Suggestion.initialize();}
        if (Topbar.View.Asn.initialize)         {Topbar.View.Asn.initialize();}
    }

    ,QuickSearch: {
        create: function() {
            this.$formSearch = $("form[role='search']");
            this.$edtSearch = this.$formSearch.find("input.typeahead");
            this.$btnSearch = this.$formSearch.find("button[type='submit']");

            this.$formSearch.on("submit", function() {Topbar.View.QuickSearch.onSubmitFormSearch(this);});
            this.$btnSearch .on("click", function(e) {Topbar.View.QuickSearch.onClickBtnSearch(e, this);});

            this.$formSearch.attr("method", "get");
            var term = Topbar.Model.QuickSearch.getQuickSearchTerm();
            this.setActionFormSearch(term);
        }
        ,initialize: function() {
        }

        ,onClickBtnSearch : function(event, ctrl) {
            var term = this.getValueEdtSearch();
            this.setActionFormSearch(term);
            //event.preventDefault();
        }

        ,onSubmitFormSearch : function(ctrl) {
            var term = this.getValueEdtSearch();
            Topbar.Controller.QuickSearch.viewChangedQuickSearchTerm(term);
            return false;
        }

        ,setActionFormSearch: function(term) {
            var url = App.getContextPath() + "/plugin/search"
            if (Acm.isNotEmpty(term)) {
                url += "?q=" + term;
            }
            this.$formSearch.attr("action", url);
        }
        ,getValueEdtSearch: function() {
            return Acm.Object.getPlaceHolderInput(this.$edtSearch);
        }
        ,setValueEdtSearch: function(val) {
            return Acm.Object.setPlaceHolderInput(this.$edtSearch, val);
        }
    }

    ,Suggestion: {
        create: function() {
            this.$formSearch = $("form[role='search']");
            this.$edtSearch = this.$formSearch.find("input.typeahead");
            this.useTypeAhead(this.$edtSearch);

            Acm.Dispatcher.addEventListener(Topbar.Controller.Suggestion.MODEL_CHANGED_SUGGESTION, this.onModelChangedSuggestion);
        }
        ,initialize: function() {
        }

        ,onModelChangedSuggestion: function(process) {
            process(Topbar.Model.Suggestion.getKeys());
        }

        ,useTypeAhead: function($s) {
            $s.typeahead({
                source: function ( query, process ) {
                    _.debounce(Topbar.Service.Suggestion.retrieveSuggestion( query, process ), 300);
                }
                ,highlighter: function( item ){
                    html = '<div class="ctr">';
                    var ctr = Topbar.Model.Suggestion.getObject(item);
                    if (ctr) {
                        var icon = "";
                        var type = Acm.goodValue(ctr.object_type_s, "UNKNOWN");
                        if (type == "COMPLAINT") {
                            icon = '<i class="i i-notice i-2x"></i>';
                        } else if (type == "CASE") {
                            icon = '<i class="i i-folder i-2x"></i>';
                        } else if (type == "TASK") {
                            icon = '<i class="i i-checkmark i-2x"></i>';
                        } else if (type == "DOCUMENT") {
                            icon = '<i class="i i-file i-2x"></i>';
                        } else {
                            icon = '<i class="i i-circle i-2x"></i>';
                        }

                        html += '<div class="icontype">' + icon + '</div>'
                            + '<div class="title">' + Acm.goodValue(ctr.title_t) + '</div>'
                            + '<div class="identifier">' + Acm.goodValue(ctr.name) + ' ('+ Acm.goodValue(ctr.object_type_s) + ')' + '</div>'
                            + '<div class="author">By ' + ctr.author  + ' on '+ Acm.getDateTimeFromDatetime(ctr.last_modified) + '</div>'
                        html += '</div>';
                    }
                    return html;
                }
                , updater: function ( selectedtitle ) {
                    var ctr = Topbar.Model.Suggestion.getObject(selectedtitle);
                    $( "#ctrId" ).val(ctr.object_id_s);
                    return selectedtitle;
                }
                ,hint: true
                ,highlight: true
                ,minLength: 1

            }); //end $s.typeahead
        }
    }

    ,Asn: {
        create: function() {
            this.$ulAsn = $("ul.nav-user");
            this.$spanCntWarning = $('a .count', this.$ulAsn);
            this.$spanCntTotal = $('header .count', this.$ulAsn);
            this.$divAsnList = $('.list-group', this.$ulAsn);
            this.$lnkAsn = $("ul.nav-user a[data-toggle='dropdown']");
            this.$lnkAsn.on("click", function(e) {Topbar.View.Asn.onClickLnkAsn(e, this);});
            this.$sectionAsn = this.$divAsnList.closest("section.dropdown-menu");

            Acm.Dispatcher.addEventListener(Topbar.Controller.Asn.MODEL_RETRIEVED_ASN_LIST        ,this.onModelRetrievedAsnList);

        }
        ,initialize: function() {
        }


        ,_removeAsnFromPopup: function(asnIdToRemove) {
            //if only one child left, remove whole list
            var $children = this.$divAsnList.children();
            var childCnt = $children.length;
            if (1 >= childCnt) {
                this.closeAsnList();
                return;
            }

            $children.each(function(i){
                var $hidAsnId = $(this).find("input[name='asnId']");
                var asnId = $hidAsnId.val();
                if (asnId) {
                    if (asnIdToRemove == asnId) {
                        $(this).remove();
                    }
                }
            }); //end each
        }
        ,_registerToRemove: function(asnList) {
            for (var i = 0; i < asnList.length; i++) {
                var asn = asnList[i];
                if (asn && asn.id) {
                    Acm.Timer.registerListener(asn.id
                        ,8
                        ,function(asnId) {
                            Topbar.View.Asn._removeAsnFromPopup(asnId);
                            Topbar.Controller.Asn.viewChangedAsnAction(asnId, Topbar.Model.Asn.ACTION_EXPIRED);
                            return false;
                        }
                    );
                }
            }
        }
        ,showAsnList: function(asnList) {
            var visibleAsnList = Acm.Object.isVisible(this.$divAsnList);
            var visibleAsnHeader = Acm.Object.isVisible(this.$divAsnList.prev());
            if (!visibleAsnList) {          //no list is shown, popup new ASNs
                var asnListNew = Topbar.Model.Asn.buildAsnListNew(asnList);
                this.$divAsnList.empty();
                this.$divAsnList.prev().hide();
                this.$divAsnList.next().hide();
                this._buildAsnListUiDropdown(asnListNew);
                this._registerToRemove(asnListNew);
                this.$sectionAsn.fadeIn();

            } else if (!visibleAsnHeader) { //ASN popup is already shown, update new ASN
                var newMore = Topbar.Model.Asn.getAsnListNewMore(asnList);
                this._buildAsnListUiPopup(newMore);
                this._registerToRemove(newMore);

                var noLonger = Topbar.Model.Asn.getAsnListNewNoLonger(asnList);
                for (var j = 0; j < noLonger.length; j++) {
                    var asn = noLonger[j];
                    if (asn && asn.id) {
                        this._removeAsnFromPopup(asn.id);
                    }
                } //for j

                Topbar.Model.Asn.buildAsnListNew(asnList);

            } else {        //user is viewing ASN list; do nothing
                return;
            }
        }
        ,closeAsnList: function() {
            this.$sectionAsn.fadeOut();
//            this.$divAsnList.empty();
//            this.$divAsnList.prev().hide();
//            this.$divAsnList.next().hide();
        }
        ,onClickLnkAsn: function(event, ctrl) {
            var asnList = Topbar.Model.Asn.getAsnList();
            var countTotal = this._getAsnCount(asnList);
            this.setTextSpanCntTotal(countTotal);

            this.$divAsnList.empty();
            this.$divAsnList.prev().show();
            this.$divAsnList.next().show();

            this._buildAsnListUi(asnList);
            this.$sectionAsn.toggle();
        }
        ,onClickBtnMarkAsRead: function(event, ctrl) {
            var $self = $(ctrl);
            var $msg = $self.closest("div.list-group-item");
            var $hidAsnId = $msg.find("input[name='asnId']");
            var asnId = $hidAsnId.val();
            alert("onClickBtnMarkAsRead, asnId=" + asnId);
            var z = 1;
        }
        ,onClickBtnSeeResult: function(event, ctrl) {
            var $self = $(ctrl);
            var $msg = $self.closest("div.list-group-item");
            var $hidAsnId = $msg.find("input[name='asnId']");
            var asnId = $hidAsnId.val();
            alert("onClickBtnSeeResult, asnId=" + asnId);
            var z = 1;
        }
        ,onClickBtnAck: function(event, ctrl) {
            var $self = $(ctrl);
            var $msg = $self.closest("div.list-group-item");
            var $hidAsnId = $msg.find("input[name='asnId']");
            var asnId = $hidAsnId.val();

            Topbar.View.Asn._removeAsnFromPopup(asnId);
            Topbar.Controller.Asn.viewChangedAsnAction(asnId, Topbar.Model.Asn.ACTION_ACK);
        }
        ,_buildAsnListUiDropdown: function(asnList) {
            this._buildAsnListUi(asnList, "dropdown");
        }
        ,_buildAsnListUiPopup: function(asnList) {
            this._buildAsnListUi(asnList, "popup");
        }
        ,_buildAsnListUiLocal: function(asnList) {
            this._buildAsnListUi(asnList, "local");
        }
        ,_buildAsnListUi: function(asnList, type) {
            var countTotal = this._getAsnCount(asnList);
            for (var i = 0; i < countTotal; i++) {
                var asn = asnList[i];
                var msg = "<div class='media list-group-item "
                        + asn.status
                        + "><a href=''#'><span class='pull-left thumb-sm text-center'>"
                        + "<i class='fa fa-file fa-2x text-success'></i></span>"
                        + "<span class='media-body block m-b-none'>"
                        + Acm.goodValue(asn.note)
                        + "<br><small class='text-muted'>"
                        + Acm.goodValue(asn.created)
                        + "</small></span></a><input type='hidden' name='asnId' value='"
                        + Acm.goodValue(asn.id)
                        + "' /><input type='button' name='markAsRead' value='MarkAsRead'/>"
                        + "<input type='button' name='seeResult' value='See Result'/>"
                    ;

                if ("New" == Acm.goodValue(asn.action)) {
                    msg += "<input type='button' name='ack' value='Acknowledge'/>";
                }
                msg += "</div>";

                $(msg).hide().prependTo(this.$divAsnList)
                    .css('display','block')
                    //.slideDown()
                    //.fadeToggle()
                ;
            } //for i

            this.$divAsnList.find("input[name='markAsRead']").unbind("click").on("click", function(e) {Topbar.View.Asn.onClickBtnMarkAsRead(e, this);});
            this.$divAsnList.find("input[name='seeResult']") .unbind("click").on("click", function(e) {Topbar.View.Asn.onClickBtnSeeResult(e, this);});
            this.$divAsnList.find("input[name='ack']")       .unbind("click").on("click", function(e) {Topbar.View.Asn.onClickBtnAck(e, this);});

        }
        ,_getAsnCount: function(asnList) {
            var count = 0;
            if (Acm.isArray(asnList)) {
                count = asnList.length;
            }
            return count;
        }
        ,updateAsnCount: function(asnList) {
            var countTotal = this._getAsnCount(asnList);
            var countNew = 0;
            for (var i = 0; i < countTotal; i++) {
                var asn = asnList[i];
                if ("New" == Acm.goodValue(asn.action)) {
                    countNew++;
                }
            }
            this.setTextSpanCntWarning(countTotal);
            this.warnSpanCntWarning(0 < countNew);
        }
        ,setTextSpanCntWarning: function(text) {
            this.$spanCntWarning.fadeOut().fadeIn().text(text);
        }
        ,warnSpanCntWarning: function(warn) {
            if (warn) {
                this.$spanCntWarning.addClass("bg-danger");
            } else {
                this.$spanCntWarning.removeClass("bg-danger");
            }
        }
        ,setTextSpanCntTotal: function(text) {
            this.$spanCntTotal.text(text);
        }


        ,onModelRetrievedAsnList: function(asnList) {
            if (asnList.hasError) {
                Acm.Dialog.error("Failed to retrieve notifications:" + asnList.errorMsg);
            } else {
                Topbar.View.Asn.updateAsnCount(asnList);
                Topbar.View.Asn.showAsnList(asnList);
            }
        }
//        ,ctrlNotifyAsnListError: function(errorMsg) {
//            Acm.Dialog.error("Failed to retrieve notifications:" + errorMsg);
//        }
        ,ctrlNotifyAsnListUpdateError: function(errorMsg) {
            Acm.Dialog.error("Failed to update notifications:" + errorMsg);
        }
        ,ctrlNotifyAsnListUpdateSuccess: function() {
            Acm.Dialog.error("Notifications updated");
        }

    } //Asn
};

