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

    public static void main(String[] args) throws IOException {
        ArrayList<PointOfSale> pointOfSales = new ArrayList<PointOfSale>();
        ArrayList<PointOfSale> top10PointOfSalesDistance = new ArrayList<PointOfSale>();
        ArrayList<PointOfSaleFinal> pointOfSaleFinalsComplete = new ArrayList<>();
        List<PointOfSaleFinal> pointOfSaleFinalsTop10 = new ArrayList<>();
        ArrayList<PointOfSaleFinal> pointOfSaleFinals = new ArrayList<>();

        Double distance;
        List<Double> distances = new ArrayList<Double>();
        List<Double> top10Distances = new ArrayList<Double>();

        try (
                Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));
        ) {
            CsvToBean<PointOfSale> csvToBean = new CsvToBeanBuilder(reader)
                    .withType(PointOfSale.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            Iterator<PointOfSale> csvPointOfSaleIterator = csvToBean.iterator();

            for (int i = 0; i < 100; i++) {
                pointOfSales.add(csvPointOfSaleIterator.next());
            }
/*             int k=0;
             while (csvPointOfSaleIterator.hasNext()){
                pointOfSales.add(csvPointOfSaleIterator.next());
                k++;
                 System.out.println(k);
             }*/

//            while (csvUserIterator.hasNext()) {
//                PointOfSale pointOfSale = csvUserIterator.next();
//                System.out.println("pos_id : " + pointOfSale.getPos_id());
//                System.out.println("descricao atividade comercial : " + pointOfSale.getDescricaoAtividadeComercial());
//                System.out.println("descricao faixa : " + pointOfSale.getDescricaoFaixa());
//                System.out.println("latitude : " + pointOfSale.getLatitude());
//                System.out.println("longitude : " + pointOfSale.getLongitude());
//                System.out.println("==========================");
//            }
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
                Collections.sort(distances, Double::compareTo);
                int i1;
                if (pointOfSaleFinals.size() < 11) i1 = pointOfSaleFinals.size();
                else i1 = 11;
                pointOfSaleFinals.sort(Comparator.comparing(PointOfSaleFinal::getDistancia));
                pointOfSaleFinalsTop10 = pointOfSaleFinals.subList(0, i1);
                for (int k = 0; k < pointOfSaleFinalsTop10.size(); k++) {
                    pointOfSaleFinalsComplete.add(pointOfSaleFinalsTop10.get(k));
                }
                pointOfSaleFinalsTop10.clear();
                pointOfSaleFinals.clear();
            }
//            for (int i = 0; i < pointOfSaleFinalsTop10.size(); i++) {
//                System.out.println("pos_id : " + pointOfSaleFinalsTop10.get(i).getPos_id());
//                System.out.println("descricao atividade comercial : " + pointOfSaleFinalsTop10.get(i).getDescricaoAtividadeComercial());
//                System.out.println("descricao faixa : " + pointOfSaleFinalsTop10.get(i).getDescricaoFaixa());
//                System.out.println("latitude : " + pointOfSaleFinalsTop10.get(i).getLatitude());
//                System.out.println("longitude : " + pointOfSaleFinalsTop10.get(i).getLongitude());
//                System.out.println("distancia : " + pointOfSaleFinalsTop10.get(i).getDistancia());
//                System.out.println("==========");
//            }
            createCSV(pointOfSaleFinalsComplete);
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
}
