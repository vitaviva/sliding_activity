package vitaviva.com.slidingactivity.logger;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({LogLevel.FULL, LogLevel.NONE})
@Retention(RetentionPolicy.SOURCE)
public @interface LogLevel {

    /**
     * Prints all logs
     */
    int FULL = 0;

    /**
     * No log will be printed
     */
    int NONE = 1;
}
