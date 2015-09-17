package dk.mehmedbasic.betwixt

import org.apache.commons.betwixt.io.id.AbstractIDGenerator

/**
 * An id generator that generates hexidecimal strings.
 */
class HexIdGenerator extends AbstractIDGenerator {
   private var counter = 0

   override protected def nextIdImpl(): String = {
      counter += 1
      Integer.toHexString(counter)
   }
}
