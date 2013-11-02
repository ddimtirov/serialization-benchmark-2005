package serialization.domain;

public class Dog extends Animal {
    private Breed breed;

    public Dog() { }
    public Dog(String name, Breed breed) { super(name); this.breed = breed; }

    public Breed getBreed() { return breed; }
    public void setBreed(Breed breed) { this.breed = breed; }
}
