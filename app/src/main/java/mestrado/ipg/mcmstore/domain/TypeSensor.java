package mestrado.ipg.mcmstore.domain;

public enum TypeSensor {
    Gas("co2"), Luminosity("luminosidade"), Humidity("humidade"), Temperature("temperatura");

    private String value;

    TypeSensor(String value){
        this.value  = value;
    }

    public String getValue(){
        return this.value;
    }


}
