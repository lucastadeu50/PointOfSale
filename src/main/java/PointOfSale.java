import com.opencsv.bean.CsvBindByName;

public class PointOfSale {

    @CsvBindByName
    String pos_id;
    @CsvBindByName(column = "descricaoatividadecomercial")
    String descricaoAtividadeComercial;
    @CsvBindByName(column = "descricaofaixa")
    String descricaoFaixa;
    @CsvBindByName
    Double latitude;
    @CsvBindByName
    Double longitude;

    public String getPos_id() {
        return pos_id;
    }

    public void setPos_id(String pos_id) {
        this.pos_id = pos_id;
    }

    public String getDescricaoAtividadeComercial() {
        return descricaoAtividadeComercial;
    }

    public void setDescricaoAtividadeComercial(String descricaoAtividadeComercial) {
        this.descricaoAtividadeComercial = descricaoAtividadeComercial;
    }

    public String getDescricaoFaixa() {
        return descricaoFaixa;
    }

    public void setDescricaoFaixa(String descricaoFaixa) {
        this.descricaoFaixa = descricaoFaixa;
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
}
