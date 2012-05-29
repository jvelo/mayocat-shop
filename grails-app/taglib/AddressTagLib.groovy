class AddressTagLib {

  /**
   * Renders a address as HTML. Inspired by adr microformat : http://microformats.org/wiki/adr
   */
  def address = { attrs, body ->
    def adr = attrs.address
    def output = """
    <div class="adr">
    """
    if (adr.company && adr.company == "") {
      output += """
        <div class="company">${adr.company}</div>
      """
    }
    output += """
      <div class="fn">${adr.firstName} ${adr.lastName}</div>
        <div class="street-address">${adr.address}</div>
    """
    if (adr.address2 && adr.address2 != "") {
      output += """
        <div class="address-extended">${adr.address2}</div>
      """
    }
    output += """
      <span class="postal-code">${adr.zip}</span>
      <span class="locality">${adr.city}</span>
    """
    if (adr.countryCode && adr.countryCode != "") {
      output += """
        <div class="country-name">${adr.country}</div> 
      """
    }
    if (attrs.full && adr.phone) {
      output += """
        <a class="tel" href="tel:${adr.phone}">${adr.phone}</a>
      """
    }
    if (attrs.full && adr.phone2) {
      output += """
        <a class="tel" href="tel:${adr.phone2}">${adr.phone2}</a>
      """
    }
    output += """
      </div>
    """
    out << output
  }

}
