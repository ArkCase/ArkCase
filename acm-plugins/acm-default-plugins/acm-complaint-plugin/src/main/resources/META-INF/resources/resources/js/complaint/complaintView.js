/**
 * Complaint.View
 *
 * @author jwu
 */
Complaint.View = Complaint.View || {
    create : function() {
        if (Complaint.View.MicroData.create)       {Complaint.View.MicroData.create();}

    }
    ,onInitialized: function() {
        if (Complaint.View.MicroData.onInitialized)      {Complaint.View.MicroData.onInitialized();}
    }

    ,MicroData: {
        create : function() {
        }
        ,onInitialized: function() {
        }

    }


};

