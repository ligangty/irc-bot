package com.redhat.engineering.irc.ldap;

/**
 */
public class LDAPUserDisplay
{
    private String displayName;

    private String displayValue;

    private int sequence;

    public LDAPUserDisplay( String displayName, String displayValue, int sequence )
    {
        this.displayName = displayName;
        this.displayValue = displayValue;
        this.sequence = sequence;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDisplayValue()
    {
        return displayValue;
    }

    public int getSequence()
    {
        return sequence;
    }

    @Override
    public String toString()
    {
        return "{" + getDisplayName() + ": " + getDisplayValue() + "}";
    }
}
