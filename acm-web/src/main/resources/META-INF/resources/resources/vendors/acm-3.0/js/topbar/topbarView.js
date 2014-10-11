/**
 * Topbar.View
 *
 * @author jwu
 */
Topbar.View = {
    create : function() {
        if (Topbar.View.Asn.create) {Topbar.View.Asn.create();}
    }
    ,initialize: function() {
        if (Topbar.View.Asn.initialize) {Topbar.View.Asn.initialize();}
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
                            Topbar.Controller.Asn.onViewChangedAsnAction(asnId, Topbar.Model.Asn.ACTION_EXPIRED);
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
            Topbar.Controller.Asn.onViewChangedAsnAction(asnId, Topbar.Model.Asn.ACTION_ACK);
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


        ,ctrlUpdateAsnList: function(asnList) {
            Topbar.View.Asn.updateAsnCount(asnList);
            Topbar.View.Asn.showAsnList(asnList);
        }
        ,ctrlNotifyAsnListError: function(errorMsg) {
            Acm.Dialog.error("Failed to retrieve notifications:" + errorMsg);
        }
        ,ctrlNotifyAsnListUpdateError: function(errorMsg) {
            Acm.Dialog.error("Failed to update notifications:" + errorMsg);
        }
        ,ctrlNotifyAsnListUpdateSuccess: function() {
            Acm.Dialog.error("Notifications updated");
        }

    } //Asn
};

