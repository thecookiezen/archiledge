package com.thecookiezen.archiledger.application.service;

import com.thecookiezen.archiledger.domain.model.MemoryNote;
import com.thecookiezen.archiledger.domain.model.MemoryNoteId;
import com.thecookiezen.archiledger.domain.repository.EmbeddingsService;
import com.thecookiezen.archiledger.domain.repository.MemoryNoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemoryNoteServiceImplTest {

    @Mock
    private MemoryNoteRepository repository;

    @Mock
    private EmbeddingsService embeddingsService;

    @InjectMocks
    private MemoryNoteServiceImpl service;

    private MemoryNote sampleNote(String id) {
        return new MemoryNote(
                new MemoryNoteId(id),
                "Sample content for " + id,
                List.of("keyword1"),
                "test-context",
                List.of("tag1"),
                List.of(),
                "2026-03-04T16:00:00Z",
                0);
    }

    @Test
    void createNote_savesAndGeneratesEmbeddings() {
        MemoryNote note = sampleNote("note-1");
        when(repository.save(any(MemoryNote.class))).thenReturn(note);

        MemoryNote result = service.createNote(note);

        assertEquals("note-1", result.id().value());
        verify(repository).save(note);
        verify(embeddingsService).generateEmbeddings(note);
    }

    @Test
    void createNotes_savesMultiple() {
        MemoryNote note1 = sampleNote("note-1");
        MemoryNote note2 = sampleNote("note-2");
        when(repository.save(note1)).thenReturn(note1);
        when(repository.save(note2)).thenReturn(note2);

        List<MemoryNote> result = service.createNotes(List.of(note1, note2));

        assertEquals(2, result.size());
        verify(repository, times(2)).save(any(MemoryNote.class));
    }

    @Test
    void getNote_incrementsRetrievalCount() {
        MemoryNote note = sampleNote("note-1");
        when(repository.findById(new MemoryNoteId("note-1"))).thenReturn(Optional.of(note));

        Optional<MemoryNote> result = service.getNote(new MemoryNoteId("note-1"));

        assertTrue(result.isPresent());
        verify(repository).incrementRetrievalCount(new MemoryNoteId("note-1"));
    }

    @Test
    void getNote_whenNotFound_doesNotIncrementRetrievalCount() {
        when(repository.findById(new MemoryNoteId("missing"))).thenReturn(Optional.empty());

        Optional<MemoryNote> result = service.getNote(new MemoryNoteId("missing"));

        assertTrue(result.isEmpty());
        verify(repository, never()).incrementRetrievalCount(any());
    }

    @Test
    void deleteNote_delegatesAndDeletesEmbeddings() {
        service.deleteNote(new MemoryNoteId("note-1"));

        verify(repository).delete(new MemoryNoteId("note-1"));
        verify(embeddingsService).deleteEmbeddings(List.of("note-1"));
    }

    @Test
    void addLink_delegatesToRepository() {
        MemoryNoteId from = new MemoryNoteId("A");
        MemoryNoteId to = new MemoryNoteId("B");

        service.addLink(from, to, "DEPENDS_ON");

        verify(repository).addLink(from, to, "DEPENDS_ON");
    }

    @Test
    void removeLink_delegatesToRepository() {
        MemoryNoteId from = new MemoryNoteId("A");
        MemoryNoteId to = new MemoryNoteId("B");

        service.removeLink(from, to, "DEPENDS_ON");

        verify(repository).removeLink(from, to, "DEPENDS_ON");
    }

    @Test
    void getNotesByTag_delegatesToRepository() {
        List<MemoryNote> notes = List.of(sampleNote("note-1"), sampleNote("note-2"));
        when(repository.findByTag("architecture")).thenReturn(notes);

        List<MemoryNote> result = service.getNotesByTag("architecture");

        assertEquals(2, result.size());
        verify(repository).findByTag("architecture");
    }

    @Test
    void getLinkedNotes_delegatesToRepository() {
        MemoryNoteId noteId = new MemoryNoteId("A");
        List<MemoryNote> linked = List.of(sampleNote("B"), sampleNote("C"));
        when(repository.findLinkedNotes(noteId)).thenReturn(linked);

        List<MemoryNote> result = service.getLinkedNotes(noteId);

        assertEquals(2, result.size());
        verify(repository).findLinkedNotes(noteId);
    }

    @Test
    void getAllTags_delegatesToRepository() {
        when(repository.findAllTags()).thenReturn(Set.of("architecture", "decision"));

        Set<String> result = service.getAllTags();

        assertEquals(2, result.size());
        verify(repository).findAllTags();
    }

    @Test
    void readGraph_delegatesToRepository() {
        when(repository.getGraph()).thenReturn(Map.of("notes", List.of(), "links", List.of()));

        Map<String, Object> graph = service.readGraph();

        assertNotNull(graph);
        verify(repository).getGraph();
    }

    @Test
    void similaritySearch_delegatesToEmbeddingsService() {
        when(embeddingsService.findClosestMatch("architecture")).thenReturn(List.of("result1"));

        List<String> results = service.similaritySearch("architecture");

        assertEquals(1, results.size());
        verify(embeddingsService).findClosestMatch("architecture");
    }
}
