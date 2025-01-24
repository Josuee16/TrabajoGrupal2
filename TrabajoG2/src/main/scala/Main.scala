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
