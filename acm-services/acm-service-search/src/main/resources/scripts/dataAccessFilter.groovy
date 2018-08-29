import com.armedia.acm.services.search.util.AcmSolrUtil
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

import java.nio.charset.StandardCharsets

// include records with no protected object field
// include records where protected_object_b is false
// include records where public_doc_b is true
String dataAccessFilter = "{!frange l=1}sum(if(exists(protected_object_b), 0, 1), if(protected_object_b, 0, 1), if(public_doc_b, 1, 0)";

Authentication authentication = message.getInboundProperty("acmUser");

String safeUserId = encodeCharacters(authentication.getName());

// do not apply data access filters for FILE or FOLDER, when 'enableDocumentACL' is false
boolean enableDocumentACL = message.getInboundProperty("enableDocumentACL");
String query = message.getInboundProperty('query');
String rowQueryParametars = message.getInboundProperty('rowQueryParametars');
String targetType = getObjectType(query, rowQueryParametars);

String denyAccessFilter = "";

if (!enableDocumentACL && targetType != null && (targetType.equals("FILE") || targetType.equals("FOLDER") || targetType.contentEquals("CONTAINER"))) {
    dataAccessFilter += ")";
} else {
    // include records where current user is directly on allow_acl_ss
    dataAccessFilter += ", termfreq(allow_acl_ss, " + safeUserId + ")";

    for (GrantedAuthority granted : authentication.getAuthorities()) {
        String authName = granted.getAuthority();
        String safeAuthName = encodeCharacters(authName);
        // include records where current user is in a group on allow_acl_ss
        dataAccessFilter += ", termfreq(allow_acl_ss, " + safeAuthName + ")";
    }

    dataAccessFilter += ")";
}

boolean includeDenyAccessFilter = message.getInboundProperty("includeDenyAccessFilter")
if (includeDenyAccessFilter) {
    // exclude records where the user is specifically locked out
    denyAccessFilter = "-deny_acl_ss:" + safeUserId
    for (GrantedAuthority granted : authentication.getAuthorities()) {
        String authName = granted.getAuthority()
        String safeAuthName = encodeCharacters(authName)
        // exclude records where current user is in a locked-out group
        denyAccessFilter += " AND -deny_acl_ss:" + safeAuthName
    }
}

boolean includeDACFilter = message.getInboundProperty("includeDACFilter")
if (includeDACFilter) {
    message.setInboundProperty("dataAccessFilter", URLEncoder.encode(dataAccessFilter, StandardCharsets.UTF_8.displayName()))
    message.setInboundProperty("denyAccessFilter", URLEncoder.encode(denyAccessFilter, StandardCharsets.UTF_8.displayName()))
} else {
    message.setInboundProperty("dataAccessFilter", "")
    message.setInboundProperty("denyAccessFilter", "")
}

String childObjectDacFilter = "{!join from=id to=parent_ref_s}(not(exists(protected_object_b)) OR ";
childObjectDacFilter += "protected_object_b:false OR public_doc_b:true ";

childObjectDacFilter += " OR allow_acl_ss:" + safeUserId;

for (GrantedAuthority granted : authentication.getAuthorities()) {
    String authName = granted.getAuthority();
    String safeAuthName = encodeCharacters(authName);
    // include records where current user is in a group on allow_acl_ss
    childObjectDacFilter += " OR allow_acl_ss:" + safeAuthName;
}
childObjectDacFilter += " )"

if (includeDenyAccessFilter) {
// now we have to add the mandatory denies
    childObjectDacFilter += " AND -deny_acl_ss:" + safeUserId

    for (GrantedAuthority granted : authentication.getAuthorities()) {
        String authName = granted.getAuthority()
        String safeAuthName = encodeCharacters(authName)
        // include records where current user is in a group on allow_acl_ss
        childObjectDacFilter += " AND -deny_acl_ss:" + safeAuthName
    }
}

// Solr 7.2.1
// Conditionals in {!func}sum are no longer correctly evaluated, change to conditional
// Functions in fq clauses only work if they are wrapped in {!frange}.
String childObjectFilterQuery = "{!frange l=1}if(not(exists(parent_ref_s)), 1, \$dac)";

boolean filterParentRef = message.getInboundProperty("filterParentRef");

if (filterParentRef && includeDACFilter) {
    message.setInboundProperty("childObjectDacFilter", URLEncoder.encode(childObjectDacFilter, StandardCharsets.UTF_8.displayName()));
    message.setInboundProperty("childObjectFilterQuery", URLEncoder.encode(childObjectFilterQuery, StandardCharsets.UTF_8.displayName()));
} else {
    message.setInboundProperty("childObjectDacFilter", "")
    message.setInboundProperty("childObjectFilterQuery", "")
}

String subscribedFilter = "{!join from=id to=related_subscription_ref_s}object_type_s:SUBSCRIPTION";
boolean filterSubscriptionEvents = message.getInboundProperty("filterSubscriptionEvents");

if (filterSubscriptionEvents) {
    message.setInboundProperty("isSubscribed", subscribedFilter);
} else {
    message.setInboundProperty("isSubscribed", "");
}

def encodeCharacters(toBeEscaped) {
    return AcmSolrUtil.encodeSpecialCharactersForACL(toBeEscaped)
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