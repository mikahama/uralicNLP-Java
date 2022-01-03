#!/bin/bash
export GPG_TTY=$(tty)
mvn clean package source:jar javadoc:jar gpg:sign deploy
