package vitaviva.com.slidingactivity.logger;

import java.util.ArrayList;
import java.util.List;

public class CombinedPrinter implements Printer {
    private List<Printer> printers = new ArrayList<>();

    public CombinedPrinter(Printer... printers) {
        for (Printer printer : printers) {
            addPrinter(printer);
        }
    }

    public void addPrinter(Printer printer) {
        printers.add(printer);
    }

    @Override
    public Printer t(String tag, int methodCount) {
        for (Printer printer : printers) {
            printer.t(tag, methodCount);
        }
        return this;
    }

    @Override
    public void init(String tag) {
        for (Printer printer : printers) {
            printer.init(tag);
        }
    }

    @Override
    public void d(String message) {
        for (Printer printer : printers) {
            printer.d(message);
        }
    }

    @Override
    public void d(String tag, String message) {
        for (Printer printer : printers) {
            printer.d(tag, message);
        }
    }

    @Override
    public void e(String message) {
        for (Printer printer : printers) {
            printer.e(message);
        }
    }

    @Override
    public void e(String tag, String message) {
        for (Printer printer : printers) {
            printer.e(tag, message);
        }
    }

    @Override
    public void e(String message, Throwable throwable) {
        for (Printer printer : printers) {
            printer.e(message, throwable);
        }
    }

    @Override
    public void e(String tag, String message, Throwable throwable) {
        for (Printer printer : printers) {
            printer.e(tag, message, throwable);
        }
    }

    @Override
    public void w(String message) {
        for (Printer printer : printers) {
            printer.w(message);
        }
    }

    @Override
    public void w(String tag, String message) {
        for (Printer printer : printers) {
            printer.w(tag, message);
        }
    }

    @Override
    public void i(String message) {
        for (Printer printer : printers) {
            printer.i(message);
        }
    }

    @Override
    public void i(String tag, String message) {
        for (Printer printer : printers) {
            printer.i(tag, message);
        }
    }

    @Override
    public void v(String message) {
        for (Printer printer : printers) {
            printer.v(message);
        }
    }

    @Override
    public void v(String tag, String message) {
        for (Printer printer : printers) {
            printer.v(tag, message);
        }
    }

    @Override
    public void wtf(String message) {
        for (Printer printer : printers) {
            printer.wtf(message);
        }
    }

    @Override
    public void wtf(String tag, String message) {
        for (Printer printer : printers) {
            printer.wtf(tag, message);
        }
    }

    @Override
    public void json(String json) {
        for (Printer printer : printers) {
            printer.json(json);
        }
    }

    @Override
    public void json(String tag, String json) {
        for (Printer printer : printers) {
            printer.json(tag, json);
        }
    }

    @Override
    public void xml(String xml) {
        for (Printer printer : printers) {
            printer.xml(xml);
        }
    }

    @Override
    public void xml(String tag, String xml) {
        for (Printer printer : printers) {
            printer.xml(tag, xml);
        }
    }

    @Override
    public void clear() {
        for (Printer printer : printers) {
            printer.clear();
        }
        printers.clear();
    }
}
