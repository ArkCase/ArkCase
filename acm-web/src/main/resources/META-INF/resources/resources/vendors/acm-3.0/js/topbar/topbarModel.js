/**
 * Topbar.Model
 *
 * @author jwu
 */
Topbar.Model = {
    create : function() {
        if (Topbar.Model.Asn.create) {this.Asn.create();}
    }
    ,initialize: function() {
        if (Topbar.Model.Asn.initialize) {this.Asn.initialize();}
    }

    ,Asn: {
        create : function() {
            this._asnListData = new Acm.Model.SessionData("AcmAsnList");
        }

        ,STATUS_READ    : "Read"
        ,STATUS_UNREAD  : "Unread"
        ,STATUS_DELETED : "Deleted"

        ,ACTION_NEW     : "New"
        ,ACTION_ACK     : "Ack"
        ,ACTION_EXPIRED : "Expired"

        ,_asnListData: null
        ,getAsnList: function() {
            return this._asnListData.get();
        }
        ,setAsnList: function(asnList) {
            this._asnListData.set(asnList);
        }
        ,findAsn: function(asnId, asnList) {
            var found = null;
            if (asnList) {
                for (var i = 0; i < asnList.length; i++) {
                    var asn = asnList[i];
                    if (asn && asn.id) {
                        if (asn.id == asnId) {
                            found = asn;
                            break;
                        }
                    }
                }
            }
            return found;
        }

        ,_asnListNew: []
        ,getAsnListNew: function() {
            return this._asnListNew;
        }
//    ,setAsnListNew: function(asnListNew) {
//        this._asnListNew = asnListNew;
//    }
        ,buildAsnListNew: function(asnList) {
            this._asnListNew = [];
            if (asnList) {
                for (var i = 0; i < asnList.length; i++) {
                    var asn = asnList[i];
                    if (asn.action && "New" == asn.action) {
                        this._asnListNew.push(asn);
                    }
                }
            }
            return this._asnListNew;
        }
        ,getAsnListNewMore: function(asnList) {
            var asnListNewMore = [];
            if (!asnList  || !(asnList instanceof Array)) {
                return asnListNewMore;
            }

            var asnListNew = this.getAsnListNew();
            for (var i = 0; i < asnList.length; i++) {
                var asn = asnList[i];
                if (asn.action && "New" == asn.action) {
                    var found = null;
                    for (var j = 0; j < asnListNew.length; j++) {
                        var asnNew = asnListNew[j];
                        if (asn.id == asnNew.id) {
                            found =asn;
                            break;
                        }
                    }
                    if (null == found) {
                        asnListNewMore.push(asn);
                    }
                }
            }

            return asnListNewMore;
        }
        ,getAsnListNewNoLonger: function(asnList) {
            var asnListNewNoLonger = [];
            if (!asnList  || !(asnList instanceof Array)) {
                return asnListNewNoLonger;
            }

            var asnListNew = this.getAsnListNew();
            for (var i = 0; i < asnListNew.length; i++) {
                var asnNew = asnListNew[i];
                var found = null;
                for (var j = 0; j < asnList.length; j++) {
                    var asn = asnList[j];
                    if (asn.action && "New" == asn.action) {
                        if (asn.id == asnNew.id) {
                            found =asn;
                            break;
                        }
                    }
                }
                if (null == found) {
                    asnListNewNoLonger.push(asnNew);
                }
            }

            return asnListNewNoLonger;
        }


        ,ctrlRetrieveAsnList: function(user) {
            Topbar.Service.Asn.retrieveAsnList(user);
        }
        ,ctrlUpdateAsnAction: function(asnId, action) {
            var asnList = this.getAsnList();
            var asn = this.findAsn(asnId, asnList);
            if (asn) {
                asn.action = action;
                this.setAsnList(asnList);
            }
            Topbar.Service.Asn.updateAsnList(asnList);
        }
    } //Asn

};

