package io.github.creezzi.semver;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class VersionTest {
    @Test
    void parsesAndPrints() {
        String text = "1.2.3-alpha.1+build.5";
        Version v = Version.parse(text);
        assertEquals(1, v.major());
        assertEquals("alpha.1", v.prerelease());
        assertEquals(text, v.toString());
    }

    @Test
    void rejectsInvalid() {
        for (String bad : new String[] {"1.2", "01.2.3", "1.2.3-", "a.b.c", ""}) {
            assertThrows(IllegalArgumentException.class, () -> Version.parse(bad), bad);
        }
    }

    @Test
    void orderingMatchesTheSpecExample() {
        List<String> chain = List.of("1.0.0-alpha", "1.0.0-alpha.1", "1.0.0-alpha.beta", "1.0.0-beta",
                "1.0.0-beta.2", "1.0.0-beta.11", "1.0.0-rc.1", "1.0.0");
        List<Version> shuffled = new ArrayList<>(chain.stream().map(Version::parse).toList());
        Collections.reverse(shuffled);
        Collections.sort(shuffled);
        assertEquals(chain, shuffled.stream().map(Version::toString).toList());
    }

    @Test
    void buildMetadataDoesNotAffectOrdering() {
        assertEquals(0, Version.parse("1.0.0+a").compareTo(Version.parse("1.0.0+b")));
    }

    @Test
    void bumps() {
        Version v = Version.parse("1.4.2");
        assertEquals("2.0.0", v.bump(Version.Part.MAJOR).toString());
        assertEquals("1.5.0", v.bump(Version.Part.MINOR).toString());
        assertEquals("1.4.3", v.bump(Version.Part.PATCH).toString());
        assertEquals("1.4.2", Version.parse("1.4.2-rc.1").bump(Version.Part.PATCH).toString());
    }
}
