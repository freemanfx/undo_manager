package undo;

import java.util.Deque;
import java.util.LinkedList;

/**
 * {@link UndoManager} implementation
 */
public class UndoManagerImpl implements UndoManager {
    private final Document document;
    private final int bufferSize;
    private Deque<Change> changes;
    private Deque<Change> redos;

    UndoManagerImpl(Document document, int bufferSize) {
        changes = new LinkedList<>();
        redos = new LinkedList<>();
        this.document = document;
        this.bufferSize = bufferSize;
    }

    @Override
    public void registerChange(Change change) {
        if (changes.size() == bufferSize) {
            changes.removeFirst();
        }
        changes.addLast(change);
    }

    @Override
    public boolean canUndo() {
        return !changes.isEmpty();
    }

    @Override
    public void undo() {
        if (canUndo()) {
            Change change = changes.removeLast();
            redos.addLast(change);
            change.revert(document);
        } else {
            throw new IllegalStateException("Cannot undo! No more changes.");
        }
    }

    @Override
    public boolean canRedo() {
        return !redos.isEmpty();
    }

    @Override
    public void redo() {
        if (canRedo()) {
            Change change = redos.removeLast();
            change.apply(document);
            registerChange(change);
        } else {
            throw new IllegalStateException("Cannot redo!");
        }
    }
}

