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
//    ,validateAudit: function(data) {
//        return Acm.Validator.validateSolrData(data);
//    }

};

