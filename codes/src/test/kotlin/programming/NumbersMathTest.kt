@file:Suppress(
    "UNUSED_VARIABLE",
    "LocalVariableName",
    "VARIABLE_WITH_REDUNDANT_INITIALIZER",
    "RemoveRedundantQualifierName",
    "ReplaceJavaStaticMethodWithKotlinAnalog",
)

package programming

import java.math.BigInteger
import java.security.SecureRandom
import java.text.NumberFormat
import java.util.*
import kotlin.test.*

class NumbersMathTest {
    @Test
    fun `Nombres et Math`() {
        //Constantes utiles
        Byte.MIN_VALUE
        Byte.MAX_VALUE
        Short.MIN_VALUE
        Short.MAX_VALUE
        Float.MIN_VALUE
        Float.MAX_VALUE
        Math.PI
        Math.E
        val s = "-42"
        //conversion de chaine de caractères
        //vers un nombre, si possible.
        var b: Byte = java.lang.Byte.parseByte(s)
        var sh: Short = java.lang.Short.parseShort(s)
        var i: Int = java.lang.Integer.parseInt(s)
        var l: Long = java.lang.Long.parseLong(s)
        var f: Float = java.lang.Float.parseFloat(s)
        var d: Double = java.lang.Double.parseDouble(s)

        //valeur exacte
        val f_exac = java.lang.Float.valueOf(s)
        val d_exac = java.lang.Double.valueOf(s)

        //les routines de conversions entière gérent
        //les nombres dans diverses bases.
        //1011 en binare est égal a 11 en base dix
        b = java.lang.Byte.parseByte("1011", 2)
        assertEquals(11, b)
        //ff en base 16(hexa) est égal à 255 en base dix.
        sh = java.lang.Short.parseShort("ff", 16)
        assertEquals(255, sh)

        //la méthode valueOf() peut gérer des bases arbitraires.
        i = java.lang.Integer.valueOf("egg", 17).toInt()
        assertEquals(4334, i)

        //la méthode decode() gére les representations octale,
        //décimal, hexadécimal, en fonction du préfixe numérique
        //de la chaine de caractères
        //un 0 de tete signifie base 8
        //un 0x de tete signifie base 16
        //les autres sont en base 10
        val sho = java.lang.Short.decode("0377")

        //la classe Integer peut convertir les nombres
        //en diverses chaines de caractères.
        val decimal = java.lang.Integer.toString(42)
        assertEquals("42", decimal)

        val decimal_ = 42.toString()
        assertEquals("42", decimal_)

        val binary = java.lang.Integer.toBinaryString(42)
        assertEquals("101010", binary)

        val octal = java.lang.Integer.toOctalString(42)
        assertEquals("52", octal)

        val hex = java.lang.Integer.toHexString(42)
        assertEquals("2a", hex)

        val base36 = java.lang.Integer.toString(42, 36)
        assertEquals("16", base36)

        val base36_ = 42.toString(36)
        assertEquals("16", base36_)

        //java.text.NumberFormat effectue la conversion
        // d'une maniere spécifique aux parametres locaux
        //sans parametre prend la local systeme comme reference
        val nf = NumberFormat.getNumberInstance(Locale.FRANCE)
        val formatted_number = nf.format(9876543.21)
        assertNotEquals("9876543.21", formatted_number)

        //parse la chaine de caractères en fonction des parametres locaux(fr)
        val n = nf.parse("1234567,89")
        assertEquals(1234567.89, n)

        //les valeurs monétaires sont parfois formaté
        // d'une maniere differente des nombres
        val money_format = NumberFormat.getCurrencyInstance(Locale.FRANCE)


        //java.lang.Math
        d = Math.toRadians(27.0)
        d = Math.cos(d)
        d = Math.sqrt(d)
        d = Math.log(d)
        d = Math.exp(d)
        d = Math.pow(10.0, d)
        d = Math.atan(d)
        d = Math.toDegrees(d)
        //arrondi au dessus
        val up = Math.ceil(d)
        //arrondi au dessous
        val down = Math.floor(d)
        //arrondi au plus près
        val nearest = Math.round(d)

        //java.lang.Math.Random()
        val r = Math.random()
        assertTrue(r >= 0.0 && r < 1.0)

        //créé un nouvel objet Random, en l'initialisant
        //avec l'heure courante
        val generator = java.util.Random(System.currentTimeMillis())

        //prochaine valeur aléatoire de taille double
        d = generator.nextDouble()
        assertTrue((d >= 0.0) && (d < 1.0))


        //prochaine valeur aléatoire de taille float
        f = generator.nextFloat()
        assertTrue((f >= 0.0) && (f < 1.0))


        //prochaine valeur aléatoire de taille long
        l = generator.nextLong()
        assertTrue(
            (Math.abs(l) <= Long.MAX_VALUE) &&
                    (Math.abs(l) >= 0)
        )


        //prochaine valeur aléatoire de taille int
        i = generator.nextInt()
        assertTrue(
            (Math.abs(i) <= java.lang.Integer.MAX_VALUE) &&
                    (Math.abs(i) >= 0)
        )

        val limit = 100
        //prochaine valeur aléatoire de taille int
        //la limit max du ramdom est ramené à limit
        //et la limit min est 0
        i = generator.nextInt(limit)
        assertTrue(i in 0 until limit)


        //prochaine valeur aléatoire de taille booléen
        val bool = generator.nextBoolean()
        assertNotNull(bool)


        //valeur moyenne 0.0, déviation standard 1.0
        d = generator.nextGaussian()


        //randoms bytes
        //rempli un tableau avec des valeurs byte aléatoires
        val b_arr = ByteArray(128)
        generator.nextBytes(b_arr)
        b_arr.iterator().forEachRemaining {
            assertTrue(
                it <= Byte.MAX_VALUE &&
                        it >= Byte.MIN_VALUE
            )
        }

        //java.security.SecureRandom pour les nombres aléatoires
        //utilisé en cryptographie
        val secure_generator = SecureRandom()
        //le générateur génère sa propre tete de liste sur 16 octets
        secure_generator.setSeed(secure_generator.generateSeed(16))
        val sec_b_arr = ByteArray(128)
        secure_generator.nextBytes(sec_b_arr)
        sec_b_arr.iterator().forEachRemaining {
            assertTrue(
                it <= java.lang.Byte.MAX_VALUE &&
                        it >= java.lang.Byte.MIN_VALUE
            )
        }

        //java.math.BigDecimal java.math.BigInteger
        //pour travailler sur des grandes valeurs.
        //calcule de la factorielle de 1000
        var total = BigInteger.valueOf(1)
        (2..1000).forEach {
            total = total.multiply(BigInteger.valueOf(it.toLong()))
        }
        assertTrue(total.toString().length == 2568)
    }

}