package com.redhat.engineering.irc.ldap;

import com.google.common.base.Joiner;
import org.apache.commons.lang.StringUtils;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import static java.lang.System.err;

/**
 */
public class LDAPHandler
{
    private final Properties ldapConfig = new Properties();

    private static final String DEFAULT_LDAP_CFG = "ldap.properties";

    private List<LDAPAttrEntry> ldapUserAttrs;

    public LDAPHandler( Options options )
    {
        initLdapCfg( options );
        synchronized ( this )
        {
            if ( ldapUserAttrs == null )
            {
                ldapUserAttrs = new LdapAttributesHandler().getLdapUserAttrsMap( options.getLdapUserAttrConfig() );
            }
        }
    }

    private synchronized void initLdapCfg( Options options )
    {
        boolean cfgSpecified = false;
        if ( options != null )
        {
            String ldapCfg = options.getLdapConfig();
            if ( ldapCfg != null )
            {
                InputStream stream = null;
                try
                {
                    stream = new FileInputStream( new File( ldapCfg ) );
                }
                catch ( FileNotFoundException e )
                {
                    err.println( e.getMessage() );
                }
                if ( stream != null )
                {
                    cfgSpecified = true;
                    try
                    {
                        this.ldapConfig.load( stream );
                    }
                    catch ( IOException e )
                    {
                        err.println( e.getMessage() );
                    }
                }
            }

            if ( !cfgSpecified )
            {
                try
                {
                    this.ldapConfig.load( this.getClass().getClassLoader().getResourceAsStream( DEFAULT_LDAP_CFG ) );
                }
                catch ( IOException e )
                {
                    err.println( e.getMessage() );
                }
            }
        }
    }

    public List<LDAPUserDisplay> searchUserById( String... uids )
    {
        LdapConnection ldap = null;
        final List<LDAPUserDisplay> displays = new ArrayList<>( ldapUserAttrs.size() );
        final SimpleDateFormat toFormat = new SimpleDateFormat( "E, MMM dd, yyyy" );
        try
        {
            ldap = this.openLdapConnection();
            final String baseDN = ldapConfig.getProperty( "ldap.search.baseDn" );
            List<String> uidList = Arrays.asList( uids );
            String filter = "(|(uid=" + Joiner.on( ")(uid=" ).join( uidList ) + "))";
            String[] attrs = new String[ldapUserAttrs.size()];
            int i = 0;
            for ( LDAPAttrEntry entry : ldapUserAttrs )
            {
                attrs[i] = entry.getLdapAttribute();
                i++;
            }

            EntryCursor cursor = ldap.search( baseDN, filter, SearchScope.SUBTREE, attrs );

            for ( Entry entry : cursor )
            {
                for ( final Attribute attr : entry.getAttributes() )
                {
                    for ( LDAPAttrEntry ent : ldapUserAttrs )
                    {
                        if ( ent.getLdapAttribute().equalsIgnoreCase( attr.getId() ) )
                        {
                            if ( "date".equalsIgnoreCase( ent.getType() ) )
                            {
                                SimpleDateFormat fromFormat = new SimpleDateFormat( ent.getPattern() );
                                try
                                {
                                    Date date = fromFormat.parse( attr.getString() );
                                    displays.add( new LDAPUserDisplay( ent.getUserAttribute(), toFormat.format( date ),
                                                                       ent.getSequence() ) );
                                }
                                catch ( ParseException e )
                                {
                                    err.println( e.getMessage() );
                                }
                            }
                            else
                            {
                                displays.add( new LDAPUserDisplay( ent.getUserAttribute(), attr.getString(),
                                                                   ent.getSequence() ) );
                            }

                            break;
                        }
                    }
                }
            }
        }
        catch ( LdapException | NoSuchAlgorithmException e )
        {
            e.printStackTrace();
        }
        finally
        {
            if ( ldap != null )
            {
                try
                {
                    ldap.close();
                }
                catch ( IOException e )
                {
                    e.printStackTrace();
                }
            }
        }

        if ( !displays.isEmpty() )
        {
            displays.sort( ( display1, display2 ) -> display1.getSequence() >= display2.getSequence() ? 1 : -1 );
        }

        return displays;
    }

    private LdapConnection openLdapConnection()
            throws LdapException, NoSuchAlgorithmException
    {

        LdapConnectionConfig config = new LdapConnectionConfig();
        config.setLdapHost( ldapConfig.getProperty( "ldap.search.host" ) );
        config.setLdapPort( Integer.parseInt( ldapConfig.getProperty( "ldap.search.host.port" ) ) );
        config.setUseSsl( Boolean.valueOf( ldapConfig.getProperty( "ldap.search.host.useSSL" ) ) );

        LdapConnection ldap = new LdapNetworkConnection( config );

        String bindDN = ldapConfig.getProperty( "ldap.search.bindDn" );
        if ( StringUtils.isNotBlank( bindDN ) )
        {
            String credential = ldapConfig.getProperty( "ldap.search.bindDn.credential" );
            if ( StringUtils.isNotBlank( credential ) )
            {
                ldap.bind( bindDN, credential );
            }
            else
            {
                ldap.bind( bindDN );
            }
        }
        else
        {
            ldap.anonymousBind();
        }

        return ldap;

    }

}
