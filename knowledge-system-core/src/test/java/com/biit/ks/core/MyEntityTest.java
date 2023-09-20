package com.biit.ks.core;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@SpringBootTest
@Test(groups = {"myEntityListeners"})
@Listeners(TestListener.class)
public class MyEntityTest extends AbstractTestNGSpringContextTests {

}