Jakarta Commons Betwixt
=======================

Welcome to the Betwixt component of the Jakarta Commons
project.

The Betwixt build process is now Mavenized!

For those who haven't heard of Maven, its a unified 
project build processor. See

	http://jakarta.apache.org/turbine/maven/ 

for the Maven project documentation. This means that you
now need to download and install Maven if you want to 
build Betwixt. Full details of  how this can be done can 
be found on the Maven site.

Maven downloads all neccessary dependencies for you
- so you don't need to worry about that any more!

Maven uses a set of standard build commands. Here's a 
couple to get you started:

	robert% maven

this executes 'java:jar' which compiles the source, 
runs the unit tests and creates a jar.

	robert% maven dist:build

creates a distribution (including documentation) from
the current source as well as doing everything that 
the last comment did.

See the maven documentation for more details and 
information about the other standard build commands. 


Note: 
	Maven creates the documentation in 
betwixt/target/docs rather than betwixt/dist/docs.
