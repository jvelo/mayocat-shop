import org.springframework.context.i18n.LocaleContextHolder as LCH

class OrderTagLib {

  def grailsApplication

  /**
   * Renders a address as HTML. Inspired by adr microformat : http://microformats.org/wiki/adr
   * 
   * @attr address REQUIRED the address to render
   * @full renders the address with all details (like telephone number) or not. Default is false.
   */
  def address = { attrs, body ->
    def adr = attrs.address
    out << """
    <div class="adr">
    """
    if (adr.company && adr.company == "") {
      out << """
        <div class="company">${adr.company}</div>
      """
    }
    out << """
      <div class="fn">${adr.firstName} ${adr.lastName}</div>
        <div class="street-address">${adr.address}</div>
    """
    if (adr.address2 && adr.address2 != "") {
      out << """
        <div class="address-extended">${adr.address2}</div>
      """
    }
    out << """
      <span class="postal-code">${adr.zip}</span>
      <span class="locality">${adr.city}</span>
    """
    if (adr.countryCode && adr.countryCode != "") {
      out << """
        <div class="country-name">${adr.country}</div> 
      """
    }
    if (attrs.full && adr.phone) {
      out << """
        <a class="tel" href="tel:${adr.phone}">${adr.phone}</a>
      """
    }
    if (attrs.full && adr.phone2) {
      out << """
        <a class="tel" href="tel:${adr.phone2}">${adr.phone2}</a>
      """
    }
    out << """
      </div>
    """
  }

  /**
   * Displays a human-readable version of the status of an order from its internal status code.
   * 
   * @attr status REQUIRED the status of the order
   */
  def orderStatus = { attrs, body ->
    def humanReadable = attrs.status.code.toLowerCase().replaceAll("_", " ").capitalize()
    def applicationContext = grailsApplication.getMainContext()
    humanReadable = applicationContext.getMessage("orders.status." + attrs.status.code, [] as Object[], humanReadable, LCH.getLocale())
    out << humanReadable
  }

}
