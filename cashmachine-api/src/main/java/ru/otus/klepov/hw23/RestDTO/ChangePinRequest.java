package ru.otus.klepov.hw23.RestDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangePinRequest {
    private String cardNum;
    private String newPin;
    private String oldPin;
}
