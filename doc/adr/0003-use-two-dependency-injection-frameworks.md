# 3. use two dependency injection frameworks

Date: 2019-07-29

## Status

Accepted

## Context

Dependency Injection (DI) is a well-known software design paradigm that makes developing de-coupled software easier 
using Inversion of Control principles and promotes designing for testability.

Maven provides a very basic DI framework, called [Plexus](https://codehaus-plexus.github.io/), which allows for the 
injection of the Maven logger and the runtime configuration.  However that does not extend to the creation and injection
of collaborators at runtime.  This can lead to cluttered and hard to test code with having to pass around the logger and 
config.

As such it was deemed that, to facilitate testability and decoupling, another approach to bean creation and wiring was
required.

## Decision

Use the Plexus DI framework to inject the Maven-specific logger and config and make these available to a different DI 
framework, Apache [Sisu](https://www.eclipse.org/sisu/), to inject into collaborating components.

## Consequences

Now the abstract base class for all Mojos in the project must inherit from a template method parent.  Config and logging
wrappers are created by the Sisu IoC container and can then be injected into other beans created by the container.  The 
template method base class ensures that the config and logger are initialised with the correct values from the Plexus 
framework.

This makes it slightly harder to understand how the Maven-supplied config and logger make it into the Sisu-managed 
beans, however the increase to testability is worth this extra cognitive load.
