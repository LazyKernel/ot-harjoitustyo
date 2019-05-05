# Vaatimusmäärittely
## Sovelluksen tarkoitus
Sovellus on 2d vektoripohjainen pelimoottori, jossa on mukana ominaisuuksia demoava asteroids-klooni.

## Käyttäjät
Sovelluksella on _pelaajia_, _katsojia_ ja _serveri_. Serveri on aina päättävä elin pelissä. Pelaajat pystyvät hallitsemaan omaa alustaan. Katsojat näkevät serverin tilan, mutta eivät pysty mitenkään vaikuttamaan pelin kulkuun. Servereitä on yksi, pelaajia maksimissaan neljä (tosin tätä voi muokata muuttamalla yhtä muuttujaa) ja katsojia rajaton määrä. 

## Tämän hetkiset toiminnallisuudet
### Ennen yhdistämistä
* käyttäjä voi yhdistää serveriin
  * jos käyttäjätunnusta ei anneta, yhdistetään katselijana

### Kirjautumisen jälkeen
* pelaaja luodaan peliin
* pelaajalla lähtetään tiedot muista jo serverillä olevista objekteista
* peli suoritetaan palvelimella ja clientillä
  * palvelin päättää, mitä pelissä tapahtuu
    * pelaajat lähettävät syötteen palvelimelle ja palvelin palauttaa muuttuneen tilan pelaajille
  
## Jatkokehitysideoita
Perusversion jälkeen voidaan täydentää seuraavilla ideoilla
* salasanat (jonkin verran turvallisia edes)
* kustomointia
  * alusten väri
  * alusten muoto
  * pelaajien avatar
* tallennetaan pisteet palvelimelle
  * muut pelaajat näkevät pisteet
* useita pelejä palvelimella, joita pelaajat voivat luoda
* pelaajat voivat valita pieniä muutoksia pelimuoihin ennen pelin luontia