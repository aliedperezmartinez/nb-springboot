package com.github.alexfalappa.nbspringboot;

import org.openide.util.ContextGlobalProvider;
import org.openide.util.Lookup;

public class TestContextGlobalProvider implements ContextGlobalProvider {

    private static Lookup lookup;

    @Override
    public Lookup createGlobalContext() {
        return lookup;
    }

    public static void setLookup(Lookup lookup) {
        TestContextGlobalProvider.lookup = lookup;
    }

}
