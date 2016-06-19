package com.redhat.engineering.irc.ldap;

import org.apache.commons.lang.StringUtils;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 */
public class LdapAttributesHandler
{
    private Map<String, String> ldapUserAttrsMap = new HashMap<>();

    private static final String DEFAULT_LDAP_XML = "ldapuser.xml";

    public void parseXml( String ldapXml )
            throws Exception
    {
        XMLInputFactory xmlif = XMLInputFactory.newInstance();
        XMLStreamReader xmlr = null;
        if ( StringUtils.isNotBlank( ldapXml ) )
        {
            xmlr = xmlif.createXMLStreamReader( new FileReader( ldapXml ) );
        }
        else
        {
            xmlr = xmlif.createXMLStreamReader(
                    this.getClass().getClassLoader().getResourceAsStream( DEFAULT_LDAP_XML ) );
        }
        while ( xmlr.hasNext() )
        {
            switch ( xmlr.getEventType() )
            {
                case XMLStreamConstants.START_ELEMENT:
                    System.out.println( "Start Element:" + xmlr.getName() );
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    System.out.println( "End Element:" + xmlr.getName() );
                    break;
                case XMLStreamConstants.CHARACTERS:
                    int start = xmlr.getTextStart();
                    int length = xmlr.getTextLength();
                    System.out.println( "CHARACTERS:" + new String( xmlr.getTextCharacters(), start, length ) );
                    break;
                default:
                    break;
            }
            xmlr.next();
        }
    }
}
