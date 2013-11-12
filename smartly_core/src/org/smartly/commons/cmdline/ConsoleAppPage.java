package org.smartly.commons.cmdline;

import org.smartly.commons.Delegates;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class ConsoleAppPage {
// --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private String _name;
    private String _description;

    private final Map<String, Command> _commands;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public ConsoleAppPage() {
        _commands = new HashMap<String, Command>();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final Set<String> keys = _commands.keySet();
        for (final String key : keys) {

        }
        return sb.toString();
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public String enumCommands() {
        final StringBuilder sb = new StringBuilder();
        final Set<String> keys = _commands.keySet();
        for (final String key : keys) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            final Command cmd = this.getCommand(key);
            sb.append("'").append(key).append("' ");
            sb.append(cmd.getName());
        }
        return sb.toString();
    }


    public String getName() {
        return _name;
    }

    public void setName(final String value) {
        _name = value;
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(final String value) {
        _description = value;
    }

    public Command getCommand(final String key) {
        return _commands.get(key);
    }

    public Object runCommand(final String key, final Object... args) {
        final Command cmd = _commands.get(key);
        if (null != cmd) {
            return cmd.run(args);
        }
        return null;
    }

    public void addCommand(final String key, final String name, final Delegates.Function cmd) {
        _commands.put(key, new Command(key, name, cmd));
    }

    // --------------------------------------------------------------------
    //               EMBEDDED
    // --------------------------------------------------------------------

    public class Command {

        private final String _key;
        private final Delegates.Function _cmd;
        private String _name;

        public Command(final String key, final String name, final Delegates.Function cmd) {
            _key = key;
            _cmd = cmd;
            _name = name;
        }

        public String getKey() {
            return _key;
        }

        public Object run(final Object... args) {
            return null != _cmd ? _cmd.handle(args) : null;
        }

        public String getName() {
            return _name;
        }

        public void setName(final String value) {
            _name = value;
        }

    }
}
