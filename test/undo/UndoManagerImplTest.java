package undo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class UndoManagerImplTest {

    private UndoManager undoManager;

    @Mock
    private Document documentMock;

    @Mock
    private Change change1Mock, change2Mock;

    @Before
    public void setUp() {
        undoManager = new UndoManagerFactoryImpl().createUndoManager(documentMock, 2);
    }

    @Test
    public void testRegisterChange() {
        assertFalse(undoManager.canUndo());

        undoManager.registerChange(change1Mock);

        assertTrue(undoManager.canUndo());
    }

    @Test
    public void testReplaceOldestChange() {
        undoManager.registerChange(change1Mock);
        undoManager.registerChange(change2Mock);
        undoManager.registerChange(change2Mock);

        undoManager.undo();
        undoManager.undo();

        assertFalse(undoManager.canUndo());
    }

    @Test
    public void testUndo() {
        undoManager.registerChange(change1Mock);

        undoManager.undo();

        verify(change1Mock).revert(documentMock);
        verifyNoMoreInteractions(documentMock);
    }

    @Test(expected = IllegalStateException.class)
    public void testUndoFailsIfThereAreNoChanges() {
        undoManager.undo();
    }

    @Test
    public void undoingAChangeAllowToRedoIt() {
        assertFalse(undoManager.canRedo());

        undoManager.registerChange(change1Mock);
        undoManager.undo();

        assertTrue(undoManager.canRedo());
    }

    @Test
    public void redoingWillApplyTheLastChangeUndone() {
        undoManager.registerChange(change1Mock);
        undoManager.registerChange(change2Mock);
        undoManager.undo();

        undoManager.redo();

        verifyZeroInteractions(change1Mock);
        verify(change2Mock).apply(documentMock);
    }

    @Test
    public void redoWillPutTheRedoneChangeBackToTheChangeList() {
        undoManager.registerChange(change1Mock);
        undoManager.undo();
        assertFalse(undoManager.canUndo());

        undoManager.redo();
        assertTrue(undoManager.canUndo());
    }

    @Test(expected = IllegalStateException.class)
    public void redoIsNotPossibleIfNoUndoExecuted() {
        undoManager.registerChange(change1Mock);
        undoManager.redo();
    }

}