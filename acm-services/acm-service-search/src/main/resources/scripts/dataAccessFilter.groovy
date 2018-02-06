import groovy.transform.Field
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

import java.nio.charset.StandardCharsets

@Field
def SPACE_REPLACE = "_0020_";
@Field
def COMMA_REPLACE = "_002C_";
@Field
def OPENING_PARENTHESIS_REPLACE = "_0028_";
@Field
def CLOSING_PARENTHESIS_REPLACE = "_0029_";

// include records with no protected object field
// include records where protected_object_b is false
// include records where public_doc_b is true
String dataAccessFilter = "{!frange l=1}sum(if(exists(protected_object_b), 0, 1), if(protected_object_b, 0, 1), if(public_doc_b, 1, 0)";

Authentication authentication = message.getInboundProperty("acmUser");

String safeUserId = authentication.getName().replace(" ", SPACE_REPLACE);

// do not apply data access filters for FILE or FOLDER, when 'enableDocumentACL' is false
boolean enableDocumentACL = message.getInboundProperty("enableDocumentACL");
String query = message.getInboundProperty('query');
String rowQueryParametars = message.getInboundProperty('rowQueryParametars');
String targetType = getObjectType(query, rowQueryParametars);

String denyAccessFilter = "";

if (!enableDocumentACL && targetType != null && (targetType.equals("FILE") || targetType.equals("FOLDER") || targetType.contentEquals("CONTAINER")))
{
    dataAccessFilter += ")";
}
else
{
    // include records where current user is directly on allow_acl_ss
    dataAccessFilter += ", termfreq(allow_acl_ss, " + safeUserId + ")";
    
    // exclude records where the user is specifically locked out
    denyAccessFilter = "-deny_acl_ss:" + safeUserId;
    
    for (GrantedAuthority granted : authentication.getAuthorities())
    {
        String authName = granted.getAuthority();
        String safeAuthName = escapeCharacters(authName);
        // include records where current user is in a group on allow_acl_ss
        dataAccessFilter += ", termfreq(allow_acl_ss, " + safeAuthName + ")";
        // exclude records where current user is in a locked-out group
        denyAccessFilter += " AND -deny_acl_ss:" + safeAuthName;
    }
    
    dataAccessFilter += ")";
}

message.setInboundProperty("dataAccessFilter", URLEncoder.encode(dataAccessFilter, StandardCharsets.UTF_8.displayName()));
message.setInboundProperty("denyAccessFilter", URLEncoder.encode(denyAccessFilter, StandardCharsets.UTF_8.displayName()));

// for child objects, we want to grant access only if user has access to parent object, at least for now
String isTopLevelObjectFilter = "if(exists(parent_ref_s), 0, 1)";  // this tells us if object is a toplevel object

String childObjectDacFilter = "{!join from=id to=parent_ref_s}(not(exists(protected_object_b)) OR ";
childObjectDacFilter += "protected_object_b:false OR public_doc_b:true ";

childObjectDacFilter += " OR allow_acl_ss:" + safeUserId;

for (GrantedAuthority granted : authentication.getAuthorities())
{
    String authName = granted.getAuthority();
    String safeAuthName = escapeCharacters(authName);
    // include records where current user is in a group on allow_acl_ss
    childObjectDacFilter += " OR allow_acl_ss:" + safeAuthName;
}

// now we have to add the mandatory denies
childObjectDacFilter += " ) AND -deny_acl_ss:" + safeUserId;
for (GrantedAuthority granted : authentication.getAuthorities())
{
    String authName = granted.getAuthority();
    String safeAuthName = escapeCharacters(authName);
    // include records where current user is in a group on allow_acl_ss
    childObjectDacFilter += " AND -deny_acl_ss:" + safeAuthName;
}

String childObjectFilterQuery = "{!frange l=1}sum(\$topLevel, \$dac)";

boolean filterParentRef = message.getInboundProperty("filterParentRef");

if (filterParentRef)
{
    message.setInboundProperty("isTopLevelObjectFilter", isTopLevelObjectFilter);
    message.setInboundProperty("childObjectDacFilter", URLEncoder.encode(childObjectDacFilter, StandardCharsets.UTF_8.displayName()));
    message.setInboundProperty("childObjectFilterQuery", URLEncoder.encode(childObjectFilterQuery, StandardCharsets.UTF_8.displayName()));
} else
{
    message.setInboundProperty("isTopLevelObjectFilter", "");
    message.setInboundProperty("childObjectDacFilter", "");
    message.setInboundProperty("childObjectFilterQuery", "");
}

String subscribedFilter = "{!join from=id to=related_subscription_ref_s}object_type_s:SUBSCRIPTION";
boolean filterSubscriptionEvents = message.getInboundProperty("filterSubscriptionEvents");

if(filterSubscriptionEvents)
{
    message.setInboundProperty("isSubscribed", subscribedFilter);
} else
{
    message.setInboundProperty("isSubscribed", "");
}

def escapeCharacters(toBeEscaped)
{
    toBeEscaped
            .replace(" ", SPACE_REPLACE)
            .replace(",", COMMA_REPLACE)
            .replace("(", OPENING_PARENTHESIS_REPLACE)
            .replace(")", CLOSING_PARENTHESIS_REPLACE);
}

def getObjectType(query, rowQueryParametars)
{
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