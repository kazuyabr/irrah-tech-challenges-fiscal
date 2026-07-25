package com.erpvarejo.service;

import com.erpvarejo.dto.*;
import com.erpvarejo.enums.CategoriaProduto;
import com.erpvarejo.enums.StatusNota;
import com.erpvarejo.enums.Uf;
import com.erpvarejo.model.Produto;
import com.erpvarejo.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class FiscalService {

    private static final BigDecimal MARGEM_TOLERANCIA = new BigDecimal("0.02");

    private static final BigDecimal ICMS_INTERNO = new BigDecimal("18");
    private static final BigDecimal ICMS_INTERESTADUAL = new BigDecimal("12");
    private static final BigDecimal PIS_PADRAO = new BigDecimal("1.65");
    private static final BigDecimal COFINS_PADRAO = new BigDecimal("7.60");

    private final ProdutoRepository produtoRepository;

    public FiscalService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public NotaFiscalResponse validarNota(NotaFiscalRequest request) {
        Uf origem = Uf.valueOf(request.getUfOrigem());
        Uf destino = Uf.valueOf(request.getUfDestino());

        List<DivergenciaResponse> divergencias = new ArrayList<>();
        BigDecimal totalImpostosCalculados = BigDecimal.ZERO;
        BigDecimal valorTotalNota = BigDecimal.ZERO;

        for (ItemNotaRequest item : request.getItens()) {
            Produto produto = produtoRepository.findByCodigo(item.getCodigoProduto())
                    .orElseThrow(() -> new RuntimeException("Produto nao encontrado: " + item.getCodigoProduto()));

            BigDecimal baseCalculo = calcularBaseCalculo(item.getQuantidade(), item.getValorUnitario(), item.getDesconto());
            valorTotalNota = valorTotalNota.add(baseCalculo);

            CategoriaProduto categoria = CategoriaProduto.valueOf(item.getCategoria());

            BigDecimal icmsCalculado = calcularIcms(baseCalculo, origem, destino, categoria);
            BigDecimal pisCalculado = calcularPis(baseCalculo, categoria);
            BigDecimal cofinsCalculado = calcularCofins(baseCalculo, categoria);

            totalImpostosCalculados = totalImpostosCalculados.add(icmsCalculado).add(pisCalculado).add(cofinsCalculado);

            compararImpostos(item, baseCalculo, origem, destino, icmsCalculado, pisCalculado, cofinsCalculado, divergencias);
        }

        StatusNota status = divergencias.isEmpty() ? StatusNota.APROVADA : StatusNota.DIVERGENTE;

        return new NotaFiscalResponse(
                request.getNumeroNota(),
                status,
                valorTotalNota,
                totalImpostosCalculados,
                divergencias
        );
    }

    public BigDecimal calcularBaseCalculo(Integer quantidade, BigDecimal valorUnitario, BigDecimal desconto) {
        BigDecimal subtotal = valorUnitario.multiply(BigDecimal.valueOf(quantidade));
        if (desconto != null) {
            return subtotal.subtract(desconto);
        }
        return subtotal;
    }

    public BigDecimal calcularIcms(BigDecimal baseCalculo, Uf origem, Uf destino, CategoriaProduto categoria) {
        if (categoria == CategoriaProduto.CESTA_BASICA) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal aliquota = origem.mesmoEstado(destino) ? ICMS_INTERNO : ICMS_INTERESTADUAL;
        return baseCalculo.multiply(aliquota).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularPis(BigDecimal baseCalculo, CategoriaProduto categoria) {
        if (categoria == CategoriaProduto.BEBIDAS_ALCOOLICAS) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return baseCalculo.multiply(PIS_PADRAO).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularCofins(BigDecimal baseCalculo, CategoriaProduto categoria) {
        if (categoria == CategoriaProduto.BEBIDAS_ALCOOLICAS) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return baseCalculo.multiply(COFINS_PADRAO).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private void compararImpostos(ItemNotaRequest item, BigDecimal baseCalculo, Uf origem, Uf destino,
                                  BigDecimal icmsCalculado, BigDecimal pisCalculado, BigDecimal cofinsCalculado,
                                  List<DivergenciaResponse> divergencias) {

        compararUmImposto(item, "ICMS", item.getImpostosInformados().getIcms(), icmsCalculado,
                gerarMensagemIcms(baseCalculo, origem, destino), divergencias);

        compararUmImposto(item, "PIS", item.getImpostosInformados().getPis(), pisCalculado,
                gerarMensagemPis(baseCalculo), divergencias);

        compararUmImposto(item, "COFINS", item.getImpostosInformados().getCofins(), cofinsCalculado,
                gerarMensagemCofins(baseCalculo), divergencias);
    }

    private void compararUmImposto(ItemNotaRequest item, String nomeImposto, BigDecimal informado, BigDecimal correto,
                                   String mensagemBase, List<DivergenciaResponse> divergencias) {
        if (informado.subtract(correto).abs().compareTo(MARGEM_TOLERANCIA) > 0) {
            String mensagem = String.format("Divergencia de %s: %s", nomeImposto, mensagemBase);
            divergencias.add(new DivergenciaResponse(
                    item.getCodigoProduto(),
                    nomeImposto,
                    informado,
                    correto,
                    mensagem
            ));
        }
    }

    private String gerarMensagemIcms(BigDecimal baseCalculo, Uf origem, Uf destino) {
        if (origem.mesmoEstado(destino)) {
            return String.format("Operacao interna (%s -> %s) deve aplicar 18%% sobre a base R$ %s.", origem, destino, baseCalculo);
        }
        return String.format("Operacao interestadual (%s -> %s) deve aplicar 12%% sobre a base R$ %s.", origem, destino, baseCalculo);
    }

    private String gerarMensagemPis(BigDecimal baseCalculo) {
        return String.format("Alquota padrao de PIS e 1,65%% sobre a base R$ %s.", baseCalculo);
    }

    private String gerarMensagemCofins(BigDecimal baseCalculo) {
        return String.format("Alquota padrao de COFINS e 7,60%% sobre a base R$ %s.", baseCalculo);
    }
}
