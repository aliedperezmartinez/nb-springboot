package com.github.alexfalappa.nbspringboot.cfgprops.completion;

import com.github.alexfalappa.nbspringboot.TestContextGlobalProvider;
import com.github.alexfalappa.nbspringboot.projects.service.api.SpringBootService;
import javax.swing.text.JTextComponent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netbeans.api.project.Project;
import org.netbeans.junit.MockServices;
import org.netbeans.spi.editor.completion.CompletionProvider;
import org.netbeans.spi.editor.completion.CompletionTask;
import org.openide.util.Lookup;
import org.openide.util.Utilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CfgPropsCompletionProviderTest {

    @Mock
    private JTextComponent jtc;
    @Mock
    private Lookup lookup;
    @Mock
    private Project project;
    @Mock
    private SpringBootService springBootService;

    @Test
    public void testCreateTaskAllQueryType() {
        final CfgPropsCompletionProvider instance = new CfgPropsCompletionProvider();

        final CompletionTask result = instance.createTask(CompletionProvider.COMPLETION_ALL_QUERY_TYPE, jtc);

        assertNull(result);
    }

    @Test
    public void testCreateTaskNoActiveProject() {
        MockServices.setServices(TestContextGlobalProvider.class);
        TestContextGlobalProvider.setLookup(lookup);
        // Utilities.actionsGlobalContext() caches lookup the first time.
        // We'll have to use this lookup
        final Lookup lookup = Utilities.actionsGlobalContext();
        when(lookup.lookup(Project.class)).thenReturn(null);
        final CfgPropsCompletionProvider instance = new CfgPropsCompletionProvider();

        final CompletionTask result = instance.createTask(0, jtc);

        assertNull(result);
    }

    @Test
    public void testCreateTaskNoActiveSpringBootProject() {
        MockServices.setServices(TestContextGlobalProvider.class);
        TestContextGlobalProvider.setLookup(lookup);
        // Utilities.actionsGlobalContext() caches lookup the first time.
        // We'll have to use this lookup
        final Lookup lookup = Utilities.actionsGlobalContext();
        when(lookup.lookup(Project.class)).thenReturn(project);
        when(project.getLookup()).thenReturn(this.lookup);
//        when(this.lookup.lookup(SpringBootService.class)).thenReturn(springBootService);

        final CfgPropsCompletionProvider instance = new CfgPropsCompletionProvider();

        final CompletionTask result = instance.createTask(0, jtc);

        assertNull(result);
    }

    @Test
    public void testCreateTaskActiveSpringBootProjectDefaultQueryType() {
        MockServices.setServices(TestContextGlobalProvider.class);
        TestContextGlobalProvider.setLookup(lookup);
        // Utilities.actionsGlobalContext() caches lookup the first time.
        // We'll have to use this lookup
        final Lookup lookup = Utilities.actionsGlobalContext();
        when(lookup.lookup(Project.class)).thenReturn(project);
        when(project.getLookup()).thenReturn(this.lookup);
        when(this.lookup.lookup(SpringBootService.class)).thenReturn(springBootService);

        final CfgPropsCompletionProvider instance = new CfgPropsCompletionProvider();

        final CompletionTask result = instance.createTask(0, jtc);

        assertNull(result);
    }

    @ParameterizedTest
    @ValueSource(ints = {
        CompletionProvider.COMPLETION_QUERY_TYPE,
        CompletionProvider.DOCUMENTATION_QUERY_TYPE,
        CompletionProvider.TOOLTIP_QUERY_TYPE
    })
    public void testCreateTaskActiveSpringBootProjectQueryType(int number) {
        MockServices.setServices(TestContextGlobalProvider.class);
        TestContextGlobalProvider.setLookup(lookup);
        // Utilities.actionsGlobalContext() caches lookup the first time.
        // We'll have to use this lookup
        final Lookup lookup = Utilities.actionsGlobalContext();
        when(lookup.lookup(Project.class)).thenReturn(project);
        when(project.getLookup()).thenReturn(this.lookup);
        when(this.lookup.lookup(SpringBootService.class)).thenReturn(springBootService);

        final CfgPropsCompletionProvider instance = new CfgPropsCompletionProvider();

        final CompletionTask result = instance.createTask(number, jtc);

        assertNotNull(result);
    }

    @Test
    public void testGetAutoQueryTypes() {
        final CfgPropsCompletionProvider instance = new CfgPropsCompletionProvider();

        final int result = instance.getAutoQueryTypes(jtc, "string");
        assertEquals(0, result);
    }

}
