package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.hesaid.common.fileops.SimpleLogger;
import org.hesaid.trading.ib.apiconnector.Connector;
import org.hesaid.trading.ib.apiconnector.exception.IBException;
import org.hesaid.trading.ib.apiconnector.misc.IBCodes;
import org.hesaid.trading.ib.apiconnector.order.OrderBus;

import java.util.Map;

public class IBFxBase {
    private final IBFxController parentController;
    public static IBFxSettings settings;
    public static String appLogFile = "application";
    public static String appSetFile = "settings.json";
    public IBFxBase(IBFxController parentController){
        this.parentController = parentController;
    }
    public  ObservableList<IBFxRowOrder> ordersList = FXCollections.observableArrayList();
    public void connect() throws IBException {
        SimpleLogger.log(settings.toString());
        Connector.getInstance().setCustomEventHandler(new IBFxHandler(this));
        Connector.getInstance().
                setConnectionParams(settings.ip,settings.port,settings.accountId).
                connect();
        try {
            Thread.sleep(1000);
            if (!Connector.getInstance().isConnected())
                throw new IBException(IBCodes.CONNECT_FAIL, "No connection to the IB Gateway");
        }catch (InterruptedException e){
            SimpleLogger.logExceptionWithRuntime(e);
        }
    }

    public void getActiveOrders() {
        ordersList.clear();
        for (Map.Entry<Integer, OrderBus.ActiveOrder> entry : Connector.getInstance().getOrderBus().getActiveOrdersMap().entrySet()) {
            ordersList.add(new IBFxRowOrder(
                    entry.getKey().toString(),
                    entry.getValue().contract.symbol(),
                    entry.getValue().order.getOrderType().get(),
                    entry.getValue().order.getAction().get(),
                    entry.getValue().order.getAuxPrice().toString(),
                    entry.getValue().order.getLmtPrice().toString(),
                    String.valueOf(entry.getValue().order.getQuantity()),
                    entry.getValue().status.get()));
        }

    }

    public void disconnect(){
        Connector.getInstance().disconnect();
    }
    protected  void onIBEvent(String var){parentController.onIBEvent(var);}

}
