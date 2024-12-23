package com.pushbait.webapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration // Torna esta classe um Bean gerenciado pelo Spring
@ConfigurationProperties("app.pix") // Mapeia as propriedades com prefixo "app.pix"
public class PixConfig {
    private String clientId;
    private String clientSecret;
    private boolean sandbox;
    private boolean debug;
    private String certificatePath;

    // Getters e Setters necessários para o Spring configurar as propriedades
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public boolean isSandbox() {
        return sandbox;
    }

    public void setSandbox(boolean sandbox) {
        this.sandbox = sandbox;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getCertificatePath() {
        return certificatePath;
    }

    public void setCertificatePath(String certificatePath) {
        this.certificatePath = certificatePath;
    }
}
