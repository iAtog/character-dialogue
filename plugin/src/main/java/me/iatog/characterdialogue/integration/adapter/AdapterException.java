package me.iatog.characterdialogue.integration.adapter;

public class AdapterException extends Exception {
    private final String adapterName;

    public AdapterException(String adapterName, String message) {
        super(message);
        this.adapterName = adapterName;
    }

    public AdapterException(String adapterName, String message, Throwable cause) {
        super(message, cause);
        this.adapterName = adapterName;
    }

    public String getAdapterName() {
        return adapterName;
    }
}