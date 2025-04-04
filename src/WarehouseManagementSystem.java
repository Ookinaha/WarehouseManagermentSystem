import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.io.*;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;

public class WarehouseManagementSystem extends JFrame {
    private ArrayList<Product> products = new ArrayList<>();
    private ArrayList<InventoryImport> imports = new ArrayList<>();
    private ArrayList<Employee> employees = new ArrayList<>();
    private final int MINIMUM_STOCK_LEVEL = 10;
    private DefaultTableModel productTableModel, importTableModel, employeeTableModel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JLabel imageLabel;
    private static final String DATA_FILE = "warehouse_data.txt";

    public WarehouseManagementSystem() {
        setTitle("Hệ thống quản lý kho nội thất");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.add(createProductPanel(), "Sản phẩm");
        contentPanel.add(createImportPanel(), "Nhập kho");
        contentPanel.add(createEmployeePanel(), "Nhân viên");
        contentPanel.add(createReportPanel(), "Báo cáo");
        add(contentPanel, BorderLayout.CENTER);

        loadData();
        updateProductTable();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                saveData();
            }
        });
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(40, 44, 52));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));

        JLabel logoLabel = new JLabel("QUẢN LÝ KHO", JLabel.CENTER);
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        logoLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        sidebar.add(logoLabel);

        String[] menuItems = {"Sản phẩm", "Nhập kho", "Nhân viên", "Báo cáo"};
        for (String item : menuItems) {
            JButton menuButton = new JButton(item);
            menuButton.setFont(new Font("Arial", Font.PLAIN, 16));
            menuButton.setForeground(Color.WHITE);
            menuButton.setBackground(new Color(40, 44, 52));
            menuButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            menuButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            menuButton.setHorizontalAlignment(SwingConstants.LEFT);
            menuButton.addActionListener(e -> cardLayout.show(contentPanel, item));
            sidebar.add(menuButton);
        }
        return sidebar;
    }

    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] columns = {"Mã SP", "Tên", "Xuất xứ", "Nhà SX", "Bảo hành", "Kích thước", "Màu sắc", "Giá", "Số lượng", "Tình trạng", "Ảnh"};
        productTableModel = new DefaultTableModel(columns, 0);
        JTable productTable = new JTable(productTableModel);

        // Tùy chỉnh renderer cho cột "Ảnh"
        productTable.getColumnModel().getColumn(10).setCellRenderer(new ImageRenderer());
        productTable.getColumnModel().getColumn(10).setPreferredWidth(160); // Tăng chiều rộng cột ảnh
        productTable.setRowHeight(160); // Tăng chiều cao hàng để phù hợp với ảnh

        JScrollPane scrollPane = new JScrollPane(productTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = createProductForm(productTable);
        panel.add(formPanel, BorderLayout.EAST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Tìm kiếm");
        JComboBox<String> filterComboBox = new JComboBox<>(new String[]{"Tất cả", "Còn hàng", "Sắp hết hàng", "Hết hàng"});
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(filterComboBox);
        panel.add(searchPanel, BorderLayout.NORTH);

        searchBtn.addActionListener(e -> searchAndFilterProducts(searchField.getText(), filterComboBox.getSelectedItem().toString()));
        filterComboBox.addActionListener(e -> searchAndFilterProducts(searchField.getText(), filterComboBox.getSelectedItem().toString()));

        return panel;
    }

    private class ImageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            if (value != null) {
                // Tăng kích thước ảnh từ 100x100 lên 150x150
                ImageIcon imageIcon = new ImageIcon(new ImageIcon(value.toString()).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH));
                label.setIcon(imageIcon);
            }
            label.setHorizontalAlignment(SwingConstants.CENTER);
            return label;
        }
    }

    private JPanel createProductForm(JTable productTable) {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField[] fields = new JTextField[9];
        String[] labels = {"Mã SP:", "Tên:", "Xuất xứ:", "Nhà SX:", "Bảo hành (năm):", "Kích thước:", "Màu sắc:", "Giá:", "Số lượng:"};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            formPanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            fields[i] = new JTextField(15);
            formPanel.add(fields[i], gbc);
        }

        gbc.gridx = 0; gbc.gridy = 9;
        formPanel.add(new JLabel("Ảnh:"), gbc);
        gbc.gridx = 1;
        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(100, 100));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        formPanel.add(imageLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2;
        JButton uploadButton = new JButton("Tải ảnh lên");
        formPanel.add(uploadButton, gbc);

        gbc.gridy = 11;
        JButton addBtn = new JButton("Thêm");
        JButton updateBtn = new JButton("Cập nhật");
        JButton deleteBtn = new JButton("Xóa");
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(addBtn); btnPanel.add(updateBtn); btnPanel.add(deleteBtn);
        formPanel.add(btnPanel, gbc);

        final String[] imagePathHolder = {null};
        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "png", "jpeg");
            fileChooser.setFileFilter(filter);
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                imagePathHolder[0] = file.getAbsolutePath();
                ImageIcon imageIcon = new ImageIcon(new ImageIcon(file.getAbsolutePath()).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                imageLabel.setIcon(imageIcon);
            }
        });

        addBtn.addActionListener(e -> addProduct(fields, imagePathHolder[0]));
        updateBtn.addActionListener(e -> updateProduct(fields, productTable, imagePathHolder[0]));
        deleteBtn.addActionListener(e -> deleteProduct(productTable));
        productTable.getSelectionModel().addListSelectionListener(e -> loadProductToForm(productTable, fields, imageLabel));

        return formPanel;
    }

    private void addProduct(JTextField[] fields, String imagePath) {
        try {
            String productId = fields[0].getText().trim();
            String name = fields[1].getText().trim();
            String origin = fields[2].getText().trim();
            String manufacturer = fields[3].getText().trim();
            String warrantyText = fields[4].getText().trim();
            String dimensions = fields[5].getText().trim();
            String color = fields[6].getText().trim();
            String priceText = fields[7].getText().trim();
            String quantityText = fields[8].getText().trim();

            if (productId.isEmpty() || name.isEmpty() || warrantyText.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ các trường bắt buộc (Mã SP, Tên, Bảo hành, Giá, Số lượng)!");
                return;
            }

            int warrantyPeriod;
            double price;
            int quantity;
            try {
                warrantyPeriod = Integer.parseInt(warrantyText);
                price = Double.parseDouble(priceText);
                quantity = Integer.parseInt(quantityText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Bảo hành, Giá và Số lượng phải là số hợp lệ!");
                return;
            }

            if (warrantyPeriod < 0 || price < 0 || quantity < 0) {
                JOptionPane.showMessageDialog(this, "Bảo hành, Giá và Số lượng không được âm!");
                return;
            }

            Product p = new Product(productId, name, origin, manufacturer, warrantyPeriod, dimensions, color, price, quantity, imagePath);
            products.add(p);
            updateProductTable();
            clearFields(fields);
            imageLabel.setIcon(null);
            checkInventoryLevel();
            saveData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }

    private void updateProduct(JTextField[] fields, JTable table, String imagePath) {
        int row = table.getSelectedRow();
        if (row >= 0) {
            try {
                String productId = fields[0].getText().trim();
                String name = fields[1].getText().trim();
                String origin = fields[2].getText().trim();
                String manufacturer = fields[3].getText().trim();
                String warrantyText = fields[4].getText().trim();
                String dimensions = fields[5].getText().trim();
                String color = fields[6].getText().trim();
                String priceText = fields[7].getText().trim();
                String quantityText = fields[8].getText().trim();

                if (productId.isEmpty() || name.isEmpty() || warrantyText.isEmpty() || priceText.isEmpty() || quantityText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ các trường bắt buộc (Mã SP, Tên, Bảo hành, Giá, Số lượng)!");
                    return;
                }

                int warrantyPeriod;
                double price;
                int quantity;
                try {
                    warrantyPeriod = Integer.parseInt(warrantyText);
                    price = Double.parseDouble(priceText);
                    quantity = Integer.parseInt(quantityText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Bảo hành, Giá và Số lượng phải là số hợp lệ!");
                    return;
                }

                if (warrantyPeriod < 0 || price < 0 || quantity < 0) {
                    JOptionPane.showMessageDialog(this, "Bảo hành, Giá và Số lượng không được âm!");
                    return;
                }

                Product p = new Product(productId, name, origin, manufacturer, warrantyPeriod, dimensions, color, price, quantity, imagePath);
                products.set(row, p);
                updateProductTable();
                clearFields(fields);
                imageLabel.setIcon(null);
                checkInventoryLevel();
                saveData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        }
    }

    private void deleteProduct(JTable table) {
        int row = table.getSelectedRow();
        if (row >= 0 && JOptionPane.showConfirmDialog(this, "Xóa sản phẩm này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            products.remove(row);
            updateProductTable();
            imageLabel.setIcon(null);
            saveData();
        }
    }

    private void loadProductToForm(JTable table, JTextField[] fields, JLabel imageLabel) {
        int row = table.getSelectedRow();
        if (row >= 0) {
            Product p = products.get(row);
            fields[0].setText(p.getProductId());
            fields[1].setText(p.getName());
            fields[2].setText(p.getOrigin());
            fields[3].setText(p.getManufacturer());
            fields[4].setText(String.valueOf(p.getWarrantyPeriod()));
            fields[5].setText(p.getDimensions());
            fields[6].setText(p.getColor());
            fields[7].setText(String.valueOf(p.getPrice()));
            fields[8].setText(String.valueOf(p.getQuantity()));
            if (p.getImagePath() != null) {
                ImageIcon imageIcon = new ImageIcon(new ImageIcon(p.getImagePath()).getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                imageLabel.setIcon(imageIcon);
            } else {
                imageLabel.setIcon(null);
            }
        }
    }

    private void searchAndFilterProducts(String keyword, String filter) {
        productTableModel.setRowCount(0);
        for (Product p : products) {
            if ((p.getProductId().toLowerCase().contains(keyword.toLowerCase()) || p.getName().toLowerCase().contains(keyword.toLowerCase())) &&
                    (filter.equals("Tất cả") || p.getStatus().equals(filter))) {
                productTableModel.addRow(new Object[]{p.getProductId(), p.getName(), p.getOrigin(), p.getManufacturer(),
                        p.getWarrantyPeriod(), p.getDimensions(), p.getColor(), p.getPrice(), p.getQuantity(), p.getStatus(), p.getImagePath()});
            }
        }
    }

    private void updateProductTable() {
        if (productTableModel != null) {
            productTableModel.setRowCount(0);
            for (Product p : products) {
                productTableModel.addRow(new Object[]{p.getProductId(), p.getName(), p.getOrigin(), p.getManufacturer(),
                        p.getWarrantyPeriod(), p.getDimensions(), p.getColor(), p.getPrice(), p.getQuantity(), p.getStatus(), p.getImagePath()});
            }
        }
    }

    private void clearFields(JTextField[] fields) {
        for (JTextField field : fields) field.setText("");
    }

    private void checkInventoryLevel() {
        for (Product p : products) {
            if (p.getQuantity() <= MINIMUM_STOCK_LEVEL) {
                JOptionPane.showMessageDialog(this, "Cảnh báo: Sản phẩm " + p.getName() + " sắp hết hàng! Số lượng: " + p.getQuantity());
            }
        }
    }

    private void saveData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (Product p : products) {
                writer.write(String.format("%s|%s|%s|%s|%d|%s|%s|%.2f|%d|%s\n",
                        p.getProductId(), p.getName(), p.getOrigin(), p.getManufacturer(),
                        p.getWarrantyPeriod(), p.getDimensions(), p.getColor(), p.getPrice(),
                        p.getQuantity(), p.getImagePath() != null ? p.getImagePath() : ""));
            }
            for (InventoryImport imp : imports) {
                writer.write(String.format("IMPORT|%s|%s|%d|%s|%s\n",
                        imp.getImportId(), imp.getProductId(), imp.getQuantity(), imp.getSupplier(), imp.getImportDate()));
            }
            for (Employee emp : employees) {
                writer.write(String.format("EMPLOYEE|%s|%s|%s|%.2f\n",
                        emp.getEmployeeId(), emp.getName(), emp.getRole(), emp.getPerformanceScore()));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu dữ liệu: " + e.getMessage());
        }
    }

    private void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts[0].equals("IMPORT") && parts.length >= 6) {
                        imports.add(new InventoryImport(parts[1], parts[2], Integer.parseInt(parts[3]), parts[4], parts[5]));
                    } else if (parts[0].equals("EMPLOYEE") && parts.length >= 5) {
                        employees.add(new Employee(parts[1], parts[2], parts[3], Double.parseDouble(parts[4])));
                    } else if (parts.length >= 10) {
                        products.add(new Product(parts[0], parts[1], parts[2], parts[3],
                                Integer.parseInt(parts[4]), parts[5], parts[6],
                                Double.parseDouble(parts[7]), Integer.parseInt(parts[8]),
                                parts[9].isEmpty() ? null : parts[9]));
                    } else {
                        System.out.println("Dòng không hợp lệ, bỏ qua: " + line);
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Dữ liệu số không hợp lệ trong file: " + e.getMessage());
            }
        }
    }

    private JPanel createImportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] columns = {"Mã nhập", "Mã SP", "Số lượng", "Nhà cung cấp", "Ngày nhập"};
        importTableModel = new DefaultTableModel(columns, 0);
        JTable importTable = new JTable(importTableModel);
        JScrollPane scrollPane = new JScrollPane(importTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField[] fields = new JTextField[5];
        String[] labels = {"Mã nhập:", "Mã SP:", "Số lượng:", "Nhà cung cấp:", "Ngày nhập:"};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            formPanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            fields[i] = new JTextField(15);
            formPanel.add(fields[i], gbc);
        }

        JButton addBtn = new JButton("Nhập kho");
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        formPanel.add(addBtn, gbc);
        panel.add(formPanel, BorderLayout.EAST);

        addBtn.addActionListener(e -> addImport(fields));

        return panel;
    }

    private void addImport(JTextField[] fields) {
        try {
            InventoryImport imp = new InventoryImport(fields[0].getText(), fields[1].getText(),
                    Integer.parseInt(fields[2].getText()), fields[3].getText(), fields[4].getText());
            imports.add(imp);
            importTableModel.addRow(new Object[]{imp.getImportId(), imp.getProductId(), imp.getQuantity(), imp.getSupplier(), imp.getImportDate()});
            for (Product p : products) {
                if (p.getProductId().equals(imp.getProductId())) {
                    p.setQuantity(p.getQuantity() + imp.getQuantity());
                    updateProductTable();
                    break;
                }
            }
            clearFields(fields);
            checkInventoryLevel();
            saveData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ!");
        }
    }

    private JPanel createEmployeePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] columns = {"Mã NV", "Tên", "Vai trò", "Hiệu suất"};
        employeeTableModel = new DefaultTableModel(columns, 0);
        JTable employeeTable = new JTable(employeeTableModel);
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField[] fields = new JTextField[4];
        String[] labels = {"Mã NV:", "Tên:", "Vai trò:", "Hiệu suất:"};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            formPanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            fields[i] = new JTextField(15);
            formPanel.add(fields[i], gbc);
        }

        JButton addBtn = new JButton("Thêm");
        JButton updateBtn = new JButton("Cập nhật");
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(addBtn); btnPanel.add(updateBtn);
        formPanel.add(btnPanel, gbc);
        panel.add(formPanel, BorderLayout.EAST);

        addBtn.addActionListener(e -> addEmployee(fields));
        updateBtn.addActionListener(e -> updateEmployee(fields, employeeTable));
        employeeTable.getSelectionModel().addListSelectionListener(e -> loadEmployeeToForm(employeeTable, fields));

        return panel;
    }

    private void addEmployee(JTextField[] fields) {
        try {
            Employee emp = new Employee(fields[0].getText(), fields[1].getText(), fields[2].getText(),
                    Double.parseDouble(fields[3].getText()));
            employees.add(emp);
            employeeTableModel.addRow(new Object[]{emp.getEmployeeId(), emp.getName(), emp.getRole(), emp.getPerformanceScore()});
            clearFields(fields);
            saveData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ!");
        }
    }

    private void updateEmployee(JTextField[] fields, JTable table) {
        int row = table.getSelectedRow();
        if (row >= 0) {
            try {
                Employee emp = new Employee(fields[0].getText(), fields[1].getText(), fields[2].getText(),
                        Double.parseDouble(fields[3].getText()));
                employees.set(row, emp);
                employeeTableModel.setValueAt(emp.getEmployeeId(), row, 0);
                employeeTableModel.setValueAt(emp.getName(), row, 1);
                employeeTableModel.setValueAt(emp.getRole(), row, 2);
                employeeTableModel.setValueAt(emp.getPerformanceScore(), row, 3);
                clearFields(fields);
                saveData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ!");
            }
        }
    }

    private void loadEmployeeToForm(JTable table, JTextField[] fields) {
        int row = table.getSelectedRow();
        if (row >= 0) {
            Employee emp = employees.get(row);
            fields[0].setText(emp.getEmployeeId());
            fields[1].setText(emp.getName());
            fields[2].setText(emp.getRole());
            fields[3].setText(String.valueOf(emp.getPerformanceScore()));
        }
    }

    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JTextArea reportArea = new JTextArea(20, 40);
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton generateBtn = new JButton("Tạo báo cáo");
        panel.add(generateBtn, BorderLayout.SOUTH);

        generateBtn.addActionListener(e -> {
            StringBuilder report = new StringBuilder();
            report.append("BÁO CÁO TÌNH TRẠNG KHO HÀNG\n");
            report.append("Ngày: ").append(java.time.LocalDate.now()).append("\n\n");
            report.append("Sản phẩm:\n");
            for (Product p : products) {
                report.append(p.toString()).append("\n");
            }
            report.append("\nNhập kho:\n");
            for (InventoryImport imp : imports) {
                report.append(imp.toString()).append("\n");
            }
            reportArea.setText(report.toString());
        });

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WarehouseManagementSystem().setVisible(true));
    }
}