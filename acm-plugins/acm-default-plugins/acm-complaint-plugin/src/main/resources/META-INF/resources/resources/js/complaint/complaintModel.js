/**
 * Complaint.Model
 *
 * @author jwu
 */
Complaint.Model = Complaint.Model || {
    create : function() {
        if (Complaint.ServiceNew.create)       {Complaint.ServiceNew.create();}
    }
    ,onInitialized: function() {
        if (Complaint.ServiceNew.onInitialized)       {Complaint.ServiceNew.onInitialized();}
    }

    ,_objectType: "COMPLAINT"
    ,getObjectType: function() {
        return this._objectType;
    }

    ,getObjectId: function() {
        return Complaint.getComplaintId();
    }
};

