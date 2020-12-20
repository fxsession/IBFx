package sample;

import org.hesaid.trading.ib.apiconnector.Connector;
import org.hesaid.trading.ib.apiconnector.exception.IBException;
import org.hesaid.trading.ib.apiconnector.misc.ActionTypes;
import org.hesaid.trading.ib.apiconnector.misc.OrderTypes;
import org.hesaid.trading.ib.apiconnector.order.OrderStatus;
import org.hesaid.trading.ib.apiconnector.stock.BaseStock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class IBFxOrder {

    private final BaseStock ibContract;
    public IBFxOrder(String symbol){
        this.ibContract = setup(symbol);
    }
    private BaseStock setup(String symbol){
        BaseStock stock = new BaseStock();
        stock.setSymbol(symbol)
                .setSecType(IBFxBase.settings.secType)
                .setCurrency(IBFxBase.settings.currency)
                .setExchange(IBFxBase.settings.exchange)
                .setPrimeExchange(IBFxBase.settings.primeExchange);
        return stock;
    }
    public int placeStop(ActionTypes direction,int quantity, BigDecimal price) throws IBException,InterruptedException  {
        int ordId = Connector.getInstance().getOrderBus().placeStopOrder(
                    ibContract,
                    direction,
                    quantity,
                    price);
        Thread.sleep(1000);
        if (Connector.getInstance().hasMessageInResponse())
            throw new IBException(
                        Connector.getInstance().getLastErrorCode(),
                        Connector.getInstance().getLastErrorMessage());
        return ordId;
    }
    public  List<Integer> placeStopBracket(ActionTypes direction,int quantity, BigDecimal price,BigDecimal deltaPrice) throws IBException,InterruptedException  {
        List<Integer> orderList= Connector.getInstance().getOrderBus().placeStopBracketOrder(
                ibContract,
                direction,
                quantity,
                price,
                deltaPrice);
        Thread.sleep(1000);
        if (Connector.getInstance().hasMessageInResponse())
            throw new IBException(
                    Connector.getInstance().getLastErrorCode(),
                    Connector.getInstance().getLastErrorMessage());
        return orderList;
    }

    public int placeLimit(ActionTypes direction,int quantity, BigDecimal price) throws IBException,InterruptedException  {
        int ordId = Connector.getInstance().getOrderBus().placeLimitOrder(
                ibContract,
                direction,
                quantity,
                price);
        Thread.sleep(1000);
        if (Connector.getInstance().hasMessageInResponse())
            throw new IBException(
                    Connector.getInstance().getLastErrorCode(),
                    Connector.getInstance().getLastErrorMessage());
        return ordId;
    }

    public void cancelAll() throws IBException {
        Connector.getInstance().getOrderBus().cancelAll();
    }
    public void updatePrice(int orderId, BigDecimal price) throws IBException {
        Connector.getInstance().getOrderBus().
                updateOrderPrice(orderId, price);
    }
    public void updateSize(int orderId, int size) throws IBException {
        Connector.getInstance().getOrderBus().
                updateOrderSize(orderId, size);
    }
}
