import zio.json._
import zio.jdbc._

import java.time.LocalDate


object GameDate {
  // Definition of an opaque type GameDate
  type GameDate = LocalDate

  // Apply method to create a GameDate from a LocalDate
  def localDateToGameDate(date: LocalDate): GameDate = date

  // Unapply method to extract the underlying LocalDate from a GameDate
  def gameDateToLocalDate(gameDate: GameDate): LocalDate = gameDate

  // Equality for GameDate
  given CanEqual [GameDate, GameDate] = CanEqual.derived
  // JSON encoder and decoder for GameDate
  implicit val gameDateEncoder: JsonEncoder[GameDate] = JsonEncoder.localDate
  implicit val gameDateDecoder: JsonDecoder[GameDate] = JsonDecoder.localDate
}

// Definition of an opaque type SeasonYear
object SeasonYears {

  type SeasonYear <: Int = Int

  object SeasonYear {

    // Apply method to create a SeasonYear from an Int
    def apply(year: Int): SeasonYear = year

    // Safe method to create a SeasonYear from an Int within a certain range
    def safe(value: Int): Option[SeasonYear] =
      Option.when(value >= 1876 && value <= LocalDate.now.getYear)(value)

    // Unapply method to extract the underlying Int from a SeasonYear
    def unapply(seasonYear: SeasonYear): Int = seasonYear
  }

  // Equality for SeasonYear
  given CanEqual[SeasonYear, SeasonYear] = CanEqual.derived
  // JSON encoder and decoder for SeasonYear
  implicit val seasonYearEncoder: JsonEncoder[SeasonYear] = JsonEncoder.int
  implicit val seasonYearDecoder: JsonDecoder[SeasonYear] = JsonDecoder.int
}

// Definition of an opaque type HomeTeam
object HomeTeam {

  type HomeTeam = String

  // Apply method to create a HomeTeam from a String
  def stringToHomeTeam(value: String): HomeTeam = value

  // Unapply method to extract the underlying String from a HomeTeam
  def homeTeamToString(homeTeam: HomeTeam): String = homeTeam

  // Equality for HomeTeam
  given CanEqual[HomeTeam, HomeTeam] = CanEqual.derived
  // JSON encoder and decoder for HomeTeam
  implicit val homeTeamEncoder: JsonEncoder[HomeTeam] = JsonEncoder.string
  implicit val homeTeamDecoder: JsonDecoder[HomeTeam] = JsonDecoder.string
}

// Definition of an opaque type AwayTeam
object AwayTeams {

  type AwayTeam = String

  // Apply method to create an AwayTeam from a String
  def stringToAwayTeam(value: String): AwayTeam = value

  // Unapply method to extract the underlying String from an AwayTeam
  def awayTeamToString(awayTeam: AwayTeam): String = awayTeam

  // Equality for AwayTeam
  given CanEqual[AwayTeam, AwayTeam] = CanEqual.derived
  // JSON encoder and decoder for AwayTeam
  implicit val awayTeamEncoder: JsonEncoder[AwayTeam] = JsonEncoder.string
  implicit val awayTeamDecoder: JsonDecoder[AwayTeam] = JsonDecoder.string
}


import GameDate.*
import SeasonYear.*
import HomeTeam.*
import AwayTeam.*

//game definition
final case class Game(
                       date: GameDate,
                       season: SeasonYear,
                       playoffRound: Option[PlayoffRound],
                       homeTeam: HomeTeam,
                       awayTeam: AwayTeam
                     )

object Game {

  given CanEqual[Game, Game] = CanEqual.derived
  implicit val gameEncoder: JsonEncoder[Game] = DeriveJsonEncoder.gen[Game]
  implicit val gameDecoder: JsonDecoder[Game] = DeriveJsonDecoder.gen[Game]

  def unapply(game: Game): (GameDate, SeasonYear, Option[PlayoffRound], HomeTeam, AwayTeam) =
    (game.date, game.season, game.playoffRound, game.homeTeam, game.awayTeam)

  // a custom decoder from a tuple
  type Row = (String, Int, Option[Int], String, String)

  extension (g:Game)
  def toRow: Row =
  val (d, y, p, h, a) = Game.unapply(g)
  (
    GameDate.unapply(d).toString,
    SeasonYear.unapply(y),
    p.map(PlayoffRound.unapply),
    HomeTeam.unapply(h),
    AwayTeam.unapply(a)
  )

  implicit val jdbcDecoder: JdbcDecoder[Game] = JdbcDecoder[Row]().map[Game] { t =>
    val (date, season, maybePlayoff, home, away) = t
    Game(
      GameDate(LocalDate.parse(date)),
      SeasonYear(season),
      maybePlayoff.map(PlayoffRound(_)),
      HomeTeam(home),
      AwayTeam(away)
    )
  }
}