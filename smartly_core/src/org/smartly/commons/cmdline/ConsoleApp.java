package org.smartly.commons.cmdline;

import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Console Application
 */
public abstract class ConsoleApp {

    // --------------------------------------------------------------------
    //               c o n s t a n t s
    // --------------------------------------------------------------------

    private static final String WELCOME = "**********************************************************\n" +
            "\t\t\t STARTED {0} CONSOLE\n" +
            "**********************************************************";

    private static final String PAGE = "\t\t\t PAGE '{0}'\n" +
            "**********************************************************";


    private static final String CMD_EXIT = "quit";
    private static final String CMD_BACK = "back";

    private static final String PRESS_ANY_KEY = "PRESS ANY KEY TO CONTINUE... ";
    private static final String CHOOSE_PAGE = "WRITE '" + CMD_BACK + "' TO GO BACK, '" + CMD_EXIT + "' TO EXIT OR CHOOSE A PAGE.\n" +
            "PAGE NAME: ";
    private static final String CHOOSE_COMMAND = "WRITE '" + CMD_BACK + "' TO GO BACK, '" + CMD_EXIT + "' TO EXIT OR CHOOSE A COMMAND.\n" +
            "COMMAND NAME:";
    private static final String INVALID_PAGE = "INVALID PAGE NAME: '{0}'. PLEASE, CHOOSE A VALID PAGE NAME.";
    private static final String INVALID_COMMAND = "INVALID COMMAND NAME: '{0}'. PLEASE, CHOOSE A VALID COMMAND.";
    private static final String WAIT = "RUNNING COMMAND '{0}', PLEASE WAIT";
    private static final String COMMAND_RESPONSE = "Command elapsed time {0} ms with response: {1}";
    private static final String COMMAND_LIST = "COMMANDS:";


    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private final String _name;
    private final ConsoleAppPages _pages;

    private BufferedReader _in;

    private ConsoleAppPage _curr_page;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public ConsoleApp(final String name) {
        _name = name;
        _pages = new ConsoleAppPages();

        this.initialize(_pages);

        _in = new BufferedReader(new InputStreamReader(System.in));
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    protected abstract void initialize(final ConsoleAppPages pages);


    /**
     * Start Console Application
     */
    public void run() {
        this.println(FormatUtils.format(WELCOME, _name));
        this.println("");

        this.reset();

        try {
            while (true) {
                final String key = read();
                if(null==key){
                   break;
                } else if (!StringUtils.hasText(key)) {
                    this.reset();
                } else {
                    if (key.equalsIgnoreCase(CMD_EXIT)) {
                        break;
                    } else if (key.equalsIgnoreCase(CMD_BACK)) {
                        _curr_page = null;
                        this.reset();
                    } else {
                        // key is a command or a page name
                        if (null != _curr_page) {
                            // command
                            this.runCommand(key);
                        } else {
                            this.gotoPage(key, _pages.getPage(key));
                        }
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private void reset() {
        if (_pages.count() == 1) {
            // single page
            gotoPage("", _pages.getDefault());
        } else {
            // enum pages
            println(_pages.toString());
        }
    }

    private void println(final String output) {
        System.out.println(output);
    }

    private String read() {
        try {
            return _in.readLine();
        } catch (Throwable ignored) {
            return null;
        }
    }

    private void gotoPage(final String key, final ConsoleAppPage page) {
        if (null != page) {
            _curr_page = page;
            if (StringUtils.hasText(key)) {
                this.println(FormatUtils.format(PAGE, page.getName()));
            }
            this.println(COMMAND_LIST);
            this.println("");
            this.println(_curr_page.enumCommands());
            this.println("");
            this.println(CHOOSE_COMMAND);
        } else {
            this.println(FormatUtils.format(INVALID_PAGE, key));
            this.println(CHOOSE_PAGE);
        }
    }

    private void runCommand(final String key) {
        if (null != _curr_page) {
            if (_curr_page.hasCommand(key)) {
                // start command
                final long start = System.currentTimeMillis();
                final ConsoleAppTicker ticker = new ConsoleAppTicker();

                this.println(FormatUtils.format(WAIT, key));
                ticker.start();

                final Object response = _curr_page.runCommand(key);

                // command finished
                final long finish = System.currentTimeMillis();
                ticker.interrupt();

                // write response
                this.println(FormatUtils.format(COMMAND_RESPONSE, finish - start, response));
                this.println(PRESS_ANY_KEY);
            } else {
                this.println(FormatUtils.format(INVALID_COMMAND, key));
                this.println(CHOOSE_COMMAND);
            }

        } else {
            // invalid status. reset
            this.reset();
        }
    }

    // --------------------------------------------------------------------
    //               EMBEDDED
    // --------------------------------------------------------------------

    private class ConsoleAppTicker
            extends Thread {

        @Override
        public synchronized void run() {
            try {
                while (!this.isInterrupted()) {
                    Thread.sleep(500);
                    System.out.print(".");
                }
            } catch (Throwable ignored) {
            }
        }

    }
}
