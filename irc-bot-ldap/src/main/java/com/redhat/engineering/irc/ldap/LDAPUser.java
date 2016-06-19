package com.redhat.engineering.irc.ldap;

import java.io.Serializable;

/**
 */
public class LDAPUser
        implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String username;

    private String realName;

    private String email;

    private String jobTitle;

    private String employeeNumber;

    private String employeeType;

    private String location;

    private String hireDate;

    public String getEmployeeType()
    {
        return employeeType;
    }

    void setEmployeeType( String employeeType )
    {
        this.employeeType = employeeType;
    }

    public String getUsername()
    {
        return username;
    }

    void setUsername( String username )
    {
        this.username = username;
    }

    public String getRealName()
    {
        return realName;
    }

    void setRealName( String realName )
    {
        this.realName = realName;
    }

    public String getEmail()
    {
        return email;
    }

    void setEmail( String email )
    {
        this.email = email;
    }

    public String getJobTitle()
    {
        return jobTitle;
    }

    void setJobTitle( String jobTitle )
    {
        this.jobTitle = jobTitle;
    }

    public String getEmployeeNumber()
    {
        return employeeNumber;
    }

    void setEmployeeNumber( String uid )
    {
        this.employeeNumber = uid;
    }

    public String getLocation()
    {
        return location;
    }

    void setLocation( String location )
    {
        this.location = location;
    }

    public String getHireDate()
    {
        return hireDate == null ? null : hireDate.trim().substring( 0, 8 );
    }

    void setHireDate( String hire_date )
    {
        this.hireDate = hire_date;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( username == null ) ? 0 : username.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( getClass() != obj.getClass() )
        {
            return false;
        }
        LDAPUser other = (LDAPUser) obj;
        if ( username == null )
        {
            if ( other.username != null )
            {
                return false;
            }
        }
        else if ( !username.equals( other.username ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "";
    }

}
