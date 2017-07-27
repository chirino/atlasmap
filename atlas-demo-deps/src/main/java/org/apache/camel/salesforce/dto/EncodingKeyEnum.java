/*
 * Salesforce DTO generated by camel-salesforce-maven-plugin
 * Generated on: Mon Mar 02 02:58:34 EST 2015
 */
package org.apache.camel.salesforce.dto;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * Salesforce Enumeration DTO for picklist EncodingKey
 */
public enum EncodingKeyEnum {

    // UTF-8
    UTF_8("UTF-8"),
    // ISO-8859-1
    ISO_8859_1("ISO-8859-1"),
    // Shift_JIS
    SHIFT_JIS("Shift_JIS"),
    // ISO-2022-JP
    ISO_2022_JP("ISO-2022-JP"),
    // EUC-JP
    EUC_JP("EUC-JP"),
    // ks_c_5601-1987
    KS_C_5601_1987("ks_c_5601-1987"),
    // Big5
    BIG5("Big5"),
    // GB2312
    GB2312("GB2312"),
    // Big5-HKSCS
    BIG5_HKSCS("Big5-HKSCS"),
    // x-SJIS_0213
    X_SJIS_0213("x-SJIS_0213");

    final String value;

    private EncodingKeyEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static EncodingKeyEnum fromValue(String value) {
        for (EncodingKeyEnum e : EncodingKeyEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException(value);
    }

}