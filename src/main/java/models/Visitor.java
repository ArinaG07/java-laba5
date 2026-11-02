package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Visitor {
    private String name;
    private String surname;
    private String phone;
    private List<Book> favoriteBooks;
    private boolean subscribed;
}
