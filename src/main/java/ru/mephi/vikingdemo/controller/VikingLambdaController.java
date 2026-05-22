package ru.mephi.vikingdemo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.HairColor;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingLambdaService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lambda/vikings")
@Tag(name = "Viking Lambda API", description = "API для работы с викингами через лямбда-функции")
public class VikingLambdaController {

    private final VikingLambdaService lambdaService;

    public VikingLambdaController(VikingLambdaService lambdaService) {
        this.lambdaService = lambdaService;
    }

    @GetMapping("/count/age")
    @Operation(summary = "Подсчет викингов по возрасту (больше/меньше)")
    public long countByAge(
            @Parameter(description = "Возраст для сравнения", example = "30")
            @RequestParam int age,
            @Parameter(description = "Оператор: > или <", example = ">")
            @RequestParam String operator) {
        return lambdaService.countVikingsByAge(age, operator);
    }

    @GetMapping("/count/age-range")
    @Operation(summary = "Подсчет викингов по диапазону возраста")
    public long countByAgeRange(
            @Parameter(description = "Минимальный возраст", example = "20")
            @RequestParam int min,
            @Parameter(description = "Максимальный возраст", example = "40")
            @RequestParam int max,
            @Parameter(description = "Внутри диапазона (true) или вне (false)", example = "true")
            @RequestParam boolean inside) {
        return lambdaService.countVikingsByAgeRange(min, max, inside);
    }

    @GetMapping("/count/beard-hair")
    @Operation(summary = "Подсчет викингов по бороде и цвету волос")
    public long countByBeardAndHair(
            @Parameter(description = "Форма бороды", example = "LONG")
            @RequestParam BeardStyle beard,
            @Parameter(description = "Цвет волос", example = "Red")
            @RequestParam HairColor hair) {
        return lambdaService.countVikingsByBeardAndHair(beard, hair);
    }

    @GetMapping("/count/axe")
    @Operation(summary = "Подсчет викингов по количеству топоров")
    public long countByAxeCount(
            @Parameter(description = "Количество топоров (1 или 2)", example = "2")
            @RequestParam int axeCount) {
        return lambdaService.countVikingsByAxeCount(axeCount);
    }

    @GetMapping("/find/age")
    @Operation(summary = "Найти викингов по возрасту (больше/меньше)")
    public List<Viking> findVikingsByAge(
            @RequestParam int age,
            @RequestParam String operator) {
        return lambdaService.findVikingsByAge(age, operator);
    }

    @GetMapping("/find/age-range")
    @Operation(summary = "Найти викингов по диапазону возраста")
    public List<Viking> findVikingsByAgeRange(
            @RequestParam int min,
            @RequestParam int max,
            @RequestParam boolean inside) {
        return lambdaService.findVikingsByAgeRange(min, max, inside);
    }

    @GetMapping("/find/beard-hair")
    @Operation(summary = "Найти викингов по бороде и цвету волос")
    public List<Viking> findVikingsByBeardAndHair(
            @RequestParam BeardStyle beard,
            @RequestParam HairColor hair) {
        return lambdaService.findVikingsByBeardAndHair(beard, hair);
    }

    @GetMapping("/find/axe/one")
    @Operation(summary = "Найти всех викингов с одним топором")
    public List<Viking> findVikingsWithOneAxe() {
        return lambdaService.findVikingsWithOneAxe();
    }

    @GetMapping("/find/axe/two")
    @Operation(summary = "Найти всех викингов с двумя топорами")
    public List<Viking> findVikingsWithTwoAxes() {
        return lambdaService.findVikingsWithTwoAxes();
    }

    @GetMapping("/random-tall")
    @Operation(summary = "Получить случайного викинга ростом выше 180 см")
    public Viking getRandomTallViking() {
        return lambdaService.getRandomTallViking()
                .orElseThrow(() -> new RuntimeException("Викингов ростом выше 180 см не найдено"));
    }

    @GetMapping("/legendary")
    @Operation(summary = "Получить всех викингов с легендарным снаряжением")
    public List<Viking> getVikingsWithLegendaryGear() {
        return lambdaService.getVikingsWithLegendaryGear();
    }

    @GetMapping("/red-bearded")
    @Operation(summary = "Получить рыжебородых викингов, сортированных по возрасту")
    public List<Viking> getRedBeardedVikingsSortedByAge() {
        return lambdaService.getRedBeardedVikingsSortedByAge();
    }

    @GetMapping("/id/all")
    @Operation(summary = "Получить массив всех ID из базы данных")
    public String getAllIds() {
        Integer[] allIds = lambdaService.getAllIdsFromDb();
        return String.format("Все ID (%d шт.): %s", allIds.length, Arrays.toString(allIds));
    }

    @GetMapping("/id/max")
    @Operation(summary = "Найти максимальный ID из массива")
    public String findMaxIdFromArray(
            @Parameter(description = "Массив ID через запятую", example = "1,2,3,4,5")
            @RequestParam Integer[] ids) {
        Optional<Integer> maxId = lambdaService.findMaxIdInArray(ids);
        return String.format("Массив: %s\nМаксимальный ID: %s",
                Arrays.toString(ids),
                maxId.map(String::valueOf).orElse("массив пуст"));
    }

    @GetMapping("/id/even")
    @Operation(summary = "Найти все четные ID из массива")
    public String findEvenIdsFromArray(
            @Parameter(description = "Массив ID через запятую", example = "1,2,3,4,5,6,7,8,9,10")
            @RequestParam Integer[] ids) {
        Integer[] evenIds = lambdaService.findEvenIdsInArray(ids);
        return String.format("Исходный массив: %s\nЧетные ID: %s\nКоличество: %d",
                Arrays.toString(ids),
                Arrays.toString(evenIds),
                evenIds.length);
    }

    @GetMapping("/id/max-from-db")
    @Operation(summary = "Найти максимальный ID из базы данных")
    public String getMaxIdFromDb() {
        Optional<Integer> maxId = lambdaService.findMaxIdInDb();
        return String.format("Максимальный ID в БД: %s", maxId.map(String::valueOf).orElse("нет записей"));
    }

    @GetMapping("/id/even-from-db")
    @Operation(summary = "Найти все четные ID из базы данных")
    public String getEvenIdsFromDb() {
        Integer[] evenIds = lambdaService.findEvenIdsInDb();
        return String.format("Четные ID в БД (%d шт.): %s", evenIds.length, Arrays.toString(evenIds));
    }

    @PostMapping("/mass-generate")
    @Operation(summary = "Массовая генерация викингов")
    public String massGenerate(
            @Parameter(description = "Количество викингов для генерации", example = "10")
            @RequestParam int count) {
        if (count <= 0 || count > 1000) {
            throw new IllegalArgumentException("Количество должно быть от 1 до 1000");
        }
        List<Viking> newVikings = lambdaService.generateAndSaveMassVikings(count);
        return String.format("Сгенерировано и добавлено %d викингов", newVikings.size());
    }
}