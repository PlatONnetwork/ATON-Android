package cos.mos.sb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import cos.mos.sb.widget.KBubbleSeekBar;

public class TestActivity extends AppCompatActivity implements KBubbleSeekBar.OnProgressChangedListener {
    private TextView tt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        tt = findViewById(R.id.ttt);
        ((KBubbleSeekBar) findViewById(R.id.sb1)).setOnProgressChangedListener(this);
        ((KBubbleSeekBar) findViewById(R.id.sb2)).setOnProgressChangedListener(this);
        ((KBubbleSeekBar) findViewById(R.id.sb3)).setOnProgressChangedListener(this);
        ((KBubbleSeekBar) findViewById(R.id.sb4)).setOnProgressChangedListener(this);
    }

    @Override
    public void onProgressChanged(KBubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
        tt.setText("移动数值：" + progress);
    }

    @Override
    public void getProgressOnActionUp(KBubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

    }

    @Override
    public void getProgressOnFinally(KBubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

    }
}
