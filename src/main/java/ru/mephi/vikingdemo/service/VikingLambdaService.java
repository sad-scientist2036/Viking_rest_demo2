package ru.mephi.vikingdemo.service;

import org.springframework.stereotype.Service;
import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.HairColor;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.model.VikingEntity;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import ru.mephi.vikingdemo.repository.VikingStorage;

@Service
public class VikingLambdaService {

    private final VikingFactory vikingFactory;
    private final VikingStorage vikingStorage;

    @Autowired
    public VikingLambdaService(VikingFactory vikingFactory, VikingStorage vikingStorage) {
        this.vikingFactory = vikingFactory;
        this.vikingStorage = vikingStorage;
    }

    public long countVikingsByAge(int age, String operator) {
        return vikingStorage.findAll().stream()
                .filter(v -> operator.equals(">") ? v.age() > age : v.age() < age)
                .count();
    }

    public long countVikingsByAgeRange(int min, int max, boolean inside) {
        return vikingStorage.findAll().stream()
                .filter(v -> inside ? (v.age() >= min && v.age() <= max) : (v.age() < min || v.age() > max))
                .count();
    }

    public long countVikingsByBeardAndHair(BeardStyle beard, HairColor hair) {
        return vikingStorage.findAll().stream()
                .filter(v -> v.beardStyle() == beard && v.hairColor() == hair)
                .count();
    }

    public long countVikingsByAxeCount(int axeCount) {
        return vikingStorage.findAll().stream()
                .filter(v -> v.equipment().stream()
                        .filter(e -> e.name().toLowerCase().contains("axe"))
                        .count() == axeCount)
                .count();
    }
    public List<Viking> findVikingsByAge(int age, String operator) {
        return vikingStorage.findAll().stream()
                .filter(v -> operator.equals(">") ? v.age() > age : v.age() < age)
                .collect(Collectors.toList());
    }

    public List<Viking> findVikingsByAgeRange(int min, int max, boolean inside) {
        return vikingStorage.findAll().stream()
                .filter(v -> inside ? (v.age() >= min && v.age() <= max) : (v.age() < min || v.age() > max))
                .collect(Collectors.toList());
    }

    public List<Viking> findVikingsByBeardAndHair(BeardStyle beard, HairColor hair) {
        return vikingStorage.findAll().stream()
                .filter(v -> v.beardStyle() == beard && v.hairColor() == hair)
                .collect(Collectors.toList());
    }

    public List<Viking> findVikingsByAxeCount(int axeCount) {
        if (axeCount != 1 && axeCount != 2) {
            throw new IllegalArgumentException("Количество топоров должно быть 1 или 2");
        }
        return vikingStorage.findAll().stream()
                .filter(v -> v.equipment().stream()
                        .filter(e -> e.name().toLowerCase().contains("axe"))
                        .count() == axeCount)
                .collect(Collectors.toList());
    }

    public List<Viking> findVikingsWithOneAxe() {
        return findVikingsByAxeCount(1);
    }

    public List<Viking> findVikingsWithTwoAxes() {
        return findVikingsByAxeCount(2);
    }
    public Optional<Viking> getRandomTallViking() {
        List<Viking> tallVikings = vikingStorage.findAll().stream()
                .filter(v -> v.heightCm() > 180)
                .collect(Collectors.toList());

        if (tallVikings.isEmpty()) {
            return Optional.empty();
        }

        Random random = new Random();
        return Optional.of(tallVikings.get(random.nextInt(tallVikings.size())));
    }

    public List<Viking> getVikingsWithLegendaryGear() {
        return vikingStorage.findAll().stream()
                .filter(v -> v.equipment().stream()
                        .anyMatch(e -> e.quality().toLowerCase().contains("legendary")))
                .collect(Collectors.toList());
    }

    public List<Viking> getRedBeardedVikingsSortedByAge() {
        return vikingStorage.findAll().stream()
                .filter(v -> v.hairColor() == HairColor.Red && v.beardStyle() != BeardStyle.CLEAN_SHAVEN)
                .sorted(Comparator.comparingInt(Viking::age))
                .collect(Collectors.toList());
    }

    public Optional<Integer> findMaxIdInArray(Integer[] ids) {
        if (ids == null || ids.length == 0) {
            return Optional.empty();
        }
        return Arrays.stream(ids)
                .max(Integer::compareTo);
    }

    public Integer[] findEvenIdsInArray(Integer[] ids) {
        if (ids == null || ids.length == 0) {
            return new Integer[0];
        }
        return Arrays.stream(ids)
                .filter(id -> id % 2 == 0)
                .toArray(Integer[]::new);
    }

     public Integer[] getAllIdsFromDb() {
        return vikingStorage.findAllEntities().stream()
                .map(VikingEntity::id)
                .toArray(Integer[]::new);
    }
    public Optional<Integer> findMaxIdInDb() {
        Integer[] ids = getAllIdsFromDb();
        return findMaxIdInArray(ids);
    }
    public Integer[] findEvenIdsInDb() {
        Integer[] ids = getAllIdsFromDb();
        return findEvenIdsInArray(ids);
    }

    public List<Viking> generateMassVikings(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Количество должно быть положительным");
        }
        if (count > 1000) {
            throw new IllegalArgumentException("Количество не должно превышать 1000");
        }
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> vikingFactory.createRandomViking())
                .collect(Collectors.toList());
    }

    public void addMassVikings(List<Viking> vikings) {
        if (vikings == null || vikings.isEmpty()) {
            return;
        }
        vikings.forEach(v -> vikingStorage.save(v));
    }

    public List<Viking> generateAndSaveMassVikings(int count) {
        List<Viking> newVikings = generateMassVikings(count);
        addMassVikings(newVikings);
        return newVikings;
    }
}