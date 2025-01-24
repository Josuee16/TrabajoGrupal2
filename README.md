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

## Codigo usado en tanto en la extración como en la inserción de los datos: 
### Tabla .csv:
![image](https://github.com/user-attachments/assets/66d69b24-88d6-4895-aef3-1600ee54d8fb)

### Tabla en SQL:
```
CREATE TABLE Estudiantes (
    nombre VARCHAR(255),
    edad INT,
    calificacion Double,
    genero VARCHAR(1)
);
```
### Main:
```scala
import cats.effect.{IO, IOApp}
import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._
import java.io.File
import models.Estudiantes
import dao.CalificacionesDAO

// Extiende de IOApp.Simple para manejar efectos IO y recursos de forma segura
object Main extends IOApp.Simple {
  val path2DataFile2 = "src/main/resources/data/Calificaciones.csv"

  val dataSource = new File(path2DataFile2)
    .readCsv[List, Estudiantes](rfc.withHeader.withCellSeparator(','))

  val calificaciones = dataSource.collect {
    case Right(calificacion) => calificacion
  }

  // Secuencia de operaciones IO usando for-comprehension
  def run: IO[Unit] = for {
    // Inserta datos desde el archivo CSV
    result <- CalificacionesDAO.insertAll(calificaciones)
    _ <- IO.println(s"Registros insertados: ${result.size}")

    // Obtiene todos los registros de la tabla Estudiantes
    estudiantes <- CalificacionesDAO.getAll
    _ <- IO.println("Registros en la base de datos:")
    _ <- IO.println(estudiantes.mkString("\n")) // Imprime cada registro
  } yield ()
}
```
### Estadisticas:
```scala
package services

import models.Estudiantes

object Estadistica{
  def promedio(temperaturas:List[Estudiantes]): Double = {
    temperaturas.map(_.calificacion).sum / temperaturas.length
  }

  def maxima_temperatura(temperaturas:List[Estudiantes]): Double = {
    temperaturas.map(_.calificacion).max
  }

  def desviacion_estandar(temperaturas:List[Estudiantes]): Double = {
    val x = temperaturas.map(_.calificacion)
    val N = temperaturas.length
    val promedio = x.sum / N
    val sumaCuadrados = x.map(temperatura => math.pow(temperatura - promedio, 2)).sum

    math.sqrt(sumaCuadrados/N)
  }
}
```
### Estudiantes:
```scala
package models

case class Estudiantes(
                         nombre: String,
                         edad: Int,
                         calificacion: Int,
                         genero: String
                       )
```
### CalificacionesDA0:
```scala
package dao

import doobie._
import doobie.implicits._
import cats.effect.IO
import cats.implicits._

import models.Estudiantes
import config.Database

object CalificacionesDAO extends App {
  // Inserta una sola calificación en la base de datos
  def insert(calificacion: Estudiantes): ConnectionIO[Int] = {
    sql"""
     INSERT INTO Estudiantes (nombre, edad, calificacion, genero)
     VALUES (
       ${calificacion.nombre},
       ${calificacion.edad},
       ${calificacion.calificacion},
       ${calificacion.genero}
     )
   """.update.run
  }

  // Inserta una lista de calificaciones en la base de datos
  def insertAll(calificaciones: List[Estudiantes]): IO[List[Int]] = {
    Database.transactor.use { xa =>
      calificaciones.traverse(c => insert(c).transact(xa))
    }
  }

  // Obtiene todos los registros de la tabla Estudiantes
  def getAll: IO[List[Estudiantes]] = {
    val query = sql"""
      SELECT nombre, edad, calificacion, genero
      FROM Estudiantes
    """.query[Estudiantes].to[List]

    Database.transactor.use { xa =>
      query.transact(xa)
    }
  }
}
```
### DataBase:
```scala
package config

import cats.effect.{IO, Resource}
import com.typesafe.config.ConfigFactory
import doobie.hikari.HikariTransactor

import scala.concurrent.ExecutionContext

object Database {
  private val connectEC: ExecutionContext = ExecutionContext.global

  def transactor: Resource[IO, HikariTransactor[IO]] = {
    val config = ConfigFactory.load().getConfig("db")
    HikariTransactor.newHikariTransactor[
      IO
    ](
      config.getString("driver"),
      config.getString("url"),
      config.getString("user"),
      config.getString("password"),
      connectEC // ExecutionContext requerido para Doobie
    )
  }
}
```
