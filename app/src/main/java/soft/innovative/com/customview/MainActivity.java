package soft.innovative.com.customview;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements ProgressListener {
    private CountDownTimer timer;
    private ProgressIndicatorView progressIndicatorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressIndicatorView = findViewById(R.id.progressView);
        progressIndicatorView.setOnProgressBarListener(this);
        timer = new CountDownTimer(10000,100){

            @Override
            public void onTick(long millisUntilFinished) {
                if(progressIndicatorView.getProgress() < 80 )
                    progressIndicatorView.incrementProgress(1);
                else if(timer != null){
                    timer.cancel();
                    timer = null;
                }
            }

            @Override
            public void onFinish() {

            }
        };
        progressIndicatorView.setProgress(50);
        timer.start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Override
    public void onProgressUpdate(int current, int max) {
        if(current == max) {
            progressIndicatorView.setProgress(current);
        }
    }
}
