package dk.mehmedbasic.jsontransformer

import groovy.transform.TypeChecked

/**
 * A betwixt-only handling of versions.
 */
@TypeChecked
class LegacyHandlingBean {
    private String renamedAttribute
    private String element

    /**
     * Getter for the first version of the XML, always null.
     *
     * @return null
     */
    String getAttribute() {
        return null
    }

    /**
     * Setter for the first version of 'attribute', functionally same as the rest
     *
     * @param value the value
     */
    void setAttribute(String value) {
        this.renamedAttribute = value
    }

    String getElement_() {
        return null
    }

    void setElement_(String element) {
        this.element = element
    }

    String getElement() {
        return element
    }

    void setElement(String element) {
        this.element = element
    }

    String getRenamedAttribute() {
        return renamedAttribute
    }

    void setRenamedAttribute(String renamedAttribute) {
        this.renamedAttribute = renamedAttribute
    }

    String getRenamedAttribute_() {
        return null
    }

    void setRenamedAttribute_(String renamedAttribute) {
        this.renamedAttribute = renamedAttribute
    }
}
