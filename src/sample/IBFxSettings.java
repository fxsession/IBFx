package sample;

import javafx.scene.control.Alert;
import org.hesaid.common.fileops.SettingsManager;

import java.io.IOException;

public class IBFxSettings {
    public String ip;
    public int port;
    public String secType ;
    public String currency;
    public String exchange;
    public String primeExchange = "ISLAND";
    public String accountId = "DU2286498";
    public String defaultSymbol = "";
    public static IBFxSettings init(String fileName) throws IOException {
        return (IBFxSettings) SettingsManager.deserialize(fileName,IBFxSettings.class);
    }
    @Override
    public String toString(){
        return SettingsManager.getString(this);
    }
}
