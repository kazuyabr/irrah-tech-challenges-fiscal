package com.erpvarejo.dto;

import com.erpvarejo.enums.StatusNota;
import java.math.BigDecimal;
import java.util.List;

public class NotaFiscalResponse {

    private String numeroNota;
    private StatusNota status;
    private BigDecimal valorTotalNota;
    private BigDecimal totalImpostosCalculados;
    private List<DivergenciaResponse> divergencias;

    public NotaFiscalResponse() {
    }

    public NotaFiscalResponse(String numeroNota, StatusNota status, BigDecimal valorTotalNota,
                              BigDecimal totalImpostosCalculados, List<DivergenciaResponse> divergencias) {
        this.numeroNota = numeroNota;
        this.status = status;
        this.valorTotalNota = valorTotalNota;
        this.totalImpostosCalculados = totalImpostosCalculados;
        this.divergencias = divergencias;
    }

    public String getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(String numeroNota) {
        this.numeroNota = numeroNota;
    }

    public StatusNota getStatus() {
        return status;
    }

    public void setStatus(StatusNota status) {
        this.status = status;
    }

    public BigDecimal getValorTotalNota() {
        return valorTotalNota;
    }

    public void setValorTotalNota(BigDecimal valorTotalNota) {
        this.valorTotalNota = valorTotalNota;
    }

    public BigDecimal getTotalImpostosCalculados() {
        return totalImpostosCalculados;
    }

    public void setTotalImpostosCalculados(BigDecimal totalImpostosCalculados) {
        this.totalImpostosCalculados = totalImpostosCalculados;
    }

    public List<DivergenciaResponse> getDivergencias() {
        return divergencias;
    }

    public void setDivergencias(List<DivergenciaResponse> divergencias) {
        this.divergencias = divergencias;
    }
}
