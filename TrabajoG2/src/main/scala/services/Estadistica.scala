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
