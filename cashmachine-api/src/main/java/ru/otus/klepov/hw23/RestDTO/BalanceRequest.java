package ru.otus.klepov.hw23.RestDTO;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
public class BalanceRequest {
    private String cardNum;
    private String pin;
    @SuppressWarnings("checkstyle:ParameterNumber")
    public BalanceRequest(String cardNum, String pin) {
        this.cardNum=cardNum;
        this.pin=pin;
    }
    @JsonbCreator
    @SuppressWarnings("checkstyle:ParameterNumber")
    public static BalanceRequest of(@JsonbProperty("cardNum") String cardNum, @JsonbProperty("pin") String pin) {
        return new BalanceRequest(cardNum, pin);
    }
}
