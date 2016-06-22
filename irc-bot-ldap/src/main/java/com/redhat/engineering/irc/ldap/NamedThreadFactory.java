package com.redhat.engineering.irc.ldap;

import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory
        implements ThreadFactory
{

    private int counter = 0;

    private final ClassLoader ccl;

    private final String name;

    public NamedThreadFactory( final String name )
    {
        this.ccl = Thread.currentThread()
                         .getContextClassLoader();
        this.name = name;
    }

    @Override
    public Thread newThread(final Runnable runnable )
    {
        final Thread t = new Thread( runnable );
        t.setContextClassLoader( ccl );
        t.setName( name + "-" + counter++ );

        return t;
    }

}

