package models

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat.{ dateTime, dateTimeParser }
import play.api.libs.json.{ JsString, Reads, Writes }

// Value class wrapper around DateTime to avoid orphan instances.

case class IsoDateTime(value: DateTime) extends AnyVal

object IsoDateTime {
  implicit val jsonReads = Reads.of[String].map { s ⇒
    IsoDateTime(dateTimeParser.parseDateTime(s))
  }

  implicit val jsonWrites = Writes { (x: IsoDateTime) ⇒
    JsString(dateTime.print(x.value))
  }
}
