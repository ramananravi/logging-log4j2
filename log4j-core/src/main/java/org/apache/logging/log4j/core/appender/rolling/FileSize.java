/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */

package org.apache.logging.log4j.core.appender.rolling;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

/**
 * FileSize utility class.
 */
public final class FileSize {
    private static final Logger LOGGER = StatusLogger.getLogger();

    private static final long KB = 1024;
    private static final long MB = KB * KB;
    private static final long GB = KB * MB;
    private static final long TB = KB * GB;

    /**
     * Pattern for string parsing.
     */
    private static final Pattern VALUE_PATTERN =
        Pattern.compile("([0-9]+([\\.,][0-9]+)?)\\s*(|K|M|G|T)B?", Pattern.CASE_INSENSITIVE);

    private FileSize() {
    }

    /**
     * Converts a string to a number of bytes. Strings consist of a floating point value followed by
     * K, M, or G for kilobytes, megabytes, gigabytes, respectively. The
     * abbreviations KB, MB, and GB are also accepted. Matching is case insensitive.
     *
     * @param string The string to convert
     * @param defaultValue The default value if a problem is detected parsing.
     * @return The Bytes value for the string
     */
    public static long parse(final String string, final long defaultValue) {
        final Matcher matcher = VALUE_PATTERN.matcher(string);

        // Valid input?
        if (matcher.matches()) {
            try {
                // Get double precision value
                final double value = NumberFormat.getNumberInstance(Locale.ROOT).parse(
                        matcher.group(1)).doubleValue();

                // Get units specified
                final String units = matcher.group(3);

                if (units.isEmpty()) {
                    return (long) value;
                } else if (units.equalsIgnoreCase("K")) {
                    return (long) (value * KB);
                } else if (units.equalsIgnoreCase("M")) {
                    return (long) (value * MB);
                } else if (units.equalsIgnoreCase("G")) {
                    return (long) (value * GB);
                } else if (units.equalsIgnoreCase("T")) {
                    return (long) (value * TB);
                } else {
                    LOGGER.error("FileSize units not recognized: " + string);
                    return defaultValue;
                }
            } catch (final ParseException e) {
                LOGGER.error("FileSize unable to parse numeric part: " + string, e);
                return defaultValue;
            }
        }
        LOGGER.error("FileSize unable to parse bytes: " + string);
        return defaultValue;
    }

}
