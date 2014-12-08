/**
 * Audit.Model
 *
 * @author jwu
 */
Audit.Model = Audit.Model || {
    create : function() {
        this.cacheAuditList = new Acm.Model.CacheFifo(4);
    }
    ,onInitialized: function() {
    }

    ,_totalCount: 0
    ,getTotalCount: function() {
        return this._totalCount;
    }
    ,setTotalCount: function(totalCount) {
        this._totalCount = totalCount;
    }

    ,validateAudit: function(data) {
        if (Acm.isEmpty(data)) {
            return false;
        }
        if (Acm.isEmpty(data.totalCount)) {
            return false;
        }
        if (!Acm.isArray(data.resultPage)) {
            return false;
        }
        return true;
    }

};

