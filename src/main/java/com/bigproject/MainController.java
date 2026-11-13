package com.bigproject;



import com.example.bigproject.Income1;
import com.example.workershekel.DatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import java.time.LocalDate;

public class MainController {

    @FXML private Label lblTotalIncome;
    @FXML private Label lblTotalExpenses;
    @FXML private Label lblNetProfit;
    @FXML private TextField txtAmount;
    @FXML private TextField txtDescription;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbType;
    @FXML private PieChart pieChartSummary;

    @FXML private TableView<Income1> tblTransactions;
    @FXML private TableColumn<Income1, Integer> colId;
    @FXML private TableColumn<Income1, String> colType;
    @FXML private TableColumn<Income1, Double> colAmount;
    @FXML private TableColumn<Income1, String> colDescription;
    @FXML private TableColumn<Income1, LocalDate> colDate;

    private ObservableList<Income1> transactionList = FXCollections.observableArrayList();
    private FilteredList<Income1> filteredData;
    private double totalIncome = 0.0;
    private double totalExpenses = 0.0;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colType.setCellValueFactory(new PropertyValueFactory<>("clientName"));

        cmbType.setItems(FXCollections.observableArrayList("إيرادات (Income)", "مصاريف (Expense)"));
        cmbType.getSelectionModel().selectFirst();

        filteredData = new FilteredList<>(transactionList, p -> true);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(transaction -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return transaction.getDescription().toLowerCase().contains(lowerCaseFilter);
            });
        });

        tblTransactions.setItems(filteredData);
        tblTransactions.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        loadDataFromDatabase();
    }

    @FXML
    void handleInsert() {
        String amountText = txtAmount.getText();
        String description = txtDescription.getText();
        String selectedType = cmbType.getValue().contains("إيرادات") ? "إيرادات" : "مصاريف";

        if (amountText.isEmpty() || description.isEmpty()) {
            showAlert("تنبيه", "الرجاء تعبئة الحقول!", Alert.AlertType.WARNING);
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            String dateToday = LocalDate.now().toString();

            String insertSQL = "INSERT INTO transactions (type, amount, description, date) VALUES (?, ?, ?, ?)";
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                pstmt.setString(1, selectedType);
                pstmt.setDouble(2, amount);
                pstmt.setString(3, description);
                pstmt.setString(4, dateToday);
                pstmt.executeUpdate();
            }

            loadDataFromDatabase();
            txtAmount.clear();
            txtDescription.clear();

        } catch (NumberFormatException e) {
            showAlert("خطأ", "برجاء إدخال مبلغ صحيح!", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            showAlert("خطأ في قاعدة البيانات", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void handleDeleteSelected() {
        Income1 selectedItem = tblTransactions.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("تنبيه", "الرجاء تحديد معاملة من الجدول لحذفها!", Alert.AlertType.WARNING);
            return;
        }

        String deleteSQL = "DELETE FROM transactions WHERE id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, selectedItem.getId());
            pstmt.executeUpdate();

            loadDataFromDatabase();
            showAlert("نجاح", "تم حذف المعاملة بنجاح", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("خطأ", "فشل الحذف: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadDataFromDatabase() {
        transactionList.clear();
        totalIncome = 0.0;
        totalExpenses = 0.0;

        String selectSQL = "SELECT * FROM transactions";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                String description = rs.getString("description");
                LocalDate date = LocalDate.parse(rs.getString("date"));

                Income1 transaction = new Income1(id, amount, date, description, type);
                transactionList.add(transaction);

                if (type.equals("إيرادات")) {
                    totalIncome += amount;
                } else {
                    totalExpenses += amount;
                }
            }
            updateDashboardCards();
            updatePieChart();
        } catch (SQLException e) {
            System.out.println("خطأ أثناء جلب البيانات: " + e.getMessage());
        }
    }

    private void updateDashboardCards() {
        lblTotalIncome.setText(String.format("%.2f $", totalIncome));
        lblTotalExpenses.setText(String.format("%.2f $", totalExpenses));
        double netProfit = totalIncome - totalExpenses;
        lblNetProfit.setText(String.format("%.2f $", netProfit));
    }

    private void updatePieChart() {
        PieChart.Data incomeData = new PieChart.Data("إيرادات", totalIncome);
        PieChart.Data expenseData = new PieChart.Data("مصاريف", totalExpenses);
        pieChartSummary.setData(FXCollections.observableArrayList(incomeData, expenseData));
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}