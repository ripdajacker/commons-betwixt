package dk.mehmedbasic.jsontransformer

import groovy.transform.TypeChecked

/**
 * First version of the bean.
 */
@TypeChecked
class TransformingBean1 {
    String attribute
    String element

    void setElement(String element) {
        this.element = element
    }

    String getElement() {
        return element
    }
}
