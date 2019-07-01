package mestrado.ipg.mcmstore.domain;

import java.util.Date;

public class Record {
    Double value;
    String measureUnit;
    Date recordDate;
    int placeId;

    public Record(){}

    public Record(Double value, String measureUnit, Date recordDate, int placeId) {
        this.value = value;
        this.measureUnit = measureUnit;
        this.recordDate = recordDate;
        this.placeId = placeId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(String measureUnit) {
        this.measureUnit = measureUnit;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }
}
