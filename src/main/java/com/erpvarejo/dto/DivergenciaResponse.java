package com.erpvarejo.dto;

import java.math.BigDecimal;

public class DivergenciaResponse {

    private String codigoProduto;
    private String imposto;
    private BigDecimal valorInformado;
    private BigDecimal valorCorreto;
    private String mensagem;

    public DivergenciaResponse() {
    }

    public DivergenciaResponse(String codigoProduto, String imposto, BigDecimal valorInformado,
                               BigDecimal valorCorreto, String mensagem) {
        this.codigoProduto = codigoProduto;
        this.imposto = imposto;
        this.valorInformado = valorInformado;
        this.valorCorreto = valorCorreto;
        this.mensagem = mensagem;
    }

    public String getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(String codigoProduto) {
        this.codigoProduto = codigoProduto;
    }

    public String getImposto() {
        return imposto;
    }

    public void setImposto(String imposto) {
        this.imposto = imposto;
    }

    public BigDecimal getValorInformado() {
        return valorInformado;
    }

    public void setValorInformado(BigDecimal valorInformado) {
        this.valorInformado = valorInformado;
    }

    public BigDecimal getValorCorreto() {
        return valorCorreto;
    }

    public void setValorCorreto(BigDecimal valorCorreto) {
        this.valorCorreto = valorCorreto;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
