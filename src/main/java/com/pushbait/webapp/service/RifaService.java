package com.pushbait.webapp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pushbait.webapp.entity.NumeroRifa;
import com.pushbait.webapp.entity.Rifa;
import com.pushbait.webapp.payload.PixRequestPayload;
import com.pushbait.webapp.repository.NumeroRifaRepository;
import com.pushbait.webapp.repository.RifaRepository;

@Service // Define a classe como um serviço gerenciado pelo Spring
public class RifaService {

    @Autowired
    private RifaRepository rifaRepository; // Repositório para acessar os dados de rifas

    @Autowired
    private NumeroRifaRepository numeroRifaRepository; // Repositório para acessar os números das rifas

    @Autowired
    private PixService pixService; // Serviço para lidar com a integração PIX

    /**
     * Lista todos os números de rifas cadastrados.
     *
     * @return Lista de todos os números.
     */
    public List<NumeroRifa> listarTodosNumeros() {
        return numeroRifaRepository.findAll();
    }

    /**
     * Lista todas as rifas disponíveis.
     *
     * @return Lista de objetos Rifa.
     */
    public List<Rifa> listarRifas() {
        return rifaRepository.findAll();
    }

    /**
     * Busca uma rifa pelo ID.
     *
     * @param rifaId ID da rifa.
     * @return Objeto Rifa.
     */
    public Rifa buscarRifaPorId(Long rifaId) {
        return rifaRepository.findById(rifaId)
                .orElseThrow(() -> new RuntimeException("Rifa não encontrada com ID " + rifaId));
    }

    /**
     * Lista os números disponíveis para todas as rifas.
     *
     * @return Lista de números disponíveis.
     */
    public List<NumeroRifa> listarNumerosDisponiveis() {
        return numeroRifaRepository.findAll().stream()
                .filter(NumeroRifa::getDisponivel)
                .collect(Collectors.toList());
    }

    /**
     * Realiza a compra de um número de rifa e gera uma cobrança PIX.
     *
     * @param rifaId    ID da rifa.
     * @param numero    Número escolhido pelo comprador.
     * @param comprador Nome do comprador.
     * @param telefone  Telefone do comprador.
     * @return Mapa contendo informações da transação e o QRCode.
     */
    public Map<String, Object> comprarNumeroComPix(Long rifaId, Integer numero, String comprador, String telefone) {
        // Busca o número da rifa
        NumeroRifa numeroRifa = numeroRifaRepository.findByRifaIdAndNumero(rifaId, numero)
                .orElseThrow(() -> new RuntimeException("Número não encontrado!"));

        // Verifica se o número está disponível
        if (!numeroRifa.getDisponivel()) {
            throw new RuntimeException("Número já foi comprado!");
        }

        // Atualiza o número como comprado e salva as informações do comprador
        numeroRifa.setDisponivel(false);
        numeroRifa.setComprador(comprador);
        numeroRifa.setTelefone(telefone);
        numeroRifaRepository.save(numeroRifa);

        // Prepara o payload para a cobrança PIX
        PixRequestPayload payload = new PixRequestPayload("sua-chave-pix", "10.00", comprador, telefone);
        // Chama o PixService para criar o QR Code
        Map<String, Object> cobrancaPix = pixService.criarQrCode(payload);

        // Retorna os dados da transação para o frontend
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("numero", numero);
        resultado.put("comprador", comprador);
        resultado.put("telefone", telefone);
        resultado.put("qrcode", cobrancaPix.get("qrcode"));
        resultado.put("pixCopiaCola", cobrancaPix.get("pixCopiaCola"));

        return resultado;
    }
}
