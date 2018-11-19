package task.gooded.goodedtask.Network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {



        @GET("venues/{param}")
        Call<ResponseBody> getdata(@Path("param") String param, @Query("client_id") String client_id,
                                   @Query("client_secret") String client_secret, @Query("v") String v);


        @GET("venues/explore")
        Call<ResponseBody> getList(@Query("ll") String ll, @Query("client_id") String client_id,
                                   @Query("client_secret") String client_secret, @Query("v") String v,
                                   @Query("radius") String llAcc);

    }