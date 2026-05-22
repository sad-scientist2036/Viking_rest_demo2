package ru.mephi.vikingdemo.gui;

import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.HairColor;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.VikingLambdaService;
import ru.mephi.vikingdemo.service.VikingService;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class VikingDesktopFrame extends JFrame {

    private final VikingService vikingService;
    private final VikingLambdaService lambdaService;
    private final VikingTableModel tableModel = new VikingTableModel();

    public VikingDesktopFrame(VikingService vikingService, VikingLambdaService lambdaService) {
        this.vikingService = vikingService;
        this.lambdaService = lambdaService;

        setTitle("Viking Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1300, 650));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel header = new JLabel("Viking Demo", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));
        add(header, BorderLayout.NORTH);

        JTable vikingTable = new JTable(tableModel);
        vikingTable.setRowHeight(28);
        add(new JScrollPane(vikingTable), BorderLayout.CENTER);

        JButton createButton = new JButton("Create random viking");
        createButton.addActionListener(event -> onCreateViking());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(createButton);
        add(bottomPanel, BorderLayout.SOUTH);

        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        JPanel leftPanel = createLeftPanel();
        add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = createRightPanel();
        add(rightPanel, BorderLayout.EAST);

        onInit();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 3, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("1. Поиск викингов по условиям"));

        JButton btnGreater = new JButton("Возраст >");
        JButton btnLess = new JButton("Возраст <");
        JButton btnRange = new JButton("Возраст в диапазоне");
        JButton btnBeardHair = new JButton("Борода + Волосы");
        JButton btnOneAxe = new JButton("1 топор");
        JButton btnTwoAxes = new JButton("2 топора");
        JButton btnOneOrTwoAxes = new JButton("1 или 2 топора");

        panel.add(btnGreater);
        panel.add(btnLess);
        panel.add(btnRange);
        panel.add(btnBeardHair);
        panel.add(btnOneAxe);
        panel.add(btnTwoAxes);
        panel.add(btnOneOrTwoAxes);

        btnGreater.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Введите возраст:", "Старше", JOptionPane.QUESTION_MESSAGE);
            if (input != null) {
                try {
                    int age = Integer.parseInt(input);
                    List<Viking> result = lambdaService.findVikingsByAge(age, ">");
                    displayResults(result);
                    JOptionPane.showMessageDialog(this, "Найдено: " + result.size());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Ошибка: введите число");
                }
            }
        });

        btnLess.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Введите возраст:", "Младше", JOptionPane.QUESTION_MESSAGE);
            if (input != null) {
                try {
                    int age = Integer.parseInt(input);
                    List<Viking> result = lambdaService.findVikingsByAge(age, "<");
                    displayResults(result);
                    JOptionPane.showMessageDialog(this, "Найдено: " + result.size());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Ошибка: введите число");
                }
            }
        });

        btnRange.addActionListener(e -> {
            JPanel rangePanel = new JPanel(new GridLayout(2, 2, 5, 5));
            JTextField minField = new JTextField();
            JTextField maxField = new JTextField();
            rangePanel.add(new JLabel("От:"));
            rangePanel.add(minField);
            rangePanel.add(new JLabel("До:"));
            rangePanel.add(maxField);

            int result = JOptionPane.showConfirmDialog(this, rangePanel, "Диапазон возраста", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    int min = Integer.parseInt(minField.getText());
                    int max = Integer.parseInt(maxField.getText());
                    List<Viking> found = lambdaService.findVikingsByAgeRange(min, max, true);
                    displayResults(found);
                    JOptionPane.showMessageDialog(this, "Найдено: " + found.size());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Ошибка: введите числа");
                }
            }
        });

        btnBeardHair.addActionListener(e -> {
            JPanel bhPanel = new JPanel(new GridLayout(2, 2, 5, 5));
            JComboBox<BeardStyle> beardBox = new JComboBox<>(BeardStyle.values());
            JComboBox<HairColor> hairBox = new JComboBox<>(HairColor.values());
            bhPanel.add(new JLabel("Борода:"));
            bhPanel.add(beardBox);
            bhPanel.add(new JLabel("Волосы:"));
            bhPanel.add(hairBox);

            int result = JOptionPane.showConfirmDialog(this, bhPanel, "Борода и цвет волос", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                BeardStyle beard = (BeardStyle) beardBox.getSelectedItem();
                HairColor hair = (HairColor) hairBox.getSelectedItem();
                List<Viking> found = lambdaService.findVikingsByBeardAndHair(beard, hair);
                displayResults(found);
                JOptionPane.showMessageDialog(this, "Найдено: " + found.size());
            }
        });

        btnOneAxe.addActionListener(e -> {
            List<Viking> found = lambdaService.findVikingsByAxeCount(1);
            displayResults(found);
            JOptionPane.showMessageDialog(this, "Найдено викингов с 1 топором: " + found.size());
        });

        btnTwoAxes.addActionListener(e -> {
            List<Viking> found = lambdaService.findVikingsByAxeCount(2);
            displayResults(found);
            JOptionPane.showMessageDialog(this, "Найдено викингов с 2 топорами: " + found.size());
        });

        btnOneOrTwoAxes.addActionListener(e -> {
            List<Viking> found = lambdaService.findVikingsWithOneOrTwoAxes();
            displayResults(found);
            JOptionPane.showMessageDialog(this, "Найдено викингов с 1 или 2 топорами: " + found.size());
        });

        return panel;
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("2. Информация"));
        panel.setPreferredSize(new Dimension(200, 0));

        JButton btnTall = new JButton("Случайный >180см");
        JButton btnLegendary = new JButton("Легендарное снаряжение");
        JButton btnRedBearded = new JButton("Рыжебородые");

        panel.add(btnTall);
        panel.add(btnLegendary);
        panel.add(btnRedBearded);

        btnTall.addActionListener(e -> {
            var result = lambdaService.getRandomTallViking();
            displayResults(result.map(List::of).orElse(List.of()));
            JOptionPane.showMessageDialog(this, result.isPresent() ? "Показан случайный викинг" : "Не найден");
        });

        btnLegendary.addActionListener(e -> {
            List<Viking> result = lambdaService.getVikingsWithLegendaryGear();
            displayResults(result);
            JOptionPane.showMessageDialog(this, "Найдено: " + result.size());
        });

        btnRedBearded.addActionListener(e -> {
            List<Viking> result = lambdaService.getRedBeardedVikingsSortedByAge();
            displayResults(result);
            JOptionPane.showMessageDialog(this, "Найдено (сортировка по возрасту): " + result.size());
        });

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("3. Операции с массивом ID"));
        panel.setPreferredSize(new Dimension(200, 0));

        JButton btnMaxId = new JButton("Max ID");
        JButton btnEvenIds = new JButton("Четные ID");

        panel.add(btnMaxId);
        panel.add(btnEvenIds);

        btnMaxId.addActionListener(e -> {
            Integer[] allIds = lambdaService.getAllIdsFromDb();
            if (allIds.length == 0) {
                JOptionPane.showMessageDialog(this, "Нет ID в базе данных");
                return;
            }

            Optional<Integer> maxId = lambdaService.findMaxIdInArray(allIds);
            String message = "=== Операции с массивом ID ===\n\n" +
                    "Массив всех ID: " + Arrays.toString(allIds) + "\n\n" +
                    "Максимальный ID: " + maxId.orElse(-1);
            JOptionPane.showMessageDialog(this, message, "Max ID", JOptionPane.INFORMATION_MESSAGE);
        });

        btnEvenIds.addActionListener(e -> {
            Integer[] allIds = lambdaService.getAllIdsFromDb();
            if (allIds.length == 0) {
                JOptionPane.showMessageDialog(this, "Нет ID в базе данных");
                return;
            }

            Integer[] evenIds = lambdaService.findEvenIdsInArray(allIds);
            String message = "=== Операции с массивом ID ===\n\n" +
                    "Массив всех ID: " + Arrays.toString(allIds) + "\n\n" +
                    "Четные ID: " + Arrays.toString(evenIds) + "\n\n" +
                    "Количество четных ID: " + evenIds.length;
            JOptionPane.showMessageDialog(this, message, "Четные ID", JOptionPane.INFORMATION_MESSAGE);
        });

        return panel;
    }

    private void displayResults(List<Viking> vikings) {
        tableModel.clearAll();
        vikings.forEach(tableModel::addViking);
    }

    private void onCreateViking() {
        Viking viking = vikingService.createRandomViking();
        tableModel.addViking(viking);
    }

    public void addNewViking(Viking viking) {
        tableModel.addViking(viking);
    }

    private void onInit() {
        List<Viking> all = vikingService.findAll();
        if (!all.isEmpty()) {
            for (Viking viking : all) {
                tableModel.addViking(viking);
            }
        }
    }
}