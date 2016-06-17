package com.redhat.engineering.irc.ldap;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import java.util.List;

/**
 */
public class Options
{

    public static final String ERROR_LOG = "errors.log";

    @Option( name = "-i", aliases = { "--irc-host" }, metaVar = "IRCHOST[:PORT]",
             usage = "The irc server name used to connect, like host:port", required = true )
    private String ircHost;

    @Option( name = "-n", aliases = { "--nick" }, metaVar = "NICKNAME",
             usage = "Nick Name the irc bot will use in the channel" )
    private String nickName = "irc-bot";

    @Option( name = "-c", aliases = { "--channel" }, metaVar = "CHANNELS", usage = "Channels the irc bot will join",
             required = true )
    private List<String> channels;

    @Option( name = "-f", aliases = "--ldap-config", metaVar = "LDAPCONFIG",
             usage = "Ldap configuration file used to specify ldap search options, see readme for the format" )
    private String ldapConfig;

    @Option( name = "-h", aliases = { "--help" }, help = true, usage = "Print this help screen and exit" )
    private boolean help;

    public boolean parseArgs( final String[] args )
    {
        final int cols = ( System.getenv( "COLUMNS" ) == null ? 100 : Integer.valueOf( System.getenv( "COLUMNS" ) ) );
        final ParserProperties props = ParserProperties.defaults().withUsageWidth( cols );

        final CmdLineParser parser = new CmdLineParser( this, props );
        boolean canStart = true;
        try
        {
            parser.parseArgument( args );
            if ( isHelp() )
            {
                printUsage( parser, null );
                canStart = false;
            }
        }
        catch ( CmdLineException e )
        {
            printUsage( parser, e );
            canStart = false;
        }

        return canStart;
    }

    public static void printUsage( final CmdLineParser parser, final CmdLineException error )
    {
        if ( error != null )
        {
            System.err.println( "Invalid option(s): " + error.getMessage() );
            System.err.println();
        }

        System.err.println( "Usage: java -jar ${jar} (-i -c) [OPTIONS] " );
        System.err.println();
        System.err.println();
        parser.printUsage( System.err );
        System.err.println();
    }

    public String getIrcHost()
    {
        return ircHost;
    }

    public void setIrcHost( String ircHost )
    {
        this.ircHost = ircHost;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName( String nickName )
    {
        this.nickName = nickName;
    }

    public List<String> getChannels()
    {
        return channels;
    }

    public void setChannels( List<String> channels )
    {
        this.channels = channels;
    }

    public String getLdapConfig()
    {
        return ldapConfig;
    }

    public void setLdapConfig( String ldapConfig )
    {
        this.ldapConfig = ldapConfig;
    }

    public boolean isHelp()
    {
        return help;
    }

    public void setHelp( boolean help )
    {
        this.help = help;
    }

}
