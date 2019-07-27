import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;

public class PointOfSaleFinal {
    @CsvBindByName(column = "pis_id")
    @CsvBindByPosition(position = 0)
    String pos_id;
    @CsvBindByPosition(position = 1)
    String descricaoAtividadeComercial;
    @CsvBindByPosition(position = 2)
    String descricaoFaixa;
    @CsvBindByPosition(position = 3)
    Double latitude;
    @CsvBindByPosition(position = 4)
    Double longitude;
    @CsvBindByPosition(position = 5)
    Double distancia;

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

    public Double getDistancia() {
        return distancia;
    }

    public void setDistancia(Double distancia) {
        this.distancia = distancia;
    }
}
