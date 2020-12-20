package sample;

import com.ib.client.Order;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import org.hesaid.common.fileops.FileManager;
import org.hesaid.common.fileops.SimpleLogger;
import org.hesaid.trading.ib.apiconnector.Connector;
import org.hesaid.trading.ib.apiconnector.exception.IBException;
import org.hesaid.trading.ib.apiconnector.misc.ActionTypes;
import org.hesaid.trading.ib.apiconnector.order.OrderBus;
import org.hesaid.trading.ib.apiconnector.order.OrderStatus;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Map;
import java.util.Observable;


public class IBFxController {
    private String instrument;
    private int quantity = 0;
    private BigDecimal price = BigDecimal.ZERO,deltaStop =BigDecimal.ZERO;
    public Label labConnectionStatus,labInfo;
    public TextField fldInst,fldQuant,fldPrice,fldDeltaStop,fldOrderID;
    @FXML
    public TableView  <IBFxRowOrder> tblOrders;
    @FXML
    public TableColumn <IBFxRowOrder,String> colId;
    @FXML
    public TableColumn <IBFxRowOrder,String> colSymbol;
    @FXML
    public TableColumn <IBFxRowOrder,String> colType;
    @FXML
    public TableColumn <IBFxRowOrder,String> colAuxPrice;
    @FXML
    public TableColumn <IBFxRowOrder,String> colLmtPrice;
    @FXML
    public TableColumn <IBFxRowOrder,String> colVolume;
    @FXML
    public TableColumn <IBFxRowOrder,String> colStatus;
    @FXML
    public TableColumn <IBFxRowOrder,String> colOperation;
    public TextArea fldOperStat;
    private String curDir;
    private final IBFxBase ibFxBase;
    IBFxOrder order;
    public IBFxController(){
        curDir = FileManager.getCurDirectory();
        ibFxBase= new IBFxBase(this);
    }
    @FXML
    public void initialize() {
        String loggerPath = new File(FileManager.getCurDirectory(),IBFxBase.appLogFile).getPath();
        SimpleLogger.init(new File(FileManager.getCurDirectory(),IBFxBase.appLogFile).getPath());
        Connector.logPath=loggerPath;
        fldOperStat.setText("");
        String settingsPath = new File(FileManager.getCurDirectory(),IBFxBase.appSetFile).getPath();
        try {
            IBFxBase.settings = IBFxSettings.init(settingsPath);
            labInfo.setText("Log file: " + curDir + "   " + IBFxBase.settings.ip + ":" + IBFxBase.settings.port);
            fldInst.setText(IBFxBase.settings.defaultSymbol);
            order = new IBFxOrder(IBFxBase.settings.defaultSymbol);
            SimpleLogger.log("IBFX initialised");
        }catch (IOException e) {
            showAlert(Alert.AlertType.ERROR,e.getMessage());
        }
        onConnect();
        setupTable();
    }
    private void setupTable(){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSymbol.setCellValueFactory(new PropertyValueFactory<>("symbol"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colAuxPrice.setCellValueFactory(new PropertyValueFactory<>("auxprice"));
        colLmtPrice.setCellValueFactory(new PropertyValueFactory<>("lmtprice"));
        colVolume.setCellValueFactory(new PropertyValueFactory<>("volume"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colOperation.setCellValueFactory(new PropertyValueFactory<>("oper"));
        tblOrders.setItems(ibFxBase.ordersList);
        updateOrdersList();
    }
    private void readFields(){
        instrument = fldInst.getText();
        if (instrument==null)
            showAlert(Alert.AlertType.ERROR, "Instrument value - null");

        String temp = fldQuant.getText();
        if (temp.isEmpty())
            showAlert(Alert.AlertType.ERROR, "Quantity value  -null");
        else
            quantity = Integer.parseInt(temp);
        temp = fldPrice.getText();
        if (temp.isEmpty())
            showAlert(Alert.AlertType.ERROR, "Price value  -null");
        else
            price = new BigDecimal(temp);

        temp = fldDeltaStop.getText();
        if (!temp.isEmpty())
            deltaStop = new BigDecimal(fldDeltaStop.getText());
    }
    private void showAlert(Alert.AlertType type, String message){
        Platform.runLater(() -> {
            fldOperStat.appendText(message + "\n");
        });
        Alert alert = new Alert(type);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
    private void updateOrdersList(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Platform.runLater(() -> {
            onUpdateReqOrders();
        });
    }

    public void shutdown() {
        ibFxBase.disconnect();
        SimpleLogger.log("IBFX shutdown");
    }
    public void onConnect(){
        try {
            ibFxBase.connect();
            labConnectionStatus.setText("connected");
            labConnectionStatus.setTextFill(Color.GREEN);
        } catch (IBException e) {
            showAlert(Alert.AlertType.ERROR,e.getMessage());
        }
    }
    public void onDisconnect(){
        ibFxBase.disconnect();
        labConnectionStatus.setText("disconnected");
        labConnectionStatus.setTextFill(Color.RED);
    }
    public void onBuyStpSL()  {
       readFields();
       if (price.compareTo(BigDecimal.ZERO)<=0
               || quantity <=0)
        {
           showAlert(Alert.AlertType.ERROR, "Values can't be zero");
           return;
       }
        try {
            if (deltaStop.compareTo(BigDecimal.ZERO)<=0)
                order.placeStopBracket(ActionTypes.BUY,quantity,price,deltaStop);
            else
                order.placeStop(ActionTypes.BUY,quantity,price);
        } catch (IBException | InterruptedException e) {
            showAlert(Alert.AlertType.ERROR,e.getMessage());
        }
    }
    public void onSellStpSL(){
        readFields();
        if (price.compareTo(BigDecimal.ZERO)<=0
                || quantity <=0)
        {
            showAlert(Alert.AlertType.ERROR, "Values can't be zero");
            return;
        }
        try {
            if (deltaStop.compareTo(BigDecimal.ZERO)<=0)
                order.placeStopBracket(ActionTypes.SELL,quantity,price,deltaStop);
            else
                order.placeStop(ActionTypes.SELL,quantity,price);
        } catch (IBException | InterruptedException e) {
            showAlert(Alert.AlertType.ERROR,e.getMessage());
        }
    }
    public void onBuyLimit()  {
        readFields();
        if (price.compareTo(BigDecimal.ZERO)<=0
                || quantity <=0)
        {
            showAlert(Alert.AlertType.ERROR, "Values can't be zero");
            return;
        }
        try {
            order.placeLimit(ActionTypes.BUY,quantity,price);
        } catch (IBException | InterruptedException e ) {
            showAlert(Alert.AlertType.ERROR,e.getMessage());
        }
    }
    public void onSellLimit()  {
        readFields();
        if (price.compareTo(BigDecimal.ZERO)<=0
                || quantity <=0)
        {
            showAlert(Alert.AlertType.ERROR, "Values can't be zero");
            return;
        }
        try {
            order.placeLimit(ActionTypes.SELL,quantity,price);
        } catch (IBException | InterruptedException e) {
            showAlert(Alert.AlertType.ERROR,e.getMessage());
        }
    }
    public void onUpdateQuant(){
        String orderId = fldOrderID.getText();
        if (orderId.isEmpty()){
            showAlert(Alert.AlertType.ERROR,"Empty order ID");
            return;
        }
        try {
            order.updateSize(Integer.parseInt(orderId),Integer.parseInt(fldQuant.getText()));
        } catch (IBException e) {
            showAlert(Alert.AlertType.ERROR,e.getMessage());
        }
    }
    public void onUpdatePrice(){
        String orderId = fldOrderID.getText();
        if (orderId.isEmpty()){
            showAlert(Alert.AlertType.ERROR,"Empty order ID");
            return;
        }
        try {
            order.updatePrice(Integer.parseInt(orderId),new BigDecimal(fldPrice.getText()));
        } catch (IBException e) {
            showAlert(Alert.AlertType.ERROR,e.getMessage());
        }
    }

    public void onCancelAll(){
        try {
            order.cancelAll();
        } catch (IBException e) {
            showAlert(Alert.AlertType.ERROR,e.getMessage());
        }
    }

    public void onUpdateReqOrders(){
        ibFxBase.getActiveOrders();
    }

    public void onIBEvent(String var){
        Platform.runLater(() -> {
            fldOperStat.appendText(var + "\n");
        });
        updateOrdersList();
    }

}
