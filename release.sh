#!/bin/bash
export GPG_TTY=$(tty)
mvn clean jar:jar source:jar javadoc:jar gpg:sign deploy

