
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class MenuForm extends JFrame {

    private Color bgMain = new Color(248, 250, 252); 
    private Color sidebarBg = new Color(15, 23, 42); 
    private Color textDark = new Color(30, 41, 59);
    private Color textMuted = new Color(100, 116, 139);
    private Color primaryGreen = new Color(16, 185, 129); 
    private Color borderLight = new Color(226, 232, 240);

    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    
    private JPanel foodGridPanel;
    private JPanel inventoryGridPanel;
    private JLabel totalItemsValLabel;
    private JLabel availableItemsValLabel; 
    private List<JButton> filterButtonsList = new ArrayList<>();
    private List<JPanel> sidebarLinks = new ArrayList<>();
    
    private JPanel notificationsContainer;
    private JPanel activeOrdersContainer; 
    private int lastOrderCount = 0; 
    private JLabel totalEarningsLabel;

    private final String DATA_FILE = System.getProperty("user.home") + File.separator + "manam_menu_data.txt";
    private final String ORDERS_FILE = System.getProperty("user.home") + File.separator + "manam_orders_data.txt";

    public MenuForm() {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception e) {}

        setTitle("Manam - Cloud Kitchen Manager");
        setSize(1350, 800);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        foodGridPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        foodGridPanel.setOpaque(false);
        foodGridPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        inventoryGridPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        inventoryGridPanel.setOpaque(false);
        inventoryGridPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        mainContentPanel.add(createDashboardScreen(), "DASHBOARD_SCREEN");
        mainContentPanel.add(createMenuManagementScreen(), "MENU_SCREEN");
        mainContentPanel.add(createInventoryScreen(), "INVENTORY_SCREEN");
        mainContentPanel.add(createKitchenProfileScreen(), "PROFILE_SCREEN");
        mainContentPanel.add(createReportsScreen(), "REPORTS_SCREEN");

        add(createSidebar(), BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);

        loadMenuData(); 
        loadDefaultInventory();
        
        Timer orderTimer = new Timer(3000, e -> checkForNewOrders());
        orderTimer.start();
        checkForNewOrders(); 
        
        cardLayout.show(mainContentPanel, "DASHBOARD_SCREEN");
    }

    // ==========================================
    // 1. MANAGEMENT DASHBOARD SCREEN
    // ==========================================
    private JPanel createDashboardScreen() {
        JPanel dashPanel = new JPanel(new BorderLayout());
        dashPanel.setBackground(bgMain);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderLight), new EmptyBorder(15, 30, 15, 30)));
        JLabel headerTitle = new JLabel("Cloud Kitchen Management Dashboard & Analytics");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 22)); headerTitle.setForeground(textDark);
        headerPanel.add(headerTitle, BorderLayout.WEST);
        dashPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(bgMain);
        content.setBorder(new EmptyBorder(25, 30, 25, 30));

        JLabel welcome = new JLabel("Welcome back, Kitchen Manager!");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcome.setForeground(textDark);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subWelcome = new JLabel("Streamlines orders, inventory, sales, and kitchen performance with visual charts.");
        subWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subWelcome.setForeground(textMuted);
        subWelcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(welcome);
        content.add(Box.createVerticalStrut(5));
        content.add(subWelcome);
        content.add(Box.createVerticalStrut(20));

        JPanel topMetricsRow = new JPanel(new GridLayout(1, 3, 20, 0));
        topMetricsRow.setOpaque(false);
        topMetricsRow.setMaximumSize(new Dimension(1300, 140));
        topMetricsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        topMetricsRow.add(createDashboardCard("Sales Overview", "Rs. 24,580", "Profit: Rs. 8,245 | Goal: 75%", new Color(30, 41, 59), Color.WHITE));
        topMetricsRow.add(createDashboardCard("Order Status Today", "1,245", "Completed: 936  |  Pending: 309", new Color(16, 185, 129), Color.WHITE));
        topMetricsRow.add(createDashboardCard("Critical Items (Stock)", "2 Items", "Tomatoes & Cheese low on stock", new Color(239, 68, 68), Color.WHITE));

        content.add(topMetricsRow);
        content.add(Box.createVerticalStrut(20));

        JPanel chartPanel = new JPanel();
        chartPanel.setLayout(new BoxLayout(chartPanel, BoxLayout.Y_AXIS));
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(borderLight, 1, true), new EmptyBorder(20, 20, 20, 20)));
        chartPanel.setMaximumSize(new Dimension(1300, 200));
        chartPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel chartTitle = new JLabel("Weekly Revenue Distribution (Bar Diagram View)");
        chartTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        chartTitle.setForeground(textDark);
        chartPanel.add(chartTitle);
        chartPanel.add(Box.createVerticalStrut(15));

        chartPanel.add(createBarGraphRow("Monday", 65, "Rs. 18,200"));
        chartPanel.add(Box.createVerticalStrut(8));
        chartPanel.add(createBarGraphRow("Tuesday", 80, "Rs. 22,400"));
        chartPanel.add(Box.createVerticalStrut(8));
        chartPanel.add(createBarGraphRow("Wednesday", 50, "Rs. 14,100"));
        chartPanel.add(Box.createVerticalStrut(8));
        chartPanel.add(createBarGraphRow("Thursday", 90, "Rs. 26,800"));
        chartPanel.add(Box.createVerticalStrut(8));
        chartPanel.add(createBarGraphRow("Friday", 100, "Rs. 32,500"));

        content.add(chartPanel);
        content.add(Box.createVerticalStrut(20));

        JPanel middleRow = new JPanel(new GridLayout(1, 2, 20, 0));
        middleRow.setOpaque(false);
        middleRow.setMaximumSize(new Dimension(1300, 220));
        middleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel bestSellingPanel = new JPanel();
        bestSellingPanel.setLayout(new BoxLayout(bestSellingPanel, BoxLayout.Y_AXIS));
        bestSellingPanel.setBackground(Color.WHITE);
        bestSellingPanel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(borderLight, 1, true), new EmptyBorder(15, 20, 15, 20)));
        
        JLabel bsTitle = new JLabel("Top Best Selling Items");
        bsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        bsTitle.setForeground(textDark);
        bestSellingPanel.add(bsTitle);
        bestSellingPanel.add(Box.createVerticalStrut(10));
        bestSellingPanel.add(createBestSellingItemRow("1. Paneer Butter Masala", "245 sold", primaryGreen));
        bestSellingPanel.add(Box.createVerticalStrut(8));
        bestSellingPanel.add(createBestSellingItemRow("2. Chicken Biryani", "198 sold", new Color(59, 130, 246)));
        bestSellingPanel.add(Box.createVerticalStrut(8));
        bestSellingPanel.add(createBestSellingItemRow("3. Masala Dosa", "156 sold", new Color(245, 158, 11)));

        JPanel healthPanel = new JPanel();
        healthPanel.setLayout(new BoxLayout(healthPanel, BoxLayout.Y_AXIS));
        healthPanel.setBackground(Color.WHITE);
        healthPanel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(borderLight, 1, true), new EmptyBorder(15, 20, 15, 20)));
        
        JLabel hTitle = new JLabel("Kitchen Health & Operations");
        hTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        hTitle.setForeground(textDark);
        healthPanel.add(hTitle);
        healthPanel.add(Box.createVerticalStrut(15));
        healthPanel.add(createHealthIndicator("Kitchen Operating Status", "ONLINE (Accepting Orders)", primaryGreen));
        healthPanel.add(Box.createVerticalStrut(12));
        healthPanel.add(createHealthIndicator("Database File Sync", "Active (manm_menu_data.txt)", new Color(59, 130, 246)));
        healthPanel.add(Box.createVerticalStrut(12));
        healthPanel.add(createHealthIndicator("Auto-Refresh Orders", "Active (Every 3 sec)", primaryGreen));

        middleRow.add(bestSellingPanel);
        middleRow.add(healthPanel);

        content.add(middleRow);
        content.add(Box.createVerticalStrut(20));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        dashPanel.add(scroll, BorderLayout.CENTER);
        return dashPanel;
    }

    private JPanel createBarGraphRow(String day, int percentage, String valueStr) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(1200, 25));

        JLabel dayLbl = new JLabel(day);
        dayLbl.setPreferredSize(new Dimension(90, 20));
        dayLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dayLbl.setForeground(textMuted);

        JPanel barContainer = new JPanel(new BorderLayout());
        barContainer.setBackground(borderLight);
        JPanel fillBar = new JPanel();
        fillBar.setBackground(primaryGreen);
        fillBar.setPreferredSize(new Dimension(percentage * 5, 20));
        barContainer.add(fillBar, BorderLayout.WEST);

        JLabel valLbl = new JLabel(valueStr);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        valLbl.setForeground(textDark);

        row.add(dayLbl, BorderLayout.WEST);
        row.add(barContainer, BorderLayout.CENTER);
        row.add(valLbl, BorderLayout.EAST);
        return row;
    }

    private JPanel createDashboardCard(String title, String mainValue, String subText, Color headerColor, Color bg) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(new LineBorder(borderLight, 1, true), new EmptyBorder(15, 20, 15, 20)));
        
        JLabel tLbl = new JLabel(title);
        tLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tLbl.setForeground(textMuted);
        
        JLabel vLbl = new JLabel(mainValue);
        vLbl.setFont(new Font("Segoe UI", Font.BOLD, 24));
        vLbl.setForeground(textDark);
        
        JLabel sLbl = new JLabel(subText);
        sLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sLbl.setForeground(headerColor);

        card.add(tLbl);
        card.add(Box.createVerticalStrut(5));
        card.add(vLbl);
        card.add(Box.createVerticalStrut(8));
        card.add(sLbl);
        return card;
    }

    private JPanel createBestSellingItemRow(String itemName, String countText, Color barColor) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(600, 35));
        
        JLabel nameLbl = new JLabel(itemName);
        nameLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nameLbl.setForeground(textDark);
        
        JLabel countLbl = new JLabel(countText);
        countLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        countLbl.setForeground(barColor);
        
        row.add(nameLbl, BorderLayout.WEST);
        row.add(countLbl, BorderLayout.EAST);
        return row;
    }

    private JPanel createHealthIndicator(String labelText, String statusText, Color statusColor) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(600, 30));
        
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(textMuted);
        
        JLabel stat = new JLabel(statusText);
        stat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        stat.setForeground(statusColor);
        
        row.add(lbl, BorderLayout.WEST);
        row.add(stat, BorderLayout.EAST);
        return row;
    }

    // ==========================================
    // 2. INVENTORY SCREEN
    // ==========================================
    private JPanel createInventoryScreen() {
        JPanel invPanel = new JPanel(new BorderLayout());
        invPanel.setBackground(bgMain);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderLight), new EmptyBorder(15, 30, 15, 30)));
        JLabel headerTitle = new JLabel("Kitchen Inventory & Stock Management");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 22)); headerTitle.setForeground(textDark);
        headerPanel.add(headerTitle, BorderLayout.WEST);

        JButton addStockBtn = new JButton("+ Add New Ingredient");
        addStockBtn.setBackground(primaryGreen);
        addStockBtn.setForeground(Color.WHITE);
        addStockBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addStockBtn.setFocusPainted(false);
        addStockBtn.addActionListener(e -> openAddInventoryDialog());
        headerPanel.add(addStockBtn, BorderLayout.EAST);

        invPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(bgMain);
        content.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel infoLbl = new JLabel("Real-time kitchen raw material stock tracking with photo references.");
        infoLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLbl.setForeground(textMuted);
        infoLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(infoLbl);
        content.add(Box.createVerticalStrut(20));

        content.add(inventoryGridPanel);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        invPanel.add(scroll, BorderLayout.CENTER);

        return invPanel;
    }

    private void loadDefaultInventory() {
        addInventoryCardToGrid("Tomatoes", "2 kg remaining", "Critical (Low stock)", new Color(239, 68, 68), "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=320");
        addInventoryCardToGrid("Mozzarella Cheese", "1.5 kg remaining", "Critical (Low stock)", new Color(239, 68, 68), "https://images.unsplash.com/photo-1452195100486-9cc805987862?w=320");
        addInventoryCardToGrid("Basmati Rice", "25 kg remaining", "Optimal Stock", primaryGreen, "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=320");
        addInventoryCardToGrid("Boneless Chicken", "12 kg remaining", "Optimal Stock", primaryGreen, "https://images.unsplash.com/photo-1604503468506-a8da13d82791?w=320");
        addInventoryCardToGrid("Fresh Paneer", "8 kg remaining", "Optimal Stock", primaryGreen, "https://images.unsplash.com/photo-1631379578550-7038263db699?w=320");
        addInventoryCardToGrid("Cooking Oil", "18 Liters remaining", "Optimal Stock", primaryGreen, "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=320");
    }

    private void addInventoryCardToGrid(String itemName, String qtyText, String statusText, Color statusColor, String imgUrl) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(new LineBorder(borderLight, 1, true), new EmptyBorder(15, 15, 15, 15)));
        card.setMaximumSize(new Dimension(400, 200));

        JLabel imgLabel = new JLabel("Loading...");
        imgLabel.setPreferredSize(new Dimension(0, 100));
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imgLabel.setOpaque(true);
        imgLabel.setBackground(borderLight);

        if(imgUrl != null && !imgUrl.isEmpty()) {
            new Thread(() -> {
                try {
                    Image img = null;
                    if(imgUrl.startsWith("http")) img = ImageIO.read(new URL(imgUrl));
                    else if(new File(imgUrl).exists()) img = new ImageIcon(imgUrl).getImage();
                    
                    if(img != null) {
                        Image scaled = img.getScaledInstance(350, -1, Image.SCALE_SMOOTH);
                        SwingUtilities.invokeLater(() -> { imgLabel.setIcon(new ImageIcon(scaled)); imgLabel.setText(""); });
                    }
                } catch (Exception e) { SwingUtilities.invokeLater(() -> imgLabel.setText("📦 " + itemName)); }
            }).start();
        } else {
            imgLabel.setText("📦 " + itemName);
        }

        JPanel details = new JPanel();
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
        details.setBackground(Color.WHITE);
        details.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(Color.WHITE);
        JLabel title = new JLabel(itemName);
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(textDark);
        
        JLabel badge = new JLabel(statusText);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(statusColor);
        topRow.add(title, BorderLayout.WEST);
        topRow.add(badge, BorderLayout.EAST);

        JLabel qtyLbl = new JLabel(qtyText);
        qtyLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        qtyLbl.setForeground(textMuted);

        details.add(topRow);
        details.add(Box.createVerticalStrut(5));
        details.add(qtyLbl);

        card.add(imgLabel, BorderLayout.NORTH);
        card.add(details, BorderLayout.CENTER);
        
        inventoryGridPanel.add(card);
    }

    private void openAddInventoryDialog() {
        JDialog dialog = new JDialog(this, "Add New Ingredient", true);
        dialog.setSize(450, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(4, 1, 10, 10));
        form.setBorder(new EmptyBorder(20, 20, 20, 20));
        form.setBackground(Color.WHITE);

        JTextField nameField = new JTextField();
        nameField.setBorder(BorderFactory.createTitledBorder("Ingredient Name"));

        JTextField qtyField = new JTextField();
        qtyField.setBorder(BorderFactory.createTitledBorder("Quantity (e.g. 5 kg remaining)"));

        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Optimal Stock", "Critical (Low stock)"});
        statusBox.setBorder(BorderFactory.createTitledBorder("Stock Status"));

        JTextField imgField = new JTextField();
        imgField.setBorder(BorderFactory.createTitledBorder("Image Web URL or Local File Path"));

        JButton saveBtn = new JButton("Save Ingredient");
        saveBtn.setBackground(primaryGreen);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        saveBtn.addActionListener(e -> {
            if(nameField.getText().isEmpty() || qtyField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill ingredient name and quantity!");
                return;
            }
            String status = statusBox.getSelectedItem().toString();
            Color statColor = status.equals("Optimal Stock") ? primaryGreen : new Color(239, 68, 68);
            
            addInventoryCardToGrid(nameField.getText(), qtyField.getText(), status, statColor, imgField.getText());
            inventoryGridPanel.revalidate();
            inventoryGridPanel.repaint();
            dialog.dispose();
        });

        form.add(nameField);
        form.add(qtyField);
        form.add(statusBox);
        form.add(imgField);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(saveBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ==========================================
    // 3. REPORTS & ANALYTICS SCREEN
    // ==========================================
    private JPanel createReportsScreen() {
        JPanel repPanel = new JPanel(new BorderLayout());
        repPanel.setBackground(bgMain);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderLight), new EmptyBorder(15, 30, 15, 30)));
        JLabel headerTitle = new JLabel("Reports & Financial Analytics Diagrams");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 22)); headerTitle.setForeground(textDark);
        headerPanel.add(headerTitle, BorderLayout.WEST);
        repPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(bgMain);
        content.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Monthly Financial & Category Breakdown");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(textDark);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(20));

        JPanel statsGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(1300, 130));
        statsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        statsGrid.add(createDashboardCard("Gross Revenue", "Rs. 1,45,200", "+14% from last month", primaryGreen, Color.WHITE));
        statsGrid.add(createDashboardCard("Total Expenses", "Rs. 48,900", "Ingredients & Operations", new Color(239, 68, 68), Color.WHITE));
        statsGrid.add(createDashboardCard("Net Profit", "Rs. 96,300", "Profit Margin: 66.3%", new Color(59, 130, 246), Color.WHITE));

        content.add(statsGrid);
        content.add(Box.createVerticalStrut(30));

        JPanel categoryChart = new JPanel();
        categoryChart.setLayout(new BoxLayout(categoryChart, BoxLayout.Y_AXIS));
        categoryChart.setBackground(Color.WHITE);
        categoryChart.setBorder(BorderFactory.createCompoundBorder(new LineBorder(borderLight, 1, true), new EmptyBorder(20, 20, 20, 20)));
        categoryChart.setMaximumSize(new Dimension(1300, 220));
        categoryChart.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel catTitle = new JLabel("Sales Distribution by Food Category");
        catTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        catTitle.setForeground(textDark);
        categoryChart.add(catTitle);
        categoryChart.add(Box.createVerticalStrut(15));

        categoryChart.add(createBarGraphRow("North Indian", 85, "Rs. 62,000 (43%)"));
        categoryChart.add(Box.createVerticalStrut(8));
        categoryChart.add(createBarGraphRow("South Indian", 60, "Rs. 41,500 (28%)"));
        categoryChart.add(Box.createVerticalStrut(8));
        categoryChart.add(createBarGraphRow("Chinese", 40, "Rs. 25,700 (18%)"));
        categoryChart.add(Box.createVerticalStrut(8));
        categoryChart.add(createBarGraphRow("Desserts", 25, "Rs. 16,000 (11%)"));

        content.add(categoryChart);
        content.add(Box.createVerticalStrut(25));

        JButton exportBtn = new JButton("📥 Export Comprehensive PDF Report");
        exportBtn.setBackground(textDark);
        exportBtn.setForeground(Color.WHITE);
        exportBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        exportBtn.setFocusPainted(false);
        exportBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        exportBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Report exported successfully as PDF!"));
        content.add(exportBtn);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        repPanel.add(scroll, BorderLayout.CENTER);

        return repPanel;
    }

    // ==========================================
    // 4. KITCHEN PROFILE MODULE
    // ==========================================
    private JPanel createKitchenProfileScreen() {
        JPanel profilePanel = new JPanel(new BorderLayout());
        profilePanel.setBackground(bgMain);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setPreferredSize(new Dimension(0, 70));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderLight), new EmptyBorder(15, 30, 15, 30)));
        JLabel headerTitle = new JLabel("Kitchen Profile & Active Orders");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 22)); headerTitle.setForeground(textDark);
        headerPanel.add(headerTitle, BorderLayout.WEST);
        
        profilePanel.add(headerPanel, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(bgMain);
        content.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel infoCard = new JPanel(new GridLayout(2, 2, 20, 20));
        infoCard.setBackground(Color.WHITE);
        infoCard.setBorder(BorderFactory.createCompoundBorder(new LineBorder(borderLight, 1, true), new EmptyBorder(25, 25, 25, 25)));
        infoCard.setMaximumSize(new Dimension(1200, 150));
        infoCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoCard.add(createProfileDetail("Kitchen Name", "Manam Cloud Kitchen"));
        infoCard.add(createProfileDetail("Manager Title", "Kitchen Manager"));
        infoCard.add(createProfileDetail("Operating Hours", "07:00 AM - 11:00 PM"));
        infoCard.add(createProfileDetail("Location", "Anna Nagar, Trichy"));
        
        content.add(infoCard);
        content.add(Box.createVerticalStrut(20));

        JPanel opsCard = new JPanel(new GridLayout(1, 2, 20, 0));
        opsCard.setOpaque(false);
        opsCard.setMaximumSize(new Dimension(1200, 140));
        opsCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel statusBox = new JPanel(); statusBox.setLayout(new BoxLayout(statusBox, BoxLayout.Y_AXIS)); statusBox.setBackground(Color.WHITE); statusBox.setBorder(BorderFactory.createCompoundBorder(new LineBorder(borderLight, 1, true), new EmptyBorder(20, 20, 20, 20)));
        JLabel stTitle = new JLabel("Kitchen Status"); stTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel stDesc = new JLabel("Accepting new orders"); stDesc.setForeground(textMuted);
        JToggleButton toggleBtn = new JToggleButton("ONLINE"); toggleBtn.setBackground(primaryGreen); toggleBtn.setForeground(Color.WHITE); toggleBtn.setFont(new Font("Segoe UI", Font.BOLD, 14)); toggleBtn.setFocusPainted(false);
        toggleBtn.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) { toggleBtn.setText("OFFLINE"); toggleBtn.setBackground(new Color(239, 68, 68)); stDesc.setText("Not accepting orders"); } 
            else { toggleBtn.setText("ONLINE"); toggleBtn.setBackground(primaryGreen); stDesc.setText("Accepting new orders"); }
        });
        statusBox.add(stTitle); statusBox.add(Box.createVerticalStrut(5)); statusBox.add(stDesc); statusBox.add(Box.createVerticalStrut(15)); statusBox.add(toggleBtn);

        JPanel ordersBox = new JPanel(); ordersBox.setLayout(new BoxLayout(ordersBox, BoxLayout.Y_AXIS)); ordersBox.setBackground(Color.WHITE); ordersBox.setBorder(BorderFactory.createCompoundBorder(new LineBorder(borderLight, 1, true), new EmptyBorder(20, 20, 20, 20)));
        JLabel ordTitle = new JLabel("Total Earnings"); ordTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalEarningsLabel = new JLabel("Rs. 0"); totalEarningsLabel.setFont(new Font("Segoe UI", Font.BOLD, 28)); totalEarningsLabel.setForeground(primaryGreen);
        JButton viewOrdersBtn = new JButton("View Order History"); viewOrdersBtn.setBackground(new Color(30, 41, 59)); viewOrdersBtn.setForeground(Color.WHITE); viewOrdersBtn.setFocusPainted(false); viewOrdersBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        viewOrdersBtn.addActionListener(e -> openOrdersDialog());
        
        ordersBox.add(ordTitle); ordersBox.add(Box.createVerticalStrut(5)); ordersBox.add(totalEarningsLabel); ordersBox.add(Box.createVerticalStrut(15)); ordersBox.add(viewOrdersBtn);

        opsCard.add(statusBox); opsCard.add(ordersBox);
        content.add(opsCard);
        content.add(Box.createVerticalStrut(30));

        JLabel activeTitle = new JLabel("Live Kitchen Orders (Pending)");
        activeTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        activeTitle.setForeground(textDark);
        activeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(activeTitle);
        content.add(Box.createVerticalStrut(15));

        activeOrdersContainer = new JPanel();
        activeOrdersContainer.setLayout(new BoxLayout(activeOrdersContainer, BoxLayout.Y_AXIS));
        activeOrdersContainer.setBackground(bgMain);
        activeOrdersContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        refreshActiveOrdersUI(); 
        content.add(activeOrdersContainer);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        profilePanel.add(scrollPane, BorderLayout.CENTER);

        return profilePanel;
    }

    private JPanel createProfileDetail(String label, String value) {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); p.setBackground(Color.WHITE);
        JLabel l = new JLabel(label); l.setFont(new Font("Segoe UI", Font.PLAIN, 12)); l.setForeground(textMuted);
        JLabel v = new JLabel(value); v.setFont(new Font("Segoe UI", Font.BOLD, 16)); v.setForeground(textDark);
        p.add(l); p.add(Box.createVerticalStrut(5)); p.add(v);
        return p;
    }

    private void refreshActiveOrdersUI() {
        if(activeOrdersContainer == null) return;
        activeOrdersContainer.removeAll();
        
        File file = new File(ORDERS_FILE);
        boolean hasPending = false;
        
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while((line = br.readLine()) != null) {
                    String[] data = line.split("~");
                    if (data.length >= 4 && data[3].equalsIgnoreCase("Pending")) {
                        hasPending = true;
                        activeOrdersContainer.add(createActiveOrderCard(data[0], data[1], data[2]));
                        activeOrdersContainer.add(Box.createVerticalStrut(15));
                    }
                }
            } catch (Exception e) {}
        }
        
        if(!hasPending) {
            JLabel emptyLbl = new JLabel("No active orders right now. Kitchen is clear! 🧹");
            emptyLbl.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLbl.setForeground(textMuted);
            activeOrdersContainer.add(emptyLbl);
        }
        
        activeOrdersContainer.revalidate();
        activeOrdersContainer.repaint();
    }

    private JPanel createActiveOrderCard(String orderId, String items, String total) {
        JPanel card = new JPanel(new BorderLayout(20, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(new LineBorder(borderLight, 1, true), new EmptyBorder(15, 15, 15, 15)));
        card.setMaximumSize(new Dimension(1200, 100));
        
        String matchedImgPath = findImagePathForFood(items);

        JLabel imgLabel = new JLabel();
        imgLabel.setPreferredSize(new Dimension(80, 80));
        imgLabel.setOpaque(true); 
        imgLabel.setBackground(borderLight);
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        new Thread(() -> {
            try {
                Image img = null;
                if(matchedImgPath.startsWith("http")) { img = ImageIO.read(new URL(matchedImgPath)); } 
                else if(new File(matchedImgPath).exists()) { img = new ImageIcon(matchedImgPath).getImage(); }
                if(img != null) {
                    Image scaled = img.getScaledInstance(80, -1, Image.SCALE_SMOOTH);
                    SwingUtilities.invokeLater(() -> { imgLabel.setIcon(new ImageIcon(scaled)); });
                }
            } catch (Exception e) { SwingUtilities.invokeLater(() -> imgLabel.setText("Img")); }
        }).start();

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        
        JLabel idLbl = new JLabel("Order: " + orderId); idLbl.setFont(new Font("Segoe UI", Font.BOLD, 16)); idLbl.setForeground(textDark);
        JLabel itemsLbl = new JLabel("Items: " + items); itemsLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14)); itemsLbl.setForeground(textMuted);
        JLabel totalLbl = new JLabel("Total: " + total); totalLbl.setFont(new Font("Segoe UI", Font.BOLD, 14)); totalLbl.setForeground(primaryGreen);

        infoPanel.add(idLbl); infoPanel.add(Box.createVerticalStrut(5)); infoPanel.add(itemsLbl); infoPanel.add(Box.createVerticalStrut(5)); infoPanel.add(totalLbl);

        JButton deliverBtn = new JButton("Mark as Delivered ✅");
        deliverBtn.setBackground(primaryGreen); deliverBtn.setForeground(Color.WHITE);
        deliverBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deliverBtn.setFocusPainted(false);
        deliverBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        deliverBtn.addActionListener(e -> {
            updateOrderStatusInFile(orderId, "Delivered");
            refreshActiveOrdersUI(); 
        });

        card.add(imgLabel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(deliverBtn, BorderLayout.EAST);
        
        return card;
    }

    private String findImagePathForFood(String orderItemsStr) {
        String searchFood = "";
        try {
            String firstPart = orderItemsStr.split(",")[0]; 
            searchFood = firstPart.split("x ")[1].trim().toLowerCase();
        } catch (Exception e) {}

        for (Component c : foodGridPanel.getComponents()) {
            if (c instanceof JPanel) {
                JPanel menuCard = (JPanel) c;
                String title = ((String) menuCard.getClientProperty("title")).toLowerCase();
                if (title.contains(searchFood) || searchFood.contains(title)) {
                    return (String) menuCard.getClientProperty("imgPath");
                }
            }
        }
        return "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6d/Good_Food_Display_-_NCI_Visuals_Online.jpg/320px-Good_Food_Display_-_NCI_Visuals_Online.jpg";
    }

    private void updateOrderStatusInFile(String targetOrderId, String newStatus) {
        File file = new File(ORDERS_FILE);
        if (!file.exists()) return;
        List<String> updatedLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = br.readLine()) != null) {
                if (line.startsWith(targetOrderId)) {
                    String[] data = line.split("~");
                    if(data.length >= 4) {
                        line = data[0] + "~" + data[1] + "~" + data[2] + "~" + newStatus;
                    }
                }
                updatedLines.add(line);
            }
        } catch (Exception e) {}

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for(String l : updatedLines) pw.println(l);
        } catch (Exception e) {}
    }

    private void openOrdersDialog() {
        JDialog dialog = new JDialog(this, "Order History & Management", true);
        dialog.setSize(650, 600); dialog.setLocationRelativeTo(this); dialog.setLayout(new BorderLayout());

        JPanel header = new JPanel(); header.setBackground(Color.WHITE); header.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel title = new JLabel("All Placed Orders"); title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.add(title); dialog.add(header, BorderLayout.NORTH);

        JPanel ordersListPanel = new JPanel(); ordersListPanel.setLayout(new BoxLayout(ordersListPanel, BoxLayout.Y_AXIS));
        ordersListPanel.setBackground(bgMain); ordersListPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        File file = new File(ORDERS_FILE);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                List<String> orderLines = new ArrayList<>();
                String line;
                while((line = br.readLine()) != null) orderLines.add(line);
                
                if (orderLines.isEmpty()) {
                    ordersListPanel.add(new JLabel("No orders received yet."));
                } else {
                    for (int i = orderLines.size() - 1; i >= 0; i--) {
                        String[] data = orderLines.get(i).split("~");
                        if(data.length >= 3) {
                            JPanel row = new JPanel(new BorderLayout()); row.setBackground(Color.WHITE);
                            row.setBorder(BorderFactory.createCompoundBorder(new LineBorder(borderLight, 1), new EmptyBorder(15, 15, 15, 15)));
                            row.setMaximumSize(new Dimension(800, 80));
                            
                            JPanel textCol = new JPanel(); textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS)); textCol.setBackground(Color.WHITE);
                            JLabel idLbl = new JLabel(data[0]); idLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                            JLabel itemsLbl = new JLabel(data[1]); itemsLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12)); itemsLbl.setForeground(textMuted);
                            textCol.add(idLbl); textCol.add(Box.createVerticalStrut(5)); textCol.add(itemsLbl);
                            
                            String stat = data.length >= 4 ? data[3] : "Delivered";
                            JLabel priceLbl = new JLabel("<html>" + data[2] + "<br><font size='3' color='" + (stat.equals("Pending") ? "orange" : "green") + "'>" + stat + "</font></html>");
                            priceLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                            
                            row.add(textCol, BorderLayout.CENTER); row.add(priceLbl, BorderLayout.EAST);
                            ordersListPanel.add(row); ordersListPanel.add(Box.createVerticalStrut(10));
                        }
                    }
                }
            } catch (Exception e) {}
        }

        JScrollPane scroll = new JScrollPane(ordersListPanel); scroll.setBorder(null); dialog.add(scroll, BorderLayout.CENTER);
        JButton closeBtn = new JButton("Close"); closeBtn.setBackground(new Color(30,41,59)); closeBtn.setForeground(Color.WHITE); closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel footer = new JPanel(); footer.add(closeBtn); footer.setBackground(Color.WHITE); dialog.add(footer, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ==========================================
    // NOTIFICATIONS & ORDERS LOGIC
    // ==========================================
    private void checkForNewOrders() {
        File file = new File(ORDERS_FILE);
        if (!file.exists()) return;
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            List<String> orders = new ArrayList<>();
            String line;
            while((line = br.readLine()) != null) orders.add(line);
            
            if (orders.size() > lastOrderCount) {
                if (lastOrderCount != 0) {
                    String lastOrderStr = orders.get(orders.size() - 1);
                    String[] data = lastOrderStr.split("~");
                    if(data.length >= 3) showRichImagePopup(data[0], data[1], data[2]); 
                }
                lastOrderCount = orders.size();
                updateNotificationsUI(orders);
                updateTotalEarnings(orders);
                refreshActiveOrdersUI();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void showRichImagePopup(String orderId, String items, String total) {
        JWindow popup = new JWindow(this);
        JPanel p = new JPanel(new BorderLayout(15, 0));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(new LineBorder(primaryGreen, 2, true), new EmptyBorder(15, 15, 15, 15)));

        JLabel imgLabel = new JLabel("Loading...");
        imgLabel.setPreferredSize(new Dimension(80, 80));
        imgLabel.setOpaque(true); imgLabel.setBackground(borderLight); imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel textPanel = new JPanel(); textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS)); textPanel.setBackground(Color.WHITE);
        JLabel title = new JLabel("🔔 NEW MANAGEMENT ALERT!"); title.setFont(new Font("Segoe UI", Font.BOLD, 16)); title.setForeground(primaryGreen);
        JLabel idLabel = new JLabel("Order No: " + orderId); idLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel amountLabel = new JLabel("Total: " + total); amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14)); amountLabel.setForeground(new Color(239, 68, 68)); 

        textPanel.add(title); textPanel.add(Box.createVerticalStrut(5)); textPanel.add(idLabel); textPanel.add(Box.createVerticalStrut(5)); textPanel.add(amountLabel);
        p.add(imgLabel, BorderLayout.WEST); p.add(textPanel, BorderLayout.CENTER);
        popup.add(p); popup.pack();
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        popup.setLocation(screenSize.width - popup.getWidth() - 30, 80); popup.setVisible(true);
        new Thread(() -> { try { Thread.sleep(6000); } catch (Exception e) {} SwingUtilities.invokeLater(popup::dispose); }).start();
    }

    private void updateNotificationsUI(List<String> orders) {
        if (notificationsContainer == null) return;
        notificationsContainer.removeAll();
        notificationsContainer.add(createNotification("Low stock alert", "Tomatoes - 2 kg remaining", new Color(254, 226, 226)));
        notificationsContainer.revalidate(); notificationsContainer.repaint();
    }

    private void updateTotalEarnings(List<String> orders) {
        int totalRs = 0;
        for (String order : orders) {
            String[] data = order.split("~");
            if(data.length >= 3) {
                String amtStr = data[2].replaceAll("[^0-9]", "");
                if(!amtStr.isEmpty()) totalRs += Integer.parseInt(amtStr);
            }
        }
        if(totalEarningsLabel != null) totalEarningsLabel.setText("Rs. " + totalRs);
    }

    // ==========================================
    // MENU MANAGEMENT SCREEN 
    // ==========================================
    private JPanel createMenuManagementScreen() {
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(bgMain);
        mainArea.add(createTopNavBar(), BorderLayout.NORTH);

        JPanel dashboardScrollContent = new JPanel();
        dashboardScrollContent.setLayout(new BoxLayout(dashboardScrollContent, BoxLayout.Y_AXIS));
        dashboardScrollContent.setBackground(bgMain);
        dashboardScrollContent.setBorder(new EmptyBorder(20, 30, 30, 30));

        dashboardScrollContent.add(createDashboardHeader());
        dashboardScrollContent.add(Box.createVerticalStrut(20));
        dashboardScrollContent.add(createStatsRow());
        dashboardScrollContent.add(Box.createVerticalStrut(20));
        dashboardScrollContent.add(createFilterRow());
        dashboardScrollContent.add(Box.createVerticalStrut(20));
        dashboardScrollContent.add(foodGridPanel);

        JScrollPane scrollPane = new JScrollPane(dashboardScrollContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainArea.add(scrollPane, BorderLayout.CENTER);

        mainArea.add(createRightPanel(), BorderLayout.EAST);
        return mainArea;
    }

    private void loadMenuData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            addCardToGrid("Paneer Butter Masala", "North Indian", "Veg", "220", "", new Color(230, 80, 50), "Available");
            addCardToGrid("Chicken Biryani", "North Indian", "Non-Veg", "260", "", new Color(180, 100, 50), "Available");
            addCardToGrid("Masala Dosa", "South Indian", "Veg", "120", "", new Color(220, 180, 100), "Available");
            saveMenuData();
        } else {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split("~"); 
                    if (data.length >= 6) {
                        addCardToGrid(data[0], data[1], data[2], data[3], data[4], new Color(100, 116, 139), data[5]);
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
        updateStats();
    }

    private void saveMenuData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (Component c : foodGridPanel.getComponents()) {
                if (c instanceof JPanel) {
                    JPanel card = (JPanel) c;
                    String title = (String) card.getClientProperty("title");
                    String category = (String) card.getClientProperty("category");
                    String diet = (String) card.getClientProperty("diet");
                    String price = (String) card.getClientProperty("price");
                    String imgPath = (String) card.getClientProperty("imgPath");
                    String status = (String) card.getClientProperty("status");
                    writer.println(title + "~" + category + "~" + diet + "~" + price + "~" + imgPath + "~" + status);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ==========================================
    // SIDEBAR & NAVIGATION BUILDERS
    // ==========================================
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(sidebarBg);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        JPanel topContent = new JPanel();
        topContent.setLayout(new BoxLayout(topContent, BoxLayout.Y_AXIS));
        topContent.setBackground(sidebarBg);

        JLabel brand = new JLabel("MANAM");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 24)); brand.setForeground(Color.WHITE);
        JLabel subBrand = new JLabel("Cloud Kitchen Manager");
        subBrand.setFont(new Font("Segoe UI", Font.PLAIN, 12)); subBrand.setForeground(new Color(148, 163, 184));

        topContent.add(brand); topContent.add(subBrand); topContent.add(Box.createVerticalStrut(40));
        
        JPanel dashLink = createNavLink("Dashboard", true);
        dashLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { setActiveSidebar(dashLink); cardLayout.show(mainContentPanel, "DASHBOARD_SCREEN"); }
        });
        topContent.add(dashLink);
        
        JPanel profileLink = createNavLink("Kitchen Profile", false);
        profileLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { setActiveSidebar(profileLink); cardLayout.show(mainContentPanel, "PROFILE_SCREEN"); }
        });
        topContent.add(profileLink);
        
        JPanel menuLink = createNavLink("Menu Management", false);
        menuLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { setActiveSidebar(menuLink); cardLayout.show(mainContentPanel, "MENU_SCREEN"); }
        });
        topContent.add(menuLink);
        
        JPanel inventoryLink = createNavLink("Inventory & Stock", false);
        inventoryLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { setActiveSidebar(inventoryLink); cardLayout.show(mainContentPanel, "INVENTORY_SCREEN"); }
        });
        topContent.add(inventoryLink);
        
        JPanel ordersNav = createNavLink("Live Orders (POS)", false);
        ordersNav.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { setActiveSidebar(ordersNav); cardLayout.show(mainContentPanel, "PROFILE_SCREEN"); }
        });
        topContent.add(ordersNav);
        
        JPanel reportsLink = createNavLink("Reports & Analytics", false);
        reportsLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { setActiveSidebar(reportsLink); cardLayout.show(mainContentPanel, "REPORTS_SCREEN"); }
        });
        topContent.add(reportsLink);
        
        sidebar.add(topContent, BorderLayout.NORTH);

        JPanel quotePanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g); Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 41, 59)); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        quotePanel.setOpaque(false); quotePanel.setLayout(new BoxLayout(quotePanel, BoxLayout.Y_AXIS)); quotePanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel qTitle = new JLabel("Good Food, 🍽️"); qTitle.setForeground(Color.WHITE); qTitle.setFont(new Font("Georgia", Font.ITALIC, 16));
        JLabel qTitle2 = new JLabel("Great Business. 🚀"); qTitle2.setForeground(Color.WHITE); qTitle2.setFont(new Font("Georgia", Font.BOLD, 16));
        quotePanel.add(qTitle); quotePanel.add(qTitle2);
        sidebar.add(quotePanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel createNavLink(String text, boolean isActive) {
        JPanel p = new JPanel(new BorderLayout());
        p.setMaximumSize(new Dimension(220, 40));
        p.setBackground(isActive ? new Color(30, 41, 59) : sidebarBg);
        p.setBorder(new EmptyBorder(10, 15, 10, 15));
        p.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", isActive ? Font.BOLD : Font.PLAIN, 14));
        l.setForeground(isActive ? Color.WHITE : new Color(203, 213, 225));
        
        p.putClientProperty("label", l);
        p.add(l, BorderLayout.CENTER);
        sidebarLinks.add(p);
        return p;
    }

    private void setActiveSidebar(JPanel activePanel) {
        for(JPanel p : sidebarLinks) {
            p.setBackground(sidebarBg);
            JLabel l = (JLabel) p.getClientProperty("label");
            if(l != null) { l.setFont(new Font("Segoe UI", Font.PLAIN, 14)); l.setForeground(new Color(203, 213, 225)); }
        }
        activePanel.setBackground(new Color(30, 41, 59));
        JLabel activeLbl = (JLabel) activePanel.getClientProperty("label");
        if(activeLbl != null) { activeLbl.setFont(new Font("Segoe UI", Font.BOLD, 14)); activeLbl.setForeground(Color.WHITE); }
    }

    private JPanel createTopNavBar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(Color.WHITE);
        nav.setPreferredSize(new Dimension(0, 60));
        nav.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderLight), new EmptyBorder(10, 20, 10, 20)));

        JTextField searchBox = new JTextField(" Search menu items...");
        searchBox.setPreferredSize(new Dimension(300, 35));
        searchBox.setBackground(new Color(241, 245, 249));
        searchBox.setBorder(new LineBorder(borderLight, 1, true));
        
        searchBox.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { if(searchBox.getText().trim().equals("Search menu items...")) searchBox.setText(""); }
            public void focusLost(FocusEvent e) { if(searchBox.getText().trim().isEmpty()) searchBox.setText(" Search menu items..."); }
        });

        searchBox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String query = searchBox.getText().trim().toLowerCase();
                if (query.equals("search menu items...")) query = ""; 
                for (Component c : foodGridPanel.getComponents()) {
                    if (c instanceof JPanel) {
                        JPanel card = (JPanel) c;
                        String title = ((String) card.getClientProperty("title")).toLowerCase();
                        String category = ((String) card.getClientProperty("category")).toLowerCase();
                        if (title.contains(query) || category.contains(query) || query.isEmpty()) { card.setVisible(true); } 
                        else { card.setVisible(false); }
                    }
                }
                foodGridPanel.revalidate(); foodGridPanel.repaint();
            }
        });
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); leftPanel.setOpaque(false);
        leftPanel.add(new JLabel("Menu   ")); leftPanel.add(searchBox);
        JLabel profile = new JLabel("Kitchen Manager "); profile.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nav.add(leftPanel, BorderLayout.WEST); nav.add(profile, BorderLayout.EAST);
        return nav;
    }

    private JPanel createDashboardHeader() {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); p.setOpaque(false); p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel topTitles = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); topTitles.setOpaque(false);
        JPanel titleCol = new JPanel(); titleCol.setLayout(new BoxLayout(titleCol, BoxLayout.Y_AXIS)); titleCol.setOpaque(false);
        JLabel title = new JLabel("Welcome back, Kitchen Manager!"); title.setFont(new Font("Segoe UI", Font.BOLD, 28)); title.setForeground(textDark);
        JLabel sub = new JLabel("Here's an overview of your cloud kitchen performance today."); sub.setFont(new Font("Segoe UI", Font.PLAIN, 14)); sub.setForeground(textMuted);
        titleCol.add(title); titleCol.add(Box.createVerticalStrut(5)); titleCol.add(sub);
        topTitles.add(titleCol); topTitles.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(topTitles); p.add(Box.createVerticalStrut(20)); 

        JPanel quoteBanner = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 175, 123), getWidth(), getHeight(), new Color(215, 110, 165));
                g2.setPaint(gp); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        quoteBanner.setOpaque(false); quoteBanner.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15)); quoteBanner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60)); quoteBanner.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel qBannerText = new JLabel("\"Happiness is homemade, and it starts in the kitchen\""); qBannerText.setFont(new Font("Georgia", Font.BOLD | Font.ITALIC, 18)); qBannerText.setForeground(Color.WHITE);
        quoteBanner.add(qBannerText); p.add(quoteBanner);
        return p;
    }

    private JPanel createStatsRow() {
        JPanel p = new JPanel(new GridLayout(1, 4, 15, 0)); p.setOpaque(false); p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel totalItemsCard = createStatCard("Total Menu Items", "0", new Color(209, 250, 229), new Color(6, 95, 70));
        totalItemsValLabel = (JLabel) totalItemsCard.getClientProperty("valLabel");
        JPanel availableItemsCard = createStatCard("Available Items", "0", new Color(224, 242, 254), new Color(7, 89, 133));
        availableItemsValLabel = (JLabel) availableItemsCard.getClientProperty("valLabel");

        p.add(totalItemsCard); p.add(availableItemsCard);
        p.add(createStatCard("Avg. Rating", "4.7/5", new Color(254, 243, 199), new Color(146, 64, 14)));
        p.add(createStatCard("Total Revenue", "Rs. 24,560", new Color(252, 231, 243), new Color(157, 23, 77)));
        return p;
    }

    private JPanel createStatCard(String title, String val, Color bg, Color textC) {
        JPanel card = new JPanel(); card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS)); card.setBackground(bg); card.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel t = new JLabel(title); t.setFont(new Font("Segoe UI", Font.PLAIN, 13)); t.setForeground(textC);
        JLabel v = new JLabel(val); v.setFont(new Font("Segoe UI", Font.BOLD, 24)); v.setForeground(textC);
        card.add(t); card.add(Box.createVerticalStrut(5)); card.add(v);
        card.putClientProperty("valLabel", v); 
        return card;
    }

    private JPanel createFilterRow() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); p.setOpaque(false); p.setAlignmentX(Component.LEFT_ALIGNMENT);
        String[] filters = {"All Items", "South Indian", "North Indian", "Chinese", "Desserts"};
        for (int i=0; i<filters.length; i++) {
            JButton b = new JButton(filters[i]); b.setFont(new Font("Segoe UI", Font.BOLD, 12)); b.setBackground(i == 0 ? primaryGreen : Color.WHITE); b.setForeground(i == 0 ? Color.WHITE : textMuted); b.setBorder(new LineBorder(i == 0 ? primaryGreen : borderLight, 1, true)); b.setFocusPainted(false); b.setPreferredSize(new Dimension(120, 32));
            b.addActionListener(e -> {
                for(JButton btn : filterButtonsList) { btn.setBackground(Color.WHITE); btn.setForeground(textMuted); btn.setBorder(new LineBorder(borderLight, 1, true)); }
                b.setBackground(primaryGreen); b.setForeground(Color.WHITE); b.setBorder(new LineBorder(primaryGreen, 1, true));
                String filterName = b.getText();
                for(Component c : foodGridPanel.getComponents()) {
                    if (c instanceof JPanel) {
                        JPanel card = (JPanel) c; String cardCategory = (String) card.getClientProperty("category");
                        if (filterName.equals("All Items") || filterName.equals(cardCategory)) { card.setVisible(true); } else { card.setVisible(false); }
                    }
                }
            });
            filterButtonsList.add(b); p.add(b);
        }
        return p;
    }

    private void addCardToGrid(String title, String category, String diet, String price, String imgPath, Color fallbackColor, String status) {
        JPanel card = new JPanel(new BorderLayout()); card.setBackground(Color.WHITE); card.setBorder(new LineBorder(borderLight, 1, true));
        card.putClientProperty("title", title); card.putClientProperty("category", category); card.putClientProperty("diet", diet); card.putClientProperty("price", price); card.putClientProperty("imgPath", imgPath); card.putClientProperty("fallbackColor", fallbackColor); card.putClientProperty("status", status); 

        JPanel imgContainer = new JPanel(new BorderLayout()); imgContainer.setPreferredSize(new Dimension(0, 160)); imgContainer.setMaximumSize(new Dimension(400, 160)); imgContainer.setBackground(fallbackColor);
        JLabel imgLabel = new JLabel("Loading..."); imgLabel.setHorizontalAlignment(SwingConstants.CENTER); imgLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11)); imgLabel.setForeground(Color.WHITE);
        imgContainer.add(imgLabel, BorderLayout.CENTER);
        updateCardImage(imgLabel, imgPath, title);

        JPanel details = new JPanel(); details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS)); details.setBackground(Color.WHITE); details.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel catRow = new JPanel(new BorderLayout()); catRow.setBackground(Color.WHITE);
        JLabel catLabel = new JLabel(category); catLabel.setFont(new Font("Segoe UI", Font.BOLD, 11)); catLabel.setForeground(primaryGreen);
        JLabel dietLabel = new JLabel(diet.equals("Veg") ? "[Veg]" : "[Non-Veg]"); dietLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        catRow.add(catLabel, BorderLayout.WEST); catRow.add(dietLabel, BorderLayout.EAST);

        JLabel titleLabel = new JLabel(title); titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16)); titleLabel.setForeground(textDark);

        JPanel priceRow = new JPanel(new BorderLayout()); priceRow.setBackground(Color.WHITE);
        JLabel priceLabel = new JLabel("Rs. " + price); priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        JLabel stockLabel = new JLabel(status); stockLabel.setFont(new Font("Segoe UI", Font.BOLD, 12)); stockLabel.setForeground(status.equals("Available") ? primaryGreen : new Color(239, 68, 68)); stockLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        stockLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if ("Available".equals(card.getClientProperty("status"))) {
                    card.putClientProperty("status", "Unavailable"); stockLabel.setText("Unavailable"); stockLabel.setForeground(new Color(239, 68, 68)); 
                } else {
                    card.putClientProperty("status", "Available"); stockLabel.setText("Available"); stockLabel.setForeground(primaryGreen); 
                }
                updateStats(); saveMenuData(); 
            }
        });
        
        priceRow.add(priceLabel, BorderLayout.WEST); priceRow.add(stockLabel, BorderLayout.EAST);

        JPanel actionRow = new JPanel(new GridLayout(1, 2, 10, 0)); actionRow.setBackground(Color.WHITE); actionRow.setBorder(new EmptyBorder(10, 0, 0, 0));
        JButton editBtn = new JButton("Edit"); editBtn.setBackground(Color.WHITE); editBtn.setForeground(textDark); editBtn.setBorder(new LineBorder(borderLight, 1));
        JButton deleteBtn = new JButton("Delete"); deleteBtn.setBackground(Color.WHITE); deleteBtn.setForeground(new Color(239, 68, 68)); deleteBtn.setBorder(new LineBorder(new Color(254, 202, 202), 1));

        deleteBtn.addActionListener(e -> {
            if(JOptionPane.showConfirmDialog(this, "Delete this item?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                foodGridPanel.remove(card); foodGridPanel.revalidate(); foodGridPanel.repaint(); updateStats(); saveMenuData(); 
            }
        });

        editBtn.addActionListener(e -> openEditItemDialog(card, titleLabel, catLabel, dietLabel, priceLabel, imgLabel));
        actionRow.add(editBtn); actionRow.add(deleteBtn);

        details.add(catRow); details.add(Box.createVerticalStrut(5)); details.add(titleLabel); details.add(Box.createVerticalStrut(10)); details.add(priceRow); details.add(Box.createVerticalStrut(10)); details.add(actionRow);

        card.add(imgContainer, BorderLayout.NORTH); card.add(details, BorderLayout.CENTER);
        foodGridPanel.add(card);
    }

    private void updateCardImage(JLabel imgLabel, String imgPath, String defaultText) {
        if (imgPath != null && !imgPath.isEmpty()) {
            if (imgPath.startsWith("http")) { 
                new Thread(() -> {
                    try {
                        Image img = ImageIO.read(new URL(imgPath));
                        if (img != null) {
                            Image scaled = img.getScaledInstance(300, -1, Image.SCALE_SMOOTH);
                            SwingUtilities.invokeLater(() -> { imgLabel.setIcon(new ImageIcon(scaled)); imgLabel.setText(""); });
                        }
                    } catch (Exception ex) { SwingUtilities.invokeLater(() -> imgLabel.setText("Img: " + defaultText)); }
                }).start();
            } else if (new File(imgPath).exists()) {
                try {
                    ImageIcon icon = new ImageIcon(imgPath);
                    Image scaled = icon.getImage().getScaledInstance(300, -1, Image.SCALE_SMOOTH); imgLabel.setIcon(new ImageIcon(scaled)); imgLabel.setText("");
                } catch (Exception e) { imgLabel.setText("Img: " + defaultText); }
            } else { imgLabel.setText("Img: " + defaultText); imgLabel.setIcon(null); }
        } else { imgLabel.setText("Img: " + defaultText); imgLabel.setIcon(null); }
    }

    private void updateStats() {
        int total = 0; int available = 0;
        for(Component c : foodGridPanel.getComponents()) {
            if (c instanceof JPanel) {
                JPanel card = (JPanel) c; total++;
                if ("Available".equals(card.getClientProperty("status"))) { available++; }
            }
        }
        if(totalItemsValLabel != null) totalItemsValLabel.setText(String.valueOf(total));
        if(availableItemsValLabel != null) availableItemsValLabel.setText(String.valueOf(available));
    }

    private JPanel createRightPanel() {
        JPanel right = new JPanel(new BorderLayout()); right.setBackground(Color.WHITE); right.setPreferredSize(new Dimension(280, 0)); right.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, borderLight), new EmptyBorder(20, 20, 20, 20)));

        JPanel topContent = new JPanel(); topContent.setLayout(new BoxLayout(topContent, BoxLayout.Y_AXIS)); topContent.setBackground(Color.WHITE);

        JButton addBtn = new JButton("+ Add Menu Item"); addBtn.setFont(new Font("Segoe UI", Font.BOLD, 14)); addBtn.setBackground(primaryGreen); addBtn.setForeground(Color.WHITE); addBtn.setFocusPainted(false); addBtn.setMaximumSize(new Dimension(240, 45)); addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addBtn.addActionListener(e -> openAddItemDialog());
        
        JLabel notifyTitle = new JLabel("Recent Notifications"); notifyTitle.setFont(new Font("Segoe UI", Font.BOLD, 14)); notifyTitle.setBorder(new EmptyBorder(30, 0, 10, 0)); notifyTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        topContent.add(addBtn); 
        topContent.add(notifyTitle);
        
        notificationsContainer = new JPanel();
        notificationsContainer.setLayout(new BoxLayout(notificationsContainer, BoxLayout.Y_AXIS));
        notificationsContainer.setBackground(Color.WHITE);
        notificationsContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        topContent.add(notificationsContainer);
        right.add(topContent, BorderLayout.NORTH);

        JPanel freshQuote = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(52, 211, 153), 0, getHeight(), new Color(5, 150, 105));
                g2.setPaint(gp); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        freshQuote.setOpaque(false); freshQuote.setLayout(new GridBagLayout()); freshQuote.setPreferredSize(new Dimension(240, 240)); freshQuote.setMaximumSize(new Dimension(240, 240));
        JPanel innerQuote = new JPanel(); innerQuote.setLayout(new BoxLayout(innerQuote, BoxLayout.Y_AXIS)); innerQuote.setOpaque(false);
        JLabel fTitle = new JLabel("\"Fresh ingredients, 🌿"); fTitle.setFont(new Font("Georgia", Font.ITALIC, 16)); fTitle.setForeground(new Color(255, 255, 255, 220)); fTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel fSub = new JLabel("better meals! ✨\""); fSub.setFont(new Font("Georgia", Font.BOLD, 18)); fSub.setForeground(Color.WHITE); fSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        innerQuote.add(fTitle); innerQuote.add(Box.createVerticalStrut(10)); innerQuote.add(fSub); freshQuote.add(innerQuote);
        
        right.add(freshQuote, BorderLayout.SOUTH);
        return right;
    }

    private JPanel createNotification(String title, String desc, Color iconColor) {
        JPanel p = new JPanel(new BorderLayout(10, 0)); p.setBackground(Color.WHITE); p.setBorder(new EmptyBorder(10, 0, 10, 0)); p.setMaximumSize(new Dimension(240, 60));
        JPanel icon = new JPanel(); icon.setBackground(iconColor); icon.setPreferredSize(new Dimension(15, 15));
        JPanel text = new JPanel(new GridLayout(2, 1)); text.setBackground(Color.WHITE);
        JLabel t = new JLabel(title); t.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        String shortDesc = desc.length() > 30 ? desc.substring(0,30) + "..." : desc;
        JLabel d = new JLabel(shortDesc); d.setFont(new Font("Segoe UI", Font.PLAIN, 11)); d.setForeground(textMuted);
        text.add(t); text.add(d); p.add(icon, BorderLayout.WEST); p.add(text, BorderLayout.CENTER);
        return p;
    }

    private void openAddItemDialog() {
        JDialog dialog = new JDialog(this, "Add New Menu Item", true); dialog.setSize(480, 580); dialog.setLocationRelativeTo(this); dialog.setLayout(new BorderLayout());
        JPanel form = new JPanel(new GridLayout(6, 1, 10, 10)); form.setBorder(new EmptyBorder(20, 20, 20, 20)); form.setBackground(Color.WHITE);
        JTextField titleField = new JTextField(); titleField.setBorder(BorderFactory.createTitledBorder("Dish Title"));
        JTextField priceField = new JTextField(); priceField.setBorder(BorderFactory.createTitledBorder("Price (INR)"));
        JComboBox<String> categoryBox = new JComboBox<>(new String[]{"South Indian", "North Indian", "Chinese", "Desserts"}); categoryBox.setBorder(BorderFactory.createTitledBorder("Category"));
        JComboBox<String> dietBox = new JComboBox<>(new String[]{"Veg", "Non-Veg"}); dietBox.setBorder(BorderFactory.createTitledBorder("Dietary Type"));

        JPanel imgRow = new JPanel(new BorderLayout(5, 0)); imgRow.setBackground(Color.WHITE);
        JTextField imgPathField = new JTextField(); 
        imgPathField.setBorder(BorderFactory.createTitledBorder("Image File Path or Web URL"));
        
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 5, 0)); btnPanel.setBackground(Color.WHITE);
        JButton browseBtn = new JButton("Browse Local"); JButton searchBtn = new JButton("Search Web");
        
        browseBtn.addActionListener(e -> { JFileChooser chooser = new JFileChooser(); if(chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) imgPathField.setText(chooser.getSelectedFile().getAbsolutePath()); });
        searchBtn.addActionListener(e -> { String query = titleField.getText().trim(); if(query.isEmpty()) { JOptionPane.showMessageDialog(dialog, "Please enter a Dish Title first."); return; } try { Desktop.getDesktop().browse(new URI("https://www.google.com/search?tbm=isch&q=" + query.replace(" ", "+") + "+food+dish")); } catch (Exception ex) { } });

        btnPanel.add(browseBtn); btnPanel.add(searchBtn); imgRow.add(imgPathField, BorderLayout.CENTER); imgRow.add(btnPanel, BorderLayout.EAST);
        JButton submitBtn = new JButton("Save Item"); submitBtn.setBackground(primaryGreen); submitBtn.setForeground(Color.WHITE); submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        submitBtn.addActionListener(e -> {
            if(titleField.getText().isEmpty() || priceField.getText().isEmpty()) { JOptionPane.showMessageDialog(dialog, "Title and Price required!"); return; }
            addCardToGrid(titleField.getText(), categoryBox.getSelectedItem().toString(), dietBox.getSelectedItem().toString(), priceField.getText(), imgPathField.getText(), new Color(100, 116, 139), "Available");
            foodGridPanel.revalidate(); foodGridPanel.repaint(); updateStats(); saveMenuData(); dialog.dispose();
        });

        form.add(titleField); form.add(priceField); form.add(categoryBox); form.add(dietBox); form.add(imgRow); form.add(submitBtn);
        dialog.add(form, BorderLayout.CENTER); dialog.setVisible(true);
    }

    private void openEditItemDialog(JPanel card, JLabel tLabel, JLabel cLabel, JLabel dLabel, JLabel pLabel, JLabel imgLabel) {
        JDialog dialog = new JDialog(this, "Edit Menu Item", true); dialog.setSize(480, 580); dialog.setLocationRelativeTo(this); dialog.setLayout(new BorderLayout());
        JPanel form = new JPanel(new GridLayout(6, 1, 10, 10)); form.setBorder(new EmptyBorder(20, 20, 20, 20)); form.setBackground(Color.WHITE);
        JTextField titleField = new JTextField((String) card.getClientProperty("title")); titleField.setBorder(BorderFactory.createTitledBorder("Dish Title"));
        JTextField priceField = new JTextField((String) card.getClientProperty("price")); priceField.setBorder(BorderFactory.createTitledBorder("Price (INR)"));
        JComboBox<String> categoryBox = new JComboBox<>(new String[]{"South Indian", "North Indian", "Chinese", "Desserts"}); categoryBox.setSelectedItem((String) card.getClientProperty("category")); categoryBox.setBorder(BorderFactory.createTitledBorder("Category"));
        JComboBox<String> dietBox = new JComboBox<>(new String[]{"Veg", "Non-Veg"}); dietBox.setSelectedItem((String) card.getClientProperty("diet")); dietBox.setBorder(BorderFactory.createTitledBorder("Dietary Type"));
        JPanel imgRow = new JPanel(new BorderLayout(5, 0)); imgRow.setBackground(Color.WHITE);
        JTextField imgPathField = new JTextField((String) card.getClientProperty("imgPath")); imgPathField.setBorder(BorderFactory.createTitledBorder("Image File Path or Web URL"));
        
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 5, 0)); btnPanel.setBackground(Color.WHITE);
        JButton browseBtn = new JButton("Browse Local"); JButton searchBtn = new JButton("Search Web");
        browseBtn.addActionListener(e -> { JFileChooser chooser = new JFileChooser(); if(chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) imgPathField.setText(chooser.getSelectedFile().getAbsolutePath()); });
        searchBtn.addActionListener(e -> { String query = titleField.getText().trim(); if(query.isEmpty()) return; try { Desktop.getDesktop().browse(new URI("https://www.google.com/search?tbm=isch&q=" + query.replace(" ", "+") + "+food")); } catch (Exception ex) { } });
        btnPanel.add(browseBtn); btnPanel.add(searchBtn); imgRow.add(imgPathField, BorderLayout.CENTER); imgRow.add(btnPanel, BorderLayout.EAST);
        JButton submitBtn = new JButton("Update Item"); submitBtn.setBackground(new Color(30, 41, 59)); submitBtn.setForeground(Color.WHITE); submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        submitBtn.addActionListener(e -> {
            if(titleField.getText().isEmpty() || priceField.getText().isEmpty()) { JOptionPane.showMessageDialog(dialog, "Title and Price required!"); return; }
            card.putClientProperty("title", titleField.getText()); card.putClientProperty("price", priceField.getText()); card.putClientProperty("category", categoryBox.getSelectedItem().toString()); card.putClientProperty("diet", dietBox.getSelectedItem().toString()); card.putClientProperty("imgPath", imgPathField.getText());
            tLabel.setText(titleField.getText()); pLabel.setText("Rs. " + priceField.getText()); cLabel.setText(categoryBox.getSelectedItem().toString()); dLabel.setText(dietBox.getSelectedItem().toString().equals("Veg") ? "[Veg]" : "[Non-Veg]");
            updateCardImage(imgLabel, imgPathField.getText(), titleField.getText()); saveMenuData(); dialog.dispose();
        });

        form.add(titleField); form.add(priceField); form.add(categoryBox); form.add(dietBox); form.add(imgRow); form.add(submitBtn);
        dialog.add(form, BorderLayout.CENTER); dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuForm().setVisible(true));
    }
}

