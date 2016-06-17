package com.redhat.engineering.irc.ldap;

import com.google.common.base.Joiner;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;

/**
 *
 */
public class IRCBotLDAPRun
{
    public static void main( String[] args )
            throws Exception
    {
        final Options opts = new Options();
        boolean start = opts.parseArgs( args );

        if ( start )
        {
            IRCBotLDAPRun run = new IRCBotLDAPRun();
            run.boot( opts );
        }

    }

    public void boot( Options options )
            throws Exception
    {
        final StringBuilder builder = new StringBuilder();
        builder.append( "irc:" )
               .append( options.getNickName() )
               .append( "@" )
               .append( options.getIrcHost() )
               .append( "?channels=#" )
               .append( Joiner.on( ",#" ).join( options.getChannels() ) )
               .append( "&nickname=" )
               .append( options.getNickName() )
               .append(
                       "&onReply=true&onNick=false&onQuit=false&onJoin=false&onKick=false&onMode=false&onPart=false&onTopic=false" );
        System.out.println( builder.toString() );
        // create a Main instance
        final Main main= new Main();
        // add routes
        main.addRouteBuilder( new IRCRouteBuilder( builder.toString() ) );
        // add event listener
        main.addMainListener( new Events() );
        // run until you terminate the JVM
        System.out.println( "Starting Camel. Use ctrl + c to terminate the JVM.\n" );
        main.run();
    }

    private static class IRCRouteBuilder
            extends RouteBuilder
    {
        private String ircEndpoint;

        public IRCRouteBuilder( String ircEndpoint )
        {
            this.ircEndpoint = ircEndpoint;
        }

        @Override
        public void configure()
                throws Exception
        {
            from( ircEndpoint ).process( exchange -> {
                String in = exchange.getIn().getBody().toString();
                System.out.println( exchange.getIn().getBody() );
                if ( in.startsWith( "!&" ) )
                {
                    exchange.getOut().setBody( "yes I got you:" + in );
                }
                else
                {
                    exchange.getOut().setBody( "" );
                }
            } ).to( ircEndpoint );

        }
    }

    public static class Events
            extends MainListenerSupport
    {

        @Override
        public void afterStart( MainSupport main )
        {
        }

        @Override
        public void beforeStop( MainSupport main )
        {
        }
    }
}
