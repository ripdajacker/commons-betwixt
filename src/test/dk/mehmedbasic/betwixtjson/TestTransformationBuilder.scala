package dk.mehmedbasic.betwixtjson

import java.util

import com.google.gson.Gson
import dk.mehmedbasic.betwixt.json.dsl.{TransformationImplicits, TransformerBuilder}
import junit.framework.TestCase
import org.junit.Assert

import scala.language.postfixOps

/**
 * TODO - someone remind me to document this class 
 *
 * @author Jesenko Mehmedbasic
 *         created 9/21/2015.
 */
class TestTransformationBuilder extends TestCase with TransformationImplicits {
   val jsonSource =
      """|{
        |    "person": {
        |        "father": {
        |            "firstName": "John",
        |            "lastName": "Hancock",
        |            "birthday": 1956,
        |            "age": 56
        |        },
        |        "cat": {
        |            "name": "Jenny The Cat",
        |            "age": 6
        |        }
        |    }
        |}""".stripMargin

   var map: java.util.Map[String, Any] = null

   override def setUp(): Unit = {
      val json: util.Map[String, Any] = new Gson().fromJson(jsonSource, classOf[util.Map[String, Any]])
      map = json.get("person").asInstanceOf[java.util.Map[String, Any]]
   }

   def testRename(): Unit = {
      val list = new TransformerBuilder("")
                 .renameKey("father" -> "john")
                 .renameKey("john" -> "vader")
                 .renameKey("vader" -> "anakin")
                 .transformations()

      Assert.assertNotNull("There is a value named named 'father'", map.get("father"))

      list.foreach(_.apply(map))

      Assert.assertNull("There is not a value named 'father'", map.get("father"))
      Assert.assertNull("There is not a value named 'john'", map.get("john"))
      Assert.assertNull("There is not a value named 'vader'", map.get("vader"))
      Assert.assertNotNull("There is now a value named 'anakin'", map.get("anakin"))
   }

   def testMergeObjects(): Unit = {
      val list = new TransformerBuilder("")
                 .mergeKeys("father" -> "cat")
                 .transformations()

      Assert.assertNotNull("There is a value named named 'father'", map.get("father"))
      Assert.assertNotNull("There is a value named named 'cat'", map.get("cat"))

      list.foreach(_.apply(map))

      Assert.assertNull("There is not a value named named 'father'", map.get("father"))
      Assert.assertNotNull("There is a value named named 'cat'", map.get("cat"))
      Assert.assertEquals("Merged 'firstName' should be John", "John", findValue(map, "cat", "firstName"))
      Assert.assertEquals("Merged 'lastName' should be Hancock", "Hancock", findValue(map, "cat", "lastName"))
      Assert.assertEquals("Merged 'age' should be 56", 56d, findValue(map, "cat", "age"))
      Assert.assertEquals("Merged 'name' should be Jenny The Cat", "Jenny The Cat", findValue(map, "cat", "name"))
   }

   def testMergeValues(): Unit = {
      val list = TransformerBuilder("")
                 .mergeKeys("father" -> "cat")
                 .transformations()

      Assert.assertNotNull("There is a value named named 'father'", map.get("father"))
      Assert.assertNotNull("There is a value named named 'cat'", map.get("cat"))

      list.foreach(_.apply(map))

      Assert.assertNull("There is not a value named named 'father'", map.get("father"))
      Assert.assertNotNull("There is a value named named 'cat'", map.get("cat"))
      Assert.assertEquals("Merged 'firstName' should be John", "John", findValue(map, "cat", "firstName"))
      Assert.assertEquals("Merged 'lastName' should be Hancock", "Hancock", findValue(map, "cat", "lastName"))
      Assert.assertEquals("Merged 'age' should be 56", 56d, findValue(map, "cat", "age"))
      Assert.assertEquals("Merged 'name' should be Jenny The Cat", "Jenny The Cat", findValue(map, "cat", "name"))
   }

   def testMergeWithParentSub(): Unit = {
      val list = TransformerBuilder(".shit")
                 .doSomething("father" -> "cat" in "shit" downward)


   }

   private def findValue(map: util.Map[String, Any], objectKey: String, valueKey: String): Any = {
      map.get(objectKey).asInstanceOf[util.Map[String, Any]].get(valueKey)
   }

}
