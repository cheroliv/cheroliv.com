@file:Suppress(
    "RemoveRedundantQualifierName",
    "UsePropertyAccessSyntax",
    "UNUSED_VARIABLE", "LocalVariableName", "NonAsciiCharacters", "unused",
)

package playground.programming

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.*
import java.time.temporal.*
import java.util.*
import java.util.stream.Collectors
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class DatesHoursTest {
    @Test
    fun `Dates et heures`() {
        //l'heure courante en millisecondes
        val t0 = System.currentTimeMillis()
        //une autre représentation de la meme information
        val now = java.util.Date()
        //converti un objet java.util.Date en une valeur long.
        val t1 = now.getTime()
        assertTrue(t1 > Instant.EPOCH.toEpochMilli())
        //kotlin property access syntaxe style
        val t1_prime = now.time

        //java.text.DateFormat
        //affiche la date d'aujourd'hui en utilisant le format
        //par défaut des parametres locaux
        val defaultDateFormat = DateFormat.getDateInstance()
        //personnalisation du formatage et de la locale
        val df = DateFormat.getDateInstance(DateFormat.LONG, Locale.FRANCE)
        val localeFormattedDate = df.format(Date())

        //constantes pour les styles de pattern de formatage
        assertEquals(0, DateFormat.FULL)
        assertEquals(1, DateFormat.LONG)
        assertEquals(2, DateFormat.MEDIUM)
        assertEquals(3, DateFormat.SHORT)
        assertEquals(2, DateFormat.DEFAULT)

        //utilise pour l'heure un format abrégé avec
        //des parametres personnalisés
        val tf = DateFormat.getTimeInstance(
            DateFormat.SHORT,
            Locale.FRANCE
        )
        //affiche l'heure en utilisant le format de tf
        val shortTime = tf.format(Date())
        assertTrue(shortTime.contains(':'))

        //affiche la date et l'heure en utilisant
        //un format détaillé
        val longTimeStamp = DateFormat.getDateTimeInstance(
            DateFormat.FULL,
            DateFormat.FULL,
        )
        assert(longTimeStamp.format(Date()).isNotEmpty())

        //utilisez java.text.SimpleDateFormat
        //pour définir votre propre modele de formatage
        val customFormat = SimpleDateFormat("yyyy.MM.dd")
        assertEquals(10, customFormat.format(Date()).length)

        //DateFormat peut également parser les date contenu dans des chaines
        val kotlinAnnounceDate = customFormat.parse("2019.05.08")

        //la class Date et sa représentation en millisecondes
        //n'autorise qu'une forme trés simple d'arithmétique
        //on ajoute 3 600 000 millisecondes à l'heure courante
        val anHourFromNow = now.getTime() + (60 * 60 * 1000)
        assert(anHourFromNow > now.getTime())

        //java.util.Calendar
        //pour manipuler les dates et heures de facon plus sophistiquée
        //instanciation selon les parametres locaux
        //et le fuseau horaire local
        val calendar = Calendar.getInstance()
        //initialisation du calendrier à la date de maintenant
        calendar.setTime(now)
        //détermine le jour de l'année auquel correspond la date courante
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        assert(dayOfYear < 366)
        //réinitialisation de la date courante
        calendar.set(2019, Calendar.MAY, 8)
        assertEquals(4, calendar.get(Calendar.DAY_OF_WEEK))

        //à quel jour du mois correspond le deuxième mercredi de mai 2019
        //set(key,value)
        calendar.set(Calendar.YEAR, 2019)
        calendar.set(Calendar.MONTH, Calendar.MAY)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY)
        //defini à quel (n=2) semaine du mois est la date
        calendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, 2)
        //extrait le jour du mois
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        assertEquals(8, dayOfMonth)

        calendar.setTime(kotlinAnnounceDate)
        //ajoute 30j à la date
        calendar.add(Calendar.DATE, 30)
        val monthAfter = calendar.getTime()
        //date est elle avant ou apres?
        assertTrue(monthAfter.after(kotlinAnnounceDate))
    }

    class BirthdayDiary {
        private val birthdays: MutableMap<String, LocalDate>

        init {
            birthdays = HashMap()
        }

        fun addBirthday(
            name: String, day: Int, month: Int,
            year: Int
        ): LocalDate {
            val birthday: LocalDate = LocalDate.of(year, month, day)
            birthdays[name] = birthday
            return birthday
        }

        fun getBirthdayFor(name: String): LocalDate? {
            return birthdays[name]
        }

        fun getAgeInYear(name: String, year: Int): Int {
            val period: Period = Period.between(
                birthdays[name],
                birthdays[name]!!.withYear(year)
            )
            return period.getYears()
        }

        fun getFriendsOfAgeIn(age: Int, year: Int): Set<String> {
            return birthdays.keys.stream()
                .filter { p: String -> getAgeInYear(p, year) == age }
                .collect(Collectors.toSet())
        }

        fun getDaysUntilBirthday(name: String): Int {
            val period: Period = Period.between(
                LocalDate.now(),
                birthdays[name]
            )
            return period.getDays()
        }

        fun getBirthdaysIn(month: Month): Set<String> {
            return birthdays.entries.stream()
                .filter { (_, value): Map.Entry<String, LocalDate> -> value.getMonth() === month }
                .map<String> { (key): Map.Entry<String, LocalDate> -> key }
                .collect(Collectors.toSet())
        }

        val birthdaysInCurrentMonth: Set<String>
            get() = getBirthdaysIn(LocalDate.now().getMonth())
        val totalAgeInYears: Int
            get() = birthdays.keys.stream()
                .mapToInt { p: String ->
                    getAgeInYear(
                        p,
                        LocalDate.now().getYear()
                    )
                }
                .sum()
    }

    class FirstDayOfQuarter : TemporalAdjuster {
        override fun adjustInto(temporal: Temporal): Temporal? {
            val currentQuarter: Int = YearMonth.from(temporal)
                .get(IsoFields.QUARTER_OF_YEAR)
            return when (currentQuarter) {
                1 -> LocalDate.from(temporal)
                    .with(TemporalAdjusters.firstDayOfYear())

                2 -> LocalDate.from(temporal)
                    .withMonth(Month.APRIL.value)
                    .with(TemporalAdjusters.firstDayOfMonth())

                3 -> LocalDate.from(temporal)
                    .withMonth(Month.JULY.value)
                    .with(TemporalAdjusters.firstDayOfMonth())

                4 -> LocalDate.from(temporal)
                    .withMonth(Month.OCTOBER.value)
                    .with(TemporalAdjusters.firstDayOfMonth())

                else -> null
            }
        }
    }
    enum class Quarter {
        FIRST, SECOND, THIRD, FOURTH
    }
    class QuarterOfYearQuery : TemporalQuery<Quarter> {
        override fun queryFrom(temporal: TemporalAccessor): Quarter {
            val now = LocalDate.from(temporal)
            return if (now.isBefore(now.with(Month.APRIL).withDayOfMonth(1))) {
                Quarter.FIRST
            } else if (now.isBefore(
                    now.with(Month.JULY)
                        .withDayOfMonth(1)
                )
            ) {
                Quarter.SECOND
            } else if (now.isBefore(
                    now.with(Month.NOVEMBER)
                        .withDayOfMonth(1)
                )
            ) {
                Quarter.THIRD
            } else {
                Quarter.FOURTH
            }
        }
    }
    @Test
    fun `Dates et heures après java 8`() {
        val today = LocalDate.now()
        val currentMonth = today.month
        val firstMonthOfQuarter = currentMonth.firstMonthOfQuarter()

        val q = QuarterOfYearQuery()
        // Direct
        var quarter: Quarter? = q.queryFrom(LocalDate.now())
        println(quarter)
        // Indirect
        quarter = LocalDate.now().query(q)
        println(quarter)


        val now = LocalDate.now()
        val fdoq: Temporal = now.with(FirstDayOfQuarter())
        println(fdoq)
    }
}
