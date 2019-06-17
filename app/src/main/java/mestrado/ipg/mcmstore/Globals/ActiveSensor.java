package mestrado.ipg.mcmstore.Globals;

public class ActiveSensor {

    private static ActiveSensor instance;
    String sensor_id;
    String townhouse_id;
    String state;
    String sensorTypeDes;


    public String getSensorTypeDes() {
        return sensorTypeDes;
    }

    public void setSensorTypeDes(String sensorTypeDes) {
        this.sensorTypeDes = sensorTypeDes;
    }

    public String getSensor_id() {
        return sensor_id;
    }

    public void setSensor_id(String sensor_id) {
        this.sensor_id = sensor_id;
    }

    public String getTownhouse_id() {
        return townhouse_id;
    }

    public void setTownhouse_id(String townhouse_id) {
        this.townhouse_id = townhouse_id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public static synchronized ActiveSensor getInstance() {
        if (instance == null) {
            instance = new ActiveSensor();
        }

        return instance;
    }


}
