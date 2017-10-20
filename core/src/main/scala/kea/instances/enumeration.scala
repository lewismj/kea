package kea
package instances


import com.typesafe.config.Config
import scala.reflect.ClassTag


trait EnumerationInstances {

  implicit def enumValueReader[T <: Enumeration : ClassTag]: ConfigReader[T#Value] = (c: Config, p: String) =>
    validated({
      val cl = implicitly[ClassTag[T]].runtimeClass
      val field = cl.getField("MODULE$")
      // scalastyle:off null
      val enum = field.get(null).asInstanceOf[T]
      // scalastyle:on null
      enum.values.find(_.toString == c.getString(p)).get.asInstanceOf[T#Value]
    })
}
