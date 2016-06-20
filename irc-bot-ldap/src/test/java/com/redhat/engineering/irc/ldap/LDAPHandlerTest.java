package com.redhat.engineering.irc.ldap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

public class LDAPHandlerTest
{
    private LDAPHandler handler;

    @Before
    public void setUp()
    {
        Options options = new Options();
        options.setLdapConfig( "/etc/irc-bot/ldap.properties" );
        options.setLdapUserAttrConfig( "/etc/irc-bot/ldapuser.xml" );
        handler = new LDAPHandler( options );
    }

    @Test
    @Ignore
    public void test()
    {
        List<LDAPUserDisplay> result = handler.searchUserById( "gli" );
        System.out.println( result );
    }

}
