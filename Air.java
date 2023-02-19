
import org.json.JSONObject;


import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Air {


    public static void main(String[] args) throws IOException, URISyntaxException {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter \n1) plane registration\n2) flight number");
        String input = null;
        String accessKey = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiI0IiwianRpIjoiMDVhMzlmNTQzZDllZDk3YmVjZmU2NjY3YmI4ZTI2YzI5NWMxNTg4YTczNGQyYzkwYTc1NmJiYWRkN2Q1M2E0M2VhMWQzZDI0MmZiM2Y0NTQiLCJpYXQiOjE2NzY3OTA4MTIsIm5iZiI6MTY3Njc5MDgxMiwiZXhwIjoxNzA4MzI2ODEyLCJzdWIiOiIyMDE1MSIsInNjb3BlcyI6W119.ql4smo_f8Ts-GIhNI1klrKXz3FazaETs1bmfVSVyLbXnx2jJzTN70T82bt2MXNcJNRpI2V4MMgEPTU-7wcm1TQ";
        switch (Integer.parseInt(s.next())){
            case 1 -> {
                System.out.println("Enter plane registration number: ");
                input = "&regNum=" + s.next();
            }
            case 2 -> {
                System.out.println("Enter plane flight number: ");
                input = "&flightNum=" + s.next();
            }
            default -> System.exit(0);
        }

        URL url = new URL("https://app.goflightlabs.com/flights?access_key=" + accessKey + input);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        //System.out.println(content);
        JSONObject jo = new JSONObject(content.toString());

        String latitude = String.valueOf(jo.getJSONArray("data").getJSONObject(0).getJSONObject("geography").getBigDecimal("latitude"));
        String longitude = String.valueOf(jo.getJSONArray("data").getJSONObject(0).getJSONObject("geography").getBigDecimal("longitude"));
        String arrival = String.valueOf(jo.getJSONArray("data").getJSONObject(0).getJSONObject("arrival").getString("iataCode"));
        String departure = String.valueOf(jo.getJSONArray("data").getJSONObject(0).getJSONObject("departure").getString("iataCode"));

        System.out.println("latitude " + latitude);
        System.out.println("longitude " + longitude);
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("http://maps.google.com/maps?z=12&t=m&q=loc:" + latitude + "+" + longitude));
        }

        List<List<String>> records = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File("External/airport-codes.csv"));) {
            while (scanner.hasNextLine()) {
                records.add(getRecordFromLine(scanner.nextLine()));
            }
        }
        
        String arrC = null, depC = null;
        for(List<String> ls : records){
            if(ls.size() < 4) //error
                continue;
            if(ls.get(3).equals(arrival))
                arrC = ls.get(1) + ", " + ls.get(2) + "," + ls.get(0);
            if(ls.get(3).equals(departure))
                depC = ls.get(1) + ", " + ls.get(2) + "," + ls.get(0);
            if (arrC != null && depC != null)
                break;
        }
        System.out.println("Arriving to: " + arrC);
        System.out.println("Departure from: " + depC);
        in.close();
        con.disconnect();
    }

    private static List<String> getRecordFromLine(String line) {
        List<String> values = new ArrayList<String>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(",");
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }
}
