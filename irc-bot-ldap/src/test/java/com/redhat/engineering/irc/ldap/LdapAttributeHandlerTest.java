package com.redhat.engineering.irc.ldap;

import org.junit.Test;

/**
 */
public class LdapAttributeHandlerTest
{
    LdapAttributesHandler handler = new LdapAttributesHandler();
    @Test
    public void test() throws Exception{
        handler.parseXml( null );
    }
}
