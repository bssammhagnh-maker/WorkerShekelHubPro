package com.example.workershekel;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class MainApp extends Application {

    private Stage primaryStage;

    private Label lblTotalWorkers = new Label("0");
    private Label lblTotalIncome = new Label("₪ 0.00");
    private Label lblTotalExpenses = new Label("₪ 0.00");
    private Label lblTotalMaterialsCost = new Label("₪ 0.00");

    private TableView<Worker> tblWorkers = new TableView<>();
    private ObservableList<Worker> workerList = FXCollections.observableArrayList();

    private TableView<AttendanceRecord> tblAttendance = new TableView<>();
    private ObservableList<AttendanceRecord> attendanceList = FXCollections.observableArrayList();

    private TableView<FinanceRecord> tblFinances = new TableView<>();
    private ObservableList<FinanceRecord> financeList = FXCollections.observableArrayList();

    private TableView<MaterialRecord> tblMaterials = new TableView<>();
    private ObservableList<MaterialRecord> materialList = FXCollections.observableArrayList();

    private TableView<SalarySummary> tblSalaries = new TableView<>();
    private ObservableList<SalarySummary> salaryList = FXCollections.observableArrayList();

    private LineChart<String, Number> attendanceChart;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        DatabaseHelper.initializeDatabase();
        showLoginScene();
    }

    private void showLoginScene() {
        BorderPane root = new BorderPane();
        root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #0b0f19, #111827, #1e293b);");

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setMaxWidth(420);
        centerBox.setStyle("-fx-background-color: rgba(17, 24, 39, 0.85); -fx-padding: 40; -fx-background-radius: 16; -fx-border-color: #374151; -fx-border-radius: 16; -fx-border-width: 1.5;");

        FadeTransition ft = new FadeTransition(Duration.millis(800), centerBox);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        Label title = new Label("⭐ Worker Shekel Hub Pro");
        title.setStyle("-fx-text-fill: #fbbf24; -fx-font-size: 22px; -fx-font-weight: bold;");

        Label subtitle = new Label("منصة إدارة المقاولات والعمال الذكية");
        subtitle.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px;");

        TabPane authTabs = new TabPane();
        authTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab loginTab = new Tab("כניסה למערכת", createLoginForm());
        Tab registerTab = new Tab("הרשמה חדשה", createRegisterForm());

        authTabs.getTabs().addAll(loginTab, registerTab);
        centerBox.getChildren().addAll(title, subtitle, authTabs);

        root.setCenter(centerBox);

        Scene scene = new Scene(root, 1300, 800);
        applyGlobalStyles(scene);
        primaryStage.setTitle("Worker Shekel Hub Pro - Authentication");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createLoginForm() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20, 0, 0, 0));
        box.setAlignment(Pos.CENTER);

        TextField txtUser = new TextField();
        txtUser.setPromptText("שם משתמש (Username)");

        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("סיסמה (Password)");

        Button btnLogin = new Button("🚀 دخول للمنصة");
        btnLogin.setPrefWidth(Double.MAX_VALUE);

        btnLogin.setOnAction(e -> {
            String u = txtUser.getText().trim();
            String p = txtPass.getText().trim();
            if(DatabaseHelper.validateUser(u, p)) {
                transitionToWorkspace();
            } else {
                showAlert("שם משתמש أو كلمة المرور غير صحيحة!");
            }
        });

        box.getChildren().addAll(txtUser, txtPass, btnLogin);
        return box;
    }

    private VBox createRegisterForm() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20, 0, 0, 0));
        box.setAlignment(Pos.CENTER);

        TextField txtUser = new TextField();
        txtUser.setPromptText("اختر اسم مستخدم جديد");

        PasswordField txtPass = new PasswordField();
        txtPass.setPromptText("اختر كلمة مرور");

        Button btnRegister = new Button("✨ إنشاء الحساب والانضمام");
        btnRegister.setPrefWidth(Double.MAX_VALUE);

        btnRegister.setOnAction(e -> {
            String u = txtUser.getText().trim();
            String p = txtPass.getText().trim();
            if(u.isEmpty() || p.isEmpty()) {
                showAlert("الرجاء تعبئة جميع الحقول!");
                return;
            }
            if(DatabaseHelper.registerUser(u, p)) {
                Alert ok = new Alert(Alert.AlertType.INFORMATION);
                ok.setTitle("نجاح");
                ok.setHeaderText(null);
                ok.setContentText("تم إنشاء الحساب بنجاح! يمكنك الآن تسجيل الدخول.");
                ok.showAndWait();
            } else {
                showAlert("اسم المستخدم مستخدم مسبقاً، اختر اسماً آخر.");
            }
        });

        box.getChildren().addAll(txtUser, txtPass, btnRegister);
        return box;
    }

    private void transitionToWorkspace() {
        BorderPane root = new BorderPane();
        root.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        root.getStyleClass().add("root-pane");

        HBox header = new HBox(15);
        header.getStyleClass().add("header-bar");
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("⭐ Worker Shekel Hub Pro - ניהול פרויקטים ועובדים");
        title.setStyle("-fx-text-fill: #fbbf24; -fx-font-size: 20px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        DatePicker dpReportMonth = new DatePicker(LocalDate.now());
        dpReportMonth.setPrefWidth(135);

        Button btnExportExcel = new Button("📊 ייצוא לאקסל (Excel)");
        btnExportExcel.setStyle("-fx-background-color: #059669;");
        btnExportExcel.setOnAction(e -> {
            LocalDate selectedDate = dpReportMonth.getValue() != null ? dpReportMonth.getValue() : LocalDate.now();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("שמור דוח אקסל חודשי");
            fileChooser.setInitialFileName("דוח_עבודה_" + selectedDate.getMonthValue() + "_" + selectedDate.getYear() + ".xlsx");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files (*.xlsx)", "*.xlsx"));
            File file = fileChooser.showSaveDialog(primaryStage);

            if (file != null) {
                try {
                    ExcelExporter.exportMonthlyReport(
                            file,
                            selectedDate.getYear(),
                            selectedDate.getMonthValue(),
                            workerList,
                            attendanceList,
                            financeList
                    );
                    Alert ok = new Alert(Alert.AlertType.INFORMATION);
                    ok.setTitle("הצלחה");
                    ok.setHeaderText(null);
                    ok.setContentText("קובץ האקסל נוצר ונשמר בהצלחה!");
                    ok.showAndWait();
                } catch (Exception ex) {
                    showAlert("שגיאה ביצירת קובץ האקסל: " + ex.getMessage());
                }
            }
        });

        Button btnLogout = new Button("🚪 تسجيل خروج");
        btnLogout.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        btnLogout.setOnAction(e -> showLoginScene());

        header.getChildren().addAll(title, spacer, dpReportMonth, btnExportExcel, btnLogout);
        root.setTop(header);

        TabPane tabPane = new TabPane();
        Tab tabDashboard = new Tab("📊 לוח בקרה", createDashboardView());
        Tab tabWorkers = new Tab("👷 ניהול עובדים", createWorkersView());
        Tab tabAttendance = new Tab("📅 נוכחות והיעדרויות", createAttendanceView());
        Tab tabSalaries = new Tab("💰 משכורות וזכאים", createSalariesView());
        Tab tabFinances = new Tab("💵 הכנסות והוצאות", createFinancesView());
        Tab tabMaterials = new Tab("🧱 חומרי בניין", createMaterialsView());

        for (Tab t : tabPane.getTabs()) t.setClosable(false);
        tabPane.getTabs().addAll(tabDashboard, tabWorkers, tabAttendance, tabSalaries, tabFinances, tabMaterials);

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null && newTab.getContent() != null) {
                FadeTransition ftTab = new FadeTransition(Duration.millis(300), newTab.getContent());
                ftTab.setFromValue(0.1);
                ftTab.setToValue(1.0);
                ftTab.play();
            }
        });

        root.setCenter(tabPane);

        Scene scene = new Scene(root, 1300, 800);
        applyGlobalStyles(scene);

        FadeTransition ftScreen = new FadeTransition(Duration.millis(500), root);
        ftScreen.setFromValue(0.0);
        ftScreen.setToValue(1.0);
        ftScreen.play();

        primaryStage.setTitle("Worker Shekel Hub Pro - Workspace");
        primaryStage.setScene(scene);
        refreshAllData();
    }

    private VBox createDashboardView() {
        VBox box = new VBox(20);
        box.setStyle("-fx-padding: 20;");

        HBox cards = new HBox(15);
        cards.getChildren().addAll(
                createCard("סה\"כ עובדים רשומים", lblTotalWorkers, "#1e3a8a", "#2563eb"),
                createCard("סה\"כ הכנסות", lblTotalIncome, "#065f46", "#059669"),
                createCard("סה\"כ הוצאות", lblTotalExpenses, "#991b1b", "#dc2626"),
                createCard("עלות חומרי בניין", lblTotalMaterialsCost, "#78350f", "#d97706")
        );
        for (javafx.scene.Node n : cards.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("תאריך");
        yAxis.setLabel("מדד נוכחות");
        attendanceChart = new LineChart<>(xAxis, yAxis);
        attendanceChart.setTitle("מעקב נוכחות ותנועת עבודה");
        attendanceChart.setStyle("-fx-background-color: #111827; -fx-background-radius: 12; -fx-padding: 15;");

        box.getChildren().addAll(cards, attendanceChart);
        VBox.setVgrow(attendanceChart, Priority.ALWAYS);
        return box;
    }

    private VBox createCard(String title, Label valLabel, String c1, String c2) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: linear-gradient(to right, " + c1 + ", " + c2 + "); -fx-padding: 20; -fx-background-radius: 12;");
        Label t = new Label(title);
        t.setStyle("-fx-text-fill: #e2e8f0; -fx-font-size: 13px;");
        valLabel.setStyle("-fx-text-fill: white; -fx-font-size: 26px; -fx-font-weight: bold;");
        card.getChildren().addAll(t, valLabel);
        return card;
    }

    private VBox createWorkersView() {
        VBox box = new VBox(15);
        box.setStyle("-fx-padding: 20;");

        HBox form = new HBox(10);
        form.setAlignment(Pos.CENTER_LEFT);
        form.setStyle("-fx-background-color: #111827; -fx-padding: 15; -fx-background-radius: 10; -fx-border-color: #1f2937;");

        TextField txtName = new TextField(); txtName.setPromptText("שם העובד המלא");
        TextField txtWage = new TextField(); txtWage.setPromptText("שכר יומי (₪)");
        txtName.setPrefWidth(240); txtWage.setPrefWidth(160);

        Button btnAdd = new Button("➕ הוסף עובד חדש");
        btnAdd.setOnAction(e -> {
            try {
                if(txtName.getText().isEmpty() || txtWage.getText().isEmpty()) return;
                DatabaseHelper.addWorker(txtName.getText().trim(), Double.parseDouble(txtWage.getText().trim()));
                txtName.clear(); txtWage.clear();
                refreshAllData();
            } catch (Exception ex) { showAlert("נא להזין שכר יומי תקין!"); }
        });

        form.getChildren().addAll(txtName, txtWage, btnAdd);

        Button btnDel = new Button("🗑️ מחק עובד נבחר");
        btnDel.getStyleClass().add("delete-button");
        btnDel.setOnAction(e -> {
            Worker sel = tblWorkers.getSelectionModel().getSelectedItem();
            if(sel != null) {
                try { DatabaseHelper.deleteWorker(sel.getId()); refreshAllData(); }
                catch (SQLException ex) { ex.printStackTrace(); }
            }
        });

        setupTable(tblWorkers, col("מזהה", "id", 70), col("שם העובד", "name", 350), col("שכר יומי (₪)", "dailyWage", 220));
        tblWorkers.setItems(workerList);
        VBox.setVgrow(tblWorkers, Priority.ALWAYS);

        box.getChildren().addAll(form, btnDel, tblWorkers);
        return box;
    }

    private VBox createAttendanceView() {
        VBox box = new VBox(15);
        box.setStyle("-fx-padding: 20;");

        HBox form = new HBox(10);
        form.setAlignment(Pos.CENTER_LEFT);
        form.setStyle("-fx-background-color: #111827; -fx-padding: 15; -fx-background-radius: 10; -fx-border-color: #1f2937;");

        ComboBox<String> cmbWorkers = new ComboBox<>();
        cmbWorkers.setPromptText("בחר עובד");
        cmbWorkers.setPrefWidth(220);

        ComboBox<String> cmbStatus = new ComboBox<>();
        cmbStatus.getItems().addAll("נוכח", "נעדר", "חצי יום", "חופשה/שבת");
        cmbStatus.setValue("נוכח");
        cmbStatus.setPrefWidth(150);

        DatePicker dpDate = new DatePicker(LocalDate.now());
        dpDate.setPrefWidth(160);

        Label lblWeekendAlert = new Label("");
        lblWeekendAlert.setStyle("-fx-text-fill: #fbbf24; -fx-font-weight: bold;");

        dpDate.valueProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                String dayName = newV.getDayOfWeek().name();
                if (dayName.equals("FRIDAY") || dayName.equals("SATURDAY")) {
                    lblWeekendAlert.setText("⚠️ סוף שבוע / חופשה (שישי-שבת)");
                    cmbStatus.setValue("חופשה/שבת");
                } else {
                    lblWeekendAlert.setText("");
                }
            }
        });

        Button btnAdd = new Button("✔️ רישום נוכחות");
        btnAdd.setOnAction(e -> {
            String wName = cmbWorkers.getValue();
            String status = cmbStatus.getValue();
            LocalDate date = dpDate.getValue();
            if(wName == null || status == null || date == null) return;
            try {
                DatabaseHelper.addAttendance(wName, status, date);
                refreshAllData();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        form.getChildren().addAll(cmbWorkers, cmbStatus, dpDate, btnAdd, lblWeekendAlert);

        Button btnDel = new Button("🗑️ מחק רישום נבחר");
        btnDel.getStyleClass().add("delete-button");
        btnDel.setOnAction(e -> {
            AttendanceRecord sel = tblAttendance.getSelectionModel().getSelectedItem();
            if(sel != null) {
                try { DatabaseHelper.deleteAttendance(sel.getId()); refreshAllData(); }
                catch (SQLException ex) { ex.printStackTrace(); }
            }
        });

        setupTable(tblAttendance, col("מזהה", "id", 70), col("שם העובד", "workerName", 300), col("סטטוס נוכחות", "status", 180), col("תאריך", "date", 200));
        tblAttendance.setItems(attendanceList);
        VBox.setVgrow(tblAttendance, Priority.ALWAYS);

        box.getChildren().addAll(form, btnDel, tblAttendance);

        box.setOnMouseEntered(e -> {
            try {
                List<Worker> ws = DatabaseHelper.getAllWorkers();
                ObservableList<String> names = FXCollections.observableArrayList();
                for(Worker w : ws) names.add(w.getName());
                cmbWorkers.setItems(names);
            } catch (Exception ignored) {}
        });

        return box;
    }

    private VBox createSalariesView() {
        VBox box = new VBox(15);
        box.setStyle("-fx-padding: 20;");
        Label info = new Label("💡 חישוב משכורות אוטומטי: (נוכח = 1 יום، حצי יום = 0.5، נעדר/חופשה = 0)");
        info.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14px;");

        setupTable(tblSalaries, col("שם העובד", "workerName", 300), col("שכר יומי (₪)", "dailyWage", 180), col("ימי עבודה בפועל", "effectiveDays", 200), col("סה\"כ משכורת לתשלום (₪)", "totalSalary", 240));
        tblSalaries.setItems(salaryList);
        VBox.setVgrow(tblSalaries, Priority.ALWAYS);

        box.getChildren().addAll(info, tblSalaries);
        return box;
    }

    private VBox createFinancesView() {
        VBox box = new VBox(15);
        box.setStyle("-fx-padding: 20;");

        HBox form = new HBox(10);
        form.setAlignment(Pos.CENTER_LEFT);
        form.setStyle("-fx-background-color: #111827; -fx-padding: 15; -fx-background-radius: 10; -fx-border-color: #1f2937;");

        ComboBox<String> cmbType = new ComboBox<>();
        cmbType.getItems().addAll("הכנסה", "הוצאה");
        cmbType.setValue("הוצאה");
        cmbType.setPrefWidth(130);

        TextField txtCat = new TextField(); txtCat.setPromptText("קטגוריה (לדוגמה: דלק, הוצאות שונות)");
        TextField txtAmount = new TextField(); txtAmount.setPromptText("סכום (₪)");
        TextField txtDesc = new TextField(); txtDesc.setPromptText("תיאור");
        DatePicker dpDate = new DatePicker(LocalDate.now());

        txtCat.setPrefWidth(210); txtAmount.setPrefWidth(130); txtDesc.setPrefWidth(200); dpDate.setPrefWidth(150);

        Button btnAdd = new Button("➕ הוסף תנועה");
        btnAdd.setOnAction(e -> {
            try {
                if(txtAmount.getText().isEmpty()) return;
                String typeHeb = cmbType.getValue().equals("הכנסה") ? "إيراد" : "مصروف";
                DatabaseHelper.addFinance(typeHeb, txtCat.getText(), Double.parseDouble(txtAmount.getText()), txtDesc.getText(), dpDate.getValue());
                txtCat.clear(); txtAmount.clear(); txtDesc.clear();
                refreshAllData();
            } catch (Exception ex) { showAlert("נא להזין סכום תקין!"); }
        });

        form.getChildren().addAll(cmbType, txtCat, txtAmount, txtDesc, dpDate, btnAdd);

        Button btnDel = new Button("🗑️ מחק תנועה נבחרת");
        btnDel.getStyleClass().add("delete-button");
        btnDel.setOnAction(e -> {
            FinanceRecord sel = tblFinances.getSelectionModel().getSelectedItem();
            if(sel != null) {
                try { DatabaseHelper.deleteFinance(sel.getId()); refreshAllData(); }
                catch (SQLException ex) { ex.printStackTrace(); }
            }
        });

        setupTable(tblFinances, col("מזהה", "id", 60), col("סוג", "type", 110), col("קטגוריה", "category", 170), col("סכום (₪)", "amount", 140), col("תיאור", "description", 300), col("תאריך", "date", 150));
        tblFinances.setItems(financeList);
        VBox.setVgrow(tblFinances, Priority.ALWAYS);

        box.getChildren().addAll(form, btnDel, tblFinances);
        return box;
    }

    private VBox createMaterialsView() {
        VBox box = new VBox(15);
        box.setStyle("-fx-padding: 20;");

        HBox form = new HBox(10);
        form.setAlignment(Pos.CENTER_LEFT);
        form.setStyle("-fx-background-color: #111827; -fx-padding: 15; -fx-background-radius: 10; -fx-border-color: #1f2937;");

        TextField txtMatName = new TextField(); txtMatName.setPromptText("שם החומר");
        TextField txtQty = new TextField(); txtQty.setPromptText("כמות");
        TextField txtCost = new TextField(); txtCost.setPromptText("עלות (₪)");
        TextField txtSupplier = new TextField(); txtSupplier.setPromptText("ספק");
        DatePicker dpDate = new DatePicker(LocalDate.now());

        txtMatName.setPrefWidth(190); txtQty.setPrefWidth(130); txtCost.setPrefWidth(150); txtSupplier.setPrefWidth(170); dpDate.setPrefWidth(140);

        Button btnAdd = new Button("➕ הוסף חומר");
        btnAdd.setOnAction(e -> {
            try {
                if(txtMatName.getText().isEmpty() || txtCost.getText().isEmpty()) return;
                DatabaseHelper.addMaterial(txtMatName.getText(), txtQty.getText(), Double.parseDouble(txtCost.getText()), txtSupplier.getText(), dpDate.getValue());
                txtMatName.clear(); txtQty.clear(); txtCost.clear(); txtSupplier.clear();
                refreshAllData();
            } catch (Exception ex) { showAlert("שגיאה בנתוני העלות!"); }
        });

        form.getChildren().addAll(txtMatName, txtQty, txtCost, txtSupplier, dpDate, btnAdd);

        Button btnDel = new Button("🗑️ מחק חומר נבחר");
        btnDel.getStyleClass().add("delete-button");
        btnDel.setOnAction(e -> {
            MaterialRecord sel = tblMaterials.getSelectionModel().getSelectedItem();
            if(sel != null) {
                try { DatabaseHelper.deleteMaterial(sel.getId()); refreshAllData(); }
                catch (SQLException ex) { ex.printStackTrace(); }
            }
        });

        setupTable(tblMaterials, col("מזהה", "id", 60), col("שם החומר", "materialName", 220), col("כמות", "quantity", 140), col("עלות (₪)", "cost", 140), col("ספק", "supplier", 200), col("תאריך", "date", 140));
        tblMaterials.setItems(materialList);
        VBox.setVgrow(tblMaterials, Priority.ALWAYS);

        box.getChildren().addAll(form, btnDel, tblMaterials);
        return box;
    }

    @SuppressWarnings("unchecked")
    private <T> void setupTable(TableView<T> table, TableColumn<T, ?>... cols) {
        table.getColumns().clear();
        table.getColumns().addAll(cols);
    }

    private <T, V> TableColumn<T, V> col(String title, String property, double width) {
        TableColumn<T, V> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        c.setPrefWidth(width);
        return c;
    }

    private void refreshAllData() {
        try {
            workerList.setAll(DatabaseHelper.getAllWorkers());
            attendanceList.setAll(DatabaseHelper.getAllAttendance());
            financeList.setAll(DatabaseHelper.getAllFinances());
            materialList.setAll(DatabaseHelper.getAllMaterials());

            lblTotalWorkers.setText(String.valueOf(workerList.size()));

            double income = 0, expenses = 0, materialsCost = 0;
            for(FinanceRecord f : financeList) {
                if(f.getType().equals("إيراد") || f.getType().equals("הכנסה")) income += f.getAmount();
                else expenses += f.getAmount();
            }
            for(MaterialRecord m : materialList) materialsCost += m.getCost();

            lblTotalIncome.setText(String.format("₪ %.2f", income));
            lblTotalExpenses.setText(String.format("₪ %.2f", expenses));
            lblTotalMaterialsCost.setText(String.format("₪ %.2f", materialsCost));

            salaryList.clear();
            for(Worker w : workerList) {
                double effectiveDays = 0;
                for(AttendanceRecord a : attendanceList) {
                    if(a.getWorkerName().equals(w.getName())) {
                        if(a.getStatus().equals("נוכח") || a.getStatus().equals("حاضر")) effectiveDays += 1.0;
                        else if(a.getStatus().equals("חצי יום") || a.getStatus().equals("نصف يوم")) effectiveDays += 0.5;
                    }
                }
                double totalSalary = effectiveDays * w.getDailyWage();
                salaryList.add(new SalarySummary(w.getName(), w.getDailyWage(), effectiveDays, totalSalary));
            }

            if(attendanceChart != null) {
                attendanceChart.getData().clear();
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("רישומי נוכחות");
                for(AttendanceRecord a : attendanceList) {
                    series.getData().add(new XYChart.Data<>(a.getDate().toString(), 1));
                }
                attendanceChart.getData().add(series);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void applyGlobalStyles(Scene scene) {
        String css =
                ".root-pane { -fx-background-color: #0b0f19; -fx-font-family: 'Segoe UI', 'David', sans-serif; }" +
                        ".header-bar { -fx-background-color: #111827; -fx-padding: 15; -fx-border-color: #1f2937; -fx-border-width: 0 0 2 0; }" +

                        ".table-view { -fx-background-color: #111827; -fx-table-cell-border-color: #1f2937; -fx-background-radius: 10; -fx-border-color: #1f2937; -fx-border-radius: 10; }" +
                        ".table-view .column-header-background { -fx-background-color: #172033; -fx-background-radius: 10 10 0 0; }" +
                        ".table-view .column-header { -fx-background-color: transparent; -fx-border-color: #1f2937; }" +
                        ".table-view .column-header .label { -fx-text-fill: #fbbf24; -fx-font-weight: bold; -fx-font-size: 14px; }" +
                        ".table-row-cell { -fx-background-color: #111827; -fx-text-fill: #f8fafc; -fx-cell-size: 38px; }" +
                        ".table-row-cell:odd { -fx-background-color: #161e2e; }" +
                        ".table-row-cell:selected { -fx-background-color: #2563eb; -fx-text-fill: white; }" +
                        ".table-cell { -fx-text-fill: #e2e8f0; -fx-alignment: CENTER_RIGHT; }" +

                        ".text-field, .password-field, .combo-box, .date-picker { -fx-background-color: #1f2937; -fx-text-fill: white; -fx-prompt-text-fill: #94a3b8; -fx-border-color: #374151; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 8; }" +
                        ".text-field:focused, .password-field:focused, .combo-box:focused, .date-picker:focused { -fx-border-color: #fbbf24; -fx-effect: dropshadow(three-pass-box, rgba(251, 191, 36, 0.3), 8, 0, 0, 0); }" +

                        ".button { -fx-background-color: #d97706; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 18; -fx-cursor: hand; }" +
                        ".button:hover { -fx-background-color: #f59e0b; -fx-scale-x: 1.04; -fx-scale-y: 1.04; -fx-effect: dropshadow(three-pass-box, rgba(245, 158, 11, 0.4), 10, 0, 0, 0); }" +
                        ".button:pressed { -fx-scale-x: 0.97; -fx-scale-y: 0.97; }" +

                        ".delete-button { -fx-background-color: #dc2626; }" +
                        ".delete-button:hover { -fx-background-color: #ef4444; }" +

                        ".tab-pane .tab-header-background { -fx-background-color: #0b0f19; }" +
                        ".tab { -fx-background-color: #111827; -fx-background-radius: 8 8 0 0; -fx-padding: 8 18; -fx-border-color: #1f2937; -fx-cursor: hand; }" +
                        ".tab:selected { -fx-background-color: #2563eb; -fx-scale-y: 1.05; }" +
                        ".tab .tab-label { -fx-text-fill: #94a3b8; -fx-font-weight: bold; }" +
                        ".tab:selected .tab-label { -fx-text-fill: white; }";

        scene.getStylesheets().add("data:text/css," + css.replace("#", "%23"));
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("הודעת מערכת");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static class SalarySummary {
        private String workerName;
        private double dailyWage, effectiveDays, totalSalary;
        public SalarySummary(String workerName, double dailyWage, double effectiveDays, double totalSalary) {
            this.workerName = workerName; this.dailyWage = dailyWage; this.effectiveDays = effectiveDays; this.totalSalary = totalSalary;
        }
        public String getWorkerName() { return workerName; }
        public double getDailyWage() { return dailyWage; }
        public double getEffectiveDays() { return effectiveDays; }
        public double getTotalSalary() { return totalSalary; }
    }

    public static void main(String[] args) {
        launch(args);
    }
}