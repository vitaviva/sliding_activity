package vitaviva.com.slidingactivity;

import android.os.Bundle;
import android.widget.TextView;

import vitaviva.com.slidingactivity.slidingactivity.SlidingActivity;

public class ThirdActivity extends SlidingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = (TextView) findViewById(R.id.tv);
        tv.setText("The Third Activity");
    }
}
