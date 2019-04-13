# Multiplayer Asteroids
## Features
* Simple 2 person multiplayer
* OpenGL (most likely)
* Simple UI
* Score data
* Simple player customization
  
## Linkkejä

[Määrittelydokumentti](https://github.com/LazyKernel/ot-harjoitustyo/blob/master/dokumentointi/m%C3%A4%C3%A4rittelydokumentti.md)

[Työaikakirjanpito](https://github.com/LazyKernel/ot-harjoitustyo/blob/master/dokumentointi/ty%C3%B6aikakirjanpito.md)

[Arkkitehtuuri](https://github.com/LazyKernel/ot-harjoitustyo/blob/master/dokumentointi/arkkitehtuuri.md)

## Komentorivi
### Suorittaminen
Ohjelman voi suorittaa komennolla
```
compile exec:java -Dexec.mainClass=asteroids.Main
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
Raportti löytyy tiedostosta _target/site/jacoco/index.html_

### Asetukset
Ohjelmaan voi syöttää tällä hetkellä komentorivillä asetuksia, asetukset tulevat normisuorituskomennon perään -Dexec.args osaan.
```
compile exec:java -Dexec.mainClass=asteroids.Main "-Dexec.args=<argumentit>" 
```

| argumentti | tietoa |
| __-o__ | Offline, ei yhdistä serverille, mutta sallii pelaamisen yksin. Suoritetaan oletusarvoisesti, jos muita argumentteja ei annettu. |
| __-c <ip>__ | Käynnistää clientin ja yhdistää serverille kyseiseen ip-osoitteeseen. Testiserveri _saattaa_ olla käynnissä osoitteessa __okay.works__. |
| __-s__ | Käynnistää headless-serverin, eli komentorivipohjaisen serverin, joka ei vaadi OpenGL:ää tai näyttöä. |
| __-s__ ja __-v__ | Käynnistää serverin ja ikkunan, josta näkee serverin tilan. __-v__ ei tee yksinään mitään. Hyödyllinen desync ongelmien debuggaamiseen. |

Jos useita networking-tyyppejä yritetään käynnistää samaan aikaan, viimeisenä kirjoitettu otetaan käyttöön.