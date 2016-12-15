package com.pkulak.webhooks;

public class WebhookResponse {
    private String response;

    public WebhookResponse(String response) {
        this.response = response;
    }

    public String getSpeech() {
        return response;
    }

    public String getDisplayText() {
        return response;
    }
}
