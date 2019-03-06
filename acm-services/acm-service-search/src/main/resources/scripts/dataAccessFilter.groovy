import java.nio.charset.StandardCharsets

Long authenticatedUserId = message.getInboundProperty("acmUser")
def authenticatedUserGroupIds = message.getInboundProperty("acmUserGroupIds")
String query = message.getInboundProperty('query')
String rowQueryParametars = message.getInboundProperty('rowQueryParametars')

boolean includeDACFilter = message.getInboundProperty("includeDACFilter")
// do not apply data access filters for FILE or FOLDER, when 'enableDocumentACL' is false
boolean enableDocumentACL = message.getInboundProperty("enableDocumentACL")
String targetType = getObjectType(query, rowQueryParametars)

boolean includeDenyAccessFilter = message.getInboundProperty("includeDenyAccessFilter")

StringBuilder dataAccessFilter = StringBuilder.newInstance();

if (includeDACFilter) {
    if (!enableDocumentACL && targetType != null && (targetType.equals("FILE")
            || targetType.equals("FOLDER") || targetType.contentEquals("CONTAINER"))) {
        message.setInboundProperty("dataAccessFilter", "")
    } else {
        dataAccessFilter.append("(protected_object_b:true AND (allow_user_ls:" + authenticatedUserId)

        for (Long groupId : authenticatedUserGroupIds) {
            // include records where current user is in a group on allow_group_ls
            dataAccessFilter.append(" OR allow_group_ls:" + groupId)
        }
        dataAccessFilter.append(")) OR (public_doc_b:true OR protected_object_b:false OR (*:* -protected_object_b:[* TO *]) " +
                "OR parent_allow_user_ls:" + authenticatedUserId)

        for (Long groupId : authenticatedUserGroupIds) {
            // include records where current user is in a group on parent_allow_group_ls
            dataAccessFilter.append(" OR parent_allow_group_ls:" + groupId)
        }
        dataAccessFilter.append(")")
        message.setInboundProperty("dataAccessFilter", URLEncoder.encode(dataAccessFilter.toString(), StandardCharsets.UTF_8.displayName()))
    }
} else {
    message.setInboundProperty("dataAccessFilter", "")
}

StringBuilder denyAccessFilter = StringBuilder.newInstance();

if (includeDenyAccessFilter) {
    // exclude records where the user is specifically locked out
    denyAccessFilter.append("-deny_user_ls:" + authenticatedUserId)
    for (Long groupId : authenticatedUserGroupIds) {
        // exclude records where current user is in a locked-out group
        denyAccessFilter.append(" AND -deny_group_ls:" + groupId)
    }
    denyAccessFilter.append(" AND -deny_parent_user_ls:" + authenticatedUserId)

    for (Long groupId : authenticatedUserGroupIds) {
        // exclude records where current user is in a locked-out group
        denyAccessFilter.append(" AND -deny_parent_group_ls:" + groupId)
    }
    message.setInboundProperty("denyAccessFilter", URLEncoder.encode(denyAccessFilter.toString(), StandardCharsets.UTF_8.displayName()))
} else {
    message.setInboundProperty("denyAccessFilter", "")
}


message.setInboundProperty("childObjectDacFilter", "")
message.setInboundProperty("childObjectFilterQuery", "")


String subscribedFilter = "{!join from=id to=related_subscription_ref_s}object_type_s:SUBSCRIPTION"
boolean filterSubscriptionEvents = message.getInboundProperty("filterSubscriptionEvents")

if (filterSubscriptionEvents) {
    message.setInboundProperty("isSubscribed", subscribedFilter)
} else {
    message.setInboundProperty("isSubscribed", "")
}


def getObjectType(query, rowQueryParametars) {
    // find object type from query or rowQueryParameters that looks like "...object_type_s:FILE..."
    def regex = /(.*)(\s|^|\(|=|\)|})object_type_s:([a-zA-Z0-9_]*)(.*)/;
    def m = (query =~ regex);

    if (m.matches()) {
        return m[0][3];
    } else {
        m = (rowQueryParametars =~ regex);
        if (m.matches()) {
            return m[0][3];
        }
    }

    // find object type from query "id:103-FILE"
    regex = /id:([0-9]+)\-([a-zA-Z0-9_]*)(.*)/;
    m = (query =~ regex);
    if (m.matches()) {
        return m[0][2];
    }
}

return payload;
