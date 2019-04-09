#Arkkitehtuuri
## Rakenne
Projektin rakenne noudattaa suunnilleen Entity-Component -systeemiä. Pelin logiikka on pääosin komponenteissa ja Game-luokassa. Komponenteissa on _init_, _render_, _update_ ja _destroy_ -metodit.
* _init_: alustaa luokan tarvitsemat resurssit (esim. opengl objektit)
* _render_: piirtää näytölle komponentin tarvitsemat asiat (ei suoriteta __headless__-serverillä)
* _update_: suoritetaan joka frame, esimerkiksi liikkuminen päivitetään tässä
* _destroy_: poistetaan luokan alustamat resurssit (esim. opengl objektit jotka luotiin __init__ metodissa)

### Luokkarakenne
Entity-Component -systeemin tavoin _Renderer_ sisältää kaikki _Entityt_, jotka taas sisältävät _EntityComponentteja_, jotka oikeasti määrittelevät, mitä _Entity_ tekee.
![entitycomponent](https://i.imgur.com/R4MTfWH.png)

### Pakkausrakenne
_Core_ sisältää pelimoottorin, joka on erillinen osa pelistä ja sitä voisi käyttää melkein sellaisenaan muissakin projekteissa.

_Game_ sisältää itse pelin logiikan ja sille erityiset komponentit.

![pakkausrakenne1](https://i.imgur.com/52wDCqB.png)
![pakkausrakenne2](https://i.imgur.com/NY2bd2s.png)