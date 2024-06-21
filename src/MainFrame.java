import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MainFrame extends JFrame {
    private JTable computerTable;
    private JButton addButton, deleteButton, updateButton, logoutButton;
    private DefaultTableModel tableModel;
    private JComboBox<String> categoryComboBox, typeComboBox;
    private HashMap<String, StoreItems> computers;
    private HashMap<String, Staff> staffMap;
    private ImageIcon customIcon;

    // Form panel instance
    private FormPanel formPanel;

    public MainFrame(Staff staff, HashMap<String, StoreItems> computers, HashMap<String, Staff> staffMap) {
        this.computers = computers;
        this.staffMap = staffMap;

        setTitle("Computer Store Management: " + staff.getUsername());
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        try {
            BufferedImage logoImage = ImageIO.read(new File("storeLogoImage.jpg"));
            Image scaledImage = logoImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            customIcon = new ImageIcon(scaledImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Top Panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 2, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Category
        topPanel.add(new JLabel("Computer Category"));
        categoryComboBox = new JComboBox<>(new String[]{"All", "Desktop PC", "Laptop", "Tablet"});
        categoryComboBox.addActionListener(e -> updateTypeComboBox());
        topPanel.add(categoryComboBox);

        // Type
        topPanel.add(new JLabel("Computer Type"));
        typeComboBox = new JComboBox<>(new String[]{"All"});
        typeComboBox.addActionListener(e -> filterComputers());
        topPanel.add(typeComboBox);

        add(topPanel, BorderLayout.NORTH);

        // Setup the table
        tableModel = new DefaultTableModel(new String[]{"Category", "Type", "ID", "Brand", "CPU", "Price"}, 0);
        computerTable = new JTable(tableModel);
        loadComputerData();
        add(new JScrollPane(computerTable), BorderLayout.CENTER);

        // Selection listener for table
        computerTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && computerTable.getSelectedRow() != -1) {
                    displaySelectedComputerDetails();
                }
            }
        });

        // Form Panel
        formPanel = new FormPanel();
        add(formPanel, BorderLayout.EAST);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (staff instanceof Manager) {
            addButton = new JButton("Add");
            updateButton = new JButton("Update");
            deleteButton = new JButton("Delete");

            buttonPanel.add(addButton);
            buttonPanel.add(updateButton);
            buttonPanel.add(deleteButton);

            addButton.addActionListener(e -> addComputer());
            updateButton.addActionListener(e -> updateComputer());
            deleteButton.addActionListener(e -> deleteComputer());
        }
        logoutButton = new JButton("Logout");
        buttonPanel.add(logoutButton);

        logoutButton.addActionListener(e -> logout());

        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadComputerData() {
        for (StoreItems item : computers.values()) {
            tableModel.addRow(new Object[]{item.getCategory(), item.getType(), item.getId(), item.getBrand(), item.getCpuFamily(), item.getPrice()});
        }
    }

    private void updateTypeComboBox() {
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        typeComboBox.removeAllItems();
        typeComboBox.addItem("All");

        if (selectedCategory != null && !selectedCategory.equals("All")) {
            List<String> types = computers.values().stream()
                    .filter(item -> item.getCategory().equals(selectedCategory))
                    .map(StoreItems::getType)
                    .distinct()
                    .collect(Collectors.toList());

            for (String type : types) {
                typeComboBox.addItem(type);
            }
        }
        filterComputers();
    }

    private void filterComputers() {
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        String selectedType = (String) typeComboBox.getSelectedItem();

        if (selectedCategory == null || selectedType == null) {
            return;
        }

        tableModel.setRowCount(0);

        for (StoreItems item : computers.values()) {
            boolean matchesCategory = selectedCategory.equals("All") || item.getCategory().equals(selectedCategory);
            boolean matchesType = selectedType.equals("All") || item.getType().equals(selectedType);

            if (matchesCategory && matchesType) {
                tableModel.addRow(new Object[]{item.getCategory(), item.getType(), item.getId(), item.getBrand(), item.getCpuFamily(), item.getPrice()});
            }
        }
    }

    private void displaySelectedComputerDetails() {
        int selectedRow = computerTable.getSelectedRow();
        String id = (String) tableModel.getValueAt(selectedRow, 2);
        StoreItems selectedItem = computers.get(id);

        if (selectedItem == null) {
            System.err.println("Selected item with ID " + id + " not found in the computers map.");
            return;
        }

        formPanel.getIdField().setText(selectedItem.getId());
        formPanel.getCategoryField().setSelectedItem(selectedItem.getCategory());
        updateTypeComboBoxInFormPanel(selectedItem.getCategory());
        formPanel.getTypeField().setSelectedItem(selectedItem.getType());
        formPanel.getBrandField().setText(selectedItem.getBrand());
        formPanel.getCpuField().setText(selectedItem.getCpuFamily());
        formPanel.getPriceField().setText(String.valueOf(selectedItem.getPrice()));

        if (selectedItem instanceof Desktop) {
            formPanel.getMemoryField().setText(String.valueOf(((Desktop) selectedItem).getMemorySize()));
            formPanel.getSsdField().setText(String.valueOf(((Desktop) selectedItem).getSsdCapacity()));
            formPanel.getScreenSizeField().setText(""); // Not applicable for Desktop
        } else if (selectedItem instanceof Laptop) {
            formPanel.getMemoryField().setText(String.valueOf(((Laptop) selectedItem).getMemorySize()));
            formPanel.getSsdField().setText(String.valueOf(((Laptop) selectedItem).getSsdCapacity()));
            formPanel.getScreenSizeField().setText(String.valueOf(((Laptop) selectedItem).getScreenSize()));
        } else if (selectedItem instanceof Tablet) {
            formPanel.getMemoryField().setText(""); // Not applicable for Tablet
            formPanel.getSsdField().setText(""); // Not applicable for Tablet
            formPanel.getScreenSizeField().setText(String.valueOf(((Tablet) selectedItem).getScreenSize()));
        }
    }

    private void updateTypeComboBoxInFormPanel(String category) {
        JComboBox<String> typeField = formPanel.getTypeField();
        typeField.removeAllItems();

        List<String> types = computers.values().stream()
                .filter(item -> item.getCategory().equals(category))
                .map(StoreItems::getType)
                .distinct()
                .collect(Collectors.toList());

        for (String type : types) {
            typeField.addItem(type);
        }
    }

    private void addComputer() {
        // Dialog for adding a new computer
        JDialog addDialog = new JDialog(this, "Add New Computer", true);
        addDialog.setSize(400, 400);
        addDialog.setLayout(new GridLayout(10, 2));

        JTextField newIdField = new JTextField();
        JComboBox<String> newCategoryField = new JComboBox<>(new String[]{"-- Select Category --", "Desktop PC", "Laptop", "Tablet"});
        JComboBox<String> newTypeField = new JComboBox<>();
        JTextField newBrandField = new JTextField();
        JTextField newCpuField = new JTextField();
        JTextField newMemoryField = new JTextField();
        JTextField newSsdField = new JTextField();
        JTextField newScreenSizeField = new JTextField();
        JTextField newPriceField = new JTextField();

        newTypeField.setEnabled(false); //

        addDialog.add(new JLabel("Model ID:"));
        addDialog.add(newIdField);
        addDialog.add(new JLabel("Category:"));
        addDialog.add(newCategoryField);
        addDialog.add(new JLabel("Type:"));
        addDialog.add(newTypeField);
        addDialog.add(new JLabel("Brand:"));
        addDialog.add(newBrandField);
        addDialog.add(new JLabel("CPU Family:"));
        addDialog.add(newCpuField);
        addDialog.add(new JLabel("Memory Size (GB):"));
        addDialog.add(newMemoryField);
        addDialog.add(new JLabel("SSD Capacity (GB):"));
        addDialog.add(newSsdField);
        addDialog.add(new JLabel("Screen Size (inches):"));
        addDialog.add(newScreenSizeField);
        addDialog.add(new JLabel("Price ($):"));
        addDialog.add(newPriceField);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        saveButton.setEnabled(false); // Disable the save button, enable after checking fields are filled in

        // Adds action listener to update the Type dropdown depending on selected Category
        newCategoryField.addActionListener(e -> {
            String selectedCategory = (String) newCategoryField.getSelectedItem();
            newTypeField.removeAllItems();
            newTypeField.setEnabled(!"-- Select Category --".equals(selectedCategory));

            newMemoryField.setEnabled(false);
            newSsdField.setEnabled(false);
            newScreenSizeField.setEnabled(false);

            if (selectedCategory != null && !selectedCategory.equals("-- Select Category --")) {
                List<String> types = computers.values().stream()
                        .filter(item -> item.getCategory().equals(selectedCategory))
                        .map(StoreItems::getType)
                        .distinct()
                        .collect(Collectors.toList());

                for (String type : types) {
                    newTypeField.addItem(type);
                }

                // Enables the relevant fields based on the selected category
                switch (selectedCategory) {
                    case "Desktop PC":
                        newMemoryField.setEnabled(true);
                        newSsdField.setEnabled(true);
                        newMemoryField.setBackground(Color.white);
                        newSsdField.setBackground(Color.white);
                        newScreenSizeField.setBackground(Color.lightGray);

                        newScreenSizeField.setText(""); //prevent user from selecting laptop, entering screensize and then selecting desktop etc.
                        break;
                    case "Laptop":
                        newMemoryField.setEnabled(true);
                        newSsdField.setEnabled(true);
                        newScreenSizeField.setEnabled(true);
                        newMemoryField.setBackground(Color.white);
                        newSsdField.setBackground(Color.white);
                        newScreenSizeField.setBackground(Color.white);
                        break;
                    case "Tablet":
                        newScreenSizeField.setEnabled(true);
                        newMemoryField.setBackground(Color.lightGray);
                        newSsdField.setBackground(Color.lightGray);
                        newScreenSizeField.setBackground(Color.white);

                        newMemoryField.setText(""); //prevent invalid input
                        newSsdField.setText("");
                        break;
                }
            }

            // Check if all required fields are filled before enabling the Save button
            checkRequiredFields(newIdField, newCategoryField, newTypeField, newBrandField, newCpuField, newMemoryField, newSsdField, newScreenSizeField, newPriceField, saveButton);
        });

        // The document listeners check if Save button should be enabled
        DocumentListener checkFieldsListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkRequiredFields(newIdField, newCategoryField, newTypeField, newBrandField, newCpuField, newMemoryField, newSsdField, newScreenSizeField, newPriceField, saveButton);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkRequiredFields(newIdField, newCategoryField, newTypeField, newBrandField, newCpuField, newMemoryField, newSsdField, newScreenSizeField, newPriceField, saveButton);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkRequiredFields(newIdField, newCategoryField, newTypeField, newBrandField, newCpuField, newMemoryField, newSsdField, newScreenSizeField, newPriceField, saveButton);
            }
        };

        newIdField.getDocument().addDocumentListener(checkFieldsListener);
        newBrandField.getDocument().addDocumentListener(checkFieldsListener);
        newCpuField.getDocument().addDocumentListener(checkFieldsListener);
        newPriceField.getDocument().addDocumentListener(checkFieldsListener);
        newMemoryField.getDocument().addDocumentListener(checkFieldsListener);
        newSsdField.getDocument().addDocumentListener(checkFieldsListener);
        newScreenSizeField.getDocument().addDocumentListener(checkFieldsListener);

        saveButton.addActionListener(e -> {
            String id = newIdField.getText();
            if (computers.containsKey(id)) {
                JOptionPane.showMessageDialog(MainFrame.this, "Model ID already exists. Please use a unique ID.", "Error", JOptionPane.ERROR_MESSAGE, customIcon);
                return;
            }

            String category = (String) newCategoryField.getSelectedItem();
            String type = (String) newTypeField.getSelectedItem();
            String brand = newBrandField.getText();
            String cpu = newCpuField.getText();
            double price;

            try {
                price = Double.parseDouble(newPriceField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(MainFrame.this, "Please enter a valid number for Price.", "Error", JOptionPane.ERROR_MESSAGE, customIcon);
                return;
            }

            StoreItems newItem = null;
            try {
                switch (category) {
                    case "Desktop PC":
                        int memorySize = Integer.parseInt(newMemoryField.getText());
                        int ssdCapacity = Integer.parseInt(newSsdField.getText());
                        newItem = new Desktop(category, type, id, brand, cpu, price, memorySize, ssdCapacity);
                        break;
                    case "Laptop":
                        memorySize = Integer.parseInt(newMemoryField.getText());
                        ssdCapacity = Integer.parseInt(newSsdField.getText());
                        double screenSize = Double.parseDouble(newScreenSizeField.getText());
                        newItem = new Laptop(category, type, id, brand, cpu, price, memorySize, ssdCapacity, screenSize);
                        break;
                    case "Tablet":
                        double tabletScreenSize = Double.parseDouble(newScreenSizeField.getText());
                        newItem = new Tablet(category, type, id, brand, cpu, price, tabletScreenSize);
                        break;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(MainFrame.this, "Please enter valid numbers for memory, SSD capacity, and screen size.", "Error", JOptionPane.ERROR_MESSAGE, customIcon);
                return;
            }

            computers.put(id, newItem);
            tableModel.addRow(new Object[]{category, type, id, brand, cpu, price});

            //add item Confirmation Message
            JOptionPane.showMessageDialog(MainFrame.this, "The record for the computer has been added successfully", "Success", JOptionPane.INFORMATION_MESSAGE, customIcon);

            addDialog.dispose();
        });

        cancelButton.addActionListener(e -> addDialog.dispose());

        addDialog.add(saveButton);
        addDialog.add(cancelButton);

        addDialog.setVisible(true);
    }

    // Update method
    private void updateComputer() {
        int selectedRow = computerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No computer selected!", "Error", JOptionPane.ERROR_MESSAGE, customIcon);
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 2);
        StoreItems selectedItem = computers.get(id);

        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this, "Selected computer not found!", "Error", JOptionPane.ERROR_MESSAGE, customIcon);
            return;
        }

        // Dialog for Updating the selected item.
        JDialog updateDialog = new JDialog(this, "Update Item", true);
        updateDialog.setSize(400, 400);
        updateDialog.setLayout(new GridLayout(10, 2));

        JTextField updateIdField = new JTextField(selectedItem.getId());
        JComboBox<String> updateCategoryField = new JComboBox<>(new String[]{"Desktop PC", "Laptop", "Tablet"});
        updateCategoryField.setSelectedItem(selectedItem.getCategory());
        JComboBox<String> updateTypeField = new JComboBox<>();
        JTextField updateBrandField = new JTextField(selectedItem.getBrand());
        JTextField updateCpuField = new JTextField(selectedItem.getCpuFamily());
        JTextField updateMemoryField = new JTextField();
        JTextField updateSsdField = new JTextField();
        JTextField updateScreenSizeField = new JTextField();
        JTextField updatePriceField = new JTextField(String.valueOf(selectedItem.getPrice()));

        updateDialog.add(new JLabel("Model ID:"));
        updateDialog.add(updateIdField);
        updateDialog.add(new JLabel("Category:"));
        updateDialog.add(updateCategoryField);
        updateDialog.add(new JLabel("Type:"));
        updateDialog.add(updateTypeField);
        updateDialog.add(new JLabel("Brand:"));
        updateDialog.add(updateBrandField);
        updateDialog.add(new JLabel("CPU Family:"));
        updateDialog.add(updateCpuField);
        updateDialog.add(new JLabel("Memory Size (GB):"));
        updateDialog.add(updateMemoryField);
        updateDialog.add(new JLabel("SSD Capacity (GB):"));
        updateDialog.add(updateSsdField);
        updateDialog.add(new JLabel("Screen Size (inches):"));
        updateDialog.add(updateScreenSizeField);
        updateDialog.add(new JLabel("Price ($):"));
        updateDialog.add(updatePriceField);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        // It populates the TypeComboBox and the relevant fields depending on the category
        updateTypeField.removeAllItems();
        List<String> types = computers.values().stream()
                .filter(item -> item.getCategory().equals(selectedItem.getCategory()))
                .map(StoreItems::getType)
                .distinct()
                .collect(Collectors.toList());
        for (String type : types) {
            updateTypeField.addItem(type);
        }
        updateTypeField.setSelectedItem(selectedItem.getType());

        switch (selectedItem.getCategory()) {
            case "Desktop PC":
                updateMemoryField.setText(String.valueOf(((Desktop) selectedItem).getMemorySize()));
                updateSsdField.setText(String.valueOf(((Desktop) selectedItem).getSsdCapacity()));
                updateScreenSizeField.setEnabled(false);
                break;
            case "Laptop":
                updateMemoryField.setText(String.valueOf(((Laptop) selectedItem).getMemorySize()));
                updateSsdField.setText(String.valueOf(((Laptop) selectedItem).getSsdCapacity()));
                updateScreenSizeField.setText(String.valueOf(((Laptop) selectedItem).getScreenSize()));
                break;
            case "Tablet":
                updateMemoryField.setEnabled(false);
                updateSsdField.setEnabled(false);
                updateScreenSizeField.setText(String.valueOf(((Tablet) selectedItem).getScreenSize()));
                break;
        }

        // Action listener for updating the Type dropdown depending on selected Category
        updateCategoryField.addActionListener(e -> {
            String selectedCategory = (String) updateCategoryField.getSelectedItem();
            updateTypeField.removeAllItems();
            updateTypeField.setEnabled(selectedCategory != null);
            updateMemoryField.setEnabled(false);
            updateSsdField.setEnabled(false);
            updateScreenSizeField.setEnabled(false);

            if (selectedCategory != null) {
                List<String> newTypes = computers.values().stream()
                        .filter(item -> item.getCategory().equals(selectedCategory))
                        .map(StoreItems::getType)
                        .distinct()
                        .collect(Collectors.toList());

                for (String type : newTypes) {
                    updateTypeField.addItem(type);
                }

                // Enable only relevant fields depending on the selected category
                switch (selectedCategory) {
                    case "Desktop PC":
                        updateMemoryField.setEnabled(true);
                        updateSsdField.setEnabled(true);
                        updateScreenSizeField.setText("");
                        updateMemoryField.setBackground(Color.white);
                        updateSsdField.setBackground(Color.white);
                        updateScreenSizeField.setBackground(Color.lightGray);
                        break;
                    case "Laptop":
                        updateMemoryField.setEnabled(true);
                        updateSsdField.setEnabled(true);
                        updateScreenSizeField.setEnabled(true);
                        updateMemoryField.setBackground(Color.white);
                        updateSsdField.setBackground(Color.white);
                        updateScreenSizeField.setBackground(Color.white);
                        break;
                    case "Tablet":
                        updateScreenSizeField.setEnabled(true);
                        updateMemoryField.setText("");
                        updateSsdField.setText("");
                        updateMemoryField.setBackground(Color.lightGray);
                        updateSsdField.setBackground(Color.lightGray);
                        updateScreenSizeField.setBackground(Color.white);
                        break;
                }
            }

            // Checks if all required fields are filled, if so enable the Save button
            checkRequiredFields(updateIdField, updateCategoryField, updateTypeField, updateBrandField, updateCpuField, updateMemoryField, updateSsdField, updateScreenSizeField, updatePriceField, saveButton);
        });

        // Adds document listeners to text fields to check if Save button should be enabled
        DocumentListener checkFieldsListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkRequiredFields(updateIdField, updateCategoryField, updateTypeField, updateBrandField, updateCpuField, updateMemoryField, updateSsdField, updateScreenSizeField, updatePriceField, saveButton);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkRequiredFields(updateIdField, updateCategoryField, updateTypeField, updateBrandField, updateCpuField, updateMemoryField, updateSsdField, updateScreenSizeField, updatePriceField, saveButton);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkRequiredFields(updateIdField, updateCategoryField, updateTypeField, updateBrandField, updateCpuField, updateMemoryField, updateSsdField, updateScreenSizeField, updatePriceField, saveButton);
            }
        };

        updateIdField.getDocument().addDocumentListener(checkFieldsListener);
        updateBrandField.getDocument().addDocumentListener(checkFieldsListener);
        updateCpuField.getDocument().addDocumentListener(checkFieldsListener);
        updatePriceField.getDocument().addDocumentListener(checkFieldsListener);
        updateMemoryField.getDocument().addDocumentListener(checkFieldsListener);
        updateSsdField.getDocument().addDocumentListener(checkFieldsListener);
        updateScreenSizeField.getDocument().addDocumentListener(checkFieldsListener);

        saveButton.addActionListener(e -> {
            String updatedId = updateIdField.getText();
            if (!updatedId.equals(id) && computers.containsKey(updatedId)) {
                JOptionPane.showMessageDialog(MainFrame.this, "Model ID already exists. Please use a unique ID.", "Error", JOptionPane.ERROR_MESSAGE, customIcon);
                return;
            }

            String updatedCategory = (String) updateCategoryField.getSelectedItem();
            String updatedType = (String) updateTypeField.getSelectedItem();
            String updatedBrand = updateBrandField.getText();
            String updatedCpu = updateCpuField.getText();
            double updatedPrice;

            try {
                updatedPrice = Double.parseDouble(updatePriceField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(MainFrame.this, "Please enter a valid number for Price.", "Error", JOptionPane.ERROR_MESSAGE, customIcon);
                return;
            }

            StoreItems updatedItem = null;
            try {
                switch (updatedCategory) {
                    case "Desktop PC":
                        int updatedMemorySize = Integer.parseInt(updateMemoryField.getText());
                        int updatedSsdCapacity = Integer.parseInt(updateSsdField.getText());
                        updatedItem = new Desktop(updatedCategory, updatedType, updatedId, updatedBrand, updatedCpu, updatedPrice, updatedMemorySize, updatedSsdCapacity);
                        break;
                    case "Laptop":
                        updatedMemorySize = Integer.parseInt(updateMemoryField.getText());
                        updatedSsdCapacity = Integer.parseInt(updateSsdField.getText());
                        double updatedScreenSize = Double.parseDouble(updateScreenSizeField.getText());
                        updatedItem = new Laptop(updatedCategory, updatedType, updatedId, updatedBrand, updatedCpu, updatedPrice, updatedMemorySize, updatedSsdCapacity, updatedScreenSize);
                        break;
                    case "Tablet":
                        double tabletUpdatedScreenSize = Double.parseDouble(updateScreenSizeField.getText());
                        updatedItem = new Tablet(updatedCategory, updatedType, updatedId, updatedBrand, updatedCpu, updatedPrice, tabletUpdatedScreenSize);
                        break;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(MainFrame.this, "Please enter valid numbers for memory, SSD capacity, and screen size.", "Error", JOptionPane.ERROR_MESSAGE, customIcon);
                return;
            }

            computers.put(updatedId, updatedItem);
            tableModel.setValueAt(updatedCategory, selectedRow, 0);
            tableModel.setValueAt(updatedType, selectedRow, 1);
            tableModel.setValueAt(updatedId, selectedRow, 2);
            tableModel.setValueAt(updatedBrand, selectedRow, 3);
            tableModel.setValueAt(updatedCpu, selectedRow, 4);
            tableModel.setValueAt(updatedPrice, selectedRow, 5);

            // update Confirmation Message
            JOptionPane.showMessageDialog(MainFrame.this, "The record for the computer has been updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE, customIcon);

            updateDialog.dispose();
        });

        cancelButton.addActionListener(e -> updateDialog.dispose());

        updateDialog.add(saveButton);
        updateDialog.add(cancelButton);

        updateDialog.setVisible(true);
    }

    // Delete
    private void deleteComputer() {
        int selectedRow = computerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No computer selected!", "Error", JOptionPane.ERROR_MESSAGE, customIcon);
            return;
        }

        String id = (String) tableModel.getValueAt(selectedRow, 2);
        StoreItems selectedItem = computers.get(id);

        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this, "Selected computer not found!", "Error", JOptionPane.ERROR_MESSAGE, customIcon);
            return;
        }

        // Confirmation message
        int response = JOptionPane.showOptionDialog(
                this,
                "Are you sure you would like to delete: " + selectedItem.getId() + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                customIcon,
                new String[]{"Delete", "Cancel"},
                "Cancel"
        );

        if (response == JOptionPane.YES_OPTION) {
            computers.remove(id);
            tableModel.removeRow(selectedRow);

            // Delete theConfirmation Message
            JOptionPane.showMessageDialog(this, "The record for the computer has been deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE, customIcon);
        }
    }

    private void checkRequiredFields(
            JTextField idField,
            JComboBox<String> categoryField,
            JComboBox<String> typeField,
            JTextField brandField,
            JTextField cpuField,
            JTextField memoryField,
            JTextField ssdField,
            JTextField screenSizeField,
            JTextField priceField,
            JButton saveButton) {

        String id = idField.getText().trim();
        String category = (String) categoryField.getSelectedItem();
        String type = (String) typeField.getSelectedItem();
        String brand = brandField.getText().trim();
        String cpu = cpuField.getText().trim();
        String price = priceField.getText().trim();

        boolean allFieldsFilled = !id.isEmpty()
                && category != null
                && !category.equals("-- Select Category --")
                && type != null
                && !type.isEmpty()
                && !brand.isEmpty()
                && !cpu.isEmpty()
                && !price.isEmpty();

        // Additional checks depending on category
        if ("Desktop PC".equals(category)) {
            allFieldsFilled = allFieldsFilled
                    && !memoryField.getText().trim().isEmpty()
                    && !ssdField.getText().trim().isEmpty();
        } else if ("Laptop".equals(category)) {
            allFieldsFilled = allFieldsFilled
                    && !memoryField.getText().trim().isEmpty()
                    && !ssdField.getText().trim().isEmpty()
                    && !screenSizeField.getText().trim().isEmpty();
        } else if ("Tablet".equals(category)) {
            allFieldsFilled = allFieldsFilled
                    && !screenSizeField.getText().trim().isEmpty();
        }

        // Check if the price field contains valid number
        try {
            if (!price.isEmpty()) {
                Double.parseDouble(price);
            } else {
                allFieldsFilled = false;
            }
        } catch (NumberFormatException e) {
            allFieldsFilled = false;
        }

        // Check if the memory,  SSD, and screensize fields contain valid numbers
        try {
            if ("Desktop PC".equals(category) || "Laptop".equals(category)) {
                if (!memoryField.getText().trim().isEmpty()) {
                    Integer.parseInt(memoryField.getText().trim());
                } else {
                    allFieldsFilled = false;
                }
                if (!ssdField.getText().trim().isEmpty()) {
                    Integer.parseInt(ssdField.getText().trim());
                } else {
                    allFieldsFilled = false;
                }
            }
            if ("Laptop".equals(category) || "Tablet".equals(category)) {
                if (!screenSizeField.getText().trim().isEmpty()) {
                    Double.parseDouble(screenSizeField.getText().trim());
                } else {
                    allFieldsFilled = false;
                }
            }
        } catch (NumberFormatException e) {
            allFieldsFilled = false;
        }

        saveButton.setEnabled(allFieldsFilled);
    }

    private void logout() {
        // Dispose frame
        this.dispose();
        // Back to the login frame
        SwingUtilities.invokeLater(() -> new LoginFrame(staffMap, computers));
    }
}
