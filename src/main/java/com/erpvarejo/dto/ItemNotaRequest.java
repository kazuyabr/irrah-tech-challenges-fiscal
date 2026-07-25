package com.erpvarejo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ItemNotaRequest {

    @NotBlank(message = "Codigo do produto e obrigatorio")
    private String codigoProduto;

    @NotBlank(message = "Nome do produto e obrigatorio")
    private String nome;

    @NotNull(message = "Categoria e obrigatoria")
    private String categoria;

    @NotNull(message = "Quantidade e obrigatoria")
    @Min(value = 1, message = "Quantidade deve ser maior que zero")
    private Integer quantidade;

    @NotNull(message = "Valor unitario e obrigatorio")
    @DecimalMin(value = "0.01", message = "Valor unitario deve ser maior que zero")
    private BigDecimal valorUnitario;

    @DecimalMin(value = "0.00", message = "Desconto nao pode ser negativo")
    private BigDecimal desconto;

    @NotNull(message = "Impostos informados sao obrigatorios")
    @Valid
    private ImpostosInformados impostosInformados;

    public ItemNotaRequest() {
    }

    public String getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(String codigoProduto) {
        this.codigoProduto = codigoProduto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public ImpostosInformados getImpostosInformados() {
        return impostosInformados;
    }

    public void setImpostosInformados(ImpostosInformados impostosInformados) {
        this.impostosInformados = impostosInformados;
    }
}
