package lambdas.league.models

case class WLStats(nWins: Int, nLosses: Int, nHidden: Int)

object WLStats {
  val zero: WLStats = WLStats(0, 0, 0)
}
