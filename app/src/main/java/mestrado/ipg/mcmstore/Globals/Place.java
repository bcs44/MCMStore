package mestrado.ipg.mcmstore;

public class Place{

    private String _id;
    private String _desc;

    public Place(){
        this._id = "";
        this._desc = "";
    }

    public void setId(String id){
        this._id = id;
    }

    public String getId(){
        return this._id;
    }

    public void setDesc(String desc){
        this._desc = desc;
    }

    public String getDesc(){
        return this._desc;
    }
}