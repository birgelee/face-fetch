package facefetch;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FaceFetch {

    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println(getClassImageLinks(2019));
        System.out.println(getClassImageLinks(2018));
        System.out.println(getClassImageLinks(2017));
        System.out.println(getClassImageLinks(2016));
    }
    
    static String loginCookie = "csrftoken=HzcndWgiI25cXRkJe8K04tiTnmcmFoR6; sessionid=p5bearmti1woe82kpbjxytv6f4a3gu6p";
    

    public static List<String> getClassImageLinks(int classYear) {
        try {
            URL url = new URL("https://tigerbook.herokuapp.com/search?q=" + classYear);
            
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            connection.setRequestMethod("GET");
            
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.setRequestProperty("Cookie", loginCookie);
            
            connection.connect();
            String pageString = "";
            if (connection.getResponseCode() == 200) {
                pageString = convertStreamToStringAndClose(connection.getInputStream());
            } else {
                throw new RuntimeException("Bad response code from server. Code was: " + connection.getResponseCode());
            }
            int pageCountIndex = pageString.indexOf("id=\"num-pages\">") + 15;
            int pageCountEndIndex = pageString.indexOf("</span>", pageCountIndex);
            String pageCountString = pageString.substring(pageCountIndex, pageCountEndIndex);
            int pageCount = Integer.parseInt(pageCountString);
            List<String> res = new ArrayList<String>();
            res.addAll(parseLinksFromPageString(pageString));
            for (int i = 2; i <= pageCount; i++) {
                res.addAll(getImageLinksFromPage(classYear, i));
            }
            return res;
            
            
        } catch (Exception ex) {
            Logger.getLogger(FaceFetch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    static int searchYear = 2019;
    public static List<String> getImageLinksFromPage(int classYear, int pageNumber) throws IOException, URISyntaxException {
        
            try {
                URL url = new URL("https://tigerbook.herokuapp.com/search?q=" + classYear + "&p=" + pageNumber);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");

                connection.setRequestProperty("Accept-Charset", "UTF-8");
                connection.setRequestProperty("Cookie", loginCookie);
                
                connection.connect();
                String pageString = "";
                if (connection.getResponseCode() == 200) {
                    pageString = convertStreamToStringAndClose(connection.getInputStream());
                } else {
                    throw new RuntimeException("Bad response code from server. Code was: " + connection.getResponseCode());
                }
                return parseLinksFromPageString(pageString);
            } catch (SocketException sex) {
                System.out.println("Socket exception");
                sex.printStackTrace();

            }
        return null;

    }
    
    static String convertStreamToStringAndClose(java.io.InputStream is) {
        try {
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            String res = s.hasNext() ? s.next() : "";
            is.close();
            return res;
        } catch (IOException ex) {
            Logger.getLogger(FaceFetch.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public static List<String> parseLinksFromPageString(String pageString) {
        ArrayList<String> res = new ArrayList<String>();
        int index = 0;
        while (true) {
            index = pageString.indexOf("<img src=\"https://www.princeton.edu/deptafe_internal/cimg!0/", index + 1);
            if (index == -1) {
                break;
            }
            int urlBegin = index + 10;
            int urlEnd = pageString.indexOf("\"", urlBegin);
            res.add(pageString.substring(urlBegin, urlEnd));
        }
        return res;
    }
    
    
}
