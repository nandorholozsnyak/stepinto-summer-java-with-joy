# Quarkus
- "Új" Java keretrendszer - 2019-ben publikált
- Fókuszban volt a Java EE specifikációk követése viszont az elkötelezett teljesítmény miatt bizonyos specifikációkat nem teljesen valósítanak meg
  - CDI (ArC implementációjuk) 
- A legjobban ismert technológiákat intergálták
  - CDI
  - Hibernate - Hibernate with Panache
  - JDBC Driverek (Postgres, MySQL ...)
  - JAX-RS (RestEasy) / RestEasy Reactive
  - SmallRye - Microprofile implementáció - https://microprofile.io/
  - Kubernetes
  - gRPC
  - GraphQL
  - Liquibase/Flyway
  - Springes kitejesztések
  - https://quarkus.io/guides/
- Quarkus featurek:
  - Folyamatos tesztelés (Continous testing) (Quarkus 2.0-tól)
  - DEV UI
  - Hot reload
  - Quarkus CLI
  - GraalVM "integráció"

# Teljesítmény
![Performance](https://quarkus.io/assets/images/quarkus_metrics_graphic_bootmem_wide.png)

## Miért tudnak ilyen jók lenni?
- Build-time már nagyon sok optimalizációt végeznek a kódon és vannak olyan állományok is amelyeket a végleges futtatható állományból kihagynak


# Hogyan hozzunk létre egy Quarkus projektet?
- https://code.quarkus.io/
- IDE támogatással (ha van)
- Maven vagy Gradle pluginekkel
- Quarkus CLI-vel

# Hasznos linkek
- YouTube csatorna, heti előadásokkal: https://www.youtube.com/c/Quarkusio/videos
- Hivatalos oldal: https://quarkus.io/
- GIT tároló: https://github.com/quarkusio/quarkus

---------

# GraalVM
- Magas teljesítményű
- Polyglot - többnyelvű
- Beágyazható
- Virtuális gép
- https://www.graalvm.org/docs/introduction/

## Hogyan működik? (briefly)
Az GraalVM csapat egy compilert készített el, amellyel az OpenJDK által adott JIT compilert (Just In Time) cseréli le a Java HotSpot VM felett.  
Állításuk szerint azzal, hogy a GraalVM compiler dorombol a Java HotSpot VM felett, bizonyos esetekben akár 30-40-50% (nagyon extrém és persze DEMO esetekben akár 30x) teljesítményt tudnak elérni.

Architektúrájuk: https://www.graalvm.org/docs/introduction/#graalvm-architecture
(Lentről felfelé)
![Architecture](https://www.graalvm.org/docs/img/graalvm_architecture_community.png)

## JVM alapú nyelvek
A JVM alapú nyelvekhez mint a Java, Scala, Kotlin semmi más dolgunk nincs mint leszedni az ő binárisukat és egyszerűen rárakni a PATH-ra, onnantól pedig a következő jar futtatásál már a GraalVM compiler fogja javítani az élményt.

## Nem JVM alapú nyelvek
A nem JVM alapú nyelveknél, mint a JS, Python, Ruby, R, WA, (C, C++) egy Javaban írt interpretert kell megírni amelyhez a [Truffe](https://www.graalvm.org/graalvm-as-a-platform/language-implementation-framework/) keretrendszert kell használnia a fejlesztőknek.  
Egy "egyszerű" API amivel a nyelvek közötti "konverziót" (ez nem a legjobb megfogalmazás rá) tudjuk elkészíteni és mindezt Javaban.

## GraalVM Updates (gu)
Alapból ha letöltjük és kicsomagoljuk a GraalVM valamelyik verzióját (19.0, 20.0, 21.0) akkor abban a JVM alapú nyelvekhez kapunk egyből támogatást.  
A támogatott Java verziók:
- 8
- 11
- 16 (Experimental)

Ha szeretnék a fentebb említett nyelvekhez is támogatást akkor azokat külön kell telepíteni a GraalVM `gu` nevű segédprogramjával:
- `gu install js`
- `gu install python`

Ahhoz, hogy a telepíthető csomagokról egy átfogóbb képet kapjunk használhatjuk a `gu available` parancsot.

## Natív futtatható állományok generálása
A GraalVM `native-image` segédeszköze képes egy JVM-en futó állományt bináris kódra fordítani.  
Használával alacsony memóriahasználatot érhetünk el és nagyon gyors indulási időt - természetesen ezek a kiváltságok nem jönnek ingyen, a bináris állomány előállítása sok időt vehet igénybe, plusz az alkalmazásunknak és annak függőségeinek "kompatbilisnek" kell lenniük a natív állomány előállításával.

Telepítése: `gu install native-image`

A folyamat "röviden tömören":
- Statikus kód analízis
- Ahead-of-Time compilation
  ![Native image](https://kucw.github.io/images/blog/graalvm_nativeimage2.png)

(Forrás: https://kucw.github.io/images/blog/graalvm_nativeimage2.png)

Egy komolyabb Medium cikk a teljes folyamatról és annak nehézségeivel: https://medium.com/graalvm/updates-on-class-initialization-in-graalvm-native-image-generation-c61faca461f7

## Változatok
A GraalVM mint termék két változatban érhető el:
- Community - teljesen ingyenes
- Enterprise - fizetős

GraalVM fejlesztők véleménye: Ha a performanciát tekintjük akkor nem azt mondjuk, hogy a community verzió a lassú, hanem hogy az enterprise a gyorsabb.
