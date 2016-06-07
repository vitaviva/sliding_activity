package vitaviva.com.slidingactivity.logger;

public interface Printer {

    Printer t(String tag, int methodCount);

    void init(String tag);

    void d(String message);

    void d(String tag, String message);

    void e(String message);

    void e(String tag, String message);

    void e(String message, Throwable throwable);

    void e(String tag, String message, Throwable throwable);

    void w(String message);

    void w(String tag, String message);

    void i(String message);

    void i(String tag, String message);

    void v(String message);

    void v(String tag, String message);

    void wtf(String message);

    void wtf(String tag, String message);

    void json(String json);

    void json(String tag, String json);

    void xml(String xml);

    void xml(String tag, String xml);

    void clear();
}
