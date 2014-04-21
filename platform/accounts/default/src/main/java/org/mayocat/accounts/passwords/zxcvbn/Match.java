package org.mayocat.accounts.passwords.zxcvbn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a zxcvbn match object from the {@link PasswordStrength} match sequence.
 *
 * @version $Id$
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Match
{
    private String pattern;

    private Integer i;

    private Integer j;

    private String token;

    @JsonProperty("matched_word")
    private String matchedWord;

    private Integer rank;

    @JsonProperty("dictionary_name")
    private String dictionaryName;

    @JsonProperty("base_entropy")
    private Double baseEntropy;

    @JsonProperty("uppercase_entropy")
    private Double upperCaseEntropy;

    @JsonProperty("l33t_entropy")
    private Double l33tEntropy;

    private Double entropy;

    public String getPattern()
    {
        return pattern;
    }

    public Integer getI()
    {
        return i;
    }

    public Integer getJ()
    {
        return j;
    }

    public String getToken()
    {
        return token;
    }

    public String getMatchedWord()
    {
        return matchedWord;
    }

    public Integer getRank()
    {
        return rank;
    }

    public String getDictionaryName()
    {
        return dictionaryName;
    }

    public Double getBaseEntropy()
    {
        return baseEntropy;
    }

    public Double getUpperCaseEntropy()
    {
        return upperCaseEntropy;
    }

    public Double getL33tEntropy()
    {
        return l33tEntropy;
    }

    public Double getEntropy()
    {
        return entropy;
    }
}
