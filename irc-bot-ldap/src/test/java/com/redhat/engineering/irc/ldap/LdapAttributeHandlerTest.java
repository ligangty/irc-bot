package com.redhat.engineering.irc.ldap;

import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

/**
 */
public class LdapAttributeHandlerTest
{
    LdapAttributesHandler handler = new LdapAttributesHandler();

    @Test
    @Ignore
    public void test()
            throws Exception
    {
        List<LDAPAttrEntry> result = handler.getLdapUserAttrsMap( "/etc/irc-bot/ldapuser.xml" );
        result.forEach( entry -> System.out.println( entry.getLdapAttribute() + ":" + entry.getUserAttribute() + ":"
                                                             + entry.getSequence() ) );
    }
}
