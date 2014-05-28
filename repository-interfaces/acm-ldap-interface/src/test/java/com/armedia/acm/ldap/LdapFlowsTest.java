package com.armedia.acm.ldap;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.module.ldap.api.LDAPEntry;
import org.mule.module.ldap.api.LDAPEntryAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * Created by armdev on 5/27/14.
 */
@Ignore
public class LdapFlowsTest
{
    private String muleConfigFile = "flows/ldapSyncFlow.xml";
    private MuleClient muleClient;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        Resource muleConfig = new ClassPathResource(muleConfigFile);
        muleClient = new MuleClient(muleConfig.getFile().getCanonicalPath());
        MuleContext ctx = muleClient.getMuleContext();
        ctx.start();
    }

    @After
    public void shutDown() throws Exception
    {
        if ( muleClient != null )
        {
            MuleContext ctx = muleClient.getMuleContext();
            ctx.dispose();
        }
    }

    @Test
    public void groupsForUser() throws Exception
    {
        String user = "*";

        MuleMessage received = muleClient.send("vm://ldapGroupsForUser.in", user, null);

        List<? extends Object> answer = received.getPayload(List.class);

        log.debug("Answer is of type: " + answer.getClass().getName());
        log.debug("number of results: " + answer.size());

        if ( ! answer.isEmpty() )
        {
            log.debug("First answer is of type: " + answer.get(0).getClass().getName());
        }



    }

    @Test
    public void usersInGroup() throws Exception
    {
        String group = "OPIS_B_QA";

        MuleMessage received = muleClient.send("vm://ldap.findGroupMembers.in", group, null);

        List<? extends Object> answer = received.getPayload(List.class);

        log.debug("Answer is of type: " + answer.getClass().getName());
        log.debug("number of results: " + answer.size());

        if ( ! answer.isEmpty() )
        {
            log.debug("First answer is of type: " + answer.get(0).getClass().getName());
        }



    }

    @Test
    public void findAllGroups() throws Exception
    {

        MuleMessage received = muleClient.send("vm://ldap.findAllGroups.in", null, null);

        List<? extends Object> answer = received.getPayload(List.class);

        log.debug("Answer is of type: " + answer.getClass().getName());
        log.debug("number of results: " + answer.size());

        if ( ! answer.isEmpty() )
        {
            log.debug("First answer is of type: " + answer.get(0).getClass().getName());

            for ( Object group : answer )
            {
                LDAPEntry entry = (LDAPEntry) group;
                log.debug("Group DN: " + entry.getDn());
                LDAPEntryAttribute members = entry.getAttribute("member");
                if ( members == null )
                {
                    log.debug("\t Group has no members");
                }
                else if ( members.isMultiValued() )
                {
                    List<Object> memberNames = members.getValues();
                    log.debug("\t # of members: " + memberNames.size());
                    for ( Object member : memberNames )
                    {
                        log.debug("\t member type: " + member.getClass().getName());
                        log.debug("\t\t member: " + member);
                    }
                }
                else
                {
                    log.debug("\t # of members: " + 1);
                    log.debug("\t member type: " + members.getValue().getClass().getName());
                    log.debug("\t\t member: " + members.getValue());
                }

            }
        }



    }

    @Test
    public void findUserById() throws Exception
    {
        String user = "eamillar";

        MuleMessage received = muleClient.send("vm://findUserById.in", user, null);

        LDAPEntry answer = received.getPayload(LDAPEntry.class);

        String dn = answer.getDn();

        log.debug("dn: " + dn);

        MuleMessage groupsForUser = muleClient.send("vm://ldapGroupsForUser.in", dn, null);

        List<? extends Object> groups = groupsForUser.getPayload(List.class);

        log.debug("Answer is of type: " + groups.getClass().getName());
        log.debug("number of results: " + groups.size());
        if ( ! groups.isEmpty() )
        {
            log.debug("First group is of type: " + groups.get(0).getClass().getName());
        }



    }

    @Test
    public void findUserByDn() throws Exception
    {
        String user = "CN=TestE TestE,OU=Users,OU=Test,OU=OPIS,DC=opm,DC=gov";

        MuleMessage received = muleClient.send("vm://findUserByDn.in", user, null);

        LDAPEntry answer = received.getPayload(LDAPEntry.class);

        log.debug("AD User Id: " + answer.getAttribute("samAccountName").getValue());


    }

}
