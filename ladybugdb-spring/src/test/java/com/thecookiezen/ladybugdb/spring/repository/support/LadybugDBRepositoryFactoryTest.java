package com.thecookiezen.ladybugdb.spring.repository.support;

import com.thecookiezen.ladybugdb.spring.core.LadybugDBTemplate;
import com.thecookiezen.ladybugdb.spring.mapper.EntityWriter;
import com.thecookiezen.ladybugdb.spring.mapper.RowMapper;
import com.thecookiezen.ladybugdb.spring.repository.NodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.repository.core.RepositoryInformation;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class LadybugDBRepositoryFactoryTest {

    @Mock
    private LadybugDBTemplate template;

    @Mock
    private RepositoryInformation metadata;

    private EntityRegistry entityRegistry;
    private LadybugDBRepositoryFactory factory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        entityRegistry = new EntityRegistry();

        RowMapper<Person> reader = (row) -> new Person();
        EntityWriter<Person> writer = (entity) -> Map.of();
        entityRegistry.registerDescriptor(Person.class, reader, writer);

        factory = new LadybugDBRepositoryFactory(template, entityRegistry);
    }

    @Test
    void getTargetRepository_shouldExtractRelationshipType() {
        when(metadata.getRepositoryInterface()).thenReturn((Class) PersonRepository.class);
        when(metadata.getDomainType()).thenReturn((Class) Person.class);

        Object repository = factory.getTargetRepository(metadata);

        assertThat(repository).isInstanceOf(SimpleNodeRepository.class);
        SimpleNodeRepository<?, ?, ?> simpleRepo = (SimpleNodeRepository<?, ?, ?>) repository;

        RelationshipMetadata<?> relMetadata = simpleRepo.relationshipMetadata;
        assertThat(relMetadata.getRelationshipType()).isEqualTo(TestRelationship.class);
    }

    @Test
    void getTargetRepository_shouldDefaultToObjectWhenRelationshipTypeUnresolved() {
        when(metadata.getRepositoryInterface()).thenReturn((Class) GenericRepository.class);
        when(metadata.getDomainType()).thenReturn((Class) Person.class);

        Object repository = factory.getTargetRepository(metadata);

        assertThat(repository).isInstanceOf(SimpleNodeRepository.class);
        SimpleNodeRepository<?, ?, ?> simpleRepo = (SimpleNodeRepository<?, ?, ?>) repository;

        RelationshipMetadata<?> relMetadata = simpleRepo.relationshipMetadata;
        assertThat(relMetadata.getRelationshipType()).isEqualTo(Object.class);
    }

    static class Person {
    }

    static class TestRelationship {
    }

    interface PersonRepository extends NodeRepository<Person, String, TestRelationship, Person> {
    }

    interface GenericRepository extends NodeRepository<Person, String, Object, Person> {
    }
}
