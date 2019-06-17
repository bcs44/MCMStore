package mestrado.ipg.mcmstore.Globals;


public class FileGlobal {

    private String base64;
    private String type;
    private static FileGlobal instance;



    public FileGlobal() {
        this.base64 = "";
        this.type = "";
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static synchronized FileGlobal getInstance() {
        if (instance == null) {
            instance = new FileGlobal();
        }

        return instance;
    }

}