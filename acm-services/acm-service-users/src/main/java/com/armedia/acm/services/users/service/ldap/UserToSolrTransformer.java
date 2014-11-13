//package com.armedia.acm.services.users.service.ldap;
//
//import com.armedia.acm.services.users.model.AcmUser;
//import com.armedia.acm.services.search.service.AcmObjectToSolrDocTransformer;
//import com.armedia.acm.services.search.model.solr.SolrAdvancedSearchDocument;
//import com.armedia.acm.services.search.model.solr.SolrDocument;
//
//import java.util.Date;
//import java.util.List;
//
///**
//* Created by marjan.stefanoski on 11.11.2014.
//*/
//public class UserToSolrTransformer implements AcmObjectToSolrDocTransformer<AcmUser> {
//
//    @Override
//    public List<AcmUser> getObjectsModifiedSince(Date lastModified, int start, int pageSize) {
//        return null;
//    }
//
//    @Override
//    public SolrAdvancedSearchDocument toSolrAdvancedSearch(AcmUser in) {
//        return null;
//    }
//
//    @Override
//    public SolrDocument toSolrQuickSearch(AcmUser in) {
//        return null;
//    }
//
//    @Override
//    public boolean isAcmObjectTypeSupported(Class acmObjectType) {
//        return false;
//    }
//}
