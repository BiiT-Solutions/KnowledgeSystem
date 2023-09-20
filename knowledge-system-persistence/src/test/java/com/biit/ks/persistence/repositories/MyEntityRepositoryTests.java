package com.biit.ks.persistence.repositories;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

@SpringBootTest
@Test(groups = {"myEntityRepository"})
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class MyEntityRepositoryTests extends AbstractTestNGSpringContextTests {

}
