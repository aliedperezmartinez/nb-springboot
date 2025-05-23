/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.alexfalappa.nbspringboot.projects.initializr;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link ArtifactVersion}
 *
 * @author Alessandro Falappa
 */
public class ArtifactVersionTest {

    @Test
    public void testEquals1() {
        final ArtifactVersion a1 = new ArtifactVersion(1, 4, 2);
        final ArtifactVersion a2 = new ArtifactVersion(1, 4, 2);
        System.out.format("%s equals %s\n", a1, a2);
        assertEquals(a1, a2);
    }

    @Test
    public void testEquals2() {
        final ArtifactVersion a1 = new ArtifactVersion(1, 1, 3, "SNAPSHOT");
        final ArtifactVersion a2 = new ArtifactVersion(1, 1, 3, "SNAPSHOT");
        System.out.format("%s equals %s\n", a1, a2);
        assertEquals(a1, a2);
    }

    @Test
    public void testEquals3() {
        final ArtifactVersion a1 = new ArtifactVersion(1, 1, 3, "SNAPSHOT");
        final ArtifactVersion a2 = new ArtifactVersion(1, 1, 3, "snapshot");
        System.out.format("%s equals %s\n", a1, a2);
        assertEquals(a1, a2);
    }

    @Test
    public void testEqualsSame() {
        final ArtifactVersion a1 = new ArtifactVersion(1, 1, 3, "SNAPSHOT");
        System.out.format("%s equals %s\n", a1, a1);
        assertEquals(a1, a1);
    }

    @Test
    public void testNotEquals1() {
        final ArtifactVersion a1 = new ArtifactVersion(1, 4, 2);
        final ArtifactVersion a2 = new ArtifactVersion(1, 4, 2, "M1");
        System.out.format("%s not equals %s\n", a1, a2);
        assertNotEquals(a1, a2);
    }

    @Test
    public void testNotEquals2() {
        final ArtifactVersion a1 = new ArtifactVersion(1, 4, 2, "M2");
        final ArtifactVersion a2 = new ArtifactVersion(1, 4, 2, "M1");
        System.out.format("%s not equals %s\n", a1, a2);
        assertNotEquals(a1, a2);
    }

    @Test
    public void testNotEqualsMajor() {
        final ArtifactVersion a1 = new ArtifactVersion(2, 4, 2);
        final ArtifactVersion a2 = new ArtifactVersion(1, 4, 2);
        System.out.format("%s not equals %s\n", a1, a2);
        assertNotEquals(a1, a2);
    }

    @Test
    public void testNotEqualsMinor() {
        final ArtifactVersion a1 = new ArtifactVersion(1, 5, 2);
        final ArtifactVersion a2 = new ArtifactVersion(1, 4, 2);
        System.out.format("%s not equals %s\n", a1, a2);
        assertNotEquals(a1, a2);
    }

    @Test
    public void testNotEqualsPatch() {
        final ArtifactVersion a1 = new ArtifactVersion(1, 4, 3);
        final ArtifactVersion a2 = new ArtifactVersion(1, 4, 2);
        System.out.format("%s not equals %s\n", a1, a2);
        assertNotEquals(a1, a2);
    }

    @Test
    public void testNotEqualsNull() {
        final ArtifactVersion a1 = new ArtifactVersion(1, 4, 2, "M2");
        final ArtifactVersion a2 = null;
        System.out.format("%s not equals %s\n", a1, a2);
        assertNotEquals(a1, a2);
    }

    @Test
    public void testNotEqualsOther() {
        final ArtifactVersion a1 = new ArtifactVersion(1, 4, 2, "M2");
        final Object a2 = new Object();
        System.out.format("%s not equals %s\n", a1, a2);
        assertNotEquals(a1, a2);
    }

    @Test
    public void testOf1() {
        String versionString = "1.4.2";
        System.out.println("of " + versionString);
        assertEquals(new ArtifactVersion(1, 4, 2), ArtifactVersion.of(versionString));
    }

    @Test
    public void testOf2() {
        String versionString = "1.2.3-RC";
        System.out.println("of " + versionString);
        assertEquals(new ArtifactVersion(1, 2, 3, "RC"), ArtifactVersion.of(versionString));
    }

    @Test
    public void testOf3() {
        String versionString = "3.2.1.M2";
        System.out.println("of " + versionString);
        assertEquals(new ArtifactVersion(3, 2, 1, "M2"), ArtifactVersion.of(versionString));
    }

    @Test
    public void testOf4() {
        String versionString = "1.2.3-m4";
        System.out.println("of " + versionString);
        assertEquals(new ArtifactVersion(1, 2, 3, "M4"), ArtifactVersion.of(versionString));
    }

    @Test
    public void testOf9() {
        String versionString = "1.2.3.build-snapshot";
        System.out.println("of " + versionString);
        assertEquals(new ArtifactVersion(1, 2, 3, "BUILD-SNAPSHOT"), ArtifactVersion.of(versionString));
    }

    @Test
    public void testOf5() {
        String versionString = "1.2.3_INVALID";
        System.out.println("of " + versionString);
        assertThrows(IllegalArgumentException.class, () -> ArtifactVersion.of(versionString));
    }

    @Test
    public void testOf6() {
        String versionString = "1.a.3";
        System.out.println("of " + versionString);
        assertThrows(IllegalArgumentException.class, () -> ArtifactVersion.of(versionString));
    }

    @Test
    public void testNegativeMajor() {
        assertEquals("Negative major version",
            assertThrows(IllegalArgumentException.class, () -> new ArtifactVersion(-1, 2, 3)).getMessage());
    }

    @Test
    public void testNegativeMinor() {
        assertEquals("Negative minor version",
            assertThrows(IllegalArgumentException.class, () -> new ArtifactVersion(1, -2, 3)).getMessage());
    }

    @Test
    public void testNegativePatch() {
        assertEquals("Negative patch version",
            assertThrows(IllegalArgumentException.class, () -> new ArtifactVersion(1, 2, -3)).getMessage());
    }

    @Test
    public void testCompareTo1() {
        ArtifactVersion one = new ArtifactVersion(1, 1, 2);
        ArtifactVersion two = new ArtifactVersion(1, 1, 2);
        System.out.format("compare %s = %s\n", one, two);
        assertEquals(0, one.compareTo(two));
    }

    @Test
    public void testCompareTo2() {
        ArtifactVersion one = new ArtifactVersion(1, 1, 2, "M1");
        ArtifactVersion two = new ArtifactVersion(1, 1, 2, "M1");
        System.out.format("compare %s = %s\n", one, two);
        assertEquals(0, one.compareTo(two));
    }

    @Test
    public void testCompareTo3() {
        ArtifactVersion one = new ArtifactVersion(1, 1, 2);
        ArtifactVersion two = new ArtifactVersion(2, 0, 1);
        System.out.format("compare %s < %s\n", one, two);
        assertEquals(-1, one.compareTo(two));
    }

    @Test
    public void testCompareTo4() {
        ArtifactVersion one = new ArtifactVersion(1, 0, 13);
        ArtifactVersion two = new ArtifactVersion(1, 1, 1);
        System.out.format("compare %s < %s\n", one, two);
        assertEquals(-1, one.compareTo(two));
    }

    @Test
    public void testCompareTo5() {
        ArtifactVersion one = new ArtifactVersion(1, 0, 1, "M4");
        ArtifactVersion two = new ArtifactVersion(1, 0, 1, "RC1");
        System.out.format("compare %s < %s\n", one, two);
        assertTrue(one.compareTo(two) < 0);
    }

    @Test
    public void testCompareTo6() {
        ArtifactVersion one = new ArtifactVersion(1, 0, 1, "RC1");
        ArtifactVersion two = new ArtifactVersion(1, 0, 1, "RC2");
        System.out.format("compare %s < %s\n", one, two);
        assertTrue(one.compareTo(two) < 0);
    }

    @Test
    public void testCompareTo7() {
        ArtifactVersion one = new ArtifactVersion(1, 0, 1, "RC2");
        ArtifactVersion two = new ArtifactVersion(1, 0, 1, "SNAPSHOT");
        System.out.format("compare %s < %s\n", one, two);
        assertTrue(one.compareTo(two) < 0);
    }

    @Test
    public void testCompareTo8() {
        ArtifactVersion one = new ArtifactVersion(1, 0, 1, "SNAPSHOT");
        ArtifactVersion two = new ArtifactVersion(1, 0, 1);
        System.out.format("compare %s < %s\n", one, two);
        assertTrue(one.compareTo(two) < 0);
    }

    @Test
    public void testCollectionSort() {
        final List<ArtifactVersion> versions = List.of(
                ArtifactVersion.of("2.4.0-M1"),
                ArtifactVersion.of("2.3.0-M1"),
                ArtifactVersion.of("2.3.0-RC2"),
                ArtifactVersion.of("2.3.0-M2"),
                ArtifactVersion.of("2.3.1"),
                ArtifactVersion.of("2.4.0"),
                ArtifactVersion.of("2.3.0-SNAPSHOT"),
                ArtifactVersion.of("2.4.0-RC1"),
                ArtifactVersion.of("2.3.1-SNAPSHOT"),
                ArtifactVersion.of("2.4.0-M2"),
                ArtifactVersion.of("2.3.0-RC1"),
                ArtifactVersion.of("2.4.0-SNAPSHOT"),
                ArtifactVersion.of("2.3.0"))
            .stream()
            .sorted()
            .toList();

        System.out.format("sort %s\n", versions);
        final List<ArtifactVersion> progression = List.of(
            new ArtifactVersion(2, 3, 0, "M1"),
            new ArtifactVersion(2, 3, 0, "M2"),
            new ArtifactVersion(2, 3, 0, "RC1"),
            new ArtifactVersion(2, 3, 0, "RC2"),
            new ArtifactVersion(2, 3, 0, "SNAPSHOT"),
            new ArtifactVersion(2, 3, 0),
            new ArtifactVersion(2, 3, 1, "SNAPSHOT"),
            new ArtifactVersion(2, 3, 1),
            new ArtifactVersion(2, 4, 0, "M1"),
            new ArtifactVersion(2, 4, 0, "M2"),
            new ArtifactVersion(2, 4, 0, "RC1"),
            new ArtifactVersion(2, 4, 0, "SNAPSHOT"),
            new ArtifactVersion(2, 4, 0));
        assertEquals(versions, progression);
    }

}
