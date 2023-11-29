package ru.otus.klepov.hw23;

import io.helidon.webserver.Routing;

import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.otus.klepov.hw23.Book;
import ru.otus.klepov.hw23.BookManager;

import java.util.List;

public class BookResource implements Service {

    private BookManager bookManager = new BookManager();
    public BookResource() {
        var b = new Book(-1, "default book");
        bookManager.put(b);
    }
    @Override
    public void update(Routing.Rules rules) {
        rules
                .get("/", this::books)
                .get("/add", this::add)
                .get("/{id}", this::bookById);
    }


    private void bookById(ServerRequest serverRequest, ServerResponse serverResponse) {
        var id = Integer.valueOf(serverRequest.path().param("id"));
        Book book = bookManager.getId(id);
        JSONObject jsonObject = new JSONObject(book);
        serverResponse.send(jsonObject);
    }

    private void books(ServerRequest serverRequest, ServerResponse serverResponse) {
        List<Book> books = bookManager.getAll();
        JSONArray jsonArray = new JSONArray();
        for (var b: books) {
            jsonArray.put(b);
        }
        serverResponse.send(jsonArray.toString());
    }
    private void add(ServerRequest serverRequest, ServerResponse serverResponse) {
        var b = new Book(bookManager.len(),"test_"+bookManager.len().toString());
        bookManager.put(b);
        serverResponse.send(b.toString());
    }
}