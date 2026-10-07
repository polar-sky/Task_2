package praktikum.entity;

import io.qameta.allure.Step;

import java.util.concurrent.ThreadLocalRandom;

public class User {
    private String email;
    private String name;
    private String password;

    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Step("Создание случайных данных для нового пользователя")
    public static User randomUser() {
        long num = ThreadLocalRandom.current().nextLong();
        String email = String.format("akakii%d@yandex.ru", num);
        String name = String.format("Куролесов%d", num);
        String password = String.format("P@sswor%d", num);
        return new User(email, name, password);
    }
}
