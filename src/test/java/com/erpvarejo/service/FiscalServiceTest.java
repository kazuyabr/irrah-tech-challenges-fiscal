package com.erpvarejo.service;

import com.erpvarejo.enums.CategoriaProduto;
import com.erpvarejo.enums.Uf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.erpvarejo.model.Produto;
import com.erpvarejo.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FiscalService - Motor de Calculo de Impostos")
class FiscalServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private FiscalService fiscalService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Deve calcular base de calculo com desconto")
    void deveCalcularBaseCalculoCorretamente() {
        BigDecimal resultado = fiscalService.calcularBaseCalculo(2, new BigDecimal("10.00"), new BigDecimal("1.00"));

        assertEquals(new BigDecimal("19.00"), resultado);
    }

    @Test
    @DisplayName("Deve calcular base de calculo sem desconto")
    void deveCalcularBaseCalculoSemDesconto() {
        BigDecimal resultado = fiscalService.calcularBaseCalculo(3, new BigDecimal("10.00"), BigDecimal.ZERO);

        assertEquals(new BigDecimal("30.00"), resultado);
    }

    @Test
    @DisplayName("Deve calcular ICMS interno (18%)")
    void deveCalcularIcmsInternoCorretamente() {
        BigDecimal baseCalculo = new BigDecimal("100.00");
        BigDecimal resultado = fiscalService.calcularIcms(baseCalculo, Uf.PR, Uf.PR, CategoriaProduto.ELETRONICOS);

        assertEquals(new BigDecimal("18.00"), resultado);
    }

    @Test
    @DisplayName("Deve calcular ICMS interestadual (12%)")
    void deveCalcularIcmsInterestadualCorretamente() {
        BigDecimal baseCalculo = new BigDecimal("100.00");
        BigDecimal resultado = fiscalService.calcularIcms(baseCalculo, Uf.PR, Uf.RJ, CategoriaProduto.ELETRONICOS);

        assertEquals(new BigDecimal("12.00"), resultado);
    }

    @Test
    @DisplayName("Deve calcular ICMS cesta basica (isento)")
    void deveCalcularIcmsCestaBasicaIsento() {
        BigDecimal baseCalculo = new BigDecimal("100.00");
        BigDecimal resultado = fiscalService.calcularIcms(baseCalculo, Uf.PR, Uf.PR, CategoriaProduto.CESTA_BASICA);

        assertEquals(new BigDecimal("0.00"), resultado);
    }

    @Test
    @DisplayName("Deve calcular PIS padrao (1,65%)")
    void deveCalcularPisCorretamente() {
        BigDecimal baseCalculo = new BigDecimal("100.00");
        BigDecimal resultado = fiscalService.calcularPis(baseCalculo, CategoriaProduto.ELETRONICOS);

        assertEquals(new BigDecimal("1.65"), resultado);
    }

    @Test
    @DisplayName("Deve calcular PIS bebida alcoolica (0%)")
    void deveCalcularPisBebidaAlcoolicaZerado() {
        BigDecimal baseCalculo = new BigDecimal("100.00");
        BigDecimal resultado = fiscalService.calcularPis(baseCalculo, CategoriaProduto.BEBIDAS_ALCOOLICAS);

        assertEquals(new BigDecimal("0.00"), resultado);
    }

    @Test
    @DisplayName("Deve calcular COFINS padrao (7,60%)")
    void deveCalcularCofinsCorretamente() {
        BigDecimal baseCalculo = new BigDecimal("100.00");
        BigDecimal resultado = fiscalService.calcularCofins(baseCalculo, CategoriaProduto.ELETRONICOS);

        assertEquals(new BigDecimal("7.60"), resultado);
    }

    @Test
    @DisplayName("Deve calcular COFINS bebida alcoolica (0%)")
    void deveCalcularCofinsBebidaAlcoolicaZerado() {
        BigDecimal baseCalculo = new BigDecimal("100.00");
        BigDecimal resultado = fiscalService.calcularCofins(baseCalculo, CategoriaProduto.BEBIDAS_ALCOOLICAS);

        assertEquals(new BigDecimal("0.00"), resultado);
    }

    @Test
    @DisplayName("Grupo 1 - PROD-001 Mouse USB: Venda interestadual PR->RJ com impostos corretos - APROVADA")
    void deveCalcularImpostosProdutoCorreto() {
        Produto produto = new Produto("PROD-001", "Mouse USB", CategoriaProduto.ELETRONICOS, new BigDecimal("10.00"));
        when(produtoRepository.findByCodigo("PROD-001")).thenReturn(Optional.of(produto));

        com.erpvarejo.dto.NotaFiscalRequest request = new com.erpvarejo.dto.NotaFiscalRequest();
        request.setNumeroNota("NF-TESTE");
        request.setUfOrigem("PR");
        request.setUfDestino("RJ");

        com.erpvarejo.dto.ItemNotaRequest item = new com.erpvarejo.dto.ItemNotaRequest();
        item.setCodigoProduto("PROD-001");
        item.setNome("Mouse USB");
        item.setCategoria("ELETRONICOS");
        item.setQuantidade(1);
        item.setValorUnitario(new BigDecimal("10.00"));
        item.setDesconto(BigDecimal.ZERO);

        com.erpvarejo.dto.ImpostosInformados impostos = new com.erpvarejo.dto.ImpostosInformados();
        impostos.setIcms(new BigDecimal("1.20"));
        impostos.setPis(new BigDecimal("0.17"));
        impostos.setCofins(new BigDecimal("0.76"));
        item.setImpostosInformados(impostos);

        request.setItens(java.util.List.of(item));

        com.erpvarejo.dto.NotaFiscalResponse response = fiscalService.validarNota(request);

        assertEquals(com.erpvarejo.enums.StatusNota.APROVADA, response.getStatus());
        assertTrue(response.getDivergencias().isEmpty());
    }

    @Test
    @DisplayName("Grupo 2 - PROD-004 Teclado Mecanico: Divergencia ICMS 18% vs 12% - DIVERGENTE")
    void deveDetectarDivergenciaIcms() {
        Produto produto = new Produto("PROD-004", "Teclado Mecanico", CategoriaProduto.ELETRONICOS, new BigDecimal("100.00"));
        when(produtoRepository.findByCodigo("PROD-004")).thenReturn(Optional.of(produto));

        com.erpvarejo.dto.NotaFiscalRequest request = new com.erpvarejo.dto.NotaFiscalRequest();
        request.setNumeroNota("NF-TESTE");
        request.setUfOrigem("PR");
        request.setUfDestino("RJ");

        com.erpvarejo.dto.ItemNotaRequest item = new com.erpvarejo.dto.ItemNotaRequest();
        item.setCodigoProduto("PROD-004");
        item.setNome("Teclado Mecanico");
        item.setCategoria("ELETRONICOS");
        item.setQuantidade(1);
        item.setValorUnitario(new BigDecimal("100.00"));
        item.setDesconto(new BigDecimal("10.00"));

        com.erpvarejo.dto.ImpostosInformados impostos = new com.erpvarejo.dto.ImpostosInformados();
        impostos.setIcms(new BigDecimal("16.20"));
        impostos.setPis(new BigDecimal("1.49"));
        impostos.setCofins(new BigDecimal("6.84"));
        item.setImpostosInformados(impostos);

        request.setItens(java.util.List.of(item));

        com.erpvarejo.dto.NotaFiscalResponse response = fiscalService.validarNota(request);

        assertEquals(com.erpvarejo.enums.StatusNota.DIVERGENTE, response.getStatus());
        assertFalse(response.getDivergencias().isEmpty());
        assertEquals("ICMS", response.getDivergencias().get(0).getImposto());
    }
}
