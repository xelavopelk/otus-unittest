package ru.otus.klepov.hw23;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import otus.study.cashmachine.machine.data.MoneyBox;
import ru.otus.klepov.hw23.FileDTO.Money;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


class MoneyBoxFactory {
    public MoneyBox create() throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("Money.json");
        String result = CharStreams.toString(new InputStreamReader(
                is, Charsets.UTF_8));
        ObjectMapper mapper = new ObjectMapper();
        Money money = mapper.readValue(result, Money.class);
        return new MoneyBox(money.notes[0], money.notes[1], money.notes[2], money.notes[3]);
    }
}
