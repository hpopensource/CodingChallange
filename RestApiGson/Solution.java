import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.lang.reflect.Type;
import java.net.*;
import com.google.gson.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
//
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.security.*;
/**
@author hp
*/

public class Solution {

    final static Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().registerTypeAdapter(MovieRestResponse.class, new MovieRestResponse.Deserializer()).create();

    public static String[] getMovieTitles(String substr) throws IOException {
        List<String> results = new ArrayList<>();
        int pageCount = getPageCount(substr);

        if (pageCount == 0) {
            return new String[0];
        }

        for (int i = 1; i <= pageCount; i++) {
            results.addAll(getData(substr, i));
        }

        // sort the movie list
        Collections.sort(results, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareTo(s2);
            }
        });

        return results.toArray(new String[results.size()]);
    }

    /**
     * This returns list of movie titles queried by the substr and the page number.
     *
     * @param substr
     * @param pageCount
     * @return
     * @throws IOException
     */
    static List<String> getData(String substr, int pageNumber) throws IOException {
        String serverReply = callRestApi(substr, pageNumber);

        MovieRestResponse response = gson.fromJson(serverReply, MovieRestResponse.class);
        List<String> results = new ArrayList<>();
        for (MovieInfo movie : response.getData()) {
            results.add(movie.title);
        }
        return results;
    }

    static int getPageCount(String substr) throws IOException {

        String serverReply = callRestApi(substr, 0);
        MovieRestResponse response = gson.fromJson(serverReply, MovieRestResponse.class);
        return response.totalPages;
    }

   

    /*
     * The core class to which opens JSON URL and get core JSON response.
     */
    static String callRestApi(String substr, int pageCnt) throws IOException{
        //SSL Workaorund Start
        // Without this fix on this server it was throwing unable to find valid certification path to              requested target Run time exception
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
        }
        //SSL Workaround End
        URL url;
        if(pageCnt>=1) {
                         
            url= new URL("https://jsonmock.hackerrank.com/api/movies/search/?Title=" + substr + "&page="+pageCnt);
        }else{
            url= new URL("https://jsonmock.hackerrank.com/api/movies/search/?Title=" + substr);
        }
        
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        
        //

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String f_line;
        StringBuffer output = new StringBuffer();

        while ((f_line = br.readLine()) !=null ) {
            output.append(f_line);
        }

        return output.toString();
    }
    /**
        Static class MovieResponse
    */
    static class MovieRestResponse {

        private int page;
        private int perPage;
        private int total;
        private int totalPages;
        private List<MovieInfo> data;

        public MovieRestResponse(int page, int perPage, int total, int totalPages, List<MovieInfo> data) {

            this.page = page;
            this.perPage = perPage;
            this.total = total;
            this.totalPages = totalPages;
            this.data = data;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPerPage() {
            return perPage;
        }

        public void setPerPage(int perPage) {
            this.perPage = perPage;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public List<MovieInfo> getData() {
            return data;
        }

        public void setData(List<MovieInfo> data) {
            this.data = data;
        }

        static final class Deserializer implements JsonDeserializer<MovieRestResponse> {

            /**
             * {@inheritDoc}
             */
            @Override
            public MovieRestResponse deserialize(final JsonElement json, final Type typeOfT,final JsonDeserializationContext context) throws JsonParseException {
                final JsonObject jObject = json.getAsJsonObject();
                final int page = jObject.get("page").getAsInt();
                final int perPage = jObject.get("per_page").getAsInt();
                final int total = jObject.get("total").getAsInt();
                final int totalPages = jObject.get("total_pages").getAsInt();
                final JsonArray jsonArray = jObject.get("data").getAsJsonArray();
                final List<MovieInfo> movieList = new ArrayList<>();

                for (int i = 0; i < jsonArray.size(); i++) {
                    final JsonObject movieJson = jsonArray.get(i).getAsJsonObject();
                    final String imdbId = movieJson.get("imdbID").getAsString();
                    final String title = movieJson.get("Title").getAsString();
                    final int year = movieJson.get("Year").getAsInt();
                    final MovieInfo MovieInfo = new MovieInfo(title, year,imdbId);
                    movieList.add(MovieInfo);
                }

                return new MovieRestResponse(page, perPage, total, totalPages, movieList);
            }

        }
    }
    
    /**
        Static template class for getter and setter
    */
    static class MovieInfo {
       
        private String title;
        private int year;
        private String imdbId;

        public MovieInfo(String title, int year,  String imdbId) {
           
            this.title = title;
            this.year = year;
            this.imdbId = imdbId;
        }


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public String getImdbId() {
            return imdbId;
        }

        public void setImdbId(String imdbId) {
            this.imdbId = imdbId;
        }

    }
    
    /**
        Main method reads Standard Input
        Calls getMovieTitles()
        Prints Sorted Movie Titles
    */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        
        String[] res;
        String _substr;
        try {
            _substr = in.nextLine();
        } catch (Exception e) {
            _substr = null;
        }
        try {
            //System.out.println(_substr);
            //
            res = getMovieTitles(_substr);
            //Print Sorted Movie Titles
            for(int res_i=0; res_i < res.length; res_i++) {
                System.out.println(String.valueOf(res[res_i]));
            }
            
        
        
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
}