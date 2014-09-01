package naveent.com.relcychallenge.utils;

/**
 * Created by NaveenT on 8/28/14.
 */
public class ConstructUrl {
    public static String construct(String searchString){
        String searchQuery = new String(searchString.trim().replace(" ", "%20").replace("&", "%26")
                .replace(",", "%2c").replace("(", "%28").replace(")", "%29")
                .replace("!", "%21").replace("=", "%3D").replace("<", "%3C")
                .replace(">", "%3E").replace("#", "%23").replace("$", "%24")
                .replace("'", "%27").replace("*", "%2A").replace("-", "%2D")
                .replace(".", "%2E").replace("/", "%2F").replace(":", "%3A")
                .replace(";", "%3B").replace("?", "%3F").replace("@", "%40")
                .replace("[", "%5B").replace("\\", "%5C").replace("]", "%5D")
                .replace("_", "%5F").replace("`", "%60").replace("{", "%7B")
                .replace("|", "%7C").replace("}", "%7D"));
        String url = Constants.GOOGLE_QUERY + "" + Constants.API_VERSION + "&"
                + Constants.RESULT_SIZE + "&" + Constants.USER_IP + "&" + "q=" + searchQuery;
        System.out.println(url);
        return url;
    }
    public static String construct(String searchString,int start){
        String searchQuery = new String(searchString.trim().replace(" ", "%20").replace("&", "%26")
                .replace(",", "%2c").replace("(", "%28").replace(")", "%29")
                .replace("!", "%21").replace("=", "%3D").replace("<", "%3C")
                .replace(">", "%3E").replace("#", "%23").replace("$", "%24")
                .replace("'", "%27").replace("*", "%2A").replace("-", "%2D")
                .replace(".", "%2E").replace("/", "%2F").replace(":", "%3A")
                .replace(";", "%3B").replace("?", "%3F").replace("@", "%40")
                .replace("[", "%5B").replace("\\", "%5C").replace("]", "%5D")
                .replace("_", "%5F").replace("`", "%60").replace("{", "%7B")
                .replace("|", "%7C").replace("}", "%7D"));
        String url = Constants.GOOGLE_QUERY + "" + Constants.API_VERSION + "&"
                + Constants.RESULT_SIZE + "&" + Constants.USER_IP + "&" + "q=" + searchQuery+"&"+"start="+start;
        System.out.println(url);
        return url;
    }
}
