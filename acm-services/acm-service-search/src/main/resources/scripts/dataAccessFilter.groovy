import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

String SPACE_REPLACE = "_0020_";

// include records with no protected object field
// include records where protected_object_b is false
// include records where public_doc_b is true
String dataAccessFilter = "{!frange l=1}sum(if(exists(protected_object_b), 0, 1), if(protected_object_b, 0, 1), if(public_doc_b, 1, 0)";

Authentication authentication = message.getInboundProperty("acmUser");

String safeUserId = authentication.getName().replace(" ", SPACE_REPLACE);

// include records where current user is directly on allow_acl_ss
dataAccessFilter += ", termfreq(allow_acl_ss, " + safeUserId + ")";

// exclude records where the user is specifically locked out
String denyAccessFilter = "-deny_acl_ss:" + safeUserId;

for ( GrantedAuthority granted : authentication.getAuthorities() )
{
    String authName = granted.getAuthority();
    String safeAuthName = authName.replace(" ", SPACE_REPLACE);
    // include records where current user is in a group on allow_acl_ss
    dataAccessFilter += ", termfreq(allow_acl_ss, " + safeAuthName + ")";
    // exclude records where current user is in a locked-out group
    denyAccessFilter += " AND -deny_acl_ss:" + safeAuthName;
}

dataAccessFilter += ")";

message.setInboundProperty("dataAccessFilter", URLEncoder.encode(dataAccessFilter, StandardCharsets.UTF_8.displayName()));
message.setInboundProperty("denyAccessFilter", URLEncoder.encode(denyAccessFilter, StandardCharsets.UTF_8.displayName()));

// for child objects, we want to grant access only if user has access to parent object, at least for now
String isTopLevelObjectFilter = "if(exists(parent_ref_s), 0, 1)";  // this tells us if object is a toplevel object

String childObjectDacFilter = "{!join from=id to=parent_ref_s}(not(exists(protected_object_b)) OR ";
childObjectDacFilter += "protected_object_b:false OR public_doc_b:true ";

childObjectDacFilter += " OR allow_acl_ss:" + safeUserId;

for ( GrantedAuthority granted : authentication.getAuthorities() )
{
    String authName = granted.getAuthority();
    String safeAuthName = authName.replace(" ", SPACE_REPLACE);
    // include records where current user is in a group on allow_acl_ss
    childObjectDacFilter += " OR allow_acl_ss:" + safeAuthName;
}

// now we have to add the mandatory denies
childObjectDacFilter += " ) AND -deny_acl_ss:" + safeUserId;
for ( GrantedAuthority granted : authentication.getAuthorities() )
{
    String authName = granted.getAuthority();
    String safeAuthName = authName.replace(" ", SPACE_REPLACE);
    // include records where current user is in a group on allow_acl_ss
    childObjectDacFilter += " AND -deny_acl_ss:" + safeAuthName;
}

String childObjectFilterQuery = "{!frange l=1}sum(\$topLevel, \$dac)";

message.setInboundProperty("isTopLevelObjectFilter", isTopLevelObjectFilter);
message.setInboundProperty("childObjectDacFilter", URLEncoder.encode(childObjectDacFilter, StandardCharsets.UTF_8.displayName()));
message.setInboundProperty("childObjectFilterQuery", URLEncoder.encode(childObjectFilterQuery, StandardCharsets.UTF_8.displayName()));

return payload;