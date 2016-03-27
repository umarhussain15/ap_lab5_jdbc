import java.awt.image.AreaAveragingScaleFilter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 * Created by Umar on 21-Mar-16.
 */
public class CityDataBase {

    public static final double R = 6373; // In kilometers
    Connection con = null;
    PreparedStatement stmt = null;
    PreparedStatement searchcity = null,searchnearby=null;
    ResultSet rs = null;
    public static void main(String[] args) {
        Scanner s= new Scanner(System.in);

        CityDataBase cb= new CityDataBase();

        //cb.con = DBConnection.getConnection();
        System.out.println("Enter 1 to Search City by Name\nEnter 2 to Find Nearby cities of a City or Lat/Lng");
        int op= s.nextInt();
        try {
            switch (op){
                case 1:
                    System.out.println("Enter City Name: ");
                    s.nextLine();
                     String city= s.nextLine();
                    cb.getLatLngID(city);
                    break;
                case 2:
                    System.out.println("Enter 1  For Search by Name\n 2 for Search by Lat/Lng ");
                    op=s.nextInt();
                    switch(op){
                        case 1:
                            System.out.println("Enter City Name: ");
                            s.nextLine();
                             city= s.nextLine();
                            List<Double> latlng;
                            if ((latlng=cb.getLatLngID(city))!=null){


                                double lat=latlng.get(0);
                                double lng=latlng.get(1);
                                int id=latlng.get(2).intValue();
//                                System.out.println("ID IS ::" +id);
                                id=id-10;
                                String get="SELECT latitude,longitude,city FROM citytable WHERE id>="+id+" AND id <= "+(id+20)+"  AND id<> " +(id+10);
                                cb.searchnearby=cb.con.prepareStatement(get);
                                Statement sa= cb.con.createStatement();

                                cb.rs=sa.executeQuery(get);

                                HashMap<String,Double> h= new HashMap<>();
                                while (cb.rs.next()){
                                    System.out.println(cb.rs.getString("city")+" =>"+cb.rs.getDouble("latitude")+" "+cb.rs.getDouble("longitude") );
                                    h.put(cb.rs.getString("city"),cb.distance(lat,lng,cb.rs.getDouble("latitude"),cb.rs.getDouble("longitude"),"M"));
                                }
                                //h=cb.sortByValues(h);
                                Set<String> keys=h.keySet();
                                int i=0;
                                Iterator<String> it=keys.iterator();
                                for(String c=it.next();it.hasNext();c=it.next()){
                                    if (i==10)
                                        break;
                                    System.out.println(c+" is "+h.get(it.next())+" miles away");
                                    i++;
                                }
                            }
                            else {
                                System.out.println("No Results");
                            }

                            break;
                        case 2:

                            break;
                    }
                    break;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        // cb.ReadCSV();
    }

    public CityDataBase() {
        con = DBConnection.getConnection();
    }

    public void ReadCSV(){
        String csvFile = "C:\\Users\\Umar\\IdeaProjects\\Lab5_AP\\src\\GeoLiteCity-Location.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        List<String> S= new ArrayList<>();
        try{
            con = DBConnection.getConnection();
            stmt = con.prepareStatement(
                    "INSERT INTO citytable (country,region,city,postalcode,latitude,longitude) " +
                            "VALUES (?,?,?,?,?,?)  ");

            try {

                br = new BufferedReader(new FileReader(csvFile));
                br.readLine();
                br.readLine();
                while ((line = br.readLine()) != null) {

                    // use comma as separator
                   // S.add(line);
                String[] cities = line.split(cvsSplitBy);
                    System.out.println("Size"+cities.length);
                    stmt.setString(1,cities[1]);
                    stmt.setString(2,cities[2]);
                    stmt.setString(3,cities[3]);
                    stmt.setString(4,cities[4]);
                    stmt.setDouble(5,Double.parseDouble(cities[5]));
                    stmt.setDouble(6,Double.parseDouble(cities[6]));

                    stmt.execute();
//
//                System.out.println("Country [code= " + cities[0]
//                        + " , name=" + cities[1] + "]");

                }
                System.out.println("Loaded in Program");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try {
                rs.close();
                stmt.close();
                con.close();
                // input.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
    private  double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }

        return (dist);
    }
    public  double haversine(double lat1, double lon1, double lat2, double lon2) {
        System.out.println(lat1+" "+lon1+" "+lat2+" "+lon2);
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
    public List<Double> getLatLngID(String city) {
        if (city.isEmpty()) {
            System.out.println("Error");
            return null;
        }
        List<Double> latlng= new ArrayList<>();
        city = "\"" + city + "\"";
        try {
            searchcity = con.prepareStatement(
                    "SELECT latitude,longitude,id FROM citytable WHERE city=?");
            searchcity.setString(1, city);

            ResultSet rs = searchcity.executeQuery();
            int c = 0;
            if (rs.next()) {
                c = 1;

                latlng.add(rs.getDouble(1));
                latlng.add(rs.getDouble(2));
                latlng.add(rs.getDouble(3));
                System.out.println("Coordinates for " + city + " are => " + latlng.get(0) + " " + latlng.get(1));
                return latlng;
            }
            if (c == 0)
                System.out.println("No result");
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static HashMap sortByValues(HashMap map) {
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }
}
