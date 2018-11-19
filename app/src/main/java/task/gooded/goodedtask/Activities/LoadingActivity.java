package task.gooded.goodedtask.Activities;

import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import task.gooded.goodedtask.Location.LocationFused;
import task.gooded.goodedtask.R;
import task.gooded.goodedtask.views.LoadingIndicatorView;

public class LoadingActivity extends AppCompatActivity implements  LocationFused.GotLocation{
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    LocationFused locationFused;

    @BindView(R.id.rv)
    RelativeLayout relativeLayout;

    @BindView(R.id.text)TextSwitcher text;
    @BindView(R.id.indicatior)LoadingIndicatorView indicatior;
    @BindView(R.id.image)ImageView imageView;


    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        ButterKnife.bind(this);
        text.setVisibility(View.GONE);
        // Declare in and out animations and load them using AnimationUtils class
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);


        relativeLayout.post(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animateRevealColor(relativeLayout);

                    }
                }, 0000);
            }
        });
        text.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                TextView t = new TextView(LoadingActivity.this);
                t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                t.setTextSize(20f);
                t.setTextColor(Color.WHITE);
                return t;
            }
        });
        text.setInAnimation(in);
        text.setOutAnimation(out);
        text.setCurrentText("Hi There");
    }
    private void animateRevealColor(ViewGroup viewRoot)
    {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;

        float finalRadius = (float) Math.hypot(viewRoot.getWidth(), viewRoot.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);

        viewRoot.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        anim.setDuration(500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Animation animFadeIn = AnimationUtils.loadAnimation(LoadingActivity.this, R.anim.anim_fade_in);
                text.setVisibility(View.VISIBLE);
                text.startAnimation(animFadeIn);

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
//                        getLocation();
                        if (checkInternet())
                        changeIcon();
                        else
                            Toast.makeText(LoadingActivity.this,"No Internet Connection",Toast.LENGTH_LONG).show();
                    }
                }, 1500);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        text.setCurrentText("Hi There");
    }

    private boolean checkInternet() {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            return cm.getActiveNetworkInfo() != null;
    }

    private void changeIcon() {
        indicatior.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        text.setText("Locating You...");
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                getLocation();
            }
        }, 1500);
    }

    private void getLocation()
    {
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        locationFused=new LocationFused(this);
        locationFused.connect();
    }
    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkPlayServices()) {
            Toast.makeText(this, "You need to install Google Play Services to use the App properly!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationFused.Disconnect();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }
                if (permissionsRejected.size() > 0) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(LoadingActivity.this).
                                    setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                                permissionsRejected.clear();
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                            return;
                        }
                    }
                } else {
                    if (locationFused != null) {
                        locationFused=new LocationFused(this);
                        locationFused.connect();
                    }
                }

                break;
        }
    }

    @Override
    public void notifyForLocation() {
        text.setText("Here we go....");
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                startHome();
            }
        }, 1000);
    }

    private void startHome() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}
