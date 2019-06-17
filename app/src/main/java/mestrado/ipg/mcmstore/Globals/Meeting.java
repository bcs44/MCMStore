package mestrado.ipg.mcmstore.Globals;

public class Meeting {


    private String titulo;
    private String descricao;
    private String data;
    private static Meeting instance;

    public Meeting() {
        this.titulo = "";
        this.descricao = "";
        this.data = "";
    }


    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public static synchronized Meeting getInstance() {
        if (instance == null) {
            instance = new Meeting();
        }

        return instance;
    }


}
