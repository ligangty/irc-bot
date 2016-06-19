package com.redhat.engineering.irc.ldap;

import org.junit.Before;
import org.junit.Test;

public class LDAPHandlerTest
{
    private LDAPHandler handler;

    @Before
    public void setUp(){
        Options options = new Options();
        options.setLdapConfig( "/etc/irc-bot/ldap.properties" );
        handler = new LDAPHandler( options );
    }

    @Test
    public void test(){
        handler.searchUser( "gli" );
    }

}
