package dk.mehmedbasic.betwixt

import org.apache.commons.betwixt.io.id.AbstractIDGenerator

/**
 * TODO - someone remind me to document this class 
 *
 * @author Jesenko Mehmedbasic
 *         created 6/10/2015.
 */
class HexIdGenerator extends AbstractIDGenerator {
  private var counter = 0

  override protected def nextIdImpl(): String = {
    counter += 1
    Integer.toHexString(counter)
  }
}
