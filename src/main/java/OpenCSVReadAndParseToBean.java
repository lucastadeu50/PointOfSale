import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class OpenCSVReadAndParseToBean {
    private static final String SAMPLE_CSV_FILE_PATH = "/home/lucas/PointOfSaleDistance/PointOfSale.csv";
    private static final String NULL = "NULL";
    private static final Boolean PROCESS = false;

    public static void main(String[] args) throws IOException {
        ArrayList<PointOfSale> pointOfSales = new ArrayList<PointOfSale>();
        ArrayList<PointOfSaleFinal> pointOfSaleFinalsComplete = new ArrayList<>();
        List<PointOfSaleFinal> pointOfSaleFinalsTop10 = new ArrayList<>();
        ArrayList<PointOfSaleFinal> pointOfSaleFinals = new ArrayList<>();
        Double distance;
        List<Double> distances = new ArrayList<Double>();

        try (
                Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));
        ) {
            CsvToBean<PointOfSale> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(PointOfSale.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            Iterator<PointOfSale> csvPointOfSaleIterator = csvToBean.iterator();

//            for (int i = 0; i < 10000; i++) {
//                pointOfSales.add(csvPointOfSaleIterator.next());
//            }
             int lines=0;
             while (csvPointOfSaleIterator.hasNext()){
                pointOfSales.add(csvPointOfSaleIterator.next());
                lines++;
                 System.out.println("line: " + lines);
             }
             if (!PROCESS) {
                 splitPointOfSales(pointOfSales);
             }
            if (PROCESS) {
                for (int i = 0; i < pointOfSales.size(); i++) {
                    for (int j = 0; j < pointOfSales.size(); j++) {
                        if (sameCategoryAndDescription(pointOfSales.get(i), pointOfSales.get(j))) {
                            if (isLatitudeLongitudeValidity(pointOfSales.get(i).latitude, pointOfSales.get(i).longitude) && isLatitudeLongitudeValidity(pointOfSales.get(j).latitude, pointOfSales.get(j).longitude)) {
                                PointOfSaleFinal pointOfSaleFinal = new PointOfSaleFinal();
                                distance = distance(pointOfSales.get(i).latitude, pointOfSales.get(i).longitude, pointOfSales.get(j).latitude, pointOfSales.get(j).longitude, "km");
                                distances.add(distance);
                                pointOfSaleFinal.pos_id = pointOfSales.get(j).pos_id;
                                pointOfSaleFinal.descricaoAtividadeComercial = pointOfSales.get(j).descricaoAtividadeComercial;
                                pointOfSaleFinal.descricaoFaixa = pointOfSales.get(j).descricaoFaixa;
                                pointOfSaleFinal.latitude = pointOfSales.get(j).latitude;
                                pointOfSaleFinal.longitude = pointOfSales.get(j).longitude;
                                pointOfSaleFinal.distancia = distance;
                                pointOfSaleFinals.add(pointOfSaleFinal);
                            }
                        }
                        System.out.println(j);
                    }
                    if (pointOfSaleFinals.size() < 11) {
                        int pointOfSaleFinalsSize = pointOfSaleFinals.size();
                        for (int l = pointOfSaleFinalsSize; l < 11; l++) {
                            PointOfSaleFinal pointOfSaleFinal = new PointOfSaleFinal();
                            pointOfSaleFinal.distancia = 10000000000.0;
                            pointOfSaleFinal.pos_id = NULL;
                            pointOfSaleFinal.descricaoFaixa = NULL;
                            pointOfSaleFinal.descricaoAtividadeComercial = NULL;
                            pointOfSaleFinal.longitude = 10000000000.0;
                            pointOfSaleFinal.latitude = 10000000000.0;
                            pointOfSaleFinals.add(pointOfSaleFinal);
                        }
                    }
                    pointOfSaleFinals.sort(Comparator.comparing(PointOfSaleFinal::getDistancia));
                    pointOfSaleFinalsTop10 = pointOfSaleFinals.subList(0, 11);
                    for (int k = 0; k < pointOfSaleFinalsTop10.size(); k++) {
                        pointOfSaleFinalsComplete.add(pointOfSaleFinalsTop10.get(k));
                    }
                    pointOfSaleFinalsTop10.clear();
                    pointOfSaleFinals.clear();
                }
                createCSV(pointOfSaleFinalsComplete);
            }
        }
    }

    private static Boolean sameCategoryAndDescription(PointOfSale pointOfSale1, PointOfSale pointOfSale2) {
        if (pointOfSale1.getDescricaoFaixa() != null && pointOfSale1.getDescricaoAtividadeComercial() != null && pointOfSale2.getDescricaoAtividadeComercial() != null && pointOfSale2.getDescricaoFaixa() != null) {
            return pointOfSale1.getDescricaoFaixa().equals(pointOfSale2.getDescricaoFaixa()) && pointOfSale1.getDescricaoAtividadeComercial().equals(pointOfSale2.getDescricaoAtividadeComercial());
        } else return false;
    }

    private static boolean isLatitudeLongitudeValidity(Double latitude, Double longitude) {
        return latitude != null && longitude != null && latitude < 90.0 && latitude > -90.0 && longitude < 180.0 && longitude > -180.0;
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit.equals("mi")) {
            return dist;
        } else if (unit.equals("km")) {
            dist = dist * 1.609344;
            return dist;
        } else return dist;
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    private static void createCSV(ArrayList<PointOfSaleFinal> pointOfSaleFinals) throws IOException {
        final String CSV_FILE_PATH = "/home/lucas/PointOfSaleDistance/PointOfSaleFinal.csv";
        try (
                Writer writer = Files.newBufferedWriter(Paths.get(CSV_FILE_PATH));
        ) {
            StatefulBeanToCsv<PointOfSaleFinal> beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build();
            try {
                beanToCsv.write(pointOfSaleFinals);
            } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
                e.printStackTrace();
            }
        }
    }
    private static void createCSVbyFaixa(ArrayList<PointOfSale> pointOfSales, String CsvSufix) throws IOException {
        final String CSV_FILE_PATH = "/home/lucas/PointOfSaleDistance/PointOfSale-"+CsvSufix+".csv";
        try (
                Writer writer = Files.newBufferedWriter(Paths.get(CSV_FILE_PATH));
        ) {
            StatefulBeanToCsv<PointOfSale> beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .build();
            try {
                beanToCsv.write(pointOfSales);
            } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
                e.printStackTrace();
            }
        }
    }
    public void preProcess(  ArrayList<PointOfSale> pointOfSales){
        for (int i = 0; i < pointOfSales.size(); i++) {
            if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("ATACADO KA")){

            }
            else if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("ATACADO REGIONAL")){

            }
            else if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("BANCA")){

            }
            else if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("BAR NOTURNO")){

            }
            else if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("BAR UNIVERSITARIO")){

            }
            else if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("BOATE / DISCOTECA")){

            }
            else if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("CAFE")){

            }
            else if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("DISTRIBUIDORA DE BEBIDAS")){

            }
            else if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("FITEIRO")){

            }
            else if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("HIPERMERCADO")){

            }
            else if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("HOTEL/MOTEL")){

            }
            else if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("LANCHONETE")){

            }
            else if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("LOJA CNV INDEPENDENTE")){

            }
            else if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("LOJA CNV KA")){

            }
            else if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("LOJA DE VIZINHANCA KA")){

            }
            else if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("LOTERIA")){

            }
            else if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("MERCEARIA")){

            }
            if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("MINI MERCADO")){

            }
            if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("PADARIA")){

            }
            if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("PDV FUMO DESFIADO")){

            }
            if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("PISTA POSTO DE GASOLINA")){

            }
            if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("QUIOSQUE")){

            }
            if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("RESTAURANTE")){

            }
            if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("REVENDEDOR")){

            }
            if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("REVISTARIA")){

            }
            if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("SUPERMERCADO")){

            }
            if (pointOfSales.get(i).getDescricaoAtividadeComercial().equals("TABACARIA")){

            }

        }
    }

    public static void splitPointOfSales (ArrayList<PointOfSale> pointOfSales){
        ArrayList<PointOfSale> pointOfSalesFaixa1 = new ArrayList<PointOfSale>();
        ArrayList<PointOfSale> pointOfSalesFaixa2 = new ArrayList<PointOfSale>();
        ArrayList<PointOfSale> pointOfSalesFaixa3 = new ArrayList<PointOfSale>();
        ArrayList<PointOfSale> pointOfSalesFaixa4 = new ArrayList<PointOfSale>();
        ArrayList<PointOfSale> pointOfSalesFaixa5 = new ArrayList<PointOfSale>();
        ArrayList<PointOfSale> pointOfSalesFaixa6 = new ArrayList<PointOfSale>();
        ArrayList<PointOfSale> pointOfSalesFaixa7 = new ArrayList<PointOfSale>();
        ArrayList<PointOfSale> pointOfSalesFaixa8 = new ArrayList<PointOfSale>();
        ArrayList<PointOfSale> pointOfSalesUnknownFaixa = new ArrayList<PointOfSale>();

        for (int i = 0; i < pointOfSales.size() ; i++) {
            if (pointOfSales.get(i).descricaoFaixa.equals("FAIXA 1")){
                pointOfSalesFaixa1.add(pointOfSales.get(i));
            } else if (pointOfSales.get(i).descricaoFaixa.equals("FAIXA 2")){
                pointOfSalesFaixa2.add(pointOfSales.get(i));
            } else if (pointOfSales.get(i).descricaoFaixa.equals("FAIXA 3")){
                pointOfSalesFaixa3.add(pointOfSales.get(i));
            } else if (pointOfSales.get(i).descricaoFaixa.equals("FAIXA 4")){
                pointOfSalesFaixa4.add(pointOfSales.get(i));
            } else if (pointOfSales.get(i).descricaoFaixa.equals("FAIXA 5")){
                pointOfSalesFaixa5.add(pointOfSales.get(i));
            } else if (pointOfSales.get(i).descricaoFaixa.equals("FAIXA 6")){
                pointOfSalesFaixa6.add(pointOfSales.get(i));
            } else if (pointOfSales.get(i).descricaoFaixa.equals("FAIXA 7")){
                pointOfSalesFaixa7.add(pointOfSales.get(i));
            } else if (pointOfSales.get(i).descricaoFaixa.equals("FAIXA 8")){
                pointOfSalesFaixa8.add(pointOfSales.get(i));
            } else{
                pointOfSalesUnknownFaixa.add(pointOfSales.get(i));
            }
            try {
                createCSVbyFaixa(pointOfSalesFaixa1, "FAIXA1");
                createCSVbyFaixa(pointOfSalesFaixa2, "FAIXA2");
                createCSVbyFaixa(pointOfSalesFaixa3, "FAIXA3");
                createCSVbyFaixa(pointOfSalesFaixa4, "FAIXA4");
                createCSVbyFaixa(pointOfSalesFaixa5, "FAIXA5");
                createCSVbyFaixa(pointOfSalesFaixa6, "FAIXA6");
                createCSVbyFaixa(pointOfSalesFaixa7, "FAIXA7");
                createCSVbyFaixa(pointOfSalesFaixa8, "FAIXA8");
                createCSVbyFaixa(pointOfSalesUnknownFaixa, "SEMFAIXA");

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
