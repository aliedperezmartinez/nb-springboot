package com.github.alexfalappa.nbspringboot.cfgprops.parser;

import javax.swing.event.ChangeListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CfgPropsParserTest {

    @Mock
    private ChangeListener cl;
    @Mock
    private Task task;
    @Mock
    private Snapshot snapshot;
    @Mock
    private SourceModificationEvent sme;

    @Test
    public void testParse() throws Exception {
        when(snapshot.getText()).thenReturn("key=value");
        final CfgPropsParser instance = new CfgPropsParser();

        instance.parse(snapshot, task, sme);
        final CfgPropsParser.CfgPropsParserResult result = (CfgPropsParser.CfgPropsParserResult) instance.getResult(task);

        assertEquals(snapshot, result.getSnapshot());
        assertTrue(result.getParbResult().matched);
        assertFalse(result.getParbResult().hasErrors());
        assertTrue(result.getDiagnostics().isEmpty());
        assertEquals("value", result.getParsedProps().get("key"));
        assertTrue(
            result.getCfgFile().elements().stream()
                .anyMatch(e -> e.key().getText().equals("key") && e.value().getText().equals("value")));
    }

    @Test
    public void testResultInvalidate() throws ParseException {
        when(snapshot.getText()).thenReturn("key=value");
        final CfgPropsParser instance = new CfgPropsParser();

        instance.parse(snapshot, task, sme);
        final CfgPropsParser.CfgPropsParserResult result = (CfgPropsParser.CfgPropsParserResult) instance.getResult(task);
        result.invalidate();

        assertThrows(ParseException.class, () -> result.getParbResult());
    }

    @Test
    public void testAddChangeListener() {
        final CfgPropsParser instance = new CfgPropsParser();

        instance.addChangeListener(cl);

        verifyNoInteractions(cl);
    }

    @Test
    public void testRemoveChangeListener() {
        final CfgPropsParser instance = new CfgPropsParser();

        instance.removeChangeListener(cl);

        verifyNoInteractions(cl);
    }

}
