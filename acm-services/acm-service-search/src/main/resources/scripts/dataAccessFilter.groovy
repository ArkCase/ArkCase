import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

// include records with no protected object field
// include records where protected_object_b is false
// include records where public_doc_b is true
String dataAccessFilter = "{!frange l=1}sum(if(exists(protected_object_b), 0, 1), if(protected_object_b, 0, 1), if(public_doc_b, 1, 0)";

Authentication authentication = message.getInboundProperty("acmUser");

// include records where current user is directly on allow_acl_ss
dataAccessFilter += ", termfreq(allow_acl_ss, '" + authentication.getName() + "')";

// exclude records where the user is specifically locked out
String denyAccessFilter = "-deny_acl_ss:" + authentication.getName();

for ( GrantedAuthority granted : authentication.getAuthorities() )
{
    String authName = granted.getAuthority();
    // include records where current user is in a group on allow_acl_ss
    dataAccessFilter += ", termfreq(allow_acl_ss, '" + authName + "')";
    // exclude records where current user is in a locked-out group
    denyAccessFilter += " AND -deny_acl_ss:" + authName;
}

dataAccessFilter += ")";

message.setInboundProperty("dataAccessFilter", URLEncoder.encode(dataAccessFilter, StandardCharsets.UTF_8.displayName()));
message.setInboundProperty("denyAccessFilter", URLEncoder.encode(denyAccessFilter, StandardCharsets.UTF_8.displayName()));

return payload;