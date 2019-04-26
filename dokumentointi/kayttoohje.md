# Käyttöohje
Lataa uusin [release](https://github.com/LazyKernel/ot-harjoitustyo/releases/latest).

## Edellytykset ohjelman suorittamiseen
Ohjelma tarvitsee käyttöliittymälle ja pelille omat shaderit kansiossa. Nämä tulevat releasen mukana. Näitä ei tarvita, jos ohjelman haluaa suorittaa komentolinjapohjaisena serverinä. 

Ohjelma myös tällä hetkellä vaatii OpenGL-ajurit, jotka tukevat GLSL:n versiota 4.30, tosin tämä tulee luultavimmin poistumaan seuraavassa releasessa, sillä laitoksen koneilla ei ilmeisesti ole tarpeeksi uusia ajureita. 

## Ohjelman käynnistäminen
Ohjelma käynnistetään komennolla
```
java -jar asteroids.jar <mahdolliset argumentit>
```

Mahdollisia argumentteja ovat:
| argumentti | tietoa |
| :----: | :----: |
| `-o` | Offline, ei yhdistä serverille, mutta sallii pelaamisen yksin. Suoritetaan oletusarvoisesti, jos muita argumentteja ei annettu. |
| `-c <ip> <username>` | Käynnistää clientin ja yhdistää serverille kyseiseen ip-osoitteeseen. Testiserverin pitäisi olla käynnissä osoitteessa __okay.works__. Jos haluat yhdistää omalla koneella olevaan serveriin, käytä osoitetta __localhost__. Jos käyttäjänimeä ei ole annettu, ei luoda pelaajaa, vaan siirrytään _spectator_-tilaan. Jos käytät samaa nimeä kuin joku muu samaan aikaan, et pysty ampumaan. Myöhemmin serveri kyllä tarkistaa. |
| `-s` | Käynnistää headless-serverin, eli komentorivipohjaisen serverin, joka ei vaadi OpenGL:ää tai näyttöä. |
| `-s -v` | Käynnistää serverin ja ikkunan, josta näkee serverin tilan. __-v__ ei tee yksinään mitään. Hyödyllinen desync ongelmien debuggaamiseen. |

Jos useita networking-tyyppejä yritetään käynnistää samaan aikaan, viimeisenä kirjoitettu otetaan käyttöön.

Argumentteja ei ole kuitenkaan tarve käyttää, jos haluaa vain yhdistää serverille tai pelata offline-tilassa. Serveri tulee kuitenkin avata komentolinjalta.

## Client
### Serveriin yhdistäminen
Sovellus käynnistyy yhdistämisnäkymään, jos komentolinjalta ei ole annettu muita argumentteja.
![yhdista](https://i.imgur.com/qwRdJy7.png)

_Name_-kenttään kirjoitetaan luonnollisesti pelaajan nimi ja _IP_-kenttään serverin ip-osoite.

Jos haluat pelata offline-tilassa, voit painaa oikean yläkulman raksia.

## Server
### Käynnistäminen
Serveri on käynnistettävä komentolinjalta. Katso argumentit sen tekemiseen [ylhäältä](https://github.com/LazyKernel/ot-harjoitustyo/tree/master/dokumentointi/kayttoohje.md#ohjelman-käynnistäminen).