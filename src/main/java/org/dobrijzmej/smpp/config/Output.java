package org.dobrijzmej.smpp.config;

public class Output {
    private String url;
    private String method;
    private String mask;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public Output(String url, String method, String mask) {
        this.url = url;
        this.method = method;
        this.mask = mask;
    }

    public Output() {
    }

    @Override
    public String toString() {
        return "Recipient on method ["+method+"] to URL ["+url+"] with mask ["+mask+"].";
    }
}
