package dk.mehmedbasic.betwixt.equivavlency

import groovy.transform.TypeChecked

/**
 * A comparison of equivalency of a XML file when written by the JSON writer.
 */
@TypeChecked
class EquivalencyComparison {
    InputStream xmlInput

    ReadWriteStrategy jsonStrategy
    ReadWriteStrategy xmlStrategy

    EquivalencyComparison(InputStream xmlInput, ReadWriteStrategy jsonStrategy, ReadWriteStrategy xmlStrategy) {
        this.xmlInput = xmlInput
        this.jsonStrategy = jsonStrategy
        this.xmlStrategy = xmlStrategy
    }

    Tuple2<String, String> execute() {
        // First read/write
        def model = xmlStrategy.deserializeStream(xmlInput)
        def xml = xmlStrategy.serialize(model)

        // Second write to eliminate differences
        model = xmlStrategy.deserializeReader(new StringReader(xml))
        xml = xmlStrategy.serialize(model)

        def json = jsonStrategy.serialize(model)
        def jsonModel = jsonStrategy.deserializeReader(new StringReader(json))

        def actual = xmlStrategy.serialize(jsonModel)

        return new Tuple2<String, String>(xml, actual)
    }
}
