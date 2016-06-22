package com.redhat.engineering.irc.ldap;

import org.apache.camel.CamelContext;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;

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

        private final ExecutorService executor;

        public IRCRouteBuilder( Options options )
        {
            this.ircEndpoint =
                    "irc:" + options.getNickName() + "@" + options.getIrcHost() + "?channels=#" + options.getChannel()
                            + "&nickname=" + options.getNickName()
                            + "&onReply=true&onNick=false&onQuit=false&onJoin=false&onKick=false&onMode=false&onPart=false&onTopic=false";
            this.handler = new LDAPHandler( options );
            int cpus = Runtime.getRuntime().availableProcessors();
            final ThreadFactory tfac = new NamedThreadFactory( "irc-ldap-user-search" );
            executor = Executors.newFixedThreadPool( cpus * 2, tfac );
        }

        @Override
        public void configure()
                throws Exception
        {
            final String intermediate = "direct:irc-ldap";
            final Processor findCmdProcessor = exchange -> {
                String in = exchange.getIn().getBody().toString();
                String uid = in.substring( "!&find ".length() ).trim();
                final CamelContext context = exchange.getContext();

                executor.execute( () -> {
                    List<LDAPUserDisplay> result = handler.searchUserById( uid );
                    System.out.println( String.format( "[%s] is handling the ldap searching for " + "uid: %s",
                                                       Thread.currentThread().getName(), uid ) );
                    String searchResult = "";
                    if ( result.isEmpty() )
                    {
                        searchResult = "user not found for uid: " + uid;
                    }
                    else
                    {
                        searchResult = result.toString();
                    }
                    context.createProducerTemplate().sendBody( intermediate, searchResult );
                } );
            };
            final Predicate invalidCmdPredicate = exchange -> {
                final String in = exchange.getIn().getBody().toString();
                return in.startsWith( "!&" ) && !in.substring( "!&".length() ).startsWith( "find " );
            };
            final Processor invalidCmdProcessor = exchange -> {
                String in = exchange.getIn().getBody().toString();
                exchange.getOut().setBody( "Command not support:" + in.substring( "!&".length(), in.indexOf( " " ) ) );
            };
            from( ircEndpoint ).routeId( "channeled-in" )
                               .choice()
                               .when( body().startsWith( "!&find " ) )
                               .process( findCmdProcessor )
                               .when( invalidCmdPredicate )
                               .process( invalidCmdProcessor )
                               .to( ircEndpoint )
                               .otherwise()
                               .to( "log:irc-bot-ldap?level=DEBUG" )
                               .endChoice();

            from( intermediate ).routeId( "channeled-out" ).to( ircEndpoint );
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
