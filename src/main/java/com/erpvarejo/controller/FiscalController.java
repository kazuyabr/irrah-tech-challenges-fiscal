package com.erpvarejo.controller;

import com.erpvarejo.dto.NotaFiscalRequest;
import com.erpvarejo.dto.NotaFiscalResponse;
import com.erpvarejo.service.FiscalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fiscal")
@Tag(name = "Fiscal", description = "Endpoints de validacao fiscal")
public class FiscalController {

    private final FiscalService fiscalService;

    public FiscalController(FiscalService fiscalService) {
        this.fiscalService = fiscalService;
    }

    @PostMapping("/validar-nota")
    @Operation(summary = "Validar nota fiscal", description = "Valida e calcula impostos de uma nota fiscal")
    public ResponseEntity<NotaFiscalResponse> validarNota(@Valid @RequestBody NotaFiscalRequest request) {
        NotaFiscalResponse response = fiscalService.validarNota(request);
        return ResponseEntity.ok(response);
    }
}
