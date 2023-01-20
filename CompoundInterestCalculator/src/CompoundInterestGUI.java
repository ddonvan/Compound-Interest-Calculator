import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Compound Interest GUI
 * @author danieldonovan
 */
public class CompoundInterestGUI extends Application {

    private BarChart<String, Number> barChart;
    private TableView<CompoundInterest> table;
    private ObservableList<CompoundInterest> compIntCalc;

    private TextField principleTF;
    private TextField annualTF;
    private ComboBox compFreqCB;
    private TextField numYearsTF;

    /**
     * Start function for GUI.
     * Creates all nodes used for GUI.
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {

        BorderPane bp = new BorderPane();

        //CALCULATOR BOX
        VBox calculator = new VBox(10);
        calculator.setPadding(new Insets(10,10,10,10));

        Label compIntLabel = new Label("Compound Interest Calculator");
        compIntLabel.setFont(new Font("Arial", 25));

        Label principle = new Label("Principle ($):");
        principle.setFont(new Font("Arial", 15));
        principle.setTranslateY(10);
        principleTF = new TextField();
        principleTF.setMaxWidth(200);
        principleTF.setTranslateY(10);

        Label annual = new Label("Annual Interest (%):");
        annual.setFont(new Font("Arial", 15));
        annual.setTranslateY(10);
        annualTF = new TextField();
        annualTF.setMaxWidth(200);
        annualTF.setTranslateY(10);

        Label compFreq = new Label("Compounding Frequency:");
        compFreq.setFont(new Font("Arial", 15));
        compFreq.setTranslateY(10);
        ObservableList<String> compFreqSource =
                FXCollections.observableArrayList("Monthly",
                        "Quarterly", "Semi-Annually", "Annually");
        compFreqCB = new ComboBox<>(compFreqSource);
        compFreqCB.setMaxWidth(200);
        compFreqCB.setTranslateY(10);

        Label numYears = new Label("Number of Years:");
        numYears.setFont(new Font("Arial", 15));
        numYears.setTranslateY(10);
        numYearsTF = new TextField();
        numYearsTF.setMaxWidth(200);
        numYearsTF.setTranslateY(10);

        calculator.getChildren().addAll(compIntLabel, principle,
                principleTF, annual, annualTF, compFreq, compFreqCB,
                numYears, numYearsTF);

        Button calc = new Button("Calculate");
        calc.setTranslateY(10);

        calc.setOnAction(actionEvent -> {
            getData();
            drawChart();
        });

        calculator.getChildren().add(calc);

        //TABLE BOX
        VBox tableBox = new VBox(10);
        tableBox.setPadding(new Insets(10,10,10,10));

        configureTable();
        table.setPrefWidth(600);
        table.setPrefHeight(240);
        table.setTranslateY(50);

        tableBox.getChildren().addAll(table);

        HBox top = new HBox();
        top.getChildren().addAll(calculator, table);

        //GRAPH BOX
        VBox graph = new VBox(10);
        graph.setPadding(new Insets(10,10,10,10));
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Year");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Closing Balance");
        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Balance Growth over Years");
        barChart.setAnimated(false);
        barChart.setLegendVisible(false);
        barChart.setPrefHeight(380);

        graph.getChildren().addAll(barChart);

        bp.setTop(top);
        bp.setBottom(graph);

        Scene scene = new Scene(bp, 1000, 800);
        stage.setScene(scene);
        stage.show();

    }

    /**
     * this function retrieves entry from textfield and combo box,
     * and creates compound interest objects used to create table
     * and bar chart
     */
    private void getData() {
        
        Alert alert = new Alert(Alert.AlertType.ERROR);

        int frequency = 0;
        if(compFreqCB.getValue() == "Monthly") {frequency = 12;}
        else if(compFreqCB.getValue() == "Quarterly") {frequency = 4;}
        else if(compFreqCB.getValue() == "Semi-Annually") {frequency = 2;}
        else frequency = 1;


        List<CompoundInterest> compoundInterestList = new ArrayList<>();
        try {
            double principle = Double.parseDouble(String.format(principleTF.getText()));
            for (int i = 1; i <= Integer.parseInt(numYearsTF.getText()); i++) {
                CompoundInterest compoundInterest = new CompoundInterest(i,
                        principle, (Double.parseDouble(annualTF.getText()) / 100),
                        frequency);
                compoundInterestList.add(compoundInterest);
                principle = compoundInterest.getClosingBalance();
            }
            /**
             * Error checking for each entry field.
             * Displays appropriate error message for
             * correct error.
             */
        } catch (NumberFormatException nfe){
            if(principleTF.getText().isEmpty()){
                alert.setContentText("Please enter a principle value.");
                alert.show();
            }
            else if(annualTF.getText().isEmpty()){
                alert.setContentText("Please enter an annual interest value.");
                alert.show();
            }
            else if(numYearsTF.getText().isEmpty()){
                alert.setContentText("Please enter number of years.");
                alert.show();
            }
            else {
                alert.setContentText("Invalid input, please enter a number");
                alert.show();
            }
        }

        /**
         * error check for unselected combo box
         */
        compIntCalc.setAll(compoundInterestList);
        if(compFreqCB.getValue() == null) {
            alert.setContentText("Please choose a compounding frequency.");
            alert.show();
        }

        /**
         * reset fields after
         */

    }

    /**
     * this function configures the table with corresponding data
     */
    private void configureTable(){
        table = new TableView<>();
        compIntCalc = FXCollections.observableArrayList();
        table.setItems(compIntCalc);

        TableColumn<CompoundInterest, Integer> yearCol = new TableColumn<>("Year");
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        yearCol.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        table.getColumns().add(yearCol);

        TableColumn<CompoundInterest, Double> opBalCol = new TableColumn<>("Opening Balance");
        opBalCol.setCellValueFactory(new PropertyValueFactory<>("openingBalance"));
        opBalCol.prefWidthProperty().bind(table.widthProperty().multiply(0.3));
        table.getColumns().add(opBalCol);

        NumberFormat opBalFormat= NumberFormat.getCurrencyInstance();
        opBalCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                opBalFormat.setMaximumFractionDigits(2);
                setText(empty ? "" : opBalFormat.format(value));
            }
        });

        TableColumn<CompoundInterest, Double> interestCol = new TableColumn<>("Interest");
        interestCol.setCellValueFactory(new PropertyValueFactory<>("interest"));
        interestCol.prefWidthProperty().bind(table.widthProperty().multiply(0.3));
        table.getColumns().add(interestCol);

        NumberFormat interestFormat= NumberFormat.getCurrencyInstance();
        interestCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                interestFormat.setMaximumFractionDigits(2);
                setText(empty ? "" : interestFormat.format(value));
            }
        });

        TableColumn<CompoundInterest, Double> cloBalCol = new TableColumn<>("Closing Balance");
        cloBalCol.setCellValueFactory(new PropertyValueFactory<>("closingBalance"));
        cloBalCol.prefWidthProperty().bind(table.widthProperty().multiply(0.3));
        table.getColumns().add(cloBalCol);

        NumberFormat cloBalFormat= NumberFormat.getCurrencyInstance();
        cloBalCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                cloBalFormat.setMaximumFractionDigits(2);
                setText(empty ? "" : cloBalFormat.format(value));
            }
        });
    }

    /**
     * this function draws out the chart using data retrieved
     * from function getData()
     */
    private void drawChart(){
        barChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        for (CompoundInterest ci : compIntCalc){
            series.getData().add(new XYChart.Data<>(String.valueOf(ci.getYear()), ci.getClosingBalance()));
        }
        barChart.getData().add(series);
    }
}
