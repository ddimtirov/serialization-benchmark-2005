package serialization.domain;

import serialization.domain.Human;
import serialization.domain.Dog;
import serialization.domain.Breed;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

/**
 * @author ddimitro
 *         Date: Oct 3, 2005 2:27:30 AM
 */
public class Factory {
    private static final Random rnd = new Random(System.currentTimeMillis());

    public static List<Human> createGraph(int num) {
        List<Human> list = new ArrayList<Human>(num);
        for (int i =0; i<num; i++) {
            Human spouse = new Human(name(), rnd.nextInt(), null,null);
            Dog pet = new Dog(name(), Breed.values()[rnd.nextInt(Breed.values().length)]);
            list.add(new Human(name(), rnd.nextInt(), pet, spouse));
        }
        return list;
    }

    private static String name() {
        String name;
        switch (rnd.nextInt(10)) {
            case 0: name = "John"; break;
            case 1: name = "Yoko"; break;
            case 2: name = "Rin Tin Tin"; break;
            case 3: name = "Peter"; break;
            case 4: name = "Linda"; break;
            case 5: name = "Lassie"; break;
            case 6: name = "Bill"; break;
            case 7: name = "Hillary"; break;
            case 8: name = "Huckelberry"; break;
            case 9: name = "Saddam"; break;
            default: throw new IllegalStateException();
        }
        return name + rnd.nextInt(1000);
    }
}
