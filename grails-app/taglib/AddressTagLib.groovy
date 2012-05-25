class AddressTagLib {

  /**
   * Renders a timestamp in a HTML time tag.
   */
  def address = { attrs, body ->
    def adr = attrs.address
    def output = """
    <div class="adr">
      <div class="street-address">${adr.address}</div>
    """
    if (adr.address2 && adr.address2 != "") {
      output << """
        <div class="address-extended">${adr.address2}</div>
      """
    }
    output += """
      <span class="postal-code">${adr.zip}</span>
      <span class="locality">${adr.city}</span>
    """
    if (adr.countryCode && adr.countryCode != "") {
      output << """
        <div class="country-name">${adr.country}</div> 
      """
    }
    output += """
      </div>
    """
    out << output
  }

}
