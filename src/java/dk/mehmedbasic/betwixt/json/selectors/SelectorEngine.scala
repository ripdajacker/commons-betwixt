package dk.mehmedbasic.betwixt.json.selectors

import java.io.StringReader

import com.steadystate.css.parser.{CSSOMParser, SACParserCSS3}
import dk.mehmedbasic.betwixt.json.ast.{JsonDom, JsonNode}
import org.w3c.css.sac.{Condition, ConditionalSelector, ElementSelector, InputSource}

/**
 * TODO[JEKM] - someone remind me to document this class.
 */
class SelectorEngine() {

   private val parser: CSSOMParser = new CSSOMParser(new SACParserCSS3())

   def parse(source: String) = parser.parseSelectors(new InputSource(new StringReader(source)))

   sealed trait JsonDomSelector {
      def select(dom: JsonDom): List[JsonNode]
   }


   sealed implicit class ElementSelectorDecorator(elementSelector: ElementSelector) extends JsonDomSelector {
      def value() = elementSelector.getLocalName

      override def select(jsonDom: JsonDom): List[JsonNode] = jsonDom.selectByTag(value())
   }

sealed implicit class ConditionalSelectorDecorator(conditional: ConditionalSelector) extends JsonDomSelector {
   override def select(dom: JsonDom): List[JsonNode] = {
      val short: Short = conditional.getCondition.getConditionType

      short match {
         case Condition.SAC_CLASS_CONDITION =>
         case _ =>
      }

      dom.selectByProperty("")
   }
}


}

