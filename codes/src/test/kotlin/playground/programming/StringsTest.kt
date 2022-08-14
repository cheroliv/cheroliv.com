@file:Suppress(
    "NonAsciiCharacters",
    "ConvertToStringTemplate",
    "ReplaceGetOrSet",
    "PLATFORM_CLASS_MAPPED_TO_KOTLIN",
    "KotlinConstantConditions",
    "ReplaceCallWithBinaryOperator",
    "CanBeVal",
)

package playground.programming

import java.text.Collator
import java.util.*
import kotlin.test.*

class StringsTest {
    @Test
    fun `chaines de caractères et caractères`() {
        //inférence de type
        val s = "C'est"
        assertEquals("C'est", s)

        //concaténation
        val t: String = s + " le moment."
        assertEquals("C'est le moment.", t)

        // StringBuilder
        val t1: String = s + buildString { append(" le moment.") }
        assertEquals("C'est le moment.", t1)

        //interpolation
        val t2 = "$s le moment."
        assertEquals("C'est le moment.", t2)

        //multiligne chaine de caractères
        val t3 = """$s le moment."""
        assertEquals("C'est le moment.", t3)

        //conversion de type
        val t4 = t + " " + 23.4
        assertEquals("C'est le moment. 23.4", t4)

        val t5 = 'C'.toString()
        assertEquals("C", t5)

        //taille de la chaine de caractères
        assertEquals(16, t.length)

        //sous-chaine d'une chaine de caractères

        //retourne une chaine de caractères contenant,
        //les caractères aux positions x à y-1
        //sub = t.substring(x, y)

        //t = "C'est le moment."
        //retourne les caractères 6 et 7
        var sub = t.substring(6, 8)
        assertEquals("le", sub)

        //retourne les caractères 0 à 4
        sub = t.substring(0, 5)
        assertEquals("C'est", sub)

        //la longueur d'une sous-chaine est toujours égale (y-x)
        val numChars = sub.length
        assertEquals(5 - 0, numChars)

        //extraction des caractères d'une chaine
        assertEquals('e', t.elementAt(2))
        assertEquals('e', t.get(2))
        assertEquals('e', t[2])

        //conversion d'une chaine en tableau de caractères
        val ca = t.toCharArray()
        assertEquals(t.length, ca.size)
        t.mapIndexed { index, char -> assertEquals(char, ca[index]) }

        //place les 4 premiers caractères de t1
        //dans le tableau ca a la position 2
        (t as java.lang.String).getChars(
            /* srcBegin = */ 0,
            /* srcEnd = */ 3,
            /* dst = */ ca,
            /* dstBegin = */ 1
        )
        assertEquals("CC'et le moment.", String(ca))
        //colle tous les caractères dans un meme string
        assertEquals("CC'et le moment.", ca.concatToString())

        //to lower case
        assertEquals("c'est le moment.", t.toLowerCase())
        assertEquals("c'est le moment.", t.lowercase())

        //to upper case
        assertEquals("C'EST LE MOMENT.", t.toUpperCase())
        assertEquals("C'EST LE MOMENT.", t.uppercase())

        //comparaison de chaines de caractères
        //t = "C'est le moment."
        assertFalse(t.equals("hello"))
        assertFalse(t == "hello")

        //ignore la casse
        assertTrue(t.equalsIgnoreCase("C'EST LE MOMENT."))
        assertTrue(t.equals("C'EST LE MOMENT.", ignoreCase = true))

        //démarre par
        assertTrue(t.startsWith("C'est"))
        //se finit par
        assertTrue(t.endsWith("le moment."))

        //compareTo
        //retourne une valeur < 0, car s est
        //alphabétiquement avant "N'est"
        val r1: Int = s.compareTo("N'est")
        assertTrue(r1 < 0)

        //variante ignorant la casse
        val r1Prime: Int = (s as java.lang.String).compareToIgnoreCase("n'est")
        assertTrue(r1Prime < 0)

        //retourne 0 si les chaines sont equivalente
        val r2: Int = s.compareTo("C'est")
        assertEquals(0, r2)

        //retourne une valeur > 0 car s vient apres "B'est"
        val r3: Int = s.compareTo("B'est")
        assertTrue(r3 > 0)

        //Recherche de caractères et de sous-chaines de caractères

        //recherche de caractères
        //position du premier caractères 't'
        var pos = t.indexOf('t')
        assertEquals(4, pos)

        //position du suivant
        pos = t.indexOf('t', pos + 1)
        assertEquals(14, pos)

        //retour d'érreur -1 si absence de suivant
        pos = t.indexOf('t', pos + 1)
        assertEquals(-1, pos)

        //position du dernier 't' dans la chaine: 14
        pos = t.lastIndexOf('t')
        assertEquals(14, pos)

        //recherche de 't' vers l'arrière a partir du caractères 13
        pos = t.lastIndexOf('t', pos - 1)
        assertEquals(4, pos)

        //recherche de sous-chaines
        //retourne 2
        pos = t.indexOf("est")
        assertEquals(2, pos)

        //"est" n'apparait qu'une seule fois: retourne -1
        pos = t.indexOf("est", pos + 1)
        assertEquals(-1, pos)

        //recherche d'une sous-chaine depuis l'arrière
        //t = "C'est le moment."
        //retourne 6
        pos = t.lastIndexOf("le ")
        assertEquals(6, pos)

        //extrait depuis la position 9,
        //renvoi toute la chaine après "le "
        val noun = t.substring(pos + 3)
        assertEquals(-1, noun.indexOf("le "))

        //remplacement de toutes les instances d'un caractère
        //par un autre caractère
        //ne fonctionne que avec les caractères, pas les chaines
        val exclaim: String = t.replace('.', '!')
        assertEquals('!', exclaim.get(exclaim.length - 1))
        assertEquals(exclaim.length - 1, exclaim.indexOf('!'))
        assertEquals(-1, exclaim.indexOf('.'))

        //suppression des espaces blancs
        //au début et à la fin d'une chaine
        val noextraspaces = t.trim()
        assertNotEquals(' ', noextraspaces.get(0))
        assertNotEquals(' ', noextraspaces.get(noextraspaces.length - 1))

        //extraction des instances uniques de chaines de caractères
        //avec intern()
        val s1 = s.intern()
        assertEquals(s, s1)
        val s2 = "C'est".intern()
        assertEquals("C'est", s2)
        assertEquals(s1, s2)

        //StringBuilder pour manipuler les caractères d'une chaine de caractères
        //crée un tampon StringBuilder à partir d'une chaine de caractères
        val b = StringBuilder("N'est")

        //extrait et définit des caractères individuel du tampon StringBuilder
        //le caractères à l'index 0
        val c: Char = b.get(0)
        assertEquals('N', c)

        //modifier le premier caractère de la chaine
        b.setCharAt(0, 'C')
        assertEquals(s, b.toString())

        //ajouter des données à un StringBuilder
        b.append(' ')
        b.append("le moment.")
        b.append(23)

        //insère des chaines de caractères ou autre dans le StringBuilder
        b.insert(6, "pas ")
        assertEquals("C'est pas le moment.23", b.toString())

        //remplace un sous ensemble de caractères
        //avec une chaine de caractères donnée
        b.replace(2, 9, "est")
        assertEquals("C'est le moment.23", b.toString())

        //supprime les caractères
        b.delete(15, 18)
        assertEquals("C'est le moment", b.toString())
        b.deleteCharAt(2)
        assertEquals("C'st le moment", b.toString())

        //insert à la postion 2 et décale reste à droite(sans perte de données)
        b.insert(2, 'e')

        //tronque la taille de la donnée
        b.setLength(5)
        assertEquals("C'est", b.toString())

        //inverse les caractères de la chaine
        b.reverse()
        assertEquals("tse'C", b.toString())

        //écrase le StringBuilder, pret à etre réutilisé
        b.setLength(0)
        assertEquals("", b.toString())

        //java.util.StringTokenizer pour fragmenter une chaine
        //de caractères en un ensemble de mots
        var st = StringTokenizer(t)
        //nb d'items encore présentent dans la file
        assertEquals(3, st.countTokens())

        //est ce que il y a encore des items dans la file
        assertTrue(st.hasMoreTokens())

        //récupérer le token courrant
        assertEquals("C'est", st.nextToken())
        assertEquals("le", st.nextToken())
        assertEquals("moment.", st.nextToken())
        assertFalse(st.hasMoreTokens())
        assertEquals(0, st.countTokens())

        //extraire des occurences de mots délimités
        //par des caractères autres que des expaces.
        val str = "a:b:c:d"
        st = StringTokenizer(str, ":")
        assertEquals(4, st.countTokens())
        assertTrue(st.hasMoreTokens())
        assertEquals("a", st.nextToken())
        assertEquals("b", st.nextToken())
        assertEquals("c", st.nextToken())
        assertEquals("d", st.nextToken())
        assertFalse(st.hasMoreTokens())
        assertEquals(0, st.countTokens())


        //text="C'est le moment."
        val text = t.toCharArray()
        var p = 0

        //sauter les espaces de tete
        //pour ramener p à la position du premier caractère imprimable
        while ((p < text.size) &&
            (Character.isWhitespace(text[p]))
        ) p++
        assertEquals(0, p)
        assertEquals("C'est le moment.", text.concatToString())

        //met le premier mot du texte en majuscule
        while (p < text.size && Character.isLetter(text[p])) {
            text[p] = Character.toUpperCase(text[p])
            p++
        }
        assertEquals(1, p)
        assertEquals('C', text[0])
        assertTrue(Character.isUpperCase(text[0]))
        assertFalse(Character.isLetter(text[1]))

        //comparer des chaines de caractères
        // avec les contrainte la locale système
        val col = Collator.getInstance()
        //le résulat est négatif car chica
        //est avant chico dans l'ordre alphabétique
        assertTrue(col.compare("chica", "chico") < 0)
    }
}