package com.pushbait.webapp.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.pushbait.webapp.config.PixConfig;
import com.pushbait.webapp.payload.PixRequestPayload;

import br.com.efi.efisdk.EfiPay;
import br.com.efi.efisdk.exceptions.EfiPayException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PixService {

    private final JSONObject configuracoes;

    /**
     * Construtor do PixService.
     * Configura o SDK da EfiPay com as credenciais e informações do certificado.
     *
     * @param pixConfig Configurações do Pix (client_id, client_secret, certificado, etc.)
     */
    public PixService(final PixConfig pixConfig) {
        // Inicializa o objeto de configurações do SDK com as propriedades fornecidas pelo PixConfig
        this.configuracoes = new JSONObject();
        this.configuracoes.put("client_id", pixConfig.getClientId());
        this.configuracoes.put("client_secret", pixConfig.getClientSecret());
        this.configuracoes.put("certificate", pixConfig.getCertificatePath());
        this.configuracoes.put("sandbox", pixConfig.isSandbox());
        this.configuracoes.put("debug", pixConfig.isDebug());
    }

    /**
     * Lista todas as chaves Pix cadastradas.
     *
     * @return Mapa contendo os dados das chaves Pix.
     */
    public Map<String, Object> listarChavesPix() {
        return executarOperacao("pixListEvp", new HashMap<>());
    }

    /**
     * Cria uma nova chave Pix.
     *
     * @return Mapa contendo os dados da chave Pix criada.
     */
    public Map<String, Object> criarChavePix() {
        return executarOperacao("pixCreateEvp", new HashMap<>());
    }

    /**
     * Deleta uma chave Pix específica.
     *
     * @param chavePix Chave Pix que será deletada.
     * @return Mapa contendo os dados da operação de exclusão.
     */
    public Map<String, Object> deletarChavePix(final String chavePix) {
        // Adiciona a chave Pix aos parâmetros da requisição
        Map<String, String> params = new HashMap<>();
        params.put("chave", chavePix);
        return executarOperacao("pixDeleteEvp", params);
    }

    /**
     * Cria um QR Code dinâmico para cobrança Pix.
     * Recebe os dados necessários através do PixRequestPayload.
     *
     * @param pixRequestPayload Payload com informações da cobrança (nome, telefone, valor, chave, etc.)
     * @return Mapa contendo os dados da resposta da API.
     */
    public Map<String, Object> criarQrCode(PixRequestPayload pixRequestPayload) {
        JSONObject body = new JSONObject();

        // Adiciona o tempo de expiração do QR Code
        body.put("calendario", new JSONObject().put("expiracao", 3600));

        // Adiciona os dados do devedor (nome e telefone são obrigatórios; CPF é opcional)
        JSONObject devedor = new JSONObject()
            .put("nome", pixRequestPayload.nome()) // Nome do comprador
            .put("telefone", pixRequestPayload.telefone()); // Telefone do comprador

        // Inclui o CPF, se fornecido
        pixRequestPayload.cpf().ifPresent(cpf -> devedor.put("cpf", cpf));
        body.put("devedor", devedor);

        // Adiciona o valor e a chave Pix
        body.put("valor", new JSONObject().put("original", pixRequestPayload.valor()));
        body.put("chave", pixRequestPayload.chave());

        // Adiciona informações adicionais (opcional)
        JSONArray infoAdicionais = new JSONArray();
        infoAdicionais.put(new JSONObject().put("nome", "Campo 1").put("valor", "Informação Adicional1"));
        infoAdicionais.put(new JSONObject().put("nome", "Campo 2").put("valor", "Informação Adicional2"));
        body.put("infoAdicionais", infoAdicionais);

        try {
            // Inicializa o SDK com as configurações e realiza a chamada à API
            EfiPay efi = new EfiPay(configuracoes);
            JSONObject response = efi.call("pixCreateImmediateCharge", new HashMap<>(), body);

            // Converte a resposta para um Map e retorna
            return response.toMap();
        } catch (EfiPayException e) {
            log.error("Erro da API {} {}", e.getErrorDescription(), e.getError());
        } catch (Exception e) {
            log.error("Erro genérico {}", e.getMessage());
        }

        // Retorna um mapa vazio em caso de erro
        return new HashMap<>();
    }

    /**
     * Método genérico para executar operações Pix.
     * Pode ser usado para listar chaves Pix, criar chaves, deletar chaves, etc.
     *
     * @param operacao Nome da operação (ex.: pixListEvp, pixCreateEvp, etc.)
     * @param params   Parâmetros adicionais para a operação.
     * @return Mapa contendo os dados da resposta da API.
     */
    public Map<String, Object> executarOperacao(String operacao, Map<String, String> params) {
        try {
            // Inicializa o SDK com as configurações
            EfiPay efi = new EfiPay(configuracoes);

            // Realiza a chamada à API com os parâmetros fornecidos
            JSONObject response = efi.call(operacao, params, new JSONObject());

            // Loga o resultado e retorna a resposta como Map
            log.info("Resultado da operação {}: {}", operacao, response);
            return response.toMap();
        } catch (EfiPayException e) {
            log.error("Erro na API do Efí Bank: {}", e.getErrorDescription());
        } catch (Exception e) {
            log.error("Erro genérico na operação {}: {}", operacao, e.getMessage());
        }

        // Retorna um mapa vazio em caso de erro
        return new HashMap<>();
    }
}
