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
