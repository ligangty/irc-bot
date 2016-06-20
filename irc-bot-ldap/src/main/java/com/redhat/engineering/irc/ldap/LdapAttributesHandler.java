package com.redhat.engineering.irc.ldap;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class LdapAttributesHandler
{
    private List<LDAPAttrEntry> ldapAttrEntries = new ArrayList<>();

    private static final String DEFAULT_LDAP_XML = "ldapuser.xml";

    public synchronized List<LDAPAttrEntry> getLdapUserAttrsMap( String ldapXml )
    {
        if ( ldapAttrEntries.isEmpty() )
        {
            try
            {
                ldapAttrEntries = parseXml( ldapXml );
                ldapAttrEntries.sort( ( entry1, entry2 ) -> entry1.getSequence() >= entry2.getSequence() ? 1 : -1 );
            }
            catch ( Exception e )
            {
                System.err.println( e.getMessage() );
                ldapAttrEntries = new ArrayList<>();
            }
        }
        return Lists.newArrayList( ldapAttrEntries );
    }

    private List<LDAPAttrEntry> parseXml( String ldapXml )
            throws Exception
    {
        XMLInputFactory xmlif = XMLInputFactory.newInstance();
        XMLStreamReader reader;
        if ( StringUtils.isNotBlank( ldapXml ) )
        {
            reader = xmlif.createXMLStreamReader( new FileReader( ldapXml ) );
        }
        else
        {
            reader = xmlif.createXMLStreamReader(
                    this.getClass().getClassLoader().getResourceAsStream( DEFAULT_LDAP_XML ) );
        }

        String tagContent = null;
        List<LDAPAttrEntry> entries = new ArrayList<>();
        LDAPAttrEntry entry = null;
        while ( reader.hasNext() )
        {
            int event = reader.next();

            switch ( event )
            {
                case XMLStreamConstants.START_ELEMENT:
                    if ( reader.getLocalName().equals( "entry" ) )
                    {
                        entry = new LDAPAttrEntry();
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    tagContent = reader.getText().trim();
                    break;

                case XMLStreamConstants.END_ELEMENT:
                    switch ( reader.getLocalName() )
                    {
                        case "entry":
                            entries.add( entry );
                            break;
                        case "ldapAttribute":
                            entry.setLdapAttribute( tagContent );
                            break;
                        case "userAttribute":
                            entry.setUserAttribute( tagContent );
                            break;
                        case "seq":
                            entry.setSequence( Integer.parseInt( tagContent ) );
                            break;
                        case "pattern":
                            entry.setPattern( tagContent );
                            break;
                        case "type":
                            entry.setType( tagContent );
                            break;

                    }
                    break;

                case XMLStreamConstants.START_DOCUMENT:
                    break;
                default:
                    break;
            }
        }
        return entries;
    }
}
