package dk.mehmedbasic.betwixtjson

import dk.mehmedbasic.betwixt.model.Traverser
import junit.framework.TestCase
import org.apache.commons.betwixt.XMLIntrospector
import org.apache.commons.betwixt.strategy.AlphaBean
import org.apache.commons.betwixt.strategy.BetaBean

/**
 * TODO - someone remind me to document this class 
 *
 * @author Jesenko Mehmedbasic
 * created 5/13/2015.
 */
class TestTraverser extends TestCase {

    public void testScala() throws Exception {
        def introspector = new XMLIntrospector()
        introspector.introspect(AlphaBean.class)

        def traverser = new Traverser()

        def alpha = new AlphaBean()
        alpha.setName("penis")
        alpha.addChild(new BetaBean("hej"))
        alpha.setBetaBean(new BetaBean("I am beta"))

        traverser.introspect(AlphaBean.class)
        traverser.introspect(BetaBean.class)

        traverser.traverse(alpha)

        traverser.close()
    }

}
