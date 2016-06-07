package vitaviva.com.slidingactivity.logger;

/**
 * Logger is a wrapper of {@link android.util.Log}
 * But more pretty, simple and powerful
 */
public final class Logger {

    private static final String DEFAULT_TAG = "BTIME";

    private static Printer printer;

    private static Settings settings;

    //no instance
    private Logger() {
    }

    /**
     * It is used to get the settings object in order to change settings
     *
     * @return the settings object
     */
    public static Settings init() {
        return init(DEFAULT_TAG);
    }

    /**
     * It is used to change the tag
     *
     * @param tag is the given string which will be used in Logger as TAG
     */
    public static Settings init(String tag) {
        printer = new CombinedPrinter(new LoggerPrinter(), new PersistPrinter());
        printer.init(tag);

        settings = new Settings();
        return settings;
    }

    public static void clear() {
        printer.clear();
        printer = null;
        settings = null;
    }

    public static Settings getSettings() {
        return settings;
    }

    public static Printer t(String tag) {
        return printer.t(tag, settings.getMethodCount());
    }

    public static Printer t(int methodCount) {
        return printer.t(null, methodCount);
    }

    public static Printer t(String tag, int methodCount) {
        return printer.t(tag, methodCount);
    }

    public static void d(String message) {
        d(null, message);
    }

    public static void d(String tag, String message) {
        printer.d(tag, message);
    }

    public static void e(String message) {
        e(null, message);
    }

    public static void e(String tag, String message) {
        printer.e(tag, message, null);
    }

    public static void e(String tag, String message, Throwable throwable) {
        printer.e(tag, message, throwable);
    }

    public static void e(String message, Throwable throwable) {
        printer.e(message, throwable);
    }

    public static void i(String message) {
        i(null, message);
    }

    public static void i(String tag, String message) {
        printer.i(tag, message);
    }

    public static void v(String message) {
        v(null, message);
    }

    public static void v(String tag, String message) {
        printer.v(tag, message);
    }

    public static void w(String message) {
        w(null, message);
    }

    public static void w(String tag, String message) {
        printer.w(tag, message);
    }

    public static void wtf(String message) {
        printer.wtf(message);
    }

    public static void json(String json) {
        json(null, json);
    }

    public static void json(String tag, String json) {
        printer.json(tag, json);
    }

    public static void xml(String xml) {
        xml(null, xml);
    }

    public static void xml(String tag, String xml) {
        printer.xml(tag, xml);
    }
}
