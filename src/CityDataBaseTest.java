import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Umar on 21-Mar-16.
 */
public class CityDataBaseTest extends TestCase {

    public void testGetLatLngID() throws Exception {
        CityDataBase cb= new CityDataBase();

        List<Double> ls= new ArrayList<>();
        ls.add(38.3622);
        ls.add(-98.3875);
        ls.add((double) 1660);
        Assert.assertEquals(ls,cb.getLatLngID("Chase"));

    }

    public void testSortByValues() throws Exception {
        CityDataBase cb= new CityDataBase();
        HashMap<String,Double> h1= new HashMap<>();
        h1.put("test",87.0616);
        h1.put("testq",1000.35150);
        h1.put("testqw",1222.35150);
        h1.put("testqw1",90.35150);
        HashMap<String,Double> h2= new HashMap<>();
        h2.put("test",87.0616);
        h2.put("testqw1",90.35150);
        h2.put("testq",1000.35150);
        h2.put("testqw",1222.35150);
       assertEquals(h2,cb.sortByValues(h1));
    }
}