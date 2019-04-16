# Multiplayer Asteroids
## Features
* Simple 2 person multiplayer
* OpenGL (most likely)
* Simple UI
* Score data
* Simple player customization
  
## Linkkejä

[UUSIN RELEASE](https://github.com/LazyKernel/ot-harjoitustyo/releases/tag/v1.0.1)

[Määrittelydokumentti](https://github.com/LazyKernel/ot-harjoitustyo/blob/master/dokumentointi/m%C3%A4%C3%A4rittelydokumentti.md)

[Työaikakirjanpito](https://github.com/LazyKernel/ot-harjoitustyo/blob/master/dokumentointi/ty%C3%B6aikakirjanpito.md)

[Arkkitehtuuri](https://github.com/LazyKernel/ot-harjoitustyo/blob/master/dokumentointi/arkkitehtuuri.md)

## Komentorivi
### Suorittaminen
Ohjelman voi suorittaa komennolla
```
mvn compile exec:java -Dexec.mainClass=asteroids.Main
```

### Testaus
Testit voi suorittaa komennolla 
```
mvn test
```

Testikattavuusraportin voi luoda komennolla
```
mvn jacoco:report
```
Testikattavuusraportti löytyy tiedostosta _target/site/jacoco/index.html_

Checkstyle-tarkastuksen voi suorittaa komennolla 
```
mvn jxr:jxr checkstyle:checkstyle
```
Checkstyle-raportti löytyy tiedostosta _target/site/checkstyle.html_

Suoritettavan jarin voi generoida komennolla
```
mvn package
```

### Asetukset
Ohjelmaan voi syöttää tällä hetkellä komentorivillä asetuksia, asetukset tulevat normisuorituskomennon perään -Dexec.args osaan.
```
mvn compile exec:java -Dexec.mainClass=asteroids.Main "-Dexec.args=<argumentit>" 
```

| argumentti | tietoa |
| :----: | :----: |
| `-o` | Offline, ei yhdistä serverille, mutta sallii pelaamisen yksin. Suoritetaan oletusarvoisesti, jos muita argumentteja ei annettu. |
| `-c <ip> <username>` | Käynnistää clientin ja yhdistää serverille kyseiseen ip-osoitteeseen. Testiserverin pitäisi olla käynnissä osoitteessa __okay.works__. Jos haluat yhdistää omalla koneella olevaan serveriin, käytä osoitetta __localhost__. Jos käyttäjänimeä ei ole annettu, ei luoda pelaajaa, vaan siirrytään _spectator_-tilaan. Jos käytät samaa nimeä kuin joku muu samaan aikaan, et pysty ampumaan. Myöhemmin serveri kyllä tarkistaa. |
| `-s` | Käynnistää headless-serverin, eli komentorivipohjaisen serverin, joka ei vaadi OpenGL:ää tai näyttöä. |
| `-s -v` | Käynnistää serverin ja ikkunan, josta näkee serverin tilan. __-v__ ei tee yksinään mitään. Hyödyllinen desync ongelmien debuggaamiseen. |

Jos useita networking-tyyppejä yritetään käynnistää samaan aikaan, viimeisenä kirjoitettu otetaan käyttöön.