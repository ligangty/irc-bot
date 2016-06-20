package com.redhat.engineering.irc.ldap;

import com.google.common.base.Joiner;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;

import java.util.List;

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
        // create a Main instance
        final Main main = new Main();
        // add routes
        main.addRouteBuilder( new IRCRouteBuilder( options ) );
        // add event listener
        main.addMainListener( new Events() );
        // run until you terminate the JVM
        System.out.println( "Not support as a service now, please use kill command to stop service.\n" );
        main.run();
    }

    private static class IRCRouteBuilder
            extends RouteBuilder
    {
        private String ircEndpoint;

        private LDAPHandler handler;

        public IRCRouteBuilder( Options options )
        {
            final StringBuilder builder = new StringBuilder();
            builder.append( "irc:" )
                   .append( options.getNickName() )
                   .append( "@" )
                   .append( options.getIrcHost() )
                   .append( "?channels=#" )
                   .append( options.getChannel() )
                   .append( "&nickname=" )
                   .append( options.getNickName() )
                   .append(
                           "&onReply=true&onNick=false&onQuit=false&onJoin=false&onKick=false&onMode=false&onPart=false&onTopic=false" );

            this.ircEndpoint = builder.toString();
            this.handler = new LDAPHandler( options );

        }

        @Override
        public void configure()
                throws Exception
        {
            from( ircEndpoint ).process( exchange -> {
                String in = exchange.getIn().getBody().toString();
                if ( in.startsWith( "!&" ) )
                {
                    if ( in.substring( "!&".length() ).startsWith( "find " ) )
                    {
                        String uid = in.substring( "!&find ".length() ).trim();
                        List<LDAPUserDisplay> result = handler.searchUserById( uid );
                        if ( result.isEmpty() )
                        {
                            exchange.getOut().setBody( "user not found for uid: " + uid );
                        }
                        else
                        {
                            exchange.getOut().setBody( result.toString() );
                        }
                    }
                    else
                    {
                        exchange.getOut().setBody( "Command not support:" + in );
                    }
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
