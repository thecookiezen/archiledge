package com.thecookiezen.ladybugdb.spring.repository.support;

import com.ladybugdb.Connection;
import com.ladybugdb.Database;
import com.thecookiezen.ladybugdb.spring.annotation.NodeEntity;
import com.thecookiezen.ladybugdb.spring.connection.SimpleConnectionFactory;
import com.thecookiezen.ladybugdb.spring.core.LadybugDBTemplate;
import com.thecookiezen.ladybugdb.spring.mapper.EntityWriter;
import com.thecookiezen.ladybugdb.spring.mapper.RowMapper;
import com.thecookiezen.ladybugdb.spring.mapper.ValueMappers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

class SimpleNodeRepositoryTest {

    private static Database db;
    private static SimpleConnectionFactory connectionFactory;
    private static LadybugDBTemplate template;
    private static SimpleNodeRepository<Person, String> repository;

    @BeforeAll
    static void setupAll() {
        db = new Database(":memory:");
        connectionFactory = new SimpleConnectionFactory(db);
        template = new LadybugDBTemplate(connectionFactory);

        try (Connection conn = new Connection(db)) {
            conn.query("CREATE NODE TABLE Person(name STRING PRIMARY KEY, age INT64)");
        }

        repository = new SimpleNodeRepository<>(template, Person.class,
                new EntityDescriptor<>(Person.class, personReader, personWriter));
    }

    @AfterEach
    void tearDown() {
        template.execute("MATCH (n:Person) DELETE n");
    }

    @AfterAll
    static void tearDownAll() {
        if (connectionFactory != null) {
            connectionFactory.close();
        }
        if (db != null) {
            db.close();
        }
    }

    @Test
    void save_shouldInsertNewNode() {
        Person person = new Person("Alice", 30);

        Person saved = repository.save(person);

        assertNotNull(saved);
        assertEquals("Alice", saved.name);
        assertEquals(30, saved.age);
    }

    @Test
    void save_shouldUpdateExistingNode() {
        Person person = new Person("Bob", 25);
        repository.save(person);

        person.age = 26;
        Person updated = repository.save(person);

        assertEquals("Bob", updated.name);
        assertEquals(26, updated.age);
    }

    @Test
    void findById_shouldReturnNodeWhenExists() {
        repository.save(new Person("Charlie", 35));

        Optional<Person> found = repository.findById("Charlie");

        assertTrue(found.isPresent());
        assertEquals("Charlie", found.get().name);
        assertEquals(35, found.get().age);
    }

    @Test
    void findById_shouldReturnEmptyWhenNotExists() {
        Optional<Person> found = repository.findById("NonExistent");

        assertTrue(found.isEmpty());
    }

    @Test
    void findAll_shouldReturnAllNodes() {
        repository.save(new Person("Dave", 40));
        repository.save(new Person("Eve", 28));

        List<Person> all = StreamSupport.stream(repository.findAll().spliterator(), false).toList();

        assertEquals(2, all.size());
    }

    @Test
    void findAll_shouldReturnEmptyWhenNoNodes() {
        List<Person> all = StreamSupport.stream(repository.findAll().spliterator(), false).toList();

        assertTrue(all.isEmpty());
    }

    @Test
    void count_shouldReturnNumberOfNodes() {
        repository.save(new Person("Frank", 45));
        repository.save(new Person("Grace", 32));

        long count = repository.count();

        assertEquals(2, count);
    }

    @Test
    void count_shouldReturnZeroWhenEmpty() {
        long count = repository.count();

        assertEquals(0, count);
    }

    @Test
    void existsById_shouldReturnTrueWhenExists() {
        repository.save(new Person("Henry", 50));

        assertTrue(repository.existsById("Henry"));
    }

    @Test
    void existsById_shouldReturnFalseWhenNotExists() {
        assertFalse(repository.existsById("NonExistent"));
    }

    @Test
    void deleteById_shouldRemoveNode() {
        repository.save(new Person("Ivan", 55));
        assertTrue(repository.existsById("Ivan"));

        repository.deleteById("Ivan");

        assertFalse(repository.existsById("Ivan"));
    }

    @Test
    void delete_shouldRemoveNode() {
        Person person = new Person("Julia", 60);
        repository.save(person);

        repository.delete(person);

        assertFalse(repository.existsById("Julia"));
    }

    @Test
    void deleteAll_shouldRemoveAllNodes() {
        repository.save(new Person("Karl", 65));
        repository.save(new Person("Laura", 70));

        repository.deleteAll();

        assertEquals(0, repository.count());
    }

    @Test
    void saveAll_shouldSaveMultipleNodes() {
        List<Person> people = List.of(
                new Person("Mike", 75),
                new Person("Nancy", 80));

        Iterable<Person> saved = repository.saveAll(people);
        List<Person> savedList = StreamSupport.stream(saved.spliterator(), false).toList();

        assertEquals(2, savedList.size());
        assertEquals(2, repository.count());
    }

    @Test
    void findAllById_shouldReturnMatchingNodes() {
        repository.save(new Person("Oscar", 85));
        repository.save(new Person("Paula", 90));
        repository.save(new Person("Quinn", 95));

        List<Person> found = StreamSupport.stream(
                repository.findAllById(List.of("Oscar", "Quinn")).spliterator(), false).toList();

        assertEquals(2, found.size());
    }

    @NodeEntity(label = "Person")
    static class Person {
        @Id
        String name;
        int age;

        Person() {
        }

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    static RowMapper<Person> personReader = (row, meta) -> {
        String name = ValueMappers.asString(row.getValue(0));
        int age = ValueMappers.asInteger(row.getValue(1));
        return new Person(name, age);
    };

    static EntityWriter<Person> personWriter = (entity) -> {
        return Map.of("name", entity.name, "age", entity.age);
    };
}
