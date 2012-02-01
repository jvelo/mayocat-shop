package org.mayocat.shop.grails

class BynameNormalizerService {

  def normalize = { 
    return java.text.Normalizer.normalize(it.trim().toLowerCase(), java.text.Normalizer.Form.NFKD)
		               .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                   .replaceAll("\\-", " ")
                   .replaceAll("[^\\w\\ \\'\\u2019]", "")
                   .replaceAll("[\\'\\u2019\\s]+", "-")
  }

}
