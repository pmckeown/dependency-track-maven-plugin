# 2. use maven to build plugin

Date: 2019-07-29

## Status

Accepted

## Context

This Maven Plugin needed a build tool to compile, test and package it.  Maven and Gradle were the only viable options.  

## Decision

I decided to use Maven as it felt a better fit to use Maven to build a Maven plugin, rather than using Gradle. 

## Consequences

We have a more verbose dependency management and build manifest.

Committers to a OSS project for a Maven plugin will be more familiar with that build framework than Gradle so the 
learning curve will be less. 

