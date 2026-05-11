package ru.mephi.vikingdemo.service;

import org.springframework.stereotype.Service;
import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.HairColor;
import ru.mephi.vikingdemo.model.Viking;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import ru.mephi.vikingdemo.model.VikingEntity;
import ru.mephi.vikingdemo.repository.VikingStorage;

@Service
public class VikingService {
    // каждый раз при изменении создаётся новая копия списка 

    private final VikingFactory vikingFactory;
    private final VikingStorage vikingStorage;


    @Autowired
    public VikingService(
            VikingFactory vikingFactory,
            VikingStorage vikingStorage
    ) {
        this.vikingFactory = vikingFactory;
        this.vikingStorage = vikingStorage;
    }

    public List<Viking> findAll() {
        return vikingStorage.findAll();
    }

    public Viking createRandomViking() {
        Viking viking = vikingFactory.createRandomViking();
        return vikingStorage.save(viking);
    }
    public void deleteById(int id) {
        vikingStorage.deleteById(id);
    }


    private boolean matchAgeCondition(Viking v, String condition) {
        if (condition == null || condition.isBlank()) return true;

        if (condition.startsWith(">")) {
            int age = Integer.parseInt(condition.substring(1));
            return v.age() > age;
        } else if (condition.startsWith("<")) {
            int age = Integer.parseInt(condition.substring(1));
            return v.age() < age;
        } else if (condition.contains("-") && !condition.startsWith("not")) {
            String[] parts = condition.split("-");
            int min = Integer.parseInt(parts[0]);
            int max = Integer.parseInt(parts[1]);
            return v.age() >= min && v.age() <= max;
        } else if (condition.startsWith("not")) {
            String range = condition.substring(3);
            String[] parts = range.split("-");
            int min = Integer.parseInt(parts[0]);
            int max = Integer.parseInt(parts[1]);
            return v.age() < min || v.age() > max;
        }
        return true;
    }

    private boolean matchBeardAndHair(Viking v, String condition) {
        if (condition == null || condition.isBlank()) return true;

        String[] parts = condition.split("_");
        if (parts.length == 2) {
            try {
                BeardStyle beard = BeardStyle.valueOf(parts[0]);
                HairColor hair = HairColor.valueOf(parts[1]);
                return v.beardStyle() == beard && v.hairColor() == hair;
            } catch (IllegalArgumentException e) {
                return true;
            }
        }
        return true;
    }

    private boolean matchAxeCount(Viking v, Integer requiredCount) {
        if (requiredCount == null) return true;

        long axeCount = v.equipment().stream()
                .filter(e -> e.name().toLowerCase().contains("axe"))
                .count();
        return axeCount == requiredCount;
    }
    public List<Viking> findVikingsByAge(int age, String operator) {
        return vikingStorage.findAll().stream()
                .filter(v -> operator.equals(">") ? v.age() > age : v.age() < age)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Viking> findVikingsByAgeRange(int min, int max, boolean inside) {
        return vikingStorage.findAll().stream()
                .filter(v -> inside ? (v.age() >= min && v.age() <= max) : (v.age() < min || v.age() > max))
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Viking> findVikingsByBeardAndHair(BeardStyle beard, HairColor hair) {
        return vikingStorage.findAll().stream()
                .filter(v -> v.beardStyle() == beard && v.hairColor() == hair)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Viking> findVikingsByAxeCount(int axeCount) {
        return vikingStorage.findAll().stream()
                .filter(v -> v.equipment().stream()
                        .filter(e -> e.name().toLowerCase().contains("axe"))
                        .count() == axeCount)
                .collect(java.util.stream.Collectors.toList());
    }
    public java.util.Optional<Viking> getRandomTallViking() {
        List<Viking> tallVikings = vikingStorage.findAll().stream()
                .filter(v -> v.heightCm() > 180)
                .collect(java.util.stream.Collectors.toList());

        if (tallVikings.isEmpty()) {
            return java.util.Optional.empty();
        }

        java.util.Random random = new java.util.Random();
        return java.util.Optional.of(tallVikings.get(random.nextInt(tallVikings.size())));
    }

    public List<Viking> getVikingsWithLegendaryGear() {
        return vikingStorage.findAll().stream()
                .filter(v -> v.equipment().stream()
                        .anyMatch(e -> e.quality().toLowerCase().contains("legendary")))
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Viking> getRedBeardedVikingsSortedByAge() {
        return vikingStorage.findAll().stream()
                .filter(v -> v.hairColor() == HairColor.Red && v.beardStyle() != BeardStyle.CLEAN_SHAVEN)
                .sorted(java.util.Comparator.comparingInt(Viking::age))
                .collect(java.util.stream.Collectors.toList());
    }

    public java.util.Optional<VikingEntity> findVikingEntityWithMaxId() {
        return vikingStorage.findAllEntities().stream()
                .max(java.util.Comparator.comparingInt(VikingEntity::id));
    }

    public List<VikingEntity> findVikingEntitiesWithEvenIds() {
        return vikingStorage.findAllEntities().stream()
                .filter(e -> e.id() % 2 == 0)
                .collect(java.util.stream.Collectors.toList());
    }
    public List<Viking> findVikingsByEntityIds(List<Integer> ids) {
        List<Viking> allVikings = vikingStorage.findAll();
        return allVikings.stream()
                .filter(v -> {
                    for (Integer id : ids) {
                        VikingEntity entity = vikingStorage.findAllEntities().stream()
                                .filter(e -> e.id().equals(id))
                                .findFirst()
                                .orElse(null);
                        if (entity != null && v.name().equals(entity.name()) && v.age() == entity.age()) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(java.util.stream.Collectors.toList());
    }
}