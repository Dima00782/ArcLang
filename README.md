ArcLang
================

Build the application from the command line with

```bash
mvn clean package
```

Run interpreter by

```bash
mvn exec:java -Dexec.mainClass="com.github.dima00782.Main" -Dexec.args="/path/to/your/source.arc"
```

Or

```bash
java -jar target/arclang-1.0-SNAPSHOT-jar-with-dependencies.jar /path/to/your/source.arc
```

see examples in `./examples`
