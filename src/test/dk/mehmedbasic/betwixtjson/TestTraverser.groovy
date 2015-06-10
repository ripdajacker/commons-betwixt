package dk.mehmedbasic.betwixtjson

import dk.mehmedbasic.betwixt.model.Traverser
import junit.framework.TestCase
import org.apache.commons.betwixt.XMLIntrospector
import org.apache.commons.betwixt.io.BeanWriter
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

        AlphaBean alpha = createAlpha()

        traverser.introspect(AlphaBean.class)
        traverser.introspect(BetaBean.class)

        traverser.traverse(alpha)

        traverser.close()
    }
    public void testXml() {
        def writer = new BeanWriter(new PrintWriter(System.out))
        writer.getXMLIntrospector().introspect(AlphaBean.class)
        writer.getXMLIntrospector().introspect(BetaBean.class)

        writer.write(createAlpha())
        writer.close()
    }

    private AlphaBean createAlpha() {
        def alpha = new AlphaBean()
        alpha.setName("penis")

        def bean = new BetaBean("hej")
        alpha.addChild(bean)
        alpha.addChild(bean)
        alpha.setBetaBean(new BetaBean("I am beta"))
        alpha
    }

}
