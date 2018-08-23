package org.semver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of Semantic Versioning 2.0.0 : http://semver.org
 * <p>
 * Ex: "1.2.3-beta.2+build.456"
 *
 * Examples:
 * - "1"
 * - "1.0"
 * - "1.0.0"
 *
 * Pre-release :
 * - "1.2.3-alpha"
 * - "1.2.3-beta"
 * - "1.2.3-beta.2"
 * - "1.2.3-rc.1"
 * - "1.2.3-rc.2"
 *
 * Build metadata :
 * - "1.0+build.12"
 * - "1.0+r24682"
 * - "1.0-beta+r24682"
 */
public class SemanticVersion implements Comparable<SemanticVersion> {

    // ex: 1.2.3-beta.2+build.456
    private static final Pattern PATTERN = Pattern.compile("(\\d+)(\\.(\\d+))?(\\.(\\d+))?(-(.+))?(\\+(.+))?");

    private Integer major;
    private Integer minor;
    private Integer patch;

    private PreRelease preRelease;

    private String buildMetadata;

    public SemanticVersion(String s) {
        Matcher matcher = PATTERN.matcher(s);
        if (matcher.matches()) {
            major = groupInt(matcher, 1);
            minor = groupInt(matcher, 3);
            patch = groupInt(matcher, 5);
            String preReleaseGroup = group(matcher, 7);
            preRelease = preReleaseGroup != null ? new PreRelease(preReleaseGroup) : null;
            buildMetadata = group(matcher, 9);
        } else {
            throw new IllegalArgumentException("invalid version number : " + s);
        }
    }

    static class PreRelease implements Comparable<PreRelease> {
        String full;
        String[] tokens;

        PreRelease(String s) {
            full = s;
            tokens = s.split("\\.");
        }

        public int compareTo(PreRelease other) {
            if (other == null) return -1;// 1.0-alpha < 1.0
            int i = 0;
            while (i < tokens.length) {
                String otherToken = i < other.tokens.length ? other.tokens[i] : null;
                if (otherToken != null) {
                    Integer numericToken = parseInt(tokens[i]);
                    Integer numericOtherToken = parseInt(otherToken);
                    int compare;
                    if (numericToken != null && numericOtherToken != null) {
                        compare = numericToken.compareTo(numericOtherToken);
                    } else {
                        compare = tokens[i].compareTo(otherToken);
                    }
                    if (compare != 0) return compare;
                } else {
                    // other is shorter
                    return -1; // alpha < alpha.1
                }
                i++;
            }
            // alpha < alpha.1
            return i < other.tokens.length ? -1 : 0;
        }

        private Integer parseInt(String s) {
            if (s == null || s.length() == 0) return null;
            for (int i = 0; i < s.length(); ++i) {
                if (!Character.isDigit(s.charAt(i))) {
                    return null;
                }
            }
            return new Integer(s);
        }

        @Override
        public String toString() {
            return full;
        }
    }

    private String group(Matcher matcher, int groupIndex) {
        return groupIndex < matcher.groupCount() ? matcher.group(groupIndex) : null;
    }

    private Integer groupInt(Matcher matcher, int groupIndex) {
        String group = group(matcher, groupIndex);
        return group != null ? new Integer(group) : null;
    }

    public String toString() {
        return major + nullable(".", minor) + nullable(".", patch) + nullable("-", preRelease) + nullable("+", buildMetadata);
    }

    private String nullable(String prefix, Object s) {
        return s != null ? prefix + s : "";
    }

    public int compareTo(SemanticVersion v) {
        int compare = compare(major, v.major);
        if (compare != 0) return compare;
        compare = compare(minor, v.minor);
        if (compare != 0) return compare;
        compare = compare(patch, v.patch);
        if (compare != 0) return compare;
        compare = compare(preRelease, v.preRelease);
        // semver.org #11 : Build metadata does not figure into precedence
        return compare;
    }

    private int compare(PreRelease r1, PreRelease r2) {
        if (r1 == null && r2 == null) return 0;
        if (r1 == null) return 1; // 1.0 > 1.0-alpha
        if (r2 == null) return -1; // 1.0-alpha < 1.0
        return r1.compareTo(r2);
    }

    private int compare(Integer i1, Integer i2) {
        if (i1 == null) i1 = 0;
        if (i2 == null) i2 = 0;
        return i1.compareTo(i2);
    }
}
