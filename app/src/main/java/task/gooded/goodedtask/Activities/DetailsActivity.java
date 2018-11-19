package task.gooded.goodedtask.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import task.gooded.goodedtask.Network.ApiClient;
import task.gooded.goodedtask.Place;
import task.gooded.goodedtask.R;
import task.gooded.goodedtask.Utils.Constants;

public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    TextView toolbar;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.desc)
    TextView desc;
    @BindView(R.id.address) TextView address;
    @BindView(R.id.rating) TextView rating;

    @BindView(R.id.frame)
    FrameLayout frame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        String id=getIntent().getStringExtra("id");

        getdata(id);
    }

    public void getdata(String id)
    {
        frame.setVisibility(View.VISIBLE);
        Log.i("", "getdata: "+id);
        ApiClient.getApiClient(this).getdata(id,Constants.CLIENT_ID,Constants.CLIENT_SECRET,"20181119")
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {

                            assert response.body() != null;
                            JSONObject object=new JSONObject(response.body().string()).getJSONObject("response").getJSONObject("venue");
                            Log.i("", "onResponse: "+object.toString());


                            toolbar.setText(object.getString("name"));
                            if (object.has("description"))
                                desc.setText(object.getString("description"));
                            else
                                desc.setText("OOPS!!! No description Found");

                            rating.setText("Rating : "+object.getString("rating")+"/10");

                            JSONArray addressarr=object.getJSONObject("location").getJSONArray("formattedAddress");
                            for (int i = 0; i < addressarr.length(); i++) {
                                address.append(addressarr.getString(i)+"\n");
                            }

                            String imageurl=object.getJSONObject("bestPhoto").getString("prefix")+"500"+
                                    object.getJSONObject("bestPhoto").getString("suffix");
                            Picasso.get().load(imageurl).placeholder(R.drawable.no_img).into(image);
                            frame.setVisibility(View.GONE);

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(DetailsActivity.this,"Connection Error...",Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                });
    }

}
