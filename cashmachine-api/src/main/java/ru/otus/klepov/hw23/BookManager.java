package ru.otus.klepov.hw23;

import ru.otus.klepov.hw23.Book;

import java.util.ArrayList;
import java.util.List;

public class BookManager {
    private ArrayList<Book> data;
    public BookManager() {
        data = new ArrayList<>();
    }
    public void put(Book b) {
        data.add(b);
    }
    public Book getId(Integer id) {
        return data.stream().filter(b -> b.id.equals(id)).findAny().get();
    }
    public List<Book> getAll() {
        return data;
    }
    public Integer len() {return data.size();}

}
