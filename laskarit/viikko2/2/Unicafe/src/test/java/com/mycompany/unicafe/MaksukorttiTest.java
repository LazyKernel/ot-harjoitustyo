package com.mycompany.unicafe;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class MaksukorttiTest {

    Maksukortti kortti;

    @Before
    public void setUp() {
        kortti = new Maksukortti(1000);
    }

    @Test
    public void luotuKorttiOlemassa() {
        assertTrue(kortti!=null);      
    }

    @Test
    public void saldoAlussaOikein()
    {
        assertEquals(kortti.saldo(), 1000);
    }

    @Test
    public void lataaminenKasvattaaSaldoa()
    {
        kortti.lataaRahaa(100);
        assertEquals(1100, kortti.saldo());
    }

    @Test
    public void saldoVahenee()
    {
        kortti.otaRahaa(100);
        assertEquals(900, kortti.saldo());
    }

    @Test
    public void saldoEiMuutuJosRahaaLiianVahan()
    {
        kortti.otaRahaa(2000);
        assertEquals(1000, kortti.saldo());
    }

    @Test
    public void palauttaaTrueJosTarpeeksiRahaa()
    {
        assertTrue(kortti.otaRahaa(100));
    }

    @Test
    public void palauttaaFalseJosEiTarpeeksiRahaa()
    {
        assertFalse(kortti.otaRahaa(2000));
    }

    @Test
    public void toStringToimiiOikein()
    {
        assertEquals("saldo: 10.0", kortti.toString());
    }
}
