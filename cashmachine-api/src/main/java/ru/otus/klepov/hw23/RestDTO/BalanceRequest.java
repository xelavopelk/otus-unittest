package ru.otus.klepov.hw23.RestDTO;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

public final class BalanceRequest {
    private final String cardNum;
    private final String pin;

    @SuppressWarnings("checkstyle:ParameterNumber")
    public BalanceRequest(String cardNum, String pin) {
        this.cardNum = cardNum;
        this.pin = pin;
    }

    @JsonbCreator
    @SuppressWarnings("checkstyle:ParameterNumber")
    public static BalanceRequest of(@JsonbProperty("cardNum") String cardNum, @JsonbProperty("pin") String pin) {
        return new BalanceRequest(cardNum, pin);
    }

    public String getCardNum() {
        return cardNum;
    }

    public String getPin() {
        return pin;
    }

    @Override
    public String toString() {
        return String.format("%s; %s", cardNum, pin);
    }
}
