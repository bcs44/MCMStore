package mestrado.ipg.mcmstore.Globals;

public class Manutencao {

    private String maintenance_id;
    private String maintenance_date;
    private String description;
    private String place_id;
    private String address;
    private String floor;
    private String door;
    private String user_id;
    private String username;
    private String email;
    private static Manutencao instance;


    public Manutencao() {
        this.maintenance_id = "";
        this.maintenance_date = "";
        this.description = "";
        this.place_id = "";
        this.address = "";
        this.floor = "";
        this.door = "";
        this.user_id = "";
        this.username = "";
        this.email = "";
    }

    public String getMaintenance_id() {
        return maintenance_id;
    }

    public void setMaintenance_id(String maintenance_id) {
        this.maintenance_id = maintenance_id;
    }

    public String getMaintenance_date() {
        return maintenance_date;
    }

    public void setMaintenance_date(String maintenance_date) {
        this.maintenance_date = maintenance_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getDoor() {
        return door;
    }

    public void setDoor(String door) {
        this.door = door;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static synchronized Manutencao getInstance() {
        if (instance == null) {
            instance = new Manutencao();
        }

        return instance;
    }

}