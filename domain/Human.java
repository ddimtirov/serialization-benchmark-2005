package serialization.domain;

public class Human {
    private String name;
    private int age;
    private Animal pet;
    private Human spouse;

    public Human() { }
    public Human(String name, int age, Animal pet, Human spouse) {
        setName(name);
        setAge(age);
        setPet(pet);
        setSpouse(spouse);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public Animal getPet() { return pet; }
    public void setPet(Animal pet) {
        this.pet = pet;
        if (spouse!=null && spouse.getPet()!=pet) spouse.setPet(pet);
    }
    public Human getSpouse() { return spouse; }
    public void setSpouse(Human spouse) {
        this.spouse = spouse;
        if (spouse!=null && spouse.getSpouse()!=this) {
            spouse.setSpouse(this);
            spouse.setPet(pet);
        }
    }

    public String toString() {
        return "Human{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", pet=" + pet.getName() +
                ", spouse='" + spouse.getName() + "' (" + spouse.getAge() + ")" +
                '}';
    }
}
