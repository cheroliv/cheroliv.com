@file:Suppress(
    "RemoveRedundantQualifierName",
    "UsePropertyAccessSyntax",
    "UNUSED_VARIABLE", "LocalVariableName",
)

package playground.programming

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
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

    @Test
    fun `Dates et heures après java 8`() {
    }
}