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

    public Optional<Integer> findMaxIdFromArray(Integer[] ids) {
        if (ids == null || ids.length == 0) {
            return Optional.empty();
        }
        return Arrays.stream(ids)
                .max(Integer::compareTo);
    }

    public Integer[] findEvenIdsFromArray(Integer[] ids) {
        if (ids == null || ids.length == 0) {
            return new Integer[0];
        }
        return Arrays.stream(ids)
                .filter(id -> id % 2 == 0)
                .toArray(Integer[]::new);
    }

    public Optional<Integer> findMaxIdFromIntArray(int[] ids) {
        if (ids == null || ids.length == 0) {
            return Optional.empty();
        }
        return Arrays.stream(ids)
                .boxed()
                .max(Integer::compareTo);
    }

    public int[] findEvenIdsFromIntArray(int[] ids) {
        if (ids == null || ids.length == 0) {
            return new int[0];
        }
        return Arrays.stream(ids)
                .filter(id -> id % 2 == 0)
                .toArray();
    }

    public Integer[] getAllIdsAsArray() {
        return vikingStorage.findAllEntities().stream()
                .map(VikingEntity::id)
                .toArray(Integer[]::new);
    }

    public int[] getAllIdsAsIntArray() {
        return vikingStorage.findAllEntities().stream()
                .mapToInt(VikingEntity::id)
                .toArray();
    }
}