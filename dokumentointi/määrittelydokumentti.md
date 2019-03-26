# Vaatimusmäärittely
## Sovelluksen tarkoitus
Sovellus on suunniteltu pieneksi hauskaksi asteroids-klooniksi, jota kaksi pelaajaa voivat pelata keskenään.

## Käyttäjät
Sovelluksella on vain _peruskäyttäjiä_. Tarvittaessa voidaan myös lisätä _game host_ -rooli, joka pystyy esimerkiksi potkimaan muita pelaajia pelistään ja muokkaamaan kyseisen pelisession asetuksia.

## Suunnitellut toiminnallisuudet
### Ennen kirjautumista
* käyttäjä voi luoda uuden käyttäjän, jolla on käyttäjätunnus ja salasana
  * käyttäjätunnusta käytetään käyttäjien tunnistamiseen joten sen tulee olla uniikki
  * salasana tallennetaan hyvin turvattomasti palvelimelle, sillä tietoturva ei ole kurssin pääidea
* käyttäjä voi kirjautua peliin
  * jos käyttäjätunnusta ei ole tai salasana ja käyttäjätunnus eivät sovi yhteen, tästä ilmoitetaan käyttäjälle

### Kirjautumisen jälkeen
* käyttäjän kustomointi ladataan palvelimelta
* pelaaja voi liittyä peliin
* peli suoritetaan palvelimella ja clientillä
  * palvelin päättää, mitä pelissä tapahtuu
    * pelaajat lähettävät syötteen palvelimelle ja palvelin palauttaa muuttuneen tilan pelaajille
	* client ennustaa, mitä palvelimella tapahtuu, jotta peli ei pätki (paitsi jos viive on äärimmäisen epävakaa)
* käyttäjä voi kirjautua ulos
* käyttäjä voi muokata tietojansa
  * käyttäjä voi muokata aluksensa väriä
  * käyttäjä voi muokata muille pelaajille näkyvää nimeä (ei riipu käyttäjätunnuksesta)
  
## Jatkokehitysideoita
Perusversion jälkeen voidaan täydentää seuraavilla ideoilla
* tietoturva
  * salasanat tallennetaan turvallisemmin
  * käyttäjätunnus ja salasana varmistetaan palvelimelta, jolloin palvelin antaa pelille avaimen, jonka käyttöaika umpeutuu, jos sitä ei käytetä
    * minuutin välein lähetetään _heartbeat_ -paketti palvelimelle, jotta avain ei umpeudu
  * salasana ei saa mennä väärin 5 kertaa useammin viiden minuutin sisällä tai käyttäjä menee lukkoon viideksi minuutiksi
* lisää kustomointia
  * alusten muoto
  * pelaajien avatar
* tallennetaan pisteet palvelimelle
  * muut pelaajat näkevät pisteet
* useita pelejä palvelimella, joita pelaajat voivat luoda
* pelaajat voivat valita pieniä muutoksia pelimuoihin ennen pelin luontia