/*
 * Copyright 2019 the original author or authors.
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
package com.github.alexfalappa.nbspringboot.projects.service.impl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.filechooser.FileSystemView;

/**
 * Support data for configuration property values completion.
 *
 * @author Alessandro Falappa
 */
public final class HintSupport {

    private static Set<String> cachedCharsets = null;
    private static Set<String> cachedLocales = null;
    private final static FileSystemView fsView = FileSystemView.getFileSystemView();
    private final static Map<String, ImageIcon> iconCache = new HashMap<>();
    public final static Set<String> MIMETYPES = Set.of(
        "*/*",
        "application/json",
        "application/octet-stream",
        "application/xml",
        "image/gif",
        "image/jpeg",
        "image/png",
        "text/html",
        "text/plain",
        "text/xml");

    // prevent instantiation
    private HintSupport() {
    }

    /**
     * Returns the set of all available {@link Charset} ids caching them.
     * <p>
     * Further calls get the cached set.
     *
     * @return the Set of Charset ids
     */
    public static synchronized Set<String> getAllCharsets() {
        if (cachedCharsets == null) {
            cachedCharsets = Charset.availableCharsets().keySet();
        }
        return Collections.unmodifiableSet(cachedCharsets);
    }

    /**
     * Returns the set of all available {@link Locale} ids caching them.
     * <p>
     * Further calls get the cached set.
     *
     * @return the Set of Charset ids
     */
    public static synchronized Set<String> getAllLocales() {
        if (cachedLocales == null) {
            cachedLocales = Arrays.stream(Locale.getAvailableLocales())
                .map(Locale::toString)
                .filter(locName -> !locName.isEmpty())
                .collect(Collectors.toSet());
        }
        return Collections.unmodifiableSet(cachedLocales);
    }

    /**
     * Returns the system icon for a given file, caching it and converting it to {@link ImageIcon} if necessary.
     * <p>
     * Depending on the platform file type specific icons might be returned.
     *
     * @param file a {@link File} object
     * @return the possibly cached {@link ImageIcon} for the given file
     */
    public static synchronized ImageIcon getIconFor(File file) {
        Icon ico = fsView.getSystemIcon(file);
        final String key = ico.toString();
        return iconCache.computeIfAbsent(key, k -> getImgIco(ico));
    }

    private static ImageIcon getImgIco(Icon ico) {
        if (ico instanceof ImageIcon imageIcon) {
            return imageIcon;
        } else {
            BufferedImage image = new BufferedImage(ico.getIconWidth(), ico.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            ico.paintIcon(new JPanel(), g2, 0, 0);
            g2.dispose();
            return new ImageIcon(image);
        }
    }
}
