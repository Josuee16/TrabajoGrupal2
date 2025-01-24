# TrabajoGrupal2
## Documentación del Código
En el trabajo presente vamos a realizar una incerción de datos de un archivo csv a una tabla de base de datos utilizando Scala y las librerías acontinuación: 
```scala
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "Temperaturas",
    libraryDependencies ++= Seq(
      "com.nrinaudo" %% "kantan.csv" % "0.6.1",
      "com.nrinaudo" %% "kantan.csv-generic" % "0.6.1",
      "org.tpolecat" %% "doobie-core" % "1.0.0-RC5",
      "org.tpolecat" %% "doobie-hikari" % "1.0.0-RC5",
      "com.mysql" % "mysql-connector-j" % "8.0.31",
      "com.typesafe" % "config" % "1.4.2",
      "ch.qos.logback" % "logback-classic" % "1.2.3"

    )
  )
```
Para luego terminar insertando los registros del csv a la base de datos.

![image](https://github.com/user-attachments/assets/31eec81d-5393-40d1-9483-ae7cc9185d29)


Ademas,luego terminar agregando la funcionalidad que permita obtener de la base de datos todos los registros de Estudiantes.

![image](https://github.com/user-attachments/assets/ca5d964c-a943-44c0-a464-e360cfda1dce)

