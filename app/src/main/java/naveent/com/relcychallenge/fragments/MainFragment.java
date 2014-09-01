package naveent.com.relcychallenge.fragments;

/**
 * Created by NaveenT on 8/28/14.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import naveent.com.relcychallenge.NetworkStatus;
import naveent.com.relcychallenge.R;
import naveent.com.relcychallenge.adapter.ImageAdapter;
import naveent.com.relcychallenge.utils.ConstructUrl;
import naveent.com.relcychallenge.utils.DisplayAlert;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements View.OnClickListener {
    EditText editText;
    GridView gridView;
    ImageView searchButton;
    TextView endofResultsTV;
    OkHttpClient client = new OkHttpClient();
    public static final String TAG = "MainFragment";
    public List<String> imageUrls = new ArrayList<String>();
    int start = 0;
    String searchString = "";
    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflating the views
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) rootView.findViewById(R.id.imagesGrid);
        editText = (EditText) rootView.findViewById(R.id.searchTextView);
        editText.setMaxLines(1);
        endofResultsTV = (TextView) rootView.findViewById(R.id.endofresults);
        searchButton = (ImageView) rootView.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
        endofResultsTV.setVisibility(View.INVISIBLE);
        return rootView;
    }
    //calling the onclick method for the search button click, to populate the data
    @Override
    public void onClick(View v) {
        //Reading from the edit text
        searchString = editText.getText().toString();
        //Constructing the proper url for the first time with the start value = 0
        String requestUrl = ConstructUrl.construct(searchString,start);
        //Clearing the results of the previous urls already existed
        imageUrls.clear();
        //Hiding the soft keyboard
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        //Hiding the end of results Textview for the next search results
        endofResultsTV.setVisibility(View.INVISIBLE);
        //resetting the start = 0 for every search
        start = 0;
        //Checking if the network exists, then fetch the results from the google api else display alert
        if(NetworkStatus.isNetworkAvailable(getActivity()))
            new DataFetchTask(getActivity()).execute(requestUrl);
        else
            NetworkStatus.showNetworkErrorDialog(getActivity());

    }

    /**
     * Async task to fetch the data from the Google API server.
     */
    class DataFetchTask extends AsyncTask<String, Void, Void> {

        ProgressDialog progressDialog;
        Context thisContext;
        GridView imagesGrid;
        ImageAdapter adapter;
        String nextUrl = "";
        int listViewPosition = 0;
        boolean exception = false;
        public DataFetchTask(Context thisContext){
            this.thisContext = thisContext;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(thisContext);
            progressDialog.setTitle("Google Images Search");
            progressDialog.setMessage("Loding Search Results...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        protected Void doInBackground(String... urls) {

            String responseString = processRequest(urls[0]);
            processJSON(responseString);

            return null;
        }

        protected String processRequest(String url){
            String responseString = "";
            try {
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = client.newCall(request).execute();
                responseString = response.body().string();
            } catch (IOException ioe) {
                progressDialog.dismiss();
                exception = true;
                ioe.printStackTrace();
            }
            return responseString;
        }

        public void processJSON(String responseString){
            try {
                JSONObject responseObject = new JSONObject(responseString);
                String responseStatus = responseObject.getString("responseStatus");
                if (responseStatus.equals("200")) {
                    JSONObject responseData = responseObject.getJSONObject("responseData");
                    JSONArray resultsArray = responseData.getJSONArray("results");
                    for(int i = 0;i<resultsArray.length();i++){
                        JSONObject result = resultsArray.getJSONObject(i);
                        String imageurl= result.getString("tbUrl");
                        imageUrls.add(imageurl);
                    }
                    start +=8;
                    String moreResultsUrl =ConstructUrl.construct(searchString,start);
                    String moreResultsResponse = processRequest(moreResultsUrl);
                    responseObject = new JSONObject(moreResultsResponse);
                    responseStatus = responseObject.getString("responseStatus");
                    if (responseStatus.equals("200")) {
                        responseData = responseObject.getJSONObject("responseData");
                        resultsArray = responseData.getJSONArray("results");
                        for(int i = 0;i<resultsArray.length();i++){
                            JSONObject result = resultsArray.getJSONObject(i);
                            String imageurl= result.getString("tbUrl");
                            imageUrls.add(imageurl);
                        }
                    }
                    start += 8;
                    nextUrl = ConstructUrl.construct(searchString, start);
                } else {
                    return;
                }
            }catch (JSONException je){
                je.printStackTrace();
                exception = true;
                Log.e(TAG, "JSON Exception");
            }

        }

        protected void onPostExecute(Void param) {
            progressDialog.dismiss();
            if(exception){
                DisplayAlert.showAlert(getActivity(),"Please Check the search string!");
            }
            if(getActivity()!=null) {
                imagesGrid = (GridView) getActivity().findViewById(R.id.imagesGrid);
                // Pass the results into ListViewAdapter.java
                adapter = new ImageAdapter(getActivity(), imageUrls);
                // Binds the Adapter to the ListView
                imagesGrid.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

                // Create an OnScrollListener
                imagesGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view,
                                                     int scrollState) {
                        int threshold = 1;
                        int count = imagesGrid.getCount();
                        if(start<64) {
                            if (scrollState == SCROLL_STATE_IDLE ) {
                                if (imagesGrid.getLastVisiblePosition() >= count
                                        - threshold) {
                                    // Locate listview last item
                                    listViewPosition = imagesGrid.getFirstVisiblePosition();
                                    // Execute LoadMoreDataTask AsyncTask
                                    new LoadMoreDataTask().execute();
                                }
                            }
                        }
                        else{
                            endofResultsTV.setVisibility(View.VISIBLE);
                            return;
                        }
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem,
                                         int visibleItemCount, int totalItemCount) {

                    }

                });
        }
        private class LoadMoreDataTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // Create a progressdialog
                progressDialog = new ProgressDialog(getActivity());
                // Set progressdialog title
                progressDialog.setTitle("loading more images!");
                // Set progressdialog message
                progressDialog.setMessage("Please wait...");
                progressDialog.setIndeterminate(false);
                // Show progressdialog
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                String moreResultsResponse = processRequest(nextUrl);
                processJSON(moreResultsResponse);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                progressDialog.dismiss();
                // Pass the results into ListViewAdapter.java
                adapter = new ImageAdapter(getActivity(), imageUrls);
                // Binds the Adapter to the ListView
                imagesGrid.setAdapter(adapter);
                imagesGrid.setSelection(listViewPosition);
            }
        }

    }

}
