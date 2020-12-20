package sample;

public class IBFxRowOrder {
    String id;
    String symbol;
    String type;
    String oper;
    String auxprice;
    String lmtprice;
    String volume;
    String status;

    public IBFxRowOrder(String id, String symbol, String type, String oper,String auxprice, String lmtprice, String volume, String status) {
        this.id = id;
        this.symbol = symbol;
        this.type = type;
        this.oper = oper;
        this.auxprice = (auxprice == null? "0": auxprice);
        this.lmtprice = (lmtprice == null? "0": lmtprice);
        this.volume = volume;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getType() {
        return type;
    }
    public String getOper() {
        return oper;
    }


    public void setType(String type) {
        this.type = type;
    }
    public void setOper(String oper) {
        this.type = oper;
    }

    public String getAuxprice() {
        return auxprice;
    }

    public void setAuxprice(String price) {
        this.auxprice = price;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLmtprice() {
        return lmtprice;
    }

    public void setLmtprice(String lmtprice) {
        this.lmtprice = lmtprice;
    }
}