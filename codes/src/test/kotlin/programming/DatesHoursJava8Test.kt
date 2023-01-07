@file:Suppress("NonAsciiCharacters")

package programming

import java.time.LocalDate
import java.time.Month
import java.time.Period
import java.time.YearMonth
import java.time.temporal.*
import java.util.HashMap
import java.util.stream.Collectors
import kotlin.test.Test

class DatesHoursJava8Test {
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

    //Les ajusteurs modifient les objets de date et d'heure. Supposons, par exemple, que nous voulions
    //renvoie le premier jour d'un trimestre qui contient un horodatage particulier :
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

    //Dans quel trimestre de l'année cette date se situe-t-elle ?
    class QuarterOfYearQuery : TemporalQuery<Quarter> {
        override fun queryFrom(temporal: TemporalAccessor): Quarter {
            val now = LocalDate.from(temporal)
            return if (now.isBefore(now.with(Month.APRIL).withDayOfMonth(1)))
                Quarter.FIRST
            else if (now.isBefore(
                    now.with(Month.JULY)
                        .withDayOfMonth(1)
                )
            ) Quarter.SECOND else if (now.isBefore(
                    now.with(Month.NOVEMBER)
                        .withDayOfMonth(1)
                )
            ) Quarter.THIRD else Quarter.FOURTH
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