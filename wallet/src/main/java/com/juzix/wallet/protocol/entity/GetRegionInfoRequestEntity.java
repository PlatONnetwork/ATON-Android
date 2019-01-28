package com.juzix.wallet.protocol.entity;

/**
 * @author matrixelement
 */
public class GetRegionInfoRequestEntity {

    private String query;
    private String fields;
    private String lang;

    public GetRegionInfoRequestEntity(String query, String fields, String lang) {
        this.query = query;
        this.fields = fields;
        this.lang = lang;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
