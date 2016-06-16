package com.redhat.engineering.irc;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;

/**
 *
 */
public class IRCBotLDAPRun
{
    private Main main;

    public static void main( String[] args )
            throws Exception
    {
        IRCBotLDAPRun run = new IRCBotLDAPRun();
        run.boot();
    }

    public void boot()
            throws Exception
    {
        // create a Main instance
        main = new Main();
        // bind MyBean into the registry
//        main.bind( "foo", new MyBean() );
        // add routes
        main.addRouteBuilder( new IRCRouteBuilder() );
        // add event listener
        main.addMainListener( new Events() );

        // run until you terminate the JVM
        System.out.println( "Starting Camel. Use ctrl + c to terminate the JVM.\n" );
        main.run();
    }

    private static class IRCRouteBuilder
            extends RouteBuilder
    {
        @Override
        public void configure()
                throws Exception
        {
            from( "irc:irc-bot@irc.freenode.net:8001?channels=#ligangty&nickname=irc-bot&onReply=true&onNick=false&onQuit=false&onJoin=false&onKick=false&onMode=false&onPart=false&onTopic=false" )
                    .process( new Processor()
                    {
                        public void process( Exchange exchange )
                                throws Exception
                        {
                            String in = exchange.getIn().getBody().toString();
                            System.out.println( exchange.getIn().getBody() );
                            if ( in.startsWith( "!&" ) )
                            {
                                exchange.getOut().setBody( "yes I got you:" + in );
                            }else{
                                exchange.getOut().setBody( "" );
                            }

                        }
                    } )
                    .to( "irc:irc-bot@irc.freenode.net:8001?channels=#ligangty&nickname=irc-bot&onReply=true&onNick=false&onQuit=false&onJoin=false&onKick=false&onMode=false&onPart=false&onTopic=false" );

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
