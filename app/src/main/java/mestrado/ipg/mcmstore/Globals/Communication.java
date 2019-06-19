package mestrado.ipg.mcmstore.Globals;

public class Communication {

    private String title;
    private String description;
    private String registry_date;
    private String user_id;
    private String communication_id;
    private String confirmation;
    private static Communication instance;


    public Communication() {
        this.title = "";
        this.description = "";
        this.registry_date = "";
        this.user_id = "";
    }

    public String getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }

    public String getCommunication_id() {
        return communication_id;
    }

    public void setCommunication_id(String communication_id) {
        this.communication_id = communication_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegistry_date() {
        return registry_date;
    }

    public void setRegistry_date(String registry_date) {
        this.registry_date = registry_date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public static synchronized Communication getInstance() {
        if (instance == null) {
            instance = new Communication();
        }

        return instance;
    }

}