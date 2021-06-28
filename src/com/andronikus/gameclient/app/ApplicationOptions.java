package com.andronikus.gameclient.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>Arguments given to an application. Meant to simulate input format of a UNIX program.</p>
 *
 * <p>"-p [args]" defines some tag "p" with "args" as a value.</p>
 *
 * <p>This is a stateful instance and should be reinitialized for any new arguments. Do not reuse.</p>
 *
 * @author Andronikus
 */
public class ApplicationOptions {

    private List<Option> options = new ArrayList<>();

    /**
     * Parse arguments into options.
     *
     * @param arguments The arguments
     */
    public ApplicationOptions(String[] arguments) {
        Option currentOption = null;
        for (String argument : arguments) {
            if (argument.startsWith("-") && argument.length() > 1) {
                currentOption = new Option();
                currentOption.key = argument.substring(1);
                options.add(currentOption);
            } else if (currentOption != null) {
                currentOption.values.add(argument);
            } else {
                throw new IllegalArgumentException("No label for initial argument.");
            }
        }
    }

    /**
     * Get an option from the arguments.
     *
     * @param option The name of the option
     * @param required Whether or not this option was required
     * @param expectedValues How many values are we expecting? Null means any.
     * @return Values
     */
    public List<String> getOption(String option, boolean required, Integer expectedValues) {
        final Optional<Option> matchingOption = options.stream().filter(existingOption -> existingOption.key.equals(option)).findFirst();

        if (!matchingOption.isPresent()) {
            if (required) {
                throw new IllegalArgumentException("Option \"" + option + "\" is missing.");
            } else {
                return null;
            }
        }

        final Option confirmedOption = matchingOption.get();

        if (expectedValues != null && expectedValues != confirmedOption.values.size()) {
            throw new IllegalArgumentException("Option \"" + confirmedOption.key + "\" requires " + expectedValues + " arguments but received " + confirmedOption.values.size() + ".");
        }

        confirmedOption.queried = true;
        return confirmedOption.values;
    }

    /**
     * Do an internal check that every option given in the command line was used by the program.
     */
    public void checkUnusedOptions() {
        final Optional<Option> unusedOption = options.stream().filter(option -> !option.queried).findFirst();
        if (unusedOption.isPresent()) {
            final Option confirmedUnusedOption = unusedOption.get();
            throw new IllegalStateException("Unknown option \"" + confirmedUnusedOption.key + "\".");
        }
    }

    private class Option {
        private String key;
        private List<String> values = new ArrayList<>();
        private boolean queried = false;
    }
}
