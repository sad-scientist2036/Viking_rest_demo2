package ru.mephi.vikingdemo.gui;

import ru.mephi.vikingdemo.model.EquipmentItem;
import ru.mephi.vikingdemo.model.Viking;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VikingTableModel extends AbstractTableModel {

    private final String[] columns = {"Name", "Age", "Height (cm)", "Hair color", "Beard style", "Equipment"};
    private final List<Viking> data = new ArrayList<>();

    public void addViking(Viking viking) {
        int row = data.size();
        data.add(viking);
        fireTableRowsInserted(row, row);
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Viking viking = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> viking.name();
            case 1 -> viking.age();
            case 2 -> viking.heightCm();
            case 3 -> viking.hairColor();
            case 4 -> viking.beardStyle();
            case 5 -> formatEquipment(viking.equipment());
            default -> "";
        };
    }

    private String formatEquipment(List<EquipmentItem> equipment) {
        return equipment.stream()
                .map(item -> item.name() + " [" + item.quality() + "]")
                .collect(Collectors.joining(", "));
    }
    public void clearAll() {
        data.clear();
        fireTableDataChanged();
    }
}
