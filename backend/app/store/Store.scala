package store

import scala.language.higherKinds

trait Store[F[_], K, V] {
  def deleteAll(): F[Unit]
}
