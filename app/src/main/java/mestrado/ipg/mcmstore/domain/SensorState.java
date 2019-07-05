package mestrado.ipg.mcmstore.domain;

public class SensorState {

    private int sensor_id;
    private int active_sensor_id;
    private String identification;
    private String description;
    private String type;
    private Double latitude;
    private Double longitude;
    private int state;
    private int place_id;
    private String place;

    public SensorState(){};

    public SensorState(int active_sensor_id, int sensor_id, String identification,
                       String description, Double latitude,
                       Double longitude, int state, int place_id, String place, String type) {
        this.active_sensor_id = active_sensor_id;
        this.sensor_id = sensor_id;
        this.identification = identification;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.state = state;
        this.place_id = place_id;
        this.place = place;
        this.type = type;

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPlace_id() {
        return place_id;
    }

    public void setPlace_id(int place_id) {
        this.place_id = place_id;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getActive_sensor_id() {
        return active_sensor_id;
    }

    public void setActive_sensor_id(int active_sensor_id) {
        this.active_sensor_id = active_sensor_id;
    }

    public int getSensor_id() {
        return sensor_id;
    }

    public void setSensor_id(int sensor_id) {
        this.sensor_id = sensor_id;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
