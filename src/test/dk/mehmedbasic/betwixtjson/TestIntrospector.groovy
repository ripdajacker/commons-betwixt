package dk.mehmedbasic.betwixtjson

import dk.mehmedbasic.betwixt.BeanIntrospector
import junit.framework.TestCase
import org.apache.commons.betwixt.introspection.BeanWithBeanInfoBean
import org.junit.Assert

import java.beans.Introspector

/**
 * TODO - someone remind me to document this class 
 *
 * @author Jesenko Mehmedbasic
 * created 5/21/2015.
 */
class TestIntrospector extends TestCase {
    public void testDifference() throws Exception {
        def expected = Introspector.getBeanInfo(BeanWithBeanInfoBean.class)
        def actual = BeanIntrospector.getBeanInfo(BeanWithBeanInfoBean.class)
        actual.getPropertyDescriptors()


        expected.getPropertyDescriptors() each {
            def first = it
            boolean found = false
            if (first.getReadMethod() != null) {
                if (first.getReadMethod().declaringClass == Object.class) {
                    found = true

                }
            }
            if (!found) {
                actual.getPropertyDescriptors() each {
                    def second = it
                    found |= first.equals(second)
                }
            }
            Assert.assertTrue("Properties should exist in both cases: ${first.getName()}", found)
        }
        println()
    }
}
