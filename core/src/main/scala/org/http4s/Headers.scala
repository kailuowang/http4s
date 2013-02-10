package org.http4s

import scala.collection.{mutable, immutable}
import scala.collection.generic.CanBuildFrom
import spray.http.HttpHeader
import spray.http.parser.HttpParser

class Headers private(headers: Seq[HttpHeader])
  extends immutable.Seq[HttpHeader]
  with collection.SeqLike[HttpHeader, Headers]
{
  override protected[this] def newBuilder: mutable.Builder[HttpHeader, Headers] = Headers.newBuilder

  def length: Int = headers.length

  def apply(idx: Int): HttpHeader = headers(idx)

  def iterator: Iterator[HttpHeader] = headers.iterator

  def apply(name: String): HttpHeader = get(name).get

  def get(name: String): Option[HttpHeader] =
    find(_ is name.toLowerCase) map (HttpParser.parseHeader) flatMap (_.right.toOption)

  def getAll(name: String): Seq[HttpHeader] =
    (filter(_ is name.toLowerCase) map HttpParser.parseHeader flatMap (_.right.toOption))
}

object Headers {
  val Empty = apply()

  def apply(headers: HttpHeader*): Headers = new Headers(headers)

  implicit def canBuildFrom: CanBuildFrom[Traversable[HttpHeader], HttpHeader, Headers] =
    new CanBuildFrom[TraversableOnce[HttpHeader], HttpHeader, Headers] {
      def apply(from: TraversableOnce[HttpHeader]): mutable.Builder[HttpHeader, Headers] = newBuilder
      def apply(): mutable.Builder[HttpHeader, Headers] = newBuilder
    }

  private def newBuilder: mutable.Builder[HttpHeader, Headers] =
    mutable.ListBuffer.newBuilder[HttpHeader] mapResult (new Headers(_))


}

// OCD: Alphabetize please
object HeaderNames {
   val AcceptLanguage = "Accept-Language"
   val FrontEndHttps = "Front-End-Https"
   val Referer = "Referer"
   val XForwardedFor = "X-Forwarded-For"
   val XForwardedProto = "X-Forwarded-Proto"
}
