# IRC Bot to supply user info search through ldap in irc channel

## Build

mvn clean install assembly:single

## Start this

java -jar irc-bot-ldap-0.1-SNAPSHOT-jar-with-dependencies.jar -i irchost:port -c channel1,channel2... [-n nickname -f ldap-config-file-location]

## LDAP configuration

see src/main/resources/ldap.properties as a default one

## Usage in irc

TODO