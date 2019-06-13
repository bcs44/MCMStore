package mestrado.ipg.mcmstore.Globals;

public class Sensor {

    private String sensor_id;
    private String description;
    private String brand;
    private String identification;
    private String sensortype_id;

    public Sensor() {
        this.sensor_id = "";
        this.description = "";
        this.brand = "";
        this.identification = "";
        this.sensortype_id = "";
    }

    public String getSensor_id() {
        return sensor_id;
    }

    public void setSensor_id(String sensor_id) {
        this.sensor_id = sensor_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getSensortype_id() {
        return sensortype_id;
    }

    public void setSensortype_id(String sensortype_id) {
        this.sensortype_id = sensortype_id;
    }
}
