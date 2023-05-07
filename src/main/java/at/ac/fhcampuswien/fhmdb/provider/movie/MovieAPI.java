package at.ac.fhcampuswien.fhmdb.provider.movie;

import at.ac.fhcampuswien.fhmdb.ui.ExceptionDialog;
import at.ac.fhcampuswien.fhmdb.exception.MovieAPIException;
import at.ac.fhcampuswien.fhmdb.model.Movie;
import com.google.gson.Gson;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;

public class MovieAPI implements MovieProvider {
    private static final String BASE_URL = "https://prog2.fh-campuswien.ac.at";
    private static final String ENDPOINT_MOVIES = "/movies";
    private final OkHttpClient client;
    private final Gson gson;

    public MovieAPI() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    @Override
    public List<Movie> getMovies(){
        return this.getMoviesWithQuery(null);
    }

    public List<Movie> getMoviesWithQuery(Map<String, String> queryMap){
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(BASE_URL + ENDPOINT_MOVIES)).newBuilder();

        if(queryMap != null){
            for (String key: queryMap.keySet()) {
                urlBuilder.addQueryParameter(key, queryMap.get(key));
            }
        }

        return requestMovieList(urlBuilder.build());
    }

    private List<Movie> requestMovieList(HttpUrl url){
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "http.agent")
                .build();
        try (Response response = client.newCall(request).execute()) {
            String json = response.body().string();
            return Arrays.asList(gson.fromJson(json, Movie[].class));
        }
        catch(IOException | NullPointerException | IllegalArgumentException e) {
            MovieAPIException movieApiException = new MovieAPIException("Error fetching data from resource.", e);
            ExceptionDialog.show(movieApiException);
        }

        return new ArrayList<>();
    }
}