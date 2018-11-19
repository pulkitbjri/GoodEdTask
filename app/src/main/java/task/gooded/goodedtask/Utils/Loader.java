package task.gooded.goodedtask.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import task.gooded.goodedtask.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class Loader extends Dialog {

    private String messageString= null;

    @BindView(R.id.message) TextView text;


    public Loader( @NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public Loader(@NonNull Context context) {
        super(context);
    }

    protected Loader(@NonNull Context context, boolean cancelable, @Nullable DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.progess_dialog);
        ButterKnife.bind(this);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
        getWindow().setWindowAnimations(R.style.CommonDialogAnimation);

        if (messageString != null) {
            text.setText(messageString);
        }
    }
    public void setMessageString(String messageString)
    {
        this.messageString=messageString;
    }
}
