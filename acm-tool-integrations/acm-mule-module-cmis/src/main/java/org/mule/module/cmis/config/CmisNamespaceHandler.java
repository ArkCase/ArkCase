
package org.mule.module.cmis.config;

import javax.annotation.Generated;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;


/**
 * Registers bean definitions parsers for handling elements in <code>http://www.mulesoft.org/schema/mule/cmis</code>.
 * 
 */
@Generated(value = "Mule DevKit Version 3.4.0", date = "2014-05-13T04:20:32-03:00", comments = "Build 3.4.0.1555.8df15c1")
public class CmisNamespaceHandler
    extends NamespaceHandlerSupport
{


    /**
     * Invoked by the {@link DefaultBeanDefinitionDocumentReader} after construction but before any custom elements are parsed. 
     * @see NamespaceHandlerSupport#registerBeanDefinitionParser(String, BeanDefinitionParser)
     * 
     */
    public void init() {
        registerBeanDefinitionParser("config", new CMISCloudConnectorConfigDefinitionParser());
        registerBeanDefinitionParser("repositories", new RepositoriesDefinitionParser());
        registerBeanDefinitionParser("repository-info", new RepositoryInfoDefinitionParser());
        registerBeanDefinitionParser("changelog", new ChangelogDefinitionParser());
        registerBeanDefinitionParser("get-object-by-id", new GetObjectByIdDefinitionParser());
        registerBeanDefinitionParser("get-object-by-path", new GetObjectByPathDefinitionParser());
        registerBeanDefinitionParser("create-document-by-path", new CreateDocumentByPathDefinitionParser());
        registerBeanDefinitionParser("create-document-by-path-from-content", new CreateDocumentByPathFromContentDefinitionParser());
        registerBeanDefinitionParser("get-or-create-folder-by-path", new GetOrCreateFolderByPathDefinitionParser());
        registerBeanDefinitionParser("create-document-by-id", new CreateDocumentByIdDefinitionParser());
        registerBeanDefinitionParser("create-document-by-id-from-content", new CreateDocumentByIdFromContentDefinitionParser());
        registerBeanDefinitionParser("create-folder", new CreateFolderDefinitionParser());
        registerBeanDefinitionParser("get-type-definition", new GetTypeDefinitionDefinitionParser());
        registerBeanDefinitionParser("get-checkout-docs", new GetCheckoutDocsDefinitionParser());
        registerBeanDefinitionParser("query", new QueryDefinitionParser());
        registerBeanDefinitionParser("get-parent-folders", new GetParentFoldersDefinitionParser());
        registerBeanDefinitionParser("folder", new FolderDefinitionParser());
        registerBeanDefinitionParser("get-content-stream", new GetContentStreamDefinitionParser());
        registerBeanDefinitionParser("move-object", new MoveObjectDefinitionParser());
        registerBeanDefinitionParser("update-object-properties", new UpdateObjectPropertiesDefinitionParser());
        registerBeanDefinitionParser("get-object-relationships", new GetObjectRelationshipsDefinitionParser());
        registerBeanDefinitionParser("get-acl", new GetAclDefinitionParser());
        registerBeanDefinitionParser("get-all-versions", new GetAllVersionsDefinitionParser());
        registerBeanDefinitionParser("check-out", new CheckOutDefinitionParser());
        registerBeanDefinitionParser("cancel-check-out", new CancelCheckOutDefinitionParser());
        registerBeanDefinitionParser("check-in", new CheckInDefinitionParser());
        registerBeanDefinitionParser("apply-acl", new ApplyAclDefinitionParser());
        registerBeanDefinitionParser("get-applied-policies", new GetAppliedPoliciesDefinitionParser());
        registerBeanDefinitionParser("apply-policy", new ApplyPolicyDefinitionParser());
        registerBeanDefinitionParser("delete", new DeleteDefinitionParser());
        registerBeanDefinitionParser("delete-tree", new DeleteTreeDefinitionParser());
        registerBeanDefinitionParser("apply-aspect", new ApplyAspectDefinitionParser());
        registerBeanDefinitionParser("create-relationship", new CreateRelationshipDefinitionParser());
    }

}
