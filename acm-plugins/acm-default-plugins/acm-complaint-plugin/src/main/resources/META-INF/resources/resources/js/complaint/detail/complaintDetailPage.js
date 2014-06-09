/**
 * ComplaintDetail.Page
 *
 * manages all dynamic created page element
 *
 * @author jwu
 */
ComplaintDetail.Page = {
    initialize : function() {
    }

    ,buildTableDocDocuments: function(c) {
        ComplaintDetail.Object.resetTableDocDocuments();
        var urlBase = Acm.getContextPath() + "/api/v1/plugin/ecm/download/byId";

        var childObjects = c.childObjects;
        if (Acm.isNotEmpty(childObjects)) {
            var len = childObjects.length;
            for (var i = 0; i < len; i++) {
                var obj = childObjects[i];
                if (obj.targetType == "FILE") {
                    var row = "<tr class='odd gradeA'>"
                        + "<td><a href='" + urlBase + obj.targetId + "'>" + obj.targetId + "</a></td>"
                        + "<td>" + obj.targetName + "</td>"
                        + "<td>" + Acm.getDateFromDatetime(obj.created) + "</td>"
                        + "<td>" + obj.creator + "</td>"
                        + "<td>" + obj.status + "</td>"
                        + "</tr>";
                    ComplaintDetail.Object.addRowTableDocDocuments(row);
                }
            } //for
        }
    }

};

