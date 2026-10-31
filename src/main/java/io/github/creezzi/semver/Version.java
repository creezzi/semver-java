package io.github.creezzi.semver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Semantic Versioning 2.0.0 value. Build metadata is kept but ignored for ordering. */
public record Version(int major, int minor, int patch, String prerelease, String build)
        implements Comparable<Version> {

    public enum Part { MAJOR, MINOR, PATCH }

    private static final Pattern PATTERN = Pattern.compile(
            "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)"
                    + "(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?"
                    + "(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$");

    public static Version parse(String text) {
        Matcher m = PATTERN.matcher(text.trim());
        if (!m.matches()) {
            throw new IllegalArgumentException("invalid semantic version: " + text);
        }
        return new Version(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)),
                Integer.parseInt(m.group(3)), m.group(4), m.group(5));
    }

    public Version bump(Part part) {
        return switch (part) {
            case MAJOR -> new Version(major + 1, 0, 0, null, null);
            case MINOR -> new Version(major, minor + 1, 0, null, null);
            case PATCH -> new Version(major, minor, prerelease == null ? patch + 1 : patch, null, null);
        };
    }

    @Override
    public int compareTo(Version o) {
        int c = Integer.compare(major, o.major);
        if (c == 0) {
            c = Integer.compare(minor, o.minor);
        }
        if (c == 0) {
            c = Integer.compare(patch, o.patch);
        }
        return c != 0 ? c : comparePrerelease(prerelease, o.prerelease);
    }

    private static int comparePrerelease(String a, String b) {
        if (a == null || b == null) {
            return a == null ? (b == null ? 0 : 1) : -1; // a release outranks any prerelease
        }
        String[] x = a.split("\\.");
        String[] y = b.split("\\.");
        for (int i = 0; i < Math.min(x.length, y.length); i++) {
            boolean nx = x[i].chars().allMatch(Character::isDigit);
            boolean ny = y[i].chars().allMatch(Character::isDigit);
            int c;
            if (nx && ny) {
                c = Long.compare(Long.parseLong(x[i]), Long.parseLong(y[i]));
            } else if (nx != ny) {
                c = nx ? -1 : 1; // numeric identifiers sort before text
            } else {
                c = x[i].compareTo(y[i]);
            }
            if (c != 0) {
                return c;
            }
        }
        return Integer.compare(x.length, y.length);
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch
                + (prerelease != null ? "-" + prerelease : "") + (build != null ? "+" + build : "");
    }
}
