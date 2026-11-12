 import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class OrderManager extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContainer;

    public static String loggedInUserName = "Hathija Asma";
    public static String loggedInUserPhone = "9876543210";
    public static String loggedInUserAddress = "Anna Nagar, Trichy";

    private Color primaryAccent = new Color(212, 110, 85);
    private Color sidebarDark = new Color(43, 38, 38);
    private Color bgMain = new Color(252, 250, 245);
    private Color borderLight = new Color(230, 225, 220);

    private double currentSubtotal = 0.0;
    private double discountMultiplier = 1.0;
    private StringBuilder cartItemsBuilder = new StringBuilder();
    private JTextArea orderSummaryArea;
    private JPanel dynamicMenuGrid;
    private JLabel cartTotalBottomLabel;
    private JTextField promoCodeField;
    private JTextField deliveryAddressField;
    private String selectedPaymentMethod = "UPI";
    private JButton placeOrderBtn;

    private JPanel historyListPanel;
    private JLabel namePhoneLbl;
    private JLabel addressLbl;

    private final String DATA_FILE = System.getProperty("user.home") + File.separator + "manam_menu_data.txt";
    private final String ORDERS_FILE = System.getProperty("user.home") + File.separator + "manam_orders.txt";

    public OrderManager() {
        setTitle("Manam - Customer Order & POS Portal");
        setSize(1350, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        JPanel customerOrderPanel = createCustomerOrderPanel();
        JPanel profilePanel = createProfilePanel();

        mainContainer.add(customerOrderPanel, "ORDER");
        mainContainer.add(profilePanel, "PROFILE");

        add(mainContainer);
        cardLayout.show(mainContainer, "ORDER");
    }

    private void updateProfileUIData() {
        if (namePhoneLbl != null) {
            namePhoneLbl.setText("Name: " + loggedInUserName + " | Phone: " + loggedInUserPhone);
        }
        if (addressLbl != null) {
            addressLbl.setText("Address: " + loggedInUserAddress);
        }
        refreshOrderHistory();
    }

    private JPanel createCustomerOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgMain);

        panel.add(createSidebar(), BorderLayout.WEST);

        JPanel centerContent = new JPanel(new BorderLayout());
        centerContent.setBackground(bgMain);

        JPanel topHeader = new JPanel();
        topHeader.setLayout(new BoxLayout(topHeader, BoxLayout.Y_AXIS));
        topHeader.setBackground(bgMain);
        topHeader.setBorder(new EmptyBorder(20, 30, 10, 30));

        JPanel brandRow = new JPanel(new BorderLayout());
        brandRow.setOpaque(false);
        JLabel titleBrand = new JLabel(" Manam");
        titleBrand.setFont(new Font("Segoe UI", Font.BOLD, 16));
        brandRow.add(titleBrand, BorderLayout.WEST);

        JLabel rightTop = new JLabel("POS System (Standalone File Sync Mode)");
        rightTop.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rightTop.setForeground(primaryAccent);
        JPanel rtPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); rtPanel.setOpaque(false);
        rtPanel.add(rightTop);
        brandRow.add(rtPanel, BorderLayout.EAST);
        topHeader.add(brandRow);
        topHeader.add(Box.createVerticalStrut(15));

        JPanel titlesPanel = new JPanel(new BorderLayout());
        titlesPanel.setOpaque(false);
        JLabel mainHeading = new JLabel("Traditional Flavours,");
        mainHeading.setFont(new Font("Georgia", Font.BOLD, 32));

        JLabel quoteRight = new JLabel("Good Food, Good Mood");
        quoteRight.setFont(new Font("Georgia", Font.ITALIC, 18));
        quoteRight.setForeground(new Color(110, 120, 130));

        titlesPanel.add(mainHeading, BorderLayout.WEST);
        titlesPanel.add(quoteRight, BorderLayout.EAST);
        topHeader.add(titlesPanel);

        JLabel subHeading = new JLabel("Always Fresh");
        subHeading.setFont(new Font("Georgia", Font.BOLD, 32));
        topHeader.add(subHeading);

        JLabel descLbl = new JLabel("Authentic meals, made with love.");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLbl.setForeground(new Color(120, 130, 140));
        topHeader.add(descLbl);
        topHeader.add(Box.createVerticalStrut(15));

        JPanel quoteBox = new JPanel(new FlowLayout(FlowLayout.CENTER));
        quoteBox.setBackground(new Color(247, 233, 227));
        quoteBox.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel quoteText = new JLabel("\"Love At First Bite\"");
        quoteText.setFont(new Font("Georgia", Font.BOLD | Font.ITALIC, 16));
        quoteText.setForeground(new Color(138, 64, 46));
        quoteBox.add(quoteText);
        topHeader.add(quoteBox);
        topHeader.add(Box.createVerticalStrut(15));

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);
        String[] filters = {"All Items", "South Indian", "North Indian", "Chinese", "Desserts"};
        for (String f : filters) {
            JButton fBtn = new JButton(f);
            fBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            fBtn.setBackground(f.equals("All Items") ? primaryAccent : Color.WHITE);
            fBtn.setForeground(f.equals("All Items") ? Color.WHITE : new Color(100, 110, 120));
            fBtn.setBorder(new LineBorder(borderLight, 1, true));
            fBtn.setFocusPainted(false);
            fBtn.setPreferredSize(new Dimension(120, 32));
            fBtn.addActionListener(e -> loadCustomerMenu(f.equals("All Items") ? "" : f));
            filterRow.add(fBtn);
        }
        topHeader.add(filterRow);
        centerContent.add(topHeader, BorderLayout.NORTH);

        dynamicMenuGrid = new JPanel(new GridLayout(0, 3, 20, 20));
        dynamicMenuGrid.setBackground(bgMain);
        dynamicMenuGrid.setBorder(new EmptyBorder(15, 30, 20, 30));

        JScrollPane menuScroll = new JScrollPane(dynamicMenuGrid);
        menuScroll.setBorder(null);
        menuScroll.getVerticalScrollBar().setUnitIncrement(16);
        centerContent.add(menuScroll, BorderLayout.CENTER);

        panel.add(centerContent, BorderLayout.CENTER);

        JPanel rightCartPanel = new JPanel(new BorderLayout());
        rightCartPanel.setPreferredSize(new Dimension(320, 0));
        rightCartPanel.setBackground(Color.WHITE);
        rightCartPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 0, 0, borderLight),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel cartTitle = new JLabel("Your Order");
        cartTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        rightCartPanel.add(cartTitle, BorderLayout.NORTH);

        JPanel cartCenter = new JPanel();
        cartCenter.setLayout(new BoxLayout(cartCenter, BoxLayout.Y_AXIS));
        cartCenter.setBackground(Color.WHITE);

        orderSummaryArea = new JTextArea("No items selected yet.");
        orderSummaryArea.setEditable(false);
        orderSummaryArea.setOpaque(false);
        orderSummaryArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane cartScroll = new JScrollPane(orderSummaryArea);
        cartScroll.setPreferredSize(new Dimension(280, 180));
        cartCenter.add(cartScroll);
        cartCenter.add(Box.createVerticalStrut(15));

        JPanel promoRow = new JPanel(new BorderLayout(5, 0));
        promoRow.setOpaque(false);
        promoRow.setMaximumSize(new Dimension(280, 35));
        promoCodeField = new JTextField("MANAM10");
        promoCodeField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JButton applyBtn = new JButton("Apply Discount");
        applyBtn.setBackground(new Color(240, 235, 230));
        applyBtn.setFocusPainted(false);

        applyBtn.addActionListener(e -> {
            String code = promoCodeField.getText().trim().toUpperCase();
            if (code.startsWith("MANAM")) {
                try {
                    int percent = Integer.parseInt(code.replace("MANAM", ""));
                    if (percent >= 10 && percent <= 50 && percent % 10 == 0) {
                        discountMultiplier = (100.0 - percent) / 100.0;
                        JOptionPane.showMessageDialog(this, "Promo Code Applied! " + percent + "% OFF Discount Rate Added!");
                        updateCartTotalsUI();
                    } else {
                        JOptionPane.showMessageDialog(this, "Use MANAM10 up to MANAM50 (multiples of 10).");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid code format!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Use valid code like MANAM10!");
            }
        });

        promoRow.add(promoCodeField, BorderLayout.CENTER);
        promoRow.add(applyBtn, BorderLayout.EAST);
        cartCenter.add(promoRow);
        cartCenter.add(Box.createVerticalStrut(15));

        JPanel subRow = new JPanel(new BorderLayout());
        subRow.setOpaque(false);
        subRow.setMaximumSize(new Dimension(280, 30));
        JLabel subLbl = new JLabel("Subtotal / Final");
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subLbl.setForeground(new Color(100, 110, 120));
        cartTotalBottomLabel = new JLabel("Rs. 0");
        cartTotalBottomLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        subRow.add(subLbl, BorderLayout.WEST);
        subRow.add(cartTotalBottomLabel, BorderLayout.EAST);
        cartCenter.add(subRow);
        cartCenter.add(Box.createVerticalStrut(10));

        JPanel toggleRow = new JPanel(new GridLayout(1, 2, 5, 0));
        toggleRow.setOpaque(false);
        toggleRow.setMaximumSize(new Dimension(280, 35));
        JButton delBtn = new JButton("Delivery"); delBtn.setBackground(primaryAccent); delBtn.setForeground(Color.WHITE); delBtn.setFocusPainted(false);
        JButton pkBtn = new JButton("Pickup"); pkBtn.setBackground(Color.WHITE); pkBtn.setFocusPainted(false);
        toggleRow.add(delBtn); toggleRow.add(pkBtn);
        cartCenter.add(toggleRow);
        cartCenter.add(Box.createVerticalStrut(10));

        deliveryAddressField = new JTextField("");
        deliveryAddressField.setMaximumSize(new Dimension(280, 35));
        deliveryAddressField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cartCenter.add(deliveryAddressField);
        cartCenter.add(Box.createVerticalStrut(15));

        JLabel payLabel = new JLabel("Payment Method");
        payLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cartCenter.add(payLabel);
        cartCenter.add(Box.createVerticalStrut(8));

        JPanel payRow = new JPanel(new GridLayout(1, 2, 8, 0));
        payRow.setOpaque(false);
        payRow.setMaximumSize(new Dimension(280, 35));
        JButton upiBtn = new JButton("UPI (QR)"); upiBtn.setBackground(primaryAccent); upiBtn.setForeground(Color.WHITE); upiBtn.setFocusPainted(false);
        JButton cashBtn = new JButton("Cash"); cashBtn.setBackground(Color.WHITE); cashBtn.setFocusPainted(false);

        upiBtn.addActionListener(e -> {
            selectedPaymentMethod = "UPI";
            upiBtn.setBackground(primaryAccent); upiBtn.setForeground(Color.WHITE);
            cashBtn.setBackground(Color.WHITE); cashBtn.setForeground(Color.BLACK);
        });
        cashBtn.addActionListener(e -> {
            selectedPaymentMethod = "Cash";
            cashBtn.setBackground(primaryAccent); cashBtn.setForeground(Color.WHITE);
            upiBtn.setBackground(Color.WHITE); upiBtn.setForeground(Color.BLACK);
        });

        payRow.add(upiBtn);
        payRow.add(cashBtn);
        cartCenter.add(payRow);

        rightCartPanel.add(cartCenter, BorderLayout.CENTER);

        placeOrderBtn = new JButton("Place Order | Rs. 0 ->");
        placeOrderBtn.setBackground(primaryAccent);
        placeOrderBtn.setForeground(Color.WHITE);
        placeOrderBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        placeOrderBtn.setFocusPainted(false);
        placeOrderBtn.setPreferredSize(new Dimension(0, 45));

        placeOrderBtn.addActionListener(e -> {
            double finalAmt = currentSubtotal * discountMultiplier;
            if (finalAmt <= 0) {
                JOptionPane.showMessageDialog(this, "Your cart is empty! Please add items first.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String address = deliveryAddressField.getText().trim();
            if (address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a valid delivery address.", "Address Required", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (selectedPaymentMethod.equals("UPI")) {
                showRealQRCodeDialog(finalAmt);
            } else {
                finalizeOrderInFile(finalAmt);
            }
        });

        rightCartPanel.add(placeOrderBtn, BorderLayout.SOUTH);
        panel.add(rightCartPanel, BorderLayout.EAST);
        
        return panel;
    }

    private void showRealQRCodeDialog(double amount) {
        JDialog qrDialog = new JDialog(this, "Scan UPI QR to Pay", true);
        qrDialog.setSize(380, 460);
        qrDialog.setLocationRelativeTo(this);
        qrDialog.setLayout(new BorderLayout());

        JPanel qrContent = new JPanel();
        qrContent.setLayout(new BoxLayout(qrContent, BoxLayout.Y_AXIS));
        qrContent.setBackground(Color.WHITE);
        qrContent.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLbl = new JLabel("Scan & Pay via GooglePay / PhonePe");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLbl.setForeground(primaryAccent);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel amtLbl = new JLabel("Amount to Pay: Rs. " + amount);
        amtLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        amtLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel qrImageLbl = new JLabel("Loading QR...");
        qrImageLbl.setHorizontalAlignment(SwingConstants.CENTER);
        qrImageLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        qrImageLbl.setPreferredSize(new Dimension(220, 220));

        new Thread(() -> {
            try {
                String upiData = "upi://pay?pa=manamcloudkitchen@upi&pn=ManamCloudKitchen&am=" + amount + "&cu=INR";
                String qrApiUrl = "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + java.net.URLEncoder.encode(upiData, "UTF-8");
                Image qImage = ImageIO.read(new URL(qrApiUrl));
                if (qImage != null) {
                    SwingUtilities.invokeLater(() -> qrImageLbl.setIcon(new ImageIcon(qImage)));
                    SwingUtilities.invokeLater(() -> qrImageLbl.setText(""));
                }
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> qrImageLbl.setText("<html><center><b>UPI ID:</b> manam@upi<br>Scan via GPay/PhonePe</center>"));
            }
        }).start();

        JButton confirmPaidBtn = new JButton("I Have Paid ✅ Complete Order");
        confirmPaidBtn.setBackground(primaryAccent);
        confirmPaidBtn.setForeground(Color.WHITE);
        confirmPaidBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        confirmPaidBtn.setFocusPainted(false);
        confirmPaidBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        confirmPaidBtn.addActionListener(e -> {
            qrDialog.dispose();
            finalizeOrderInFile(amount);
        });

        qrContent.add(titleLbl);
        qrContent.add(Box.createVerticalStrut(8));
        qrContent.add(amtLbl);
        qrContent.add(Box.createVerticalStrut(15));
        qrContent.add(qrImageLbl);
        qrContent.add(Box.createVerticalStrut(20));
        qrContent.add(confirmPaidBtn);

        qrDialog.add(qrContent, BorderLayout.CENTER);
        qrDialog.setVisible(true);
    }

    private void finalizeOrderInFile(double finalAmount) {
        int orderId = (int)(Math.random() * 90000) + 10000;
        String orderRecord = orderId + "~" + cartItemsBuilder.toString() + "~Rs. " + finalAmount + "~Pending";

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(ORDERS_FILE, true)))) {
            out.println(orderRecord);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(this, "Order placed successfully via " + selectedPaymentMethod + "! Check Profile & History.");
        currentSubtotal = 0.0;
        discountMultiplier = 1.0;
        cartItemsBuilder.setLength(0);
        orderSummaryArea.setText("No items selected yet.");
        cartTotalBottomLabel.setText("Rs. 0");
        if(placeOrderBtn != null) placeOrderBtn.setText("Place Order | Rs. 0 ->");
        refreshOrderHistory();
    }

    private void updateCartTotalsUI() {
        double finalAmt = currentSubtotal * discountMultiplier;
        orderSummaryArea.setText("Items in Cart:\n" + cartItemsBuilder.toString() + "\n\nDiscount Rate Applied!");
        cartTotalBottomLabel.setText("Rs. " + finalAmt);
        if(placeOrderBtn != null) placeOrderBtn.setText("Place Order | Rs. " + finalAmt + " ->");
    }

    public void loadCustomerMenu(String filterCategory) {
        if (dynamicMenuGrid == null) return;
        dynamicMenuGrid.removeAll();

        File file = new File(DATA_FILE);
        boolean found = false;

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split("~");
                    if (data.length >= 6) {
                        String dishName = data[0];
                        String category = data[1];
                        String diet = data[2];
                        String priceStr = data[3];
                        String imgPath = data[4];
                        String status = data[5];

                        if (!status.equalsIgnoreCase("Available")) continue;
                        if (!filterCategory.isEmpty() && !category.equalsIgnoreCase(filterCategory)) continue;

                        found = true;
                        JPanel foodCard = new JPanel(new BorderLayout());
                        foodCard.setBackground(Color.WHITE);
                        foodCard.setBorder(new LineBorder(borderLight, 1, true));
                        foodCard.setPreferredSize(new Dimension(240, 220));

                        JLabel imgLabel = new JLabel("Loading...");
                        imgLabel.setPreferredSize(new Dimension(0, 110));
                        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        imgLabel.setOpaque(true);
                        imgLabel.setBackground(new Color(245, 243, 238));

                        if (imgPath != null && !imgPath.isEmpty()) {
                            new Thread(() -> {
                                try {
                                    Image img = null;
                                    if (imgPath.startsWith("http")) img = ImageIO.read(new URL(imgPath));
                                    else if (new File(imgPath).exists()) img = new ImageIcon(imgPath).getImage();
                                    if (img != null) {
                                        Image scaled = img.getScaledInstance(240, -1, Image.SCALE_SMOOTH);
                                        MediaTracker tracker = new MediaTracker(new java.awt.Container());
                                        tracker.addImage(scaled, 0);
                                        tracker.waitForID(0);
                                        
                                        SwingUtilities.invokeLater(() -> {
                                            imgLabel.setIcon(new ImageIcon(scaled));
                                            imgLabel.setText("");
                                        });
                                    }
                                } catch (Exception ignored) {
                                    SwingUtilities.invokeLater(() -> imgLabel.setText("🥘 " + dishName));
                                }
                            }).start();
                        } else {
                            imgLabel.setText("🥘 " + dishName);
                        }
                        foodCard.add(imgLabel, BorderLayout.NORTH);

                        JPanel infoPanel = new JPanel();
                        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                        infoPanel.setBackground(Color.WHITE);
                        infoPanel.setBorder(new EmptyBorder(8, 10, 8, 10));

                        JLabel nameLbl = new JLabel(dishName);
                        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));

                        JPanel bottomRow = new JPanel(new BorderLayout());
                        bottomRow.setBackground(Color.WHITE);

                        JPanel priceDietCol = new JPanel();
                        priceDietCol.setLayout(new BoxLayout(priceDietCol, BoxLayout.Y_AXIS));
                        priceDietCol.setBackground(Color.WHITE);

                        JLabel priceLbl = new JLabel("Rs. " + priceStr);
                        priceLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        JLabel dietLbl = new JLabel(diet.equals("Veg") ? "Vegetarian" : "Non-Vegetarian");
                        dietLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                        dietLbl.setForeground(new Color(130, 140, 150));

                        priceDietCol.add(priceLbl);
                        priceDietCol.add(dietLbl);

                        JButton addCartBtn = new JButton("+ Add to Cart");
                        addCartBtn.setBackground(primaryAccent);
                        addCartBtn.setForeground(Color.WHITE);
                        addCartBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
                        addCartBtn.setFocusPainted(false);
                        addCartBtn.setPreferredSize(new Dimension(110, 28));

                        addCartBtn.addActionListener(e -> {
                            try {
                                double pVal = Double.parseDouble(priceStr);
                                currentSubtotal += pVal;
                                if (cartItemsBuilder.length() > 0) cartItemsBuilder.append(", ");
                                cartItemsBuilder.append("1x ").append(dishName);

                                updateCartTotalsUI();
                                JOptionPane.showMessageDialog(this, dishName + " added to cart!");
                            } catch (NumberFormatException ignored) {}
                        });

                        bottomRow.add(priceDietCol, BorderLayout.WEST);
                        bottomRow.add(addCartBtn, BorderLayout.EAST);

                        infoPanel.add(nameLbl);
                        infoPanel.add(Box.createVerticalStrut(4));
                        infoPanel.add(bottomRow);

                        foodCard.add(infoPanel, BorderLayout.CENTER);
                        dynamicMenuGrid.add(foodCard);
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        if (!found) {
            dynamicMenuGrid.add(new JLabel("No dishes found. Add via Menu Manager!"));
        }

        dynamicMenuGrid.revalidate();
        dynamicMenuGrid.repaint();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(null);
        sidebar.setPreferredSize(new Dimension(220, 700));
        sidebar.setBackground(sidebarDark);

        JLabel brand = new JLabel("MANAM");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brand.setForeground(Color.WHITE);
        brand.setBounds(20, 20, 180, 30);
        sidebar.add(brand);

        JButton btnOrder = createSidebarButton("Order & Menu", 90);
        JButton btnProfile = createSidebarButton("Profile & History", 150);

        sidebar.add(btnOrder);
        sidebar.add(btnProfile);

        btnOrder.addActionListener(e -> {
            loadCustomerMenu("");
            cardLayout.show(mainContainer, "ORDER");
        });

        btnProfile.addActionListener(e -> {
            updateProfileUIData();
            cardLayout.show(mainContainer, "PROFILE");
        });

        return sidebar;
    }

    private JButton createSidebarButton(String text, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(15, y, 190, 40);
        btn.setBackground(new Color(60, 54, 54)); 
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        return btn;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        panel.add(createSidebar(), BorderLayout.WEST);

        JPanel content = new JPanel(null);
        content.setBackground(bgMain);

        JLabel title = new JLabel("Customer Profile & Order History");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(primaryAccent);
        title.setBounds(30, 25, 450, 30);
        content.add(title);

        JPanel profileCard = new JPanel(null);
        profileCard.setBackground(Color.WHITE);
        profileCard.setBorder(BorderFactory.createLineBorder(new Color(210, 220, 215), 1));
        profileCard.setBounds(30, 70, 750, 100);

        JLabel avatar = new JLabel("👤", SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        avatar.setBounds(20, 20, 60, 60);
        profileCard.add(avatar);

        namePhoneLbl = new JLabel("Name: " + loggedInUserName + " | Phone: " + loggedInUserPhone);
        namePhoneLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        namePhoneLbl.setBounds(95, 25, 620, 20);
        profileCard.add(namePhoneLbl);

        addressLbl = new JLabel("Address: " + loggedInUserAddress);
        addressLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addressLbl.setForeground(Color.DARK_GRAY);
        addressLbl.setBounds(95, 50, 620, 20);
        profileCard.add(addressLbl);

        content.add(profileCard);

        JLabel historyTitle = new JLabel("Your Past Orders (File-Synced with Kitchen):");
        historyTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        historyTitle.setBounds(30, 190, 450, 20);
        content.add(historyTitle);

        historyListPanel = new JPanel();
        historyListPanel.setLayout(new BoxLayout(historyListPanel, BoxLayout.Y_AXIS));
        historyListPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(historyListPanel);
        scrollPane.setBounds(30, 220, 750, 400);
        content.add(scrollPane);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private void refreshOrderHistory() {
        if (historyListPanel == null) return;
        historyListPanel.removeAll();

        File file = new File(ORDERS_FILE);
        boolean hasOrders = false;

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                List<String> lines = new ArrayList<>();
                String line;
                while ((line = br.readLine()) != null) lines.add(line);

                for (int i = lines.size() - 1; i >= 0; i--) {
                    String[] data = lines.get(i).split("~");
                    if (data.length >= 4) {
                        hasOrders = true;
                        JPanel itemCard = new JPanel(new GridLayout(1, 4, 10, 0));
                        itemCard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                        itemCard.setBackground(new Color(245, 250, 247));
                        itemCard.setMaximumSize(new Dimension(730, 50));
                        
                        JLabel idLbl = new JLabel("Order #" + data[0]);
                        idLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                        
                        JLabel itemsLbl = new JLabel(data[1]);
                        itemsLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                        
                        JLabel amtLbl = new JLabel(data[2]);
                        amtLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        
                        JLabel statusLbl = new JLabel(data[3]);
                        statusLbl.setFont(new Font("Segoe UI", Font.ITALIC, 12));
                        if (data[3].equalsIgnoreCase("Pending")) {
                            statusLbl.setForeground(new Color(200, 100, 0));
                        } else {
                            statusLbl.setForeground(new Color(0, 150, 0));
                        }

                        itemCard.add(idLbl);
                        itemCard.add(itemsLbl);
                        itemCard.add(amtLbl);
                        itemCard.add(statusLbl);

                        historyListPanel.add(itemCard);
                        historyListPanel.add(Box.createVerticalStrut(5));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!hasOrders) {
            historyListPanel.add(new JLabel("No orders found. Add items to your cart to begin!"));
        }

        historyListPanel.revalidate();
        historyListPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            OrderManager frame = new OrderManager();
            frame.setVisible(true);
            frame.loadCustomerMenu("");
        });
    }
}