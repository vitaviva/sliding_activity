package vitaviva.com.slidingactivity.logger;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Logger is a wrapper for logging utils
 * But more pretty, simple and powerful
 */
final class LoggerPrinter implements Printer {

    private static final int DEBUG = 3;
    private static final int ERROR = 6;
    private static final int ASSERT = 7;
    private static final int INFO = 4;
    private static final int VERBOSE = 2;
    private static final int WARN = 5;

    /**
     * Android's max limit for a log entry is ~4076 bytes,
     * so 4000 bytes is used as chunk size since default charset
     * is UTF-8
     */
    private static final int CHUNK_SIZE = 4000;

    /**
     * It is used for json pretty print
     */
    private static final int JSON_INDENT = 4;

    /**
     * The minimum stack trace index, starts at this class after two native calls.
     */
    private static final int MIN_STACK_OFFSET = 3;

    /**
     * Drawing toolbox
     */
    private static final char TOP_LEFT_CORNER = '╔';
    private static final char BOTTOM_LEFT_CORNER = '╚';
    private static final char MIDDLE_CORNER = '╟';
    private static final char HORIZONTAL_DOUBLE_LINE = '║';
    private static final String DOUBLE_DIVIDER = "════════════════════════════════════════════";
    private static final String SINGLE_DIVIDER = "────────────────────────────────────────────";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;

    /**
     * tag is used for the Log, the name is a little different
     * in order to differentiate the logs easily with the filter
     */
    private String tag;

    /**
     * Localize single tag and method count for each thread
     */
    private final ThreadLocal<String> localTag = new ThreadLocal<>();
    private final ThreadLocal<Integer> localMethodCount = new ThreadLocal<>();

    /**
     * It is used to change the tag
     *
     * @param tag is the given string which will be used in Logger
     */
    @Override
    public void init(String tag) {
        if (tag == null) {
            throw new NullPointerException("tag may not be null");
        }
        if (tag.trim().length() == 0) {
            throw new IllegalStateException("tag may not be empty");
        }
        this.tag = tag;
    }

    @Override
    public Printer t(String tag, int methodCount) {
        if (tag != null) {
            localTag.set(tag);
        }
        localMethodCount.set(methodCount);
        return this;
    }

    @Override
    public void d(String message) {
        d(null, message);
    }

    public void d(String tag, String message) {
        if (tag != null) {
            localTag.set(tag);
        }
        log(DEBUG, message);
    }

    @Override
    public void e(String message) {
        e(null, message, null);
    }

    @Override
    public void e(String tag, String message) {
        e(tag, message, null);
    }

    @Override
    public void e(String message, Throwable throwable) {
        e(null, message, throwable);
    }

    @Override
    public void e(String tag, String message, Throwable throwable) {
        if (tag != null) {
            localTag.set(tag);
        }
        if (throwable != null && message != null) {
            message += " : " + exceptionStacktraceToString(throwable);
        }
        if (throwable != null && message == null) {
            message = exceptionStacktraceToString(throwable);
        }
        if (message == null) {
            message = "No message/exception is set";
        }
        log(ERROR, message);
    }

    @Override
    public void w(String message) {
        w(null, message);
    }

    @Override
    public void w(String tag, String message) {
        if (tag != null) {
            localTag.set(tag);
        }
        log(WARN, message);
    }

    @Override
    public void i(String message) {
        i(null, message);
    }

    @Override
    public void i(String tag, String message) {
        if (tag != null) {
            localTag.set(tag);
        }
        log(INFO, message);
    }

    @Override
    public void v(String message) {
        v(null, message);
    }

    @Override
    public void v(String tag, String message) {
        if (tag != null) {
            localTag.set(tag);
        }
        log(VERBOSE, message);
    }

    @Override
    public void wtf(String message) {
        wtf(null, message);
    }

    @Override
    public void wtf(String tag, String message) {
        if (tag != null) {
            localTag.set(tag);
        }
        log(ASSERT, message);
    }

    @Override
    public void json(String json) {
        json(null, json);
    }

    @Override
    public void json(String tag, String json) {
        if (tag != null) {
            localTag.set(tag);
        }
        if (TextUtils.isEmpty(json)) {
            d("Empty/Null json content");
            return;
        }
        try {
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                String message = jsonObject.toString(JSON_INDENT);
                d(message);
                return;
            }
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                String message = jsonArray.toString(JSON_INDENT);
                d(message);
            }
        } catch (JSONException e) {
            e(e.getCause().getMessage() + "\n" + json);
        }
    }

    @Override
    public void xml(String xml) {
        xml(null, xml);
    }

    @Override
    public void xml(String tag, String xml) {
        if (tag != null) {
            localTag.set(tag);
        }
        if (TextUtils.isEmpty(xml)) {
            d("Empty/Null xml content");
            return;
        }
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            d(xmlOutput.getWriter().toString().replaceFirst(">", ">\n"));
        } catch (TransformerException e) {
            e(e.getCause().getMessage() + "\n" + xml);
        }
    }

    @Override
    public void clear() {
    }

    /**
     * This method is synchronized in order to avoid messy of logs' order.
     */
    private synchronized void log(int logType, String message) {
        if (Logger.getSettings().getLogLevel() == LogLevel.NONE) {
            return;
        }
        String tag = getTag();
        int methodCount = getMethodCount();

        logTopBorder(logType, tag);
        logHeaderContent(logType, tag, methodCount);

        //get bytes of message with system's default charset (which is UTF-8 for Android)
        byte[] bytes = message.getBytes();
        int length = bytes.length;
        if (length <= CHUNK_SIZE) {
            if (methodCount > 0) {
                logDivider(logType, tag);
            }
            logContent(logType, tag, message);
            logBottomBorder(logType, tag);
            return;
        }
        if (methodCount > 0) {
            logDivider(logType, tag);
        }
        for (int i = 0; i < length; i += CHUNK_SIZE) {
            int count = Math.min(length - i, CHUNK_SIZE);
            //create a new String with system's default charset (which is UTF-8 for Android)
            logContent(logType, tag, new String(bytes, i, count));
        }
        logBottomBorder(logType, tag);
    }

    private void logTopBorder(int logType, String tag) {
        logChunk(logType, tag, TOP_BORDER);
    }

    private void logHeaderContent(int logType, String tag, int methodCount) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        if (Logger.getSettings().isShowThreadInfo()) {
            logChunk(logType, tag, HORIZONTAL_DOUBLE_LINE + " Thread: " + Thread.currentThread().getName());
            logDivider(logType, tag);
        }
        String level = "";

        int stackOffset = getStackOffset(trace) + Logger.getSettings().getMethodOffset();

        //corresponding method count with the current stack may exceeds the stack trace. Trims the count
        if (methodCount + stackOffset > trace.length) {
            methodCount = trace.length - stackOffset - 1;
        }

        for (int i = methodCount; i > 0; i--) {
            int stackIndex = i + stackOffset;
            if (stackIndex >= trace.length) {
                continue;
            }
            StringBuilder builder = new StringBuilder();
            builder.append("║ ")
                    .append(level)
                    .append(getSimpleClassName(trace[stackIndex].getClassName()))
                    .append(".")
                    .append(trace[stackIndex].getMethodName())
                    .append(" ")
                    .append(" (")
                    .append(trace[stackIndex].getFileName())
                    .append(":")
                    .append(trace[stackIndex].getLineNumber())
                    .append(")");
            level += "   ";
            logChunk(logType, tag, builder.toString());
        }
    }

    private void logBottomBorder(int logType, String tag) {
        logChunk(logType, tag, BOTTOM_BORDER);
    }

    private void logDivider(int logType, String tag) {
        logChunk(logType, tag, MIDDLE_BORDER);
    }

    private void logContent(int logType, String tag, String chunk) {
        String[] lines = chunk.split(System.getProperty("line.separator"));
        for (String line : lines) {
            logChunk(logType, tag, HORIZONTAL_DOUBLE_LINE + " " + line);
        }
    }

    private void logChunk(int logType, String tag, String chunk) {
        String finalTag = formatTag(tag);
        switch (logType) {
            case ERROR:
                Logger.getSettings().getLogTool().e(finalTag, chunk);
                break;
            case INFO:
                Logger.getSettings().getLogTool().i(finalTag, chunk);
                break;
            case VERBOSE:
                Logger.getSettings().getLogTool().v(finalTag, chunk);
                break;
            case WARN:
                Logger.getSettings().getLogTool().w(finalTag, chunk);
                break;
            case ASSERT:
                Logger.getSettings().getLogTool().wtf(finalTag, chunk);
                break;
            case DEBUG:
                // Fall through, log debug by default
            default:
                Logger.getSettings().getLogTool().d(finalTag, chunk);
                break;
        }
    }

    private String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    private String formatTag(String tag) {
        if (!TextUtils.isEmpty(tag) && !TextUtils.equals(this.tag, tag)) {
            return this.tag + "-" + tag;
        }
        return this.tag;
    }

    /**
     * @return the appropriate tag based on local or global
     */
    private String getTag() {
        String tag = localTag.get();
        if (tag != null) {
            localTag.remove();
            return tag;
        }
        return this.tag;
    }

    private String createMessage(String message, Object... args) {
        return args.length == 0 ? message : String.format(message, args);
    }

    private int getMethodCount() {
        Integer count = localMethodCount.get();
        int result = Logger.getSettings().getMethodCount();
        if (count != null) {
            localMethodCount.remove();
            result = count;
        }
        if (result < 0) {
            throw new IllegalStateException("methodCount cannot be negative");
        }
        return result;
    }

    /**
     * Determines the starting index of the stack trace, after method calls made by this class.
     *
     * @param trace the stack trace
     * @return the stack offset
     */
    private int getStackOffset(StackTraceElement[] trace) {
        for (int i = MIN_STACK_OFFSET; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(LoggerPrinter.class.getName()) && !name.equals(Logger.class.getName()) &&
                    !name.equals(CombinedPrinter.class.getName())) {
                return --i;
            }
        }
        return -1;
    }

    public static String exceptionStacktraceToString(Throwable e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        e.printStackTrace(ps);
        ps.close();
        return baos.toString();
    }

}
