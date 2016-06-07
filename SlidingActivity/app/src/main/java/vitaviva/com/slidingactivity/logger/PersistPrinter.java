package vitaviva.com.slidingactivity.logger;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;

import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class PersistPrinter implements Printer {
    private Subject<String, String> logSubject;
    private Subscription logSubscription;

    public PersistPrinter() {
        logSubject = new SerializedSubject<>(PublishSubject.create());
        logSubscription = logSubject
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(this::persistLogMsg, Throwable::printStackTrace);
    }

    private synchronized void persistLogMsg(String msg) {
        try {
            File fileDir = new File(getAppSdRootPath());
            if (!fileDir.isDirectory()) {
                fileDir.mkdir();
            }

            File file = new File(getAppSdRootPath() + "applog.txt");
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(msg);
            bw.newLine();
            bw.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static String getAppSdRootPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "sliding" + File.separator;
    }

    private void scheduleLogMsg(String level, String tag, String msg, Throwable throwable) {
        try {
            if (logSubscription == null || logSubscription.isUnsubscribed()) {
                return;
            }

            SimpleDateFormat time = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault());
            String logMsg = time.format(new java.util.Date());

            logMsg += " " + Thread.currentThread().getName();

            if (level != null) {
                logMsg += " " + level;
            }

            if (tag != null) {
                logMsg += " " + tag + ": ";
            }

            if (msg != null) {
                logMsg += "\t" + msg;
            }

            if (throwable != null) {
                logMsg += ", Throwable -> \n" + LoggerPrinter.exceptionStacktraceToString(throwable);
            }

            logSubject.onNext(logMsg);
        } catch (Throwable e) {
            e.getStackTrace();
        }
    }

    @Override
    public Printer t(String tag, int methodCount) {
        return null;
    }

    @Override
    public void init(String tag) {

    }

    @Override
    public void d(String message) {

    }

    @Override
    public void d(String tag, String message) {

    }

    @Override
    public void e(String message) {
        scheduleLogMsg("Err", null, message, null);
    }

    @Override
    public void e(String tag, String message) {
        scheduleLogMsg("Err", tag, message, null);
    }

    @Override
    public void e(String message, Throwable throwable) {
        scheduleLogMsg("Err", null, message, throwable);
    }

    @Override
    public void e(String tag, String message, Throwable throwable) {
        scheduleLogMsg("Err", tag, message, throwable);
    }

    @Override
    public void w(String message) {
        scheduleLogMsg("Warn", null, message, null);
    }

    @Override
    public void w(String tag, String message) {
        scheduleLogMsg("Warn", tag, message, null);
    }

    @Override
    public void i(String message) {
        scheduleLogMsg("Info", null, message, null);
    }

    @Override
    public void i(String tag, String message) {
        scheduleLogMsg("Info", tag, message, null);
    }

    @Override
    public void v(String message) {

    }

    @Override
    public void v(String tag, String message) {

    }

    @Override
    public void wtf(String message) {

    }

    @Override
    public void wtf(String tag, String message) {

    }

    @Override
    public void json(String json) {

    }

    @Override
    public void json(String tag, String json) {

    }

    @Override
    public void xml(String xml) {

    }

    @Override
    public void xml(String tag, String xml) {

    }

    @Override
    public void clear() {
        logSubscription.unsubscribe();
        logSubscription = null;
        logSubject = null;
    }
}
