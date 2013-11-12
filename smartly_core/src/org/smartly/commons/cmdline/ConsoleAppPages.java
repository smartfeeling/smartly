package org.smartly.commons.cmdline;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Manage Pages.
 */
public final class ConsoleAppPages {

    // --------------------------------------------------------------------
    //               c o n s t a n t s
    // --------------------------------------------------------------------

    private static final String DEFAULT_PAGE = "default";

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private final Map<String, ConsoleAppPage> _pages; // key/value pair (key, instance)

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public ConsoleAppPages() {
        _pages = new HashMap<String, ConsoleAppPage>();
        this.init();
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    @Override
    public String toString() {
        if (count() == 1) {
            return getPage().toString();
        } else {
            // enum all pages
            final StringBuilder sb = new StringBuilder();
            final Set<String> keys = _pages.keySet();
            for (final String key : keys) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                final ConsoleAppPage page = _pages.get(key);
                sb.append("[").append(key).append("] ");
                sb.append(page.getName()).append(": ").append(page.getDescription());
            }
            return sb.toString();
        }
    }

    public int count() {
        return _pages.size();
    }

    public ConsoleAppPage getPage() {
        return _pages.get(DEFAULT_PAGE);
    }

    public ConsoleAppPage getPage(final String key) {
        return _pages.get(key);
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private void init() {
        _pages.put(DEFAULT_PAGE, new ConsoleAppPage());
    }

}
