/*
 * Copyright 2016 the original author or authors.
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
package com.github.alexfalappa.nbspringboot;

import com.github.alexfalappa.nbspringboot.cfgprops.completion.items.FileObjectCompletionItem;
import com.github.alexfalappa.nbspringboot.cfgprops.completion.items.ValueCompletionItem;
import com.github.alexfalappa.nbspringboot.projects.customizer.BootPanel;
import com.github.alexfalappa.nbspringboot.projects.service.impl.HintSupport;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.swing.AbstractButton;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.spi.editor.completion.CompletionResultSet;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;
import org.springframework.boot.configurationmetadata.ConfigurationMetadataProperty;
import org.springframework.boot.configurationmetadata.Deprecation;
import org.springframework.boot.configurationmetadata.ValueHint;

import static com.github.alexfalappa.nbspringboot.PrefConstants.PREF_VM_OPTS;
import static com.github.alexfalappa.nbspringboot.PrefConstants.PREF_VM_OPTS_LAUNCH;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.WARNING;
/**
 * Utility methods used in the plugin.
 *
 * @author Alessandro Falappa
 * @author Diego Díez Ricondo
 */
public final class Utils {

    protected static final String SPRING_BOOT_STARTER_ARTIFACT_ID = "spring-boot-starter";
    protected static final String SPRING_BOOT_STARTER_GROUP_ID = "org.springframework.boot";
    private static final Logger logger = Logger.getLogger(Utils.class.getName());
    private static final Pattern PATTERN_JAVATYPE = Pattern.compile("(\\w+\\.)+(\\w+)");
    private static final String PREFIX_CLASSPATH = "classpath:/";
    private static final String PREFIX_FILE = "file://";
    private static final Set<String> resourcePrefixes = Set.of(PREFIX_CLASSPATH, PREFIX_FILE, "http://", "https://");

    // prevent instantiation
    private Utils() {
    }

    /**
     * Simplistic escape of ampersand and angled brackets in the given string.
     *
     * @param text the string to escape
     * @return escaped string
     */
    public static String simpleHtmlEscape(String text) {
        // order of replacements matters
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /**
     * Shortens a string representing a fully qualified Java type.
     * <p>
     * Strips all package names from the type. Also acts on generic parameters.
     * <p>
     * For example {@code java.util.List<java.lang.String>} gets shortened to {@code List<String>}.
     *
     * @param type a Java type string
     * @return the shortened type
     */
    public static String shortenJavaType(String type) {
        return PATTERN_JAVATYPE.matcher(type).replaceAll("$2");
    }

    /**
     * Builds an HTML formatted string with details on a Spring Boot configuration property extracted from its
     * {@code ItemMetadata}.
     *
     * @param cfgMeta the configuration property metadata object
     * @return the HTML formatted configuration property details
     */
    public static String cfgPropDetailsHtml(ConfigurationMetadataProperty cfgMeta) {
        StringBuilder sb = new StringBuilder();
        // deprecation (optional)
        Deprecation deprecation = cfgMeta.getDeprecation();
        if (deprecation != null) {
            sb.append("<b>");
            if (isErrorDeprecated(cfgMeta)) {
                sb.append("REMOVED");
            } else {
                sb.append("Deprecated");
            }
            sb.append("</b>");
            // deprecation reason if present
            String reason = deprecation.getReason();
            if (reason != null) {
                sb.append(": ").append(simpleHtmlEscape(reason));
            }
            sb.append("<br/>");
            String replacement = deprecation.getReplacement();
            if (replacement != null) {
                sb.append("<i>Replaced by:</i> <tt>").append(replacement).append("</tt><br/>");
            }
        }
        // description (optional)
        final String description = cfgMeta.getDescription();
        if (description != null) {
            sb.append(description).append("<br/>");
        }
        // type
        sb.append("<tt>").append(simpleHtmlEscape(shortenJavaType(cfgMeta.getType()))).append("</tt>");
        return sb.toString();
    }

    public static String vmOptsFromPrefs() {
        StringBuilder sb = new StringBuilder();
        if (NbPreferences.forModule(PrefConstants.class).getBoolean(PREF_VM_OPTS_LAUNCH, true)) {
            sb.append(BootPanel.VMOPTS_OPTIMIZE);
        }
        if (sb.length() > 0) {
            sb.append(' ');
        }
        sb.append(NbPreferences.forModule(PrefConstants.class).get(PREF_VM_OPTS, ""));
        return sb.toString();
    }

    public static boolean isErrorDeprecated(ConfigurationMetadataProperty meta) {
        return Optional.ofNullable(meta.getDeprecation())
            .map(Deprecation::getLevel)
            .map(Deprecation.Level.ERROR::equals)
            .orElse(Boolean.FALSE);
    }

    /**
     * Tries to retrieve the most appropriate {@link Project}.
     * <p>
     * Looks first in the global action context, then in the active {@link TopComponent} context. In each case tries first a
     * direct reference then via the owner of a file object and lastly via a data object.
     *
     * @return the active project or null if no active project found
     */
    public static Project getActiveProject() {
        final Lookup actionsGlobalContext = Utilities.actionsGlobalContext();
        // lookup in global context
        Project prj = actionsGlobalContext.lookup(Project.class);
        if (prj != null) {
            return prj;
        }
        FileObject foobj = actionsGlobalContext.lookup(FileObject.class);
        if (foobj != null) {
            prj = FileOwnerQuery.getOwner(foobj);
            if (prj != null) {
                return prj;
            }
        }
        DataObject dobj = actionsGlobalContext.lookup(DataObject.class);
        if (dobj != null) {
            FileObject fo = dobj.getPrimaryFile();
            prj = FileOwnerQuery.getOwner(fo);
            if (prj != null) {
                return prj;
            }
        }
        // lookup in active editor
        final TopComponent activeEditor = TopComponent.getRegistry().getActivated();
        if (activeEditor != null) {
            final Lookup tcLookup = activeEditor.getLookup();
            Project prj3 = tcLookup.lookup(Project.class);
            if (prj3 != null) {
                return prj3;
            }
            foobj = tcLookup.lookup(FileObject.class);
            if (foobj != null) {
                prj3 = FileOwnerQuery.getOwner(foobj);
                if (prj3 != null) {
                    return prj3;
                }
            }
            dobj = tcLookup.lookup(DataObject.class);
            if (dobj != null) {
                FileObject fo = dobj.getPrimaryFile();
                prj = FileOwnerQuery.getOwner(fo);
                if (prj != null) {
                    return prj;
                }
            }
        }
        logger.log(Level.FINE, "Couldn't find active project reference");
        return null;
    }

    /**
     * Retrieves the execute {@code ClassPath} object for the given project.
     *
     * @param proj the project
     * @return found ClassPath object or null
     */
    public static ClassPath execClasspathForProj(Project proj) {
        final SourceGroup[] srcGroups = getSourceGroups(proj, JavaProjectConstants.SOURCES_TYPE_JAVA);
        if (srcGroups.length > 0) {
            return ClassPath.getClassPath(srcGroups[0].getRootFolder(), ClassPath.EXECUTE);
        } else {
            logger.log(WARNING, "No sources found for project: {0}", new Object[]{proj.toString()});
        }
        return null;
    }

    public static FileObject resourcesFolderForProj(Project proj) {
        final SourceGroup[] srcGroups = getSourceGroups(proj, JavaProjectConstants.SOURCES_TYPE_RESOURCES);
        if (srcGroups.length > 0) {
            // the first sourcegroup is src/main/resources (the second is src/test/resources)
            return srcGroups[0].getRootFolder();
        }
        return proj.getProjectDirectory();
    }

    public static void completeBoolean(String filter, Consumer<ValueHint> consumer) {
        if ("true".contains(filter)) {
            consumer.accept(createHint("true"));
        }
        if ("false".contains(filter)) {
            consumer.accept(createHint("false"));
        }
    }

    public static void completeCharset(String filter, Consumer<ValueHint> consumer) {
        complete(HintSupport.getAllCharsets().stream(), filter, consumer);
    }

    public static void completeLocale(String filter, Consumer<ValueHint> consumer) {
        complete(HintSupport.getAllLocales().stream(), filter, consumer);
    }

    public static void completeMimetype(String filter, Consumer<ValueHint> consumer) {
        complete(HintSupport.MIMETYPES.stream(), filter, consumer);
    }

    public static void completeEnum(ClassPath cp, String dataType, String filter, Consumer<ValueHint> consumer) {
        try {
            Object[] enumvals = cp.getClassLoader(true).loadClass(dataType).getEnumConstants();
            if (enumvals != null) {
                Arrays.stream(enumvals)
                    .map(Object::toString)
                    .map(String::toLowerCase)
                    .filter(valName -> filter == null || valName.contains(filter))
                    .map(Utils::createEnumHint)
                    .forEach(consumer);
            }
        } catch (ClassNotFoundException ex) {
            // enum not available in project classpath, no completion possible
        }
    }

    public static void completeSpringResource(FileObject resourcesFolder, String filter, CompletionResultSet completionResultSet,
            int dotOffset, int caretOffset) {
        if (filter.startsWith(PREFIX_CLASSPATH)) {
            // classpath resource
            String resFilter = filter.substring(PREFIX_CLASSPATH.length());
            int startOffset = dotOffset + PREFIX_CLASSPATH.length();
            String filePart = resFilter;
            FileObject foBase = resourcesFolder;
            if (resFilter.contains("/")) {
                final int slashIdx = resFilter.lastIndexOf('/');
                final String basePart = resFilter.substring(0, slashIdx);
                filePart = resFilter.substring(slashIdx + 1);
                startOffset += slashIdx + 1;
                foBase = resourcesFolder.getFileObject(basePart);
            }
            for (FileObject fObj : foBase.getChildren()) {
                String fname = fObj.getNameExt();
                if (fname.contains(filePart)) {
                    completionResultSet.addItem(new FileObjectCompletionItem(fObj, startOffset, caretOffset));
                }
            }
        } else if (filter.startsWith(PREFIX_FILE)) {
            // filesystem resource
            String fileFilter = filter.substring(PREFIX_FILE.length());
            int startOffset = dotOffset + PREFIX_FILE.length();
            if (fileFilter.isEmpty()) {
                // special case: filesystem root
                Iterable<Path> rootDirs = FileSystems.getDefault().getRootDirectories();
                for (Path rootDir : rootDirs) {
                    FileObject foRoot = FileUtil.toFileObject(rootDir.toFile());
                    // filter out CD/DVD drives letter on Windows
                    if (foRoot != null) {
                        completionResultSet.addItem(new FileObjectCompletionItem(foRoot, startOffset, caretOffset));
                    }
                }
            } else {
                Path pTest = Paths.get(fileFilter);
                startOffset += fileFilter.length();
                String filePart = "";
                if (!Files.exists(pTest)) {
                    filePart = pTest.getFileName().toString();
                    pTest = pTest.getParent();
                    startOffset -= filePart.length();
                }
                if (pTest != null) {
                    try ( DirectoryStream<Path> stream = Files.newDirectoryStream(pTest)) {
                        for (Path p : stream) {
                            if (Files.isReadable(p)) {
                                String fname = p.toString().toLowerCase();
                                if (fname.contains(filePart)) {
                                    completionResultSet.addItem(new FileObjectCompletionItem(FileUtil.toFileObject(p.toFile()),
                                            startOffset, caretOffset));
                                }
                            }
                        }
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        } else {
            resourcePrefixes.stream()
                .filter(rp -> rp.contains(filter))
                .map(rp ->
                    new ValueCompletionItem(createHint(rp), dotOffset, caretOffset,
                        rp.equals(PREFIX_CLASSPATH) || rp.equals(PREFIX_FILE)))
                .forEachOrdered(completionResultSet::addItem);
        }
    }

    /**
     * Create a {@code ValueHint} object from the given value.
     * <p>
     * Created hint has no description.
     *
     * @param value the value to use
     * @return a ValueHint object
     */
    public static ValueHint createHint(String value) {
        ValueHint vh = new ValueHint();
        vh.setValue(value);
        return vh;
    }

    /**
     * Create a {@code ValueHint} object from the given java enumeration value.
     * <p>
     * Created hint has no description and has a Spring Boot property name canonical format.
     *
     * @param value the value to use
     * @return a ValueHint object
     */
    public static ValueHint createEnumHint(String value) {
        return createHint(value.replaceAll("_", "-"));
    }

    /**
     * Create a {@code ValueHint} object from the given value and description.
     *
     * @param value the value to use
     * @param description the description to use
     * @return a ValueHint object
     */
    public static ValueHint createHint(String value, String description) {
        final ValueHint vh = createHint(value);
        vh.setDescription(description);
        return vh;
    }

    /**
     * Converts an icon from the current LAF defaults into an ImageIcon by painting it.
     * <p>
     * Some ui-icons misbehave in that they unconditionally class-cast to the component type they are mostly painted on.
     * Consequently they blow up if we are trying to paint them anywhere else (f.i. in a renderer). This method tries to
     * instantiate a component of the type expected by the icon.
     * <p>
     * This method is an adaption of a cool trick by Darryl Burke/Rob Camick found at
     * http://tips4java.wordpress.com/2008/12/18/icon-table-cell-renderer/#comment-120
     *
     * @param iconName the name of the icon in UIManager
     * @return an ImageIcon with the Icon image
     */
    public static ImageIcon lafDefaultIcon(String iconName) {
        Icon ico = UIManager.getIcon(iconName);
        if(ico == null) return null;
        BufferedImage image = new BufferedImage(ico.getIconWidth(), ico.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        try {
            // paint with a generic java.awt.Component
            ico.paintIcon(new JPanel(), g2, 0, 0);
        } catch (ClassCastException e) {
            try {
                // try to instantiate the needed java.awt.Component
                String className = e.getMessage();
                className = className.substring(className.lastIndexOf(' ') + 1);
                Class<?> clazz = Class.forName(className);
                JComponent standInComponent = getSubstitute(clazz);
                ico.paintIcon(standInComponent, g2, 0, 0);
            } catch (ClassNotFoundException | IllegalAccessException ex) {
                // fallback
                g2.drawRect(0, 0, 16, 16);
                g2.drawLine(0, 0, 16, 16);
                g2.drawLine(16, 0, 0, 16);
            }
        } finally {
            g2.dispose();
        }
        return new ImageIcon(image);
    }

    /**
     * Check if any of the project dependencies artifact ids contains the given string.
     *
     * @param nbMvn NB maven project
     * @param search string to look for
     * @return true if the project has a dependency artifactId containing the search string
     */
    public static boolean dependencyArtifactIdContains(NbMavenProject nbMvn, String search) {
        return nbMvn.getMavenProject().getDependencies().stream()
            .map(Dependency::getArtifactId)
            .anyMatch(id -> id.contains(search));
    }

    /**
     * Find the 'spring-boot-starter' dependency (direct or transitive) and get its effective version.
     *
     * @param project The poject to examine
     * @return the version of the found 'spring-boot-starter' artifact
     */
    public static Optional<String> getSpringBootVersion(Project project) {
        return Stream.of(project)
            .filter(Objects::nonNull)
            .filter(NbMavenProjectImpl.class::isInstance)
            .map(NbMavenProjectImpl.class::cast)
            // All dependencies that this project has, including transitive ones.
            .map(NbMavenProjectImpl::getOriginalMavenProject)
            .map(MavenProject::getArtifacts)
            .flatMap(Set::stream)
            .filter(Utils::isSpringBootStarterArtifact)
            .map(Artifact::getVersion)
            .peek(version -> logger.log(FINE, "Spring Boot version {0} detected", version))
            .findFirst();
    }

    /**
     * Tells whether a project is a Spring Boot project (i.e. has a 'spring-boot-starter' dependency).
     *
     * @param project The poject to examine
     * @return true if the project is a Spring Boot project
     */
    public static boolean isSpringBootProject(Project project) {
        return getSpringBootVersion(project).isPresent();
    }

    private static SourceGroup[] getSourceGroups(Project proj, String type) {
        return ProjectUtils.getSources(proj)
            .getSourceGroups(type);
    }

    private static void complete(final Stream<String> charsets, String filter, Consumer<ValueHint> consumer) {
        charsets
            .filter(name -> name.toLowerCase().contains(filter.toLowerCase()))
            .map(Utils::createHint)
            .forEachOrdered(consumer);
    }

    private static boolean isSpringBootStarterArtifact(Artifact artifact) {
        return SPRING_BOOT_STARTER_GROUP_ID.equals(artifact.getGroupId())
                && SPRING_BOOT_STARTER_ARTIFACT_ID.equals(artifact.getArtifactId());
    }

    private static JComponent getSubstitute(Class<?> clazz) throws IllegalAccessException {
        try {
            return (JComponent) clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException |
            NoSuchMethodException |
            SecurityException |
            InvocationTargetException e) {
            return buildDefaultComponent();
        }
    }

    private static JComponent buildDefaultComponent() {
        final AbstractButton result = new DefaultButton();
        result.setModel(new DefaultButtonModel());
        return result;
    }

    private static class DefaultButton extends AbstractButton {}

}
