package praktikum.entity;

public class Order {

    private String[] ingredients;
    private String number;
    private String name;

    public Order(String[] ingredients) {
        this.ingredients = ingredients;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
