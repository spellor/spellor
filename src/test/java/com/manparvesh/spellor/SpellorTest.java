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

    @Before
    public void setUp() {
        spellorSimple = new Spellor();
    }

    @Test
    public void testSimple(){
        assertEquals("spelling", spellorSimple.correction("spelaing"));
        assertEquals("medieval", spellorSimple.correction("mediieval"));
    }
}