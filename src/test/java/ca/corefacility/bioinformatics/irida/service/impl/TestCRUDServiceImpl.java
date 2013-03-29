/*
 * Copyright 2013 Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>.
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
package ca.corefacility.bioinformatics.irida.service.impl;

import ca.corefacility.bioinformatics.irida.model.roles.impl.Identifier;
import ca.corefacility.bioinformatics.irida.repositories.CRUDRepository;
import ca.corefacility.bioinformatics.irida.repositories.memory.CRUDMemoryRepository;
import ca.corefacility.bioinformatics.irida.service.CRUDService;
import java.util.List;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Testing the behavior of {@link CRUDServiceImpl}
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class TestCRUDServiceImpl {

    private CRUDService<Identifier, IdentifiableTestEntity> crudService;
    private CRUDRepository<Identifier, IdentifiableTestEntity> crudRepository;
    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        crudRepository = new CRUDMemoryRepository<>();
        crudService = new CRUDServiceImpl<>(crudRepository, validator);
    }

    @Test
    public void testAddInvalidObject() {
        IdentifiableTestEntity i = new IdentifiableTestEntity(); // nothing is set, this should be invalid

        try {
            crudService.create(i);
            fail();
        } catch (ConstraintViolationException constraintViolations) {
            assertEquals(1, constraintViolations.getConstraintViolations().size());
        }
    }

    @Test
    public void testAddValidObject() {
        IdentifiableTestEntity i = new IdentifiableTestEntity();
        i.setNonNull("Definitely not null.");

        try {
            crudService.create(i);
        } catch (ConstraintViolationException constraintViolations) {
            fail();
        }
    }

    @Test
    public void testUpdateInvalidProject() {
        IdentifiableTestEntity i = new IdentifiableTestEntity();
        i.setNonNull("Definitely not null");

        crudRepository.create(i);

        // make the project invalid
        i.setNonNull(null);
        try {
            crudService.update(i);
            fail();
        } catch (ConstraintViolationException constraintViolations) {
            assertEquals(1, constraintViolations.getConstraintViolations().size());
        }
    }

    @Test
    public void testUpdateValidProject() {
        IdentifiableTestEntity i = new IdentifiableTestEntity();
        i.setNonNull("Definitely not null.");

        i = crudRepository.create(i);

        // change the non-null, but keep it non-null
        i.setNonNull("Also definitely not null.");

        try {
            crudService.update(i);
        } catch (ConstraintViolationException constraintViolations) {
            fail();
        }

        i = crudRepository.read(i.getIdentifier());
        assertEquals("Also definitely not null.", i.getNonNull());
    }

    @Test
    public void testRead() {
        IdentifiableTestEntity i = new IdentifiableTestEntity();
        i.setNonNull("Definitely not null");
        i = crudRepository.create(i);

        try {
            i = crudService.read(i.getIdentifier());
            assertNotNull(i);
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testList() {
        int itemCount = 10;
        for (int i = 0; i < itemCount; i++) {
            crudRepository.create(new IdentifiableTestEntity());
        }

        List<IdentifiableTestEntity> items = crudService.list();

        assertEquals(itemCount, items.size());
    }

    @Test
    public void testExists() {
        IdentifiableTestEntity i = new IdentifiableTestEntity();
        i = crudRepository.create(i);

        assertTrue(crudService.exists(i.getIdentifier()));
    }

    @Test
    public void testValidDelete() {
        IdentifiableTestEntity i = new IdentifiableTestEntity();
        i = crudRepository.create(i);

        try {
            crudService.delete(i.getIdentifier());
        } catch (IllegalArgumentException e) {
            fail();
        }

        assertFalse(crudRepository.exists(i.getIdentifier()));
    }

    @Test
    public void testInvalidDelete() {
        try {
            crudService.delete(new Identifier());
            fail();
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            fail();
        }
    }
}
