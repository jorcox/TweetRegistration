package es.unizar.tmdad.ucode.social;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.social.google")
public class GoogleProperties {
    /**
     * Application id.
     */
	@Value("${google.clientId}")
    private String appId;

    /**
     * Application secret.
     */
	@Value("${google.clientSecret}")
    private String appSecret;
    
    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return this.appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}