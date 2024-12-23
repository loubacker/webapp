package com.pushbait.webapp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pushbait.webapp.entity.NumeroRifa;
import com.pushbait.webapp.entity.Rifa;
import com.pushbait.webapp.service.RifaService;

@RestController
@RequestMapping("/api/rifa") // Base URL será: http://localhost:8080/api/rifa
public class RifaController {

    @Autowired
    private RifaService rifaService;

    /**
     * Endpoint para listar todas as rifas disponíveis.
     * 
     * @return Lista de rifas.
     */
    @GetMapping("/listar")
    public List<Rifa> listarRifas() {
        return rifaService.listarRifas();
    }

    /**
     * Endpoint para listar os números disponíveis de uma rifa específica.
     * 
     * @param id ID da rifa.
     * @return Lista de números disponíveis.
     */
    @GetMapping("/{id}/numeros")
    public List<NumeroRifa> listarNumeros(@PathVariable Long id) {
        return rifaService.listarNumerosDisponiveis();
    }

    /**
     * Endpoint para comprar um número de rifa com PIX.
     * 
     * @param id      ID da rifa.
     * @param numero  Número escolhido pelo comprador.
     * @param requestBody Mapa contendo os dados do comprador (nome e telefone).
     * @return Dados da transação PIX ou erro.
     */
    @PostMapping("/{id}/comprar/{numero}")
    public ResponseEntity<?> comprarNumero(
            @PathVariable Long id,
            @PathVariable Integer numero,
            @RequestBody Map<String, String> requestBody) {

        // Extrai os dados do comprador e telefone do corpo da requisição
        String comprador = requestBody.get("comprador");
        String telefone = requestBody.get("telefone");

        try {
            // Chama o serviço para processar a compra e gerar o PIX
            Map<String, Object> resultado = rifaService.comprarNumeroComPix(id, numero, comprador, telefone);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            // Retorna um erro amigável em caso de falha
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("erro", e.getMessage()));
        }
    }
}