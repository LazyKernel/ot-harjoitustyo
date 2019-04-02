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