package org.semver;

import org.junit.Assert;
import org.junit.Test;

public class SemanticVersionTest {

    @Test
    public void test_toString() {
        assertToString("1");
        assertToString("1.2");
        assertToString("1.2.3");
        assertToString("1.2.3-alpha");
        assertToString("1.2.3-beta");
        assertToString("1.2.3-beta+build.789");
    }

    private void assertToString(String s) {
        Assert.assertEquals(s, new SemanticVersion(s).toString());
    }

    @Test
    public void compare_MajorMinorPatch_optionalZeros() {
        assertEquals("1", "1.0");
        assertEquals("1", "1.0.0");
    }

    @Test
    public void compare_MajorMinorPatch() {
        assertGreaterThan("1.1", "1.0");
        assertGreaterThan("1.2", "1.1");
        assertGreaterThan("1.10", "1.2");
    }

    @Test
    public void compare_preRelease() {
        // semver.org #11 :
        assertPreReleaseLesserThan("alpha", "alpha.1");
        assertPreReleaseLesserThan("alpha.1", "alpha.beta");
        assertPreReleaseLesserThan("alpha.beta", "beta");
        assertPreReleaseLesserThan("beta", "beta.2");
        assertPreReleaseLesserThan("beta.2", "beta.11");
        assertPreReleaseLesserThan("beta.11", "rc.1");
        assertPreReleaseLesserThan("rc.1", null);
    }

    @Test
    public void compare_versionPreRelease() {
        assertGreaterThan("1.0", "1.0-alpha");
        assertGreaterThan("1.0", "1.0-beta");
        assertGreaterThan("1.0-beta", "1.0-alpha");

        // semver.org #11 :
        assertLesserThan("1.0-alpha", "1.0-alpha.1");
        assertLesserThan("1.0-alpha.1", "1.0-alpha.beta");
        assertLesserThan("1.0-alpha.beta", "1.0-beta");
        assertLesserThan("1.0-beta", "1.0-beta.2");
        assertLesserThan("1.0-beta.2", "1.0-beta.11");
        assertLesserThan("1.0-beta.11", "1.0-rc.1");
        assertLesserThan("1.0-rc.1", "1.0");
    }

    @Test
    public void compare_buildMetadata_ignored() {
        assertLesserThan("1.0", "2.0+build123");
        assertEquals("1.0", "1.0+build123");
        assertGreaterThan("2.0", "1.0+build123");
    }

    private void assertLesserThan(String v1, String v2) {
        Assert.assertTrue("was expecting " + v1 + " < " + v2, new SemanticVersion(v1).compareTo(new SemanticVersion(v2)) < 0);
    }

    private void assertEquals(String v1, String v2) {
        Assert.assertTrue("was expecting " + v1 + " == " + v2, 0 == new SemanticVersion(v1).compareTo(new SemanticVersion(v2)));
    }

    private void assertGreaterThan(String v1, String v2) {
        Assert.assertTrue("was expecting " + v1 + " > " + v2, 0 < new SemanticVersion(v1).compareTo(new SemanticVersion(v2)));
    }

    private SemanticVersion.PreRelease preRelease(String s) {
        return s != null ? new SemanticVersion.PreRelease(s) : null;
    }

    private void assertPreReleaseLesserThan(String v1, String v2) {
        int compare = preRelease(v1).compareTo(preRelease(v2));
        Assert.assertTrue("was expecting " + v1 + " < " + v2 + " but was " + compare, compare < 0);
    }
}
