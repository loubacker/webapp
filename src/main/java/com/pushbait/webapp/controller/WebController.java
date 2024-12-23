package com.pushbait.webapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.pushbait.webapp.entity.NumeroRifa;
import com.pushbait.webapp.entity.Rifa;
import com.pushbait.webapp.service.RifaService;

@Controller
public class WebController {

    @Autowired
    private RifaService rifaService;

    @GetMapping("/rifa")
    public String rifa(Model model) {
        // Adicionar Log para Depurar
        System.out.println("Iniciando busca dos números disponíveis...");

        // Buscar todos os números disponíveis da tabela numero_rifa
        List<NumeroRifa> numeros = rifaService.listarTodosNumeros();

        model.addAttribute("numerosDisponiveis", numeros);
        return "rifa"; // Renderiza rifa.html
    }

    // Página de detalhes de uma rifa
    @GetMapping("/rifa/{id}")
    public String detalhesRifa(@PathVariable Long id, Model model) {
        Rifa rifa = rifaService.listarRifas()
                .stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Rifa não encontrada!"));

        List<NumeroRifa> numerosDisponiveis = rifaService.listarNumerosDisponiveis();
        
        model.addAttribute("rifa", rifa);
        model.addAttribute("numeros", numerosDisponiveis);
        return "rifa"; // Arquivo Thymeleaf: detalhes-rifa.html
    }
}

