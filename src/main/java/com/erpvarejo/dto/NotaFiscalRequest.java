package com.erpvarejo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public class NotaFiscalRequest {

    @NotBlank(message = "Numero da nota e obrigatorio")
    private String numeroNota;

    @NotBlank(message = "UF de origem e obrigatoria")
    private String ufOrigem;

    @NotBlank(message = "UF de destino e obrigatoria")
    private String ufDestino;

    @NotEmpty(message = "Nota deve conter ao menos um item")
    @Valid
    private List<ItemNotaRequest> itens;

    public NotaFiscalRequest() {
    }

    public String getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(String numeroNota) {
        this.numeroNota = numeroNota;
    }

    public String getUfOrigem() {
        return ufOrigem;
    }

    public void setUfOrigem(String ufOrigem) {
        this.ufOrigem = ufOrigem;
    }

    public String getUfDestino() {
        return ufDestino;
    }

    public void setUfDestino(String ufDestino) {
        this.ufDestino = ufDestino;
    }

    public List<ItemNotaRequest> getItens() {
        return itens;
    }

    public void setItens(List<ItemNotaRequest> itens) {
        this.itens = itens;
    }
}
