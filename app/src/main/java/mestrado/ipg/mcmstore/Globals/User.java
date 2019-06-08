package mestrado.ipg.mcmstore.Globals;

public class User{

    private String user_id;
    private String username;
    private String api_key;
    private String api_sign;
    private String nonce;
    private String email;
    private String rule_id;

    private static User instance;

    public User(){
        this.user_id = "";
        this.username = "";
        this.api_key = "";
        this.api_sign = "";
        this.nonce = "";
        this.email = "";
        this.rule_id = "";
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

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getApi_sign() {
        return api_sign;
    }

    public void setApi_sign(String api_sign) {
        this.api_sign = api_sign;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRule_id() {
        return rule_id;
    }

    public void setRule_id(String rule_id) {
        this.rule_id = rule_id;
    }

    public static synchronized User getInstance(){
        if(instance==null){
            instance = new User();
        }

        return instance;
    }

}