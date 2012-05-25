import java.text.SimpleDateFormat

class TimeTagLib {

  /**
   * Renders a timestamp in a HTML time tag.
   */
  def time = { attrs, body ->
     def iso8601UTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
     iso8601UTC.setTimeZone(TimeZone.getTimeZone("UTC"))
     def format = attrs.format ? attrs.format : "yyyy/MM/dd"
     def bodyFormat = new SimpleDateFormat(format)
     def datetime = attrs.datetime
     def timeBody
     out << "<time "
     if (attrs['class']) {
       out << " class=" << attrs['class'] << " "
     }

     if (datetime) {
       out << "datetime=" << iso8601UTC.format(datetime)
       timeBody = body() == '' ? bodyFormat.format(datetime) : body()
     }
     else {
       timeBody = body() == '' ? '' : body()
     }
     out << "> " << timeBody << "</time>"
  }

}
