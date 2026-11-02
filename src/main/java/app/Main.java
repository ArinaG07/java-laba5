package app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.Book;
import models.SmsMessage;
import models.Visitor;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Path path = Paths.get("data/books.json");
        List<Visitor> visitors = readVisitors(path);

        if (visitors.isEmpty()) {
            System.out.println("Файл пуст, не найден или содержит ошибку.");
            return;
        }

        // Задание 1
        System.out.println("--- № 1. Посетители и их количество ---");
        System.out.println("Всего посетителей: " + visitors.size());
        visitors.forEach(v -> System.out.println(v.getName() + " " + v.getSurname()));

        // Задание 2
        System.out.println("\n--- № 2. Уникальные книги ---");
        List<Book> uniqueBooks = visitors.stream()
                .flatMap(v -> v.getFavoriteBooks().stream())
                .distinct()
                .collect(Collectors.toList());
        System.out.println("Количество уникальных книг: " + uniqueBooks.size());
        uniqueBooks.forEach(b -> System.out.println(" - " + b.getName()));

        // Задание 3
        System.out.println("\n--- № 3. Сортировка книг по году ---");
        List<Book> sortedBooks = uniqueBooks.stream()
                .sorted(Comparator.comparingInt(Book::getPublishingYear))
                .collect(Collectors.toList());
        sortedBooks.forEach(b -> System.out.println(b.getPublishingYear() + " - " + b.getName()));

        // Задание 4
        System.out.println("\n--- № 4. Поиск автора 'Jane Austen' ---");
        boolean hasAusten = visitors.stream()
                .flatMap(v -> v.getFavoriteBooks().stream())
                .anyMatch(b -> b.getAuthor().equals("Jane Austen"));
        System.out.println(hasAusten ? "Есть книги Jane Austen!" : "Книг Jane Austen нет.");

        // Задание 5
        System.out.println("\n--- № 5. Максимальное число книг у одного посетителя ---");
        int maxBooks = visitors.stream()
                .mapToInt(v -> v.getFavoriteBooks().size())
                .max()
                .orElse(0);
        System.out.println("Максимум книг: " + maxBooks);

        // Задание 6
        System.out.println("\n--- № 6. Сообщения подписчикам ---");
        double average = visitors.stream()
                .mapToInt(v -> v.getFavoriteBooks().size())
                .average()
                .orElse(0.0);
        System.out.printf("Среднее число книг: %.2f%n", average);

        List<SmsMessage> smsList = visitors.stream()
                .filter(Visitor::isSubscribed) // Только подписчики
                .map(v -> {
                    int count = v.getFavoriteBooks().size();
                    String text;
                    if (count > average) {
                        text = "you are a bookworm";
                    } else if (count < average) {
                        text = "read more";
                    } else {
                        text = "fine";
                    }
                    return new SmsMessage(v.getPhone(), text);
                })
                .collect(Collectors.toList());

        smsList.forEach(sms -> System.out.println("SMS на " + sms.getPhoneNumber() + ": " + sms.getMessage()));
    }

    private static List<Visitor> readVisitors(Path path) {
        try {
            String json = Files.readString(path, StandardCharsets.UTF_8);
            Type listType = new TypeToken<List<Visitor>>() {}.getType();
            return new Gson().fromJson(json, listType);
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
            return List.of();
        }
    }
}
