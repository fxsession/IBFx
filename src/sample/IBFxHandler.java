package sample;

import com.ib.client.Contract;
import com.ib.client.EWrapperMsgGenerator;
import com.ib.client.Order;
import com.ib.client.OrderState;
import org.hesaid.common.fileops.SimpleLogger;
import org.hesaid.trading.ib.apiconnector.IBEventHandler;
import org.hesaid.trading.ib.apiconnector.misc.OrderStateEx;

import java.math.BigDecimal;

public class IBFxHandler implements IBEventHandler {
    final private IBFxBase base;
    public IBFxHandler(IBFxBase base) {
        this.base = base;
    }

    @Override
    public void onOrderStatus(int i, OrderStateEx orderState, BigDecimal filled, BigDecimal remaining) {
        base.onIBEvent("Order status :" + i + ","+orderState.get());
    }

    @Override
    public void onOpenOrder(int i, Contract contract, Order order, OrderState orderState) {
        base.onIBEvent("Open order :" + i + ","+ EWrapperMsgGenerator.openOrder(i, contract, order, orderState));
    }

    @Override
    public void onOpenOrderEnd() {

    }
}
