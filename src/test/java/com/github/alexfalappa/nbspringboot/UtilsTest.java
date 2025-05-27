/*
 * Copyright 2018 the original author or authors.
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
import java.awt.Frame;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import javax.swing.Action;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.project.JavaProjectConstants;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.api.project.Sources;
import org.netbeans.junit.MockServices;
import org.netbeans.modules.editor.completion.CompletionResultSetImpl;
import org.netbeans.modules.editor.completion.CompletionSpiPackageAccessor;
import org.netbeans.modules.maven.NbMavenProjectImpl;
import org.netbeans.modules.maven.api.NbMavenProject;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.project.FileOwnerQueryImplementation;
import org.netbeans.spi.project.ProjectManagerImplementation;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.ContextGlobalProvider;
import org.openide.util.Lookup;
import org.openide.util.Mutex;
import org.openide.util.Utilities;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.TopComponentGroup;
import org.openide.windows.WindowManager;
import org.openide.windows.Workspace;
import org.springframework.boot.configurationmetadata.ConfigurationMetadataProperty;
import org.springframework.boot.configurationmetadata.Deprecation;
import org.springframework.boot.configurationmetadata.ValueHint;

import static com.github.alexfalappa.nbspringboot.Utils.SPRING_BOOT_STARTER_ARTIFACT_ID;
import static com.github.alexfalappa.nbspringboot.Utils.SPRING_BOOT_STARTER_GROUP_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test Utils class.
 *
 * @author Hector Espert
 */
@TestMethodOrder(MethodOrderer.DisplayName.class)
@ExtendWith(MockitoExtension.class)
public class UtilsTest {

    private static final List<Dependency> DEPENDENCIES = List.of(
        createDependency("contains"),
        createDependency("other")
    );

    private enum UtilEnum {
        ENUM1, ENUM2, THE_OTHER
    }

    @Mock
    private Consumer<ValueHint> consumer;
    @Mock
    private ClassPath cp;
    @Mock
    private MavenProject mavenProject;
    @Mock
    private NbMavenProject nbMvnProject;
    @Mock
    private Project project;
    @Mock
    private FileObject fileObject;
    @Mock
    private Lookup lookup;
    @Mock
    private Sources sources;
    @Mock
    private SourceGroup sourceGroup;
    @Mock
    private DataObject dataObject;
    @Mock
    private TopComponent topComponent;
    @Mock
    private NbMavenProjectImpl nbMavenProject;
    @Mock
    private Artifact artifact;

    @Test
    public void testSimpleHtmlEscape() {
        String result = Utils.simpleHtmlEscape("<p>STRING</p>");
        String expected = "&lt;p&gt;STRING&lt;/p&gt;";
        assertEquals(expected, result);
    }

    /**
     * Test of vmOptsFromPrefs method, of class Utils.
     */
    @Test
    public void testVmOptsFromPrefs() {
        String expResult = "-noverify -XX:TieredStopAtLevel=1 ";
        String result = Utils.vmOptsFromPrefs();
        assertEquals(expResult, result);
    }

    /**
     * Test of isErrorDeprecated method, of class Utils.
     */
    @Test
    public void testIsErrorDeprecated() {
        ConfigurationMetadataProperty meta = new ConfigurationMetadataProperty();
        assertFalse(Utils.isErrorDeprecated(meta));

        Deprecation deprecation = new Deprecation();
        deprecation.setLevel(Deprecation.Level.WARNING);
        meta.setDeprecation(deprecation);
        assertFalse(Utils.isErrorDeprecated(meta));

        deprecation.setLevel(Deprecation.Level.ERROR);
        meta.setDeprecation(deprecation);
        assertTrue(Utils.isErrorDeprecated(meta));
    }

    @Test
    public void testShortenJavaType() {
        assertEquals("UtilsTest", Utils.shortenJavaType(this.getClass().getCanonicalName()));
    }

    @Test
    public void testShortenJavaTypeGeneric() {
        assertEquals("List<String>", Utils.shortenJavaType("java.util.List<java.lang.String>"));
    }

    @Test
    public void testCreateHint() {
        final String value = "value";

        final ValueHint hint = Utils.createHint(value);

        assertEquals(value, hint.getValue());
    }

    @Test
    public void testCreateEnumHint() {
        final ValueHint hint = Utils.createEnumHint("enum_value");

        assertEquals("enum-value", hint.getValue());
    }

    @Test
    public void testCreateHintDescription() {
        final String value = "value";
        final String description = "description";

        final ValueHint hint = Utils.createHint(value, description);

        assertEquals(value, hint.getValue());
        assertEquals(description, hint.getDescription());
    }

    @Test
    public void testDependencyArtifactIdContains() {
        when(nbMvnProject.getMavenProject()).thenReturn(mavenProject);
        when(mavenProject.getDependencies()).thenReturn(DEPENDENCIES);

        assertTrue(Utils.dependencyArtifactIdContains(nbMvnProject, "contains"));
    }

    @Test
    public void testDependencyArtifactIdDoesNotContain() {
        when(nbMvnProject.getMavenProject()).thenReturn(mavenProject);
        when(mavenProject.getDependencies()).thenReturn(DEPENDENCIES);

        assertFalse(Utils.dependencyArtifactIdContains(nbMvnProject, "doesnotcontain"));
    }

    @Test
    public void testCompleteBooleanTrue() {
        doAnswer(invocation -> {
            ValueHint vh = invocation.getArgument(0);
            assertEquals("true", vh.getValue());
            return null;
        })
            .when(consumer).accept(any(ValueHint.class));

        Utils.completeBoolean("true", consumer);

        verify(consumer).accept(any(ValueHint.class));
    }

    @Test
    public void testCompleteBooleanFalse() {
        doAnswer(invocation -> {
            ValueHint vh = invocation.getArgument(0);
            assertEquals("false", vh.getValue());
            return null;
        })
            .when(consumer).accept(any(ValueHint.class));

        Utils.completeBoolean("false", consumer);

        verify(consumer).accept(any(ValueHint.class));
    }

    @Test
    public void testCompleteCharset() {
        final String charset = Charset.defaultCharset().displayName();
        doAnswer(invocation -> {
            ValueHint vh = invocation.getArgument(0);
            assertTrue(() -> ((String)vh.getValue()).toLowerCase().contains(charset.toLowerCase()));
            return null;
        })
            .when(consumer).accept(any(ValueHint.class));

        Utils.completeCharset(charset, consumer);

        verify(consumer).accept(any(ValueHint.class));
    }

    @Test
    public void testCompleteLocale() {
        final String locale = Locale.getDefault().toString();
        System.out.println("Locale: " + locale);
        doAnswer(invocation -> {
            ValueHint vh = invocation.getArgument(0);
            assertTrue(() -> ((String)vh.getValue()).toLowerCase().contains(locale.toLowerCase()));
            return null;
        })
            .when(consumer).accept(any(ValueHint.class));

        Utils.completeLocale(locale, consumer);

        verify(consumer, atLeastOnce()).accept(any(ValueHint.class));
    }

    @Test
    public void testCompleteMimetype() {
        final String mimeType = "json";
        doAnswer(invocation -> {
            ValueHint vh = invocation.getArgument(0);
            assertTrue(() -> ((String)vh.getValue()).toLowerCase().contains(mimeType));
            return null;
        })
            .when(consumer).accept(any(ValueHint.class));

        Utils.completeMimetype(mimeType, consumer);

        verify(consumer).accept(any(ValueHint.class));
    }

    @Test
    public void testCompleteEnum() {
        when(cp.getClassLoader(true)).thenReturn(getClass().getClassLoader());
        final String filter = UtilEnum.THE_OTHER.name().toLowerCase();
        doAnswer(invocation -> {
            ValueHint vh = invocation.getArgument(0);
            assertEquals("the-other", vh.getValue());
            return null;
        })
            .when(consumer).accept(any(ValueHint.class));

        Utils.completeEnum(cp, UtilEnum.class.getTypeName(), filter, consumer);

        verify(consumer).accept(any(ValueHint.class));
    }

    @Test
    public void testCompleteEnumClassNotFound() {
        when(cp.getClassLoader(true)).thenReturn(getClass().getClassLoader());
        final String filter = UtilEnum.THE_OTHER.name().toLowerCase();

        Utils.completeEnum(cp, "this_class_does_not_exist", filter, consumer);

        verify(consumer, never()).accept(any(ValueHint.class));
    }

    @Test
    public void testCfgPropDetailsHtmlEmpty() {
        ConfigurationMetadataProperty cmdp = createConfigurationMetadataProperty();

        assertEquals("<tt>String</tt>", Utils.cfgPropDetailsHtml(cmdp));
    }

    @Test
    public void testCfgPropDetailsHtmlDeprecation() {
        ConfigurationMetadataProperty cmdp = createConfigurationMetadataProperty();
        cmdp.setDeprecation(new Deprecation());

        assertEquals("<b>Deprecated</b><br/><tt>String</tt>", Utils.cfgPropDetailsHtml(cmdp));
    }

    @Test
    public void testCfgPropDetailsHtmlDeprecationError() {
        ConfigurationMetadataProperty cmdp = createConfigurationMetadataProperty();
        final Deprecation deprecation = new Deprecation();
        deprecation.setLevel(Deprecation.Level.ERROR);
        cmdp.setDeprecation(deprecation);

        assertEquals("<b>REMOVED</b><br/><tt>String</tt>", Utils.cfgPropDetailsHtml(cmdp));
    }

    @Test
    public void testCfgPropDetailsHtmlDeprecationReason() {
        ConfigurationMetadataProperty cmdp = createConfigurationMetadataProperty();
        final Deprecation deprecation = new Deprecation();
        deprecation.setReason("Reason");
        cmdp.setDeprecation(deprecation);

        assertEquals("<b>Deprecated</b>: Reason<br/><tt>String</tt>", Utils.cfgPropDetailsHtml(cmdp));
    }

    @Test
    public void testCfgPropDetailsHtmlDeprecationReplacement() {
        ConfigurationMetadataProperty cmdp = createConfigurationMetadataProperty();
        final Deprecation deprecation = new Deprecation();
        deprecation.setReplacement("Replacement");
        cmdp.setDeprecation(deprecation);

        assertEquals("<b>Deprecated</b><br/><i>Replaced by:</i> <tt>Replacement</tt><br/><tt>String</tt>", Utils.cfgPropDetailsHtml(cmdp));
    }

    @Test
    public void testCfgPropDetailsHtmlDescription() {
        ConfigurationMetadataProperty cmdp = createConfigurationMetadataProperty();
        cmdp.setDescription("Description");

        assertEquals("Description<br/><tt>String</tt>", Utils.cfgPropDetailsHtml(cmdp));
    }

    @Test
    public void testExecClasspathForProj() {
        mockClassPath(JavaProjectConstants.SOURCES_TYPE_JAVA);

        ClassPath result = Utils.execClasspathForProj(project);

        assertEquals(cp, result);
    }

    @Test
    public void testExecClasspathForProjNoSourceGroups() {
        when(project.getLookup()).thenReturn(lookup);
        when(lookup.lookup(Sources.class)).thenReturn(sources);
        when(sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_JAVA))
            .thenReturn(new SourceGroup[]{});

        assertNull(Utils.execClasspathForProj(project));
    }

    @Test
    public void testResourcesFolderForProj() {
        mockClassPath(JavaProjectConstants.SOURCES_TYPE_RESOURCES);

        final FileObject result = Utils.resourcesFolderForProj(project);

        assertEquals(fileObject, result);
    }

    @Test
    public void testResourcesFolderForProjNoSourceGroups() {
        when(project.getLookup()).thenReturn(lookup);
        when(lookup.lookup(Sources.class)).thenReturn(sources);
        when(sources.getSourceGroups(JavaProjectConstants.SOURCES_TYPE_RESOURCES))
            .thenReturn(new SourceGroup[]{});

        assertNull(Utils.resourcesFolderForProj(project));
    }

    @Test
    public void testGetActiveProjectGlobalContext() {
        MockServices.setServices(TestContextGlobalProvider.class);
        TestContextGlobalProvider.setLookup(lookup);
        // Utilities.actionsGlobalContext() caches lookup the first time.
        // We'll have to use this lookup
        final Lookup lookup = Utilities.actionsGlobalContext();
        when(lookup.lookup(Project.class)).thenReturn(project);

        assertEquals(project, Utils.getActiveProject());
    }

    @Test
    public void testGetActiveProjectGlobalContextFileObject() {
        MockServices.setServices(
            TestContextGlobalProvider.class,
            TestFileOwnerQueryImplementation.class);
        TestContextGlobalProvider.setLookup(lookup);
        TestFileOwnerQueryImplementation.setProject(project);
        // Utilities.actionsGlobalContext() caches lookup the first time.
        // We'll have to use this lookup
        final Lookup lookup = Utilities.actionsGlobalContext();
        when(lookup.lookup(Project.class)).thenReturn(null);
        when(lookup.lookup(FileObject.class)).thenReturn(fileObject);

        assertEquals(project, Utils.getActiveProject());
    }

    @Test
    public void testGetActiveProjectGlobalContextDataObject() {
        MockServices.setServices(
            TestContextGlobalProvider.class,
            TestFileOwnerQueryImplementation.class);
        TestContextGlobalProvider.setLookup(lookup);
        TestFileOwnerQueryImplementation.setProject(project);
        // Utilities.actionsGlobalContext() caches lookup the first time.
        // We'll have to use this lookup
        final Lookup lookup = Utilities.actionsGlobalContext();
        when(lookup.lookup(Project.class)).thenReturn(null);
        when(lookup.lookup(FileObject.class)).thenReturn(null, fileObject);
        when(lookup.lookup(DataObject.class)).thenReturn(dataObject);
        when(dataObject.getPrimaryFile()).thenReturn(fileObject);

        assertEquals(project, Utils.getActiveProject());
    }

    @Test
    public void testGetActiveProjectActiveEditor() {
        MockServices.setServices(
            TestContextGlobalProvider.class,
            TestFileOwnerQueryImplementation.class,
            TestWindowManager.class,
            TestTopComponentRegistry.class);
        TestContextGlobalProvider.setLookup(lookup);
        TestFileOwnerQueryImplementation.setProject(project);
        TestWindowManager.setTopComponent(topComponent);
        TestTopComponentRegistry.setTopComponent(topComponent);
        // Utilities.actionsGlobalContext() caches lookup the first time.
        // We'll have to use this lookup
        final Lookup lookup = Utilities.actionsGlobalContext();
        when(topComponent.getLookup()).thenReturn(lookup);
        when(lookup.lookup(Project.class)).thenReturn(null, project);
        when(lookup.lookup(FileObject.class)).thenReturn(null);
        when(lookup.lookup(DataObject.class)).thenReturn(null);

        assertEquals(project, Utils.getActiveProject());
    }

    @Test
    public void testGetActiveProjectActiveEditorFileObject() {
        MockServices.setServices(
            TestContextGlobalProvider.class,
            TestFileOwnerQueryImplementation.class,
            TestWindowManager.class,
            TestTopComponentRegistry.class);
        TestContextGlobalProvider.setLookup(lookup);
        TestFileOwnerQueryImplementation.setProject(project);
        TestWindowManager.setTopComponent(topComponent);
        TestTopComponentRegistry.setTopComponent(topComponent);
        // Utilities.actionsGlobalContext() caches lookup the first time.
        // We'll have to use this lookup
        final Lookup lookup = Utilities.actionsGlobalContext();
        when(topComponent.getLookup()).thenReturn(lookup);
        when(lookup.lookup(Project.class)).thenReturn(null);
        when(lookup.lookup(FileObject.class)).thenReturn(null, fileObject);
        when(lookup.lookup(DataObject.class)).thenReturn(null);

        assertEquals(project, Utils.getActiveProject());
    }

    @Test
    public void testGetActiveProjectActiveEditorDataObject() {
        MockServices.setServices(
            TestContextGlobalProvider.class,
            TestFileOwnerQueryImplementation.class,
            TestWindowManager.class,
            TestTopComponentRegistry.class);
        TestContextGlobalProvider.setLookup(lookup);
        TestFileOwnerQueryImplementation.setProject(project);
        TestWindowManager.setTopComponent(topComponent);
        TestTopComponentRegistry.setTopComponent(topComponent);
        // Utilities.actionsGlobalContext() caches lookup the first time.
        // We'll have to use this lookup
        final Lookup lookup = Utilities.actionsGlobalContext();
        when(topComponent.getLookup()).thenReturn(lookup);
        when(lookup.lookup(Project.class)).thenReturn(null);
        when(lookup.lookup(FileObject.class)).thenReturn(null);
        when(lookup.lookup(DataObject.class)).thenReturn(null, dataObject);
        when(dataObject.getPrimaryFile()).thenReturn(fileObject);

        assertEquals(project, Utils.getActiveProject());
    }

    @Test
    public void testGetActiveProjectNoProject() {
        MockServices.setServices(
            TestContextGlobalProvider.class,
            TestFileOwnerQueryImplementation.class,
            TestWindowManager.class,
            TestTopComponentRegistry.class);
        TestContextGlobalProvider.setLookup(lookup);
        TestFileOwnerQueryImplementation.setProject(project);
        TestWindowManager.setTopComponent(topComponent);
        TestTopComponentRegistry.setTopComponent(topComponent);
        // Utilities.actionsGlobalContext() caches lookup the first time.
        // We'll have to use this lookup
        final Lookup lookup = Utilities.actionsGlobalContext();
        when(topComponent.getLookup()).thenReturn(lookup);
        when(lookup.lookup(Project.class)).thenReturn(null);
        when(lookup.lookup(FileObject.class)).thenReturn(null);
        when(lookup.lookup(DataObject.class)).thenReturn(null);

        assertNull(Utils.getActiveProject());
    }

    @Test
    public void testGetSpringBootVersion(){
        final String version = "1.0.0";
        mockProjectVersion(version);

        final Optional<String> result = Utils.getSpringBootVersion(nbMavenProject);

        assertEquals(version, result.get());
    }

    @Test
    public void testGetSpringBootVersionNoStarterDependency(){
        when(nbMavenProject.getOriginalMavenProject()).thenReturn(mavenProject);
        when(mavenProject.getArtifacts()).thenReturn(Set.of(artifact));
        when(artifact.getGroupId()).thenReturn(SPRING_BOOT_STARTER_GROUP_ID);

        final Optional<String> result = Utils.getSpringBootVersion(nbMavenProject);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetSpringBootVersionNoArtifacts(){
        when(nbMavenProject.getOriginalMavenProject()).thenReturn(mavenProject);

        final Optional<String> result = Utils.getSpringBootVersion(nbMavenProject);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetSpringBootVersionNotNbMavenProjectImpl(){
        final Optional<String> result = Utils.getSpringBootVersion(project);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetSpringBootVersionNullProvect(){
        final Optional<String> result = Utils.getSpringBootVersion(null);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testIsSpringBootProject(){
        mockProjectVersion("1.0.0");

        assertTrue(Utils.isSpringBootProject(nbMavenProject));
    }

    @Test
    public void testCompleteSpringResourceClassPath() {
        CompletionResultSetImpl completionResultSet = mock(CompletionResultSetImpl.class);
        FileObject foBase = mock(FileObject.class);
        FileObject fObj = mock(FileObject.class);
        when(fileObject.getFileObject("com.example")).thenReturn(foBase);
        when(foBase.getChildren()).thenReturn(new FileObject[]{fObj});
        when(fObj.getNameExt()).thenReturn("file.ext");

        Utils.completeSpringResource(fileObject, "classpath:/com.example/file.ext", CompletionSpiPackageAccessor.get().createCompletionResultSet(completionResultSet), 0, 0);

        verify(completionResultSet).addItem(argThat(argument -> {
            return ((FileObjectCompletionItem)argument).getText().equals("file.ext");
        }));
    }

    private static Dependency createDependency(String artifactId) {
        final Dependency dependency = new Dependency();
        dependency.setArtifactId(artifactId);
        return dependency;
    }

    private static ConfigurationMetadataProperty createConfigurationMetadataProperty() {
        final ConfigurationMetadataProperty cmdp = new ConfigurationMetadataProperty();
        cmdp.setType(String.class.getTypeName());
        return cmdp;
    }

    private void mockClassPath(final String id) throws IllegalArgumentException {
        MockServices.setServices(
            TestProjectManagerImplementation.class,
            TestClassPathProvider.class);
        TestProjectManagerImplementation.setProject(project);
        TestClassPathProvider.setClassPath(cp);
        when(project.getLookup()).thenReturn(lookup);
        when(lookup.lookup(Sources.class)).thenReturn(sources);
        when(sources.getSourceGroups(id))
            .thenReturn(new SourceGroup[]{sourceGroup});
        when(sourceGroup.getRootFolder()).thenReturn(fileObject);
    }

    private void mockProjectVersion(final String version) {
        when(nbMavenProject.getOriginalMavenProject()).thenReturn(mavenProject);
        when(mavenProject.getArtifacts()).thenReturn(Set.of(artifact));
        when(artifact.getGroupId()).thenReturn(SPRING_BOOT_STARTER_GROUP_ID);
        when(artifact.getArtifactId()).thenReturn(SPRING_BOOT_STARTER_ARTIFACT_ID);
        when(artifact.getVersion()).thenReturn(version);
    }

    public static class TestProjectManagerImplementation implements ProjectManagerImplementation {

        private ProjectManagerCallBack callBack;
        private static Project project;

        @Override
        public void init(ProjectManagerCallBack callBack) {
            this.callBack = callBack;
        }

        @Override
        public Mutex getMutex() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Mutex getMutex(boolean autoSave, Project project, Project... otherProjects) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Project findProject(FileObject projectDirectory) throws IOException, IllegalArgumentException {
            return project;
        }

        @Override
        public ProjectManager.Result isProject(FileObject projectDirectory) throws IllegalArgumentException {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void clearNonProjectCache() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Set<Project> getModifiedProjects() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public boolean isModified(Project p) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public boolean isValid(Project p) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void saveProject(Project p) throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void saveAllProjects() throws IOException {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        public static void setProject(Project project) {
            TestProjectManagerImplementation.project = project;
        }

    }

    public static class TestClassPathProvider implements ClassPathProvider {

        private static ClassPath classPath;

        @Override
        public ClassPath findClassPath(FileObject file, String type) {
            return classPath;
        }

        public static void setClassPath(ClassPath classPath) {
            TestClassPathProvider.classPath = classPath;
        }
    }

    public static class TestContextGlobalProvider implements ContextGlobalProvider {

        private static Lookup lookup;

        @Override
        public Lookup createGlobalContext() {
            return lookup;
        }

        public static void setLookup(Lookup lookup) {
            TestContextGlobalProvider.lookup = lookup;
        }

    }

    public static class TestFileOwnerQueryImplementation implements FileOwnerQueryImplementation {

        private static Project project;

        @Override
        public Project getOwner(URI file) {
            return project;
        }

        @Override
        public Project getOwner(FileObject file) {
            return project;
        }

        public static void setProject(Project project) {
            TestFileOwnerQueryImplementation.project = project;
        }
    }

    public static class TestWindowManager extends WindowManager {

        private static TopComponent topComponent;

        @Override
        public Mode findMode(String name) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Mode findMode(TopComponent tc) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Set<? extends Mode> getModes() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Frame getMainWindow() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void updateUI() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        protected Component createTopComponentManager(TopComponent c) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Workspace createWorkspace(String name, String displayName) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Workspace findWorkspace(String name) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Workspace[] getWorkspaces() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void setWorkspaces(Workspace[] workspaces) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Workspace getCurrentWorkspace() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public TopComponentGroup findTopComponentGroup(String name) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener l) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener l) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        protected void topComponentOpen(TopComponent tc) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        protected void topComponentClose(TopComponent tc) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        protected void topComponentRequestActive(TopComponent tc) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        protected void topComponentRequestVisible(TopComponent tc) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        protected void topComponentDisplayNameChanged(TopComponent tc, String displayName) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        protected void topComponentHtmlDisplayNameChanged(TopComponent tc, String htmlDisplayName) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        protected void topComponentToolTipChanged(TopComponent tc, String toolTip) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        protected void topComponentIconChanged(TopComponent tc, Image icon) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        protected void topComponentActivatedNodesChanged(TopComponent tc, Node[] activatedNodes) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        protected boolean topComponentIsOpened(TopComponent tc) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        protected Action[] topComponentDefaultActions(TopComponent tc) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        protected String topComponentID(TopComponent tc, String preferredID) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public TopComponent findTopComponent(String tcID) {
            return topComponent;
        }

        public static void setTopComponent(TopComponent topComponent) {
            TestWindowManager.topComponent = topComponent;
        }
    }

    public static class TestTopComponentRegistry implements TopComponent.Registry {

        private static TopComponent topComponent;

        @Override
        public Set<TopComponent> getOpened() {
            return Set.of(topComponent);
        }

        @Override
        public TopComponent getActivated() {
            return topComponent;
        }

        @Override
        public Node[] getCurrentNodes() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public Node[] getActivatedNodes() {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener l) {
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener l) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        public static void setTopComponent(TopComponent topComponent) {
            TestTopComponentRegistry.topComponent = topComponent;
        }
    }

}
