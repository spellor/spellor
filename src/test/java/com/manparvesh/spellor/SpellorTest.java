/*
 * Copyright (c) spellor 2018.
 * Spell checker and corrector, written in Java 8
 *
 * @author Man Parvesh Singh Randhawa <manparveshsinghrandhawa@gmail.com>
 */

package com.manparvesh.spellor;

import org.junit.Before;
import org.junit.Test;

import static com.manparvesh.spellor.config.SpellorConfig.*;
import static org.junit.Assert.assertEquals;

public class SpellorTest {

    private Spellor spellorSimple;
    private Spellor spellorAdvanced;

    @Before
    public void setUp() {
        spellorSimple = new Spellor(SIMPLE_CONFIG);
        spellorAdvanced = new Spellor(ADVANCED_CONFIG);
    }

    @Test
    public void testSimple(){
    }

    @Test
    public void testTrainOnData(){
        spellorSimple.train();
        assertEquals("spelling", spellorSimple.correction("spelaing"));
        assertEquals("medieval", spellorSimple.correction("mediieval"));
    }
}