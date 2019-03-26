package com.mycompany.unicafe;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class KassapaateTest {
    Kassapaate kassa;

    @Before
    public void setUp() { kassa = new Kassapaate(); }

    @Test
    public void alustettuOikein()
    {
        assertEquals(100000, kassa.kassassaRahaa());
        assertEquals(0, kassa.edullisiaLounaitaMyyty());
        assertEquals(0, kassa.maukkaitaLounaitaMyyty());
    }

    @Test
    public void edullinenKateisosto()
    {
        assertEquals(10, kassa.syoEdullisesti(250));
        assertEquals(100240, kassa.kassassaRahaa());
        assertEquals(1, kassa.edullisiaLounaitaMyyty());
    }

    @Test
    public void maukasKateisosto()
    {
        assertEquals(100, kassa.syoMaukkaasti(500));
        assertEquals(100400, kassa.kassassaRahaa());
        assertEquals(1, kassa.maukkaitaLounaitaMyyty());
    }

    @Test
    public void edullinenKateisostoRahaEiRiita()
    {
        assertEquals(200, kassa.syoEdullisesti(200));
        assertEquals(100000, kassa.kassassaRahaa());
        assertEquals(0, kassa.edullisiaLounaitaMyyty());
    }

    @Test
    public void maukasKateisostoRahaEiRiita()
    {
        assertEquals(300, kassa.syoMaukkaasti(300));
        assertEquals(100000, kassa.kassassaRahaa());
        assertEquals(0, kassa.maukkaitaLounaitaMyyty());
    }

    @Test
    public void edullinenKorttiosto()
    {
        Maksukortti k = new Maksukortti(500);
        assertTrue(kassa.syoEdullisesti(k));
        assertEquals(260, k.saldo());
    }

    @Test
    public void maukasKorttiosto()
    {
        Maksukortti k = new Maksukortti(500);
        assertTrue(kassa.syoMaukkaasti(k));
        assertEquals(100, k.saldo());
    }

    @Test
    public void korttiostoSaldoKasvaaEdullinen()
    {
        Maksukortti k = new Maksukortti(500);
        kassa.syoEdullisesti(k);
        assertEquals(1, kassa.edullisiaLounaitaMyyty());
    }

    @Test
    public void korttiostoSaldoKasvaaMaukas()
    {
        Maksukortti k = new Maksukortti(500);
        kassa.syoMaukkaasti(k);
        assertEquals(1, kassa.maukkaitaLounaitaMyyty());
    }

    @Test
    public void kortillaEiTarpeeksiRahaaEdullinen()
    {
        Maksukortti k = new Maksukortti(100);
        assertFalse(kassa.syoEdullisesti(k));
        assertEquals(0, kassa.edullisiaLounaitaMyyty());
        assertEquals(100, k.saldo());
    }

    @Test
    public void kortillaEiTarpeeksiRahaaMaukas()
    {
        Maksukortti k = new Maksukortti(300);
        assertFalse(kassa.syoMaukkaasti(k));
        assertEquals(0, kassa.maukkaitaLounaitaMyyty());
        assertEquals(300, k.saldo());
    }

    @Test
    public void kassanRahamaaraEiMuutuEdullinen()
    {
        Maksukortti k = new Maksukortti(500);
        kassa.syoEdullisesti(k);
        assertEquals(100000, kassa.kassassaRahaa());
    }

    @Test
    public void kassanRahamaaraEiMuutuMaukas()
    {
        Maksukortti k = new Maksukortti(500);
        kassa.syoMaukkaasti(k);
        assertEquals(100000, kassa.kassassaRahaa());
    }

    @Test
    public void rahanLataaminenKortille()
    {
        Maksukortti k = new Maksukortti(100);
        kassa.lataaRahaaKortille(k, 500);
        assertEquals(600, k.saldo());
        assertEquals(100500, kassa.kassassaRahaa());
    }

    @Test
    public void kortilleEiLadataNegatiivistaRahamaaraa()
    {
        Maksukortti k = new Maksukortti(100);
        kassa.lataaRahaaKortille(k, -50);
        assertEquals(100000, kassa.kassassaRahaa());
        assertEquals(100, k.saldo());
    }
}
