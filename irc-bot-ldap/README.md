# IRC Bot to supply user info search through ldap in irc channel

## Build

mvn clean install assembly:single

## Start this

java -jar irc-bot-ldap-0.1-SNAPSHOT-jar-with-dependencies.jar -i irchost:port -c channel [-n nickname -f ldap-config-file-location]

## LDAP configuration

see src/main/resources/ldap.properties as a default one

## LDAP attr configuration

see src/main/resources/ldapuser.xml as a default one

notes:

* ldapAttribute - the ldap attribute in the ldap search result
* userAttribute - this will used as the display text for the ldap attribute upper, as {userAttribute: searched value}
* seq - used to control the display sequence for final searched results, if not specified, will display as the last one
* type - the value type, like string or date. (now only support date and string, default is string)
* pattern - if type is date, this is used to decide the date pattern to parse



## Usage in irc

in joined channel which specified by -c, type "!&find $uid", the bot will return the found results with your ldap conf and ldap user attr conf

