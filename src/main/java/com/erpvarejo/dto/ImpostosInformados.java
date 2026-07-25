package com.erpvarejo.dto;

import java.math.BigDecimal;

public class ImpostosInformados {

    private BigDecimal icms;
    private BigDecimal pis;
    private BigDecimal cofins;

    public ImpostosInformados() {
    }

    public ImpostosInformados(BigDecimal icms, BigDecimal pis, BigDecimal cofins) {
        this.icms = icms;
        this.pis = pis;
        this.cofins = cofins;
    }

    public BigDecimal getIcms() {
        return icms;
    }

    public void setIcms(BigDecimal icms) {
        this.icms = icms;
    }

    public BigDecimal getPis() {
        return pis;
    }

    public void setPis(BigDecimal pis) {
        this.pis = pis;
    }

    public BigDecimal getCofins() {
        return cofins;
    }

    public void setCofins(BigDecimal cofins) {
        this.cofins = cofins;
    }
}
