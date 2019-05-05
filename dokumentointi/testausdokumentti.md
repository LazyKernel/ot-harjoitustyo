# Testausdokumentti
Ohjelmaa on testattu yksikkö- ja integraatiotestien lisäksi manuaalisin järjestelmätason testein. 

## Yksikkö- ja integraatiotestaus
### Säiliöluokat
Säiliöluokkien, kuten ModifiableList ja Entity, testaamisessa on hyödynnetty valeluokkia. TestEntityComponent kasvattaa aina metodeja suoritettaessa counteria, jonka avulla varmistetaan, että entity varmasti kutsuu jokaista komponenttia. 

ModifiableList-luokkaa käytetään monessa testattavassa luokassa ja se tulee testattua omien testien lisäksi moneen otteeseen myös muiden luokkien testeissä. 

### Pelin komponentit
Pelin komponenttien testaaminen testaa samalla monia muita luokkia, sekä niiden integraatiota. Esimerkiksi PlayerTest testaa itse luokkansa lisäksi Entityn, KeyboardHandlerin, Rendererin ja EntityComponentin integraatiota.

## Järjestelmätestaus
### Asennus
Sovellusta on testattu [käyttöohjeen](https://github.com/LazyKernel/ot-harjoitustyo/tree/master/dokumentointi/kayttoohje.md) kuvaamalla tavalla Window-ympäristössä. Headless-serveriä on testattu linux-serverillä.

### Toiminnallisuudet
Pelin toiminnallisuudet on käyty läpi ja testattu. Virheellisten arvojen syöttämistä on myös testattu. Virheellisten pakettien käsittelyä ei ole testattu.

### Testikattavuus
Sovelluksen testauksen rivikattavuus on 69% ja haarautumakattavuus on 44%, kun käyttöliittymää, OpenGL-renderöintiä ja nettiluokkia ei huomioida.
![testikattavuus](https://i.imgur.com/aQcMk8x.png)

## Sovellukseen jääneet laatuongelmat
Koodi on paikoittain hieman hankalasti luettavaa, lähinnä UIManagerissa. UIManager on myös hyvin pitkä luokka, jossa on paljon suuria metodeja. 

Jos serveri sammuu kesken kaiken, pelaaja vain heitetään takaisin menuun ilman järkevää virheilmoitusta konsolin lisäksi.

Ei ole myöskään mahdollista poistua serveriltä käynnistämättä peliä uudelleen.

Pistenäyttö ei toimi offline-tilassa.