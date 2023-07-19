// object Game {

//   given CanEqual[Game, Game] = CanEqual.derived
//   implicit val gameEncoder: JsonEncoder[Game] = DeriveJsonEncoder.gen[Game]
//   implicit val gameDecoder: JsonDecoder[Game] = DeriveJsonDecoder.gen[Game]

//   def unapply(game: Game): (GameDate, SeasonYear, Option[PlayoffRound], HomeTeam, AwayTeam) =
//     (game.date, game.season, game.playoffRound, game.homeTeam, game.awayTeam)

//   // a custom decoder from a tuple
//   type Row = (String, Int, Option[Int], String, String)

//   extension (g:Game)
//     def toRow: Row =
//       val (d, y, p, h, a) = Game.unapply(g)
//       (
//         GameDate.unapply(d).toString,
//         SeasonYear.unapply(y),
//         p.map(PlayoffRound.unapply),
//         HomeTeam.unapply(h),
//         AwayTeam.unapply(a)
//       )

//   implicit val jdbcDecoder: JdbcDecoder[Game] = JdbcDecoder[Row]().map[Game] { t =>
//       val (date, season, maybePlayoff, home, away) = t
//       Game(
//         GameDate(LocalDate.parse(date)),
//         SeasonYear(season),
//         maybePlayoff.map(PlayoffRound(_)),
//         HomeTeam(home),
//         AwayTeam(away)
//       )
//     }
// }
