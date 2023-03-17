# WebMessageCollector

## Leverantör
Sundsvalls Kommun

## Beskrivning
Kör ett schemalagt jobb för att hämta ut meddelanden från OpenE-systemet. Sparar information om
dessa meddelanden och tillhandahåller dessa till intressenter.


## Tekniska detaljer

### Konfiguration

Konfiguration sker i filen `src/main/resources/application.properties` genom att sätta nedanstående properties till önskade värden:


|Property|Beskrivning|
|---|---|
|`spring.datasource.url`| URL till databasen
|`spring.datasource.username`| Användarnamn för databasen
|`spring.datasource.password`| Lösenord för databasen
|`scheduler.initialDelay`| Hur långt efter uppstart första jobbet ska köras
|`scheduler.fixedRate`| Vilken intervall de schemalagda jobben ska köras på

### Paketera och starta tjänsten

Paketera tjänsten som en körbar JAR-fil genom:

```
mvn package
```

Starta med:

```
java -jar target/api-service-web-message-collector-<VERSION>.jar
```

### Bygga och starta tjänsten med Docker

Bygg en Docker-image av tjänsten:

```
mvn spring-boot:build-image
```

Starta en Docker-container:

```
docker run -i --rm -p 8080:8080 evil.sundsvall.se/ms-web-message-collector:latest
```


Copyright &copy; 2023 Sundsvalls Kommun
