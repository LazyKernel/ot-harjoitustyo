# Arkkitehtuurikuvaus
## Rakenne
Projektin rakenne noudattaa suunnilleen Entity-Component -systeemiä. Pelin logiikka on pääosin komponenteissa ja Game-luokassa. Komponenteissa on _init_, _render_, _update_ ja _destroy_ -metodit.
* _init_: alustaa luokan tarvitsemat resurssit (esim. opengl objektit)
* _render_: piirtää näytölle komponentin tarvitsemat asiat (ei suoriteta __headless__-serverillä)
* _update_: suoritetaan joka frame, esimerkiksi liikkuminen päivitetään tässä
* _destroy_: poistetaan luokan alustamat resurssit (esim. opengl objektit jotka luotiin __init__ metodissa)

### Luokkarakenne
Entity-Component -systeemin tavoin _Renderer_ sisältää kaikki _Entityt_, jotka taas sisältävät _EntityComponentteja_, jotka oikeasti määrittelevät, mitä _Entity_ tekee.
![entitycomponent](https://i.imgur.com/R4MTfWH.png)

Jos komponentti halutaan replikoida netin yli, käytetään _EntityComponent_-luokan sijaan _INetworked_-luokkaa, joka sisältää _EntityComponent_-luokan metodien lisäksi __netSeriliaze__ ja __netDeserialize__ -metodit (sekä metodit komponentin omistajan ja net id:n hallintaan).

### Pakkausrakenne
_Core_ sisältää pelimoottorin, joka on erillinen osa pelistä ja sitä voisi käyttää lähes sellaisenaan muissakin projekteissa.

_Game_ sisältää itse pelin logiikan ja sille erityiset komponentit.

Kuvissa näkyvien pakkausten lisäksi on vielä tulossa yksi pakkaus fysiikkaenginelle.
![pakkausrakenne1](https://i.imgur.com/52wDCqB.png)
![pakkausrakenne2](https://i.imgur.com/NY2bd2s.png)


## Sovelluslogiikka
Kun käyttäjä yhdistää serverille, clientin ja serverin kommunikointi näyttää suunnilleen seuraavalta, olettaen, että virheitä ei tapahdu:
![sekvenssi1](https://i.imgur.com/46W0bMz.png)

Tämän jälkeen serveri ja client lähettävät paketteja toisilleen maksimissaan kerran 16 ms. Jokainen _INetworked_ lähettää yhden paketin maksimissaan 60 kertaa sekunnissa.

Kun client poistuu pelistä, kaikki tämän omistamat objektit poistetaan serveriltä ja kaikkien muiden clienttien pelistä.


## Käyttöliittymä
Ohjelma sisältää _Nuklear_-kirjastolla tehdyn käyttöliittymän. Tällä hetkellä voi luoda vain nappeja, ikkunoita, tekstejä, tekstikenttiä, rivejä ja ryhmiä, mutta mikään ei estä uusien elementtien luomista. Tarvitsee vain laajentaa _UIElement_-luokkaa ja lisätä tämä elementti _UIManageriin_ valmiiksi luotujen elementtien tavoin.

Tällä hetkellä ohjelmassa on yksi ikkuna alussa, jos komentolinjalta ei ole annettu muita ohjeita. Se näkyy aivan alussa ja jos sen sulkee, aloitetaan peli offline-tilassa. Käyttöliittymässä on tila pelaajan nimen ja serverin ip:n kirjoittamiselle sekä napit serveriin yhdistämiseen tai serverin katseluun. Näyttö sulkeutuu, kun peliin yhdistetään ja se avautuu uudelleen, jos serveriin kadotetaan yhteys.