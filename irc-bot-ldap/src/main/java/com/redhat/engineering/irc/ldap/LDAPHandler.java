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
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 */
public class LDAPHandler
{
    //    private final Options options;

    private final Properties ldapConfig = new Properties();

    private static final String DEFAULT_LDAP_CFG = "ldap.properties";

    public LDAPHandler( Options options )
    {
        //        this.options = options;
        initLdapCfg( options );
    }

    private void initLdapCfg( Options options )
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
                    System.err.println( e.getMessage() );
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
                        System.err.println( e.getMessage() );
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
                    System.err.println( e.getMessage() );
                }
            }
        }
    }

    public LDAPUser searchUser( String... uids )
    {
        LdapConnection ldap = null;
        try
        {
            ldap = this.openLdapConnection();
            //            System.out.println( "connected:" + ldap.isConnected() );
            //            System.out.println( "authenticated:" + ldap.isAuthenticated() );
            final String baseDN = ldapConfig.getProperty( "ldap.search.baseDn" );

            List<String> uidList = Arrays.asList( uids );
            String filter = "(|(uid=" + Joiner.on( ")(uid=" ).join( uidList ) + "))";
            String attrString = ldapConfig.getProperty( "ldap.search.user.attrs" );
            String[] attrs = { "uid" };
            if ( StringUtils.isNotBlank( attrString ) )
            {
                attrs = attrString.split( "," );
            }
            EntryCursor cursor = ldap.search( baseDN, filter, SearchScope.SUBTREE, attrs );

            int i = 0;

            for ( Entry entry : cursor )
            {
                for ( Attribute attr : entry.getAttributes() )
                {
                    System.out.println( attr.getId() + ":" + attr.getString() );
                }
                System.out.println( "=========================" );
                i++;
            }
            System.out.println( "num count:" + i );
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

        return null;
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
