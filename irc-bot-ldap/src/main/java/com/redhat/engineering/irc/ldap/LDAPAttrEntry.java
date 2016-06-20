package com.redhat.engineering.irc.ldap;

/**
 */
public class LDAPAttrEntry
{
    private String ldapAttribute;

    private String userAttribute;

    private String type = "String";

    private String pattern;

    private int sequence = Integer.MAX_VALUE;

    public String getLdapAttribute()
    {
        return ldapAttribute;
    }

    public void setLdapAttribute( String ldapAttribute )
    {
        this.ldapAttribute = ldapAttribute;
    }

    public String getUserAttribute()
    {
        return userAttribute;
    }

    public void setUserAttribute( String userAttribute )
    {
        this.userAttribute = userAttribute;
    }

    public String getType()
    {
        return type;
    }

    public void setType( String type )
    {
        this.type = type;
    }

    public String getPattern()
    {
        return pattern;
    }

    public void setPattern( String pattern )
    {
        this.pattern = pattern;
    }

    public int getSequence()
    {
        return sequence;
    }

    public void setSequence( int sequence )
    {
        this.sequence = sequence;
    }
}
