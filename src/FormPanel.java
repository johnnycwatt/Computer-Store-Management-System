import javax.swing.*;
import java.awt.*;

public class FormPanel extends JPanel {
    private JTextField idField, brandField, cpuField, memoryField, ssdField, screenSizeField, priceField;
    private JComboBox<String> categoryField, typeField;

    public FormPanel() {
        setLayout(new GridLayout(9, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(400, 0));

        add(new JLabel("Model ID:"));
        idField = new JTextField();
        idField.setEditable(false);
        add(idField);

        add(new JLabel("Category:"));
        categoryField = new JComboBox<>(new String[]{"Desktop PC", "Laptop", "Tablet"});
        categoryField.setEnabled(false);
        add(categoryField);

        add(new JLabel("Type:"));
        typeField = new JComboBox<>();
        typeField.setEnabled(false);
        add(typeField);

        add(new JLabel("Brand:"));
        brandField = new JTextField();
        brandField.setEditable(false);
        add(brandField);

        add(new JLabel("CPU Family:"));
        cpuField = new JTextField();
        cpuField.setEditable(false);
        add(cpuField);

        add(new JLabel("Memory Size (GB):"));
        memoryField = new JTextField();
        memoryField.setEditable(false);
        add(memoryField);

        add(new JLabel("SSD Capacity (GB):"));
        ssdField = new JTextField();
        ssdField.setEditable(false);
        add(ssdField);

        add(new JLabel("Screen Size (inches):"));
        screenSizeField = new JTextField();
        screenSizeField.setEditable(false);
        add(screenSizeField);

        add(new JLabel("Price ($):"));
        priceField = new JTextField();
        priceField.setEditable(false);
        add(priceField);
    }

    // Getters
    public JTextField getIdField() {
        return idField;
    }

    public JComboBox<String> getCategoryField() {
        return categoryField;
    }

    public JComboBox<String> getTypeField() {
        return typeField;
    }

    public JTextField getBrandField() {
        return brandField;
    }

    public JTextField getCpuField() {
        return cpuField;
    }

    public JTextField getMemoryField() {
        return memoryField;
    }

    public JTextField getSsdField() {
        return ssdField;
    }

    public JTextField getScreenSizeField() {
        return screenSizeField;
    }

    public JTextField getPriceField() {
        return priceField;
    }
}
