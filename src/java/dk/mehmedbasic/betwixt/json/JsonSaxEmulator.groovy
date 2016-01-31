package dk.mehmedbasic.betwixt.json

import groovy.transform.TypeChecked
import groovy.util.logging.Commons
import org.apache.commons.betwixt.BindingConfiguration
import org.apache.commons.betwixt.XMLIntrospector
import org.apache.commons.betwixt.io.read.ReadConfiguration

/**
 * TODO - someone remind me to document this class 
 */
@TypeChecked
@Commons
class JsonSaxEmulator {
    XMLIntrospector introspector = new XMLIntrospector();
    Set registeredClasses = new HashSet();
    BindingConfiguration bindingConfiguration = new BindingConfiguration();
    ReadConfiguration readConfiguration = new ReadConfiguration();


}
