@file:Suppress(
    "RemoveRedundantQualifierName",
    "UNUSED_VARIABLE", "LocalVariableName", "ReplaceGetOrSet",
    "ReplaceNegatedIsEmptyWithIsNotEmpty", "JavaCollectionsStaticMethod",
    "DEPRECATION", "ReplacePutWithAssignment",
    "PLATFORM_CLASS_MAPPED_TO_KOTLIN","UsePropertyAccessSyntax",
)

package playground.programming

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ArrayCollectionTest {
    @Test
    fun `Tableaux, collections, streams`() {
        //Tableau
        //java.util.Arrays définit d'utiles méthodes de manipulation de tableaux, 
        //y compris de tri et de recherche au sein d'un tableau
        val intArray = arrayOf(10, 5, 7, -3)
        //tri le tableau
        Arrays.sort(intArray)
        var pos = Arrays.binarySearch(intArray, 7)
        //la valeur 7 est trouvé a l'index 2
        assertEquals(2, pos)
        //12 pas trouvé retourne une valeur negative
        assert(Arrays.binarySearch(intArray, 12) < 0)

        //les tableaux peuvent également etre triés
        //et faire l'objet d'une recherche
        val stringArray = arrayOf("le", "moment", "c'est")
        assertEquals("c'est", stringArray[2])
        assertEquals("le", stringArray[0])
        assertEquals("moment", stringArray[1])
        Arrays.sort(stringArray)
        assertEquals("c'est", stringArray[0])
        assertEquals("le", stringArray[1])
        assertEquals("moment", stringArray[2])

        //Arrays.equals() compare tous les éléments de deux tableaux
        //Arrays.clone() copie tous les elements du tableau dans un autre
        stringArray.forEachIndexed { i, it -> assertEquals(it, stringArray.clone()[i]) }

        val data = ByteArray(100)
        //Arrays.fill() initialise tous les éléments des deux tableaux
        //initalise tous les éléments à -1
        Arrays.fill(data, -1)
        data.forEach { assertEquals(-1, it) }

        //attribue aux éléments 5, 6, 7, 8 et 9 la valeur -2
        Arrays.fill(data, 5, 10, -2)
        ((5 until (10 - 1))).forEach { assertEquals(-2, data[it]) }

        //récupère le type de data
        val type = data::class.java
        //est ce que data est un tableau?
        assertTrue(type.isArray())
        //est ce que data est un tableau de byte
        assertEquals(Byte::class.java, type.getComponentType())

        //Collection
        val s = java.util.HashSet<String>()
        s.add("test")
        assertTrue(s.contains("test"))
        assertFalse(s.contains("test2"))
        s.remove("test")
        assertFalse(s.contains("test"))

        val ss = TreeSet<String>()
        ss.add("b")
        ss.add("a")
        ss.iterator().forEach { assertTrue(it == "a" || it == "b") }

        //liste doublement chainée
        var dll: List<String> = LinkedList<String>()

        //plus efficace
        val l = java.util.ArrayList<String>()
        l.addAll(ss)
        l.addAll(1, ss)

        val obj = l.get(1)
        val obj_prime = l[1]
        assertEquals(obj, obj_prime)

        l.set(3, "nouvel élément")
        l.add("test")
        l.add(0, "test2")
        l.removeAt(1)
        l.remove("a")
        assertFalse(l.contains("a"))
        l.removeAll(ss)
        assertFalse(l.containsAll(ss))
        assertFalse(l.isEmpty())
        assertTrue(l.isNotEmpty())


        val sublist = l.subList(1, 3)
        val elements = l.toArray()
        l.clear()

        val m = HashMap<String, Integer>()
        m.put("clé", Integer(42))
        m["clé"] = Integer(42)
        val value: Integer = m.get("clé")!!
        assertEquals(Integer(42), value)
        m.remove("clé")
        assertTrue(m.isEmpty())
        val keys = m.keys
        assertTrue(keys.isEmpty())


        val set = HashSet<String>()
        set.add("key_1")
        set.add("key_2")
        set.add("key_3")
        val members = set.toArray()
        assertEquals(3, members.size)
        val list = ArrayList<String>()
        list.add("items1")
        list.add("items2")
        list.add("items3")
        val items = list.toArray()
        assertEquals(3, items.size)

        //trie et recherche d'éléments sur les collections
        list.add("clé")
        //en premier on trie
        Collections.sort(list)
        //en kotlin
        list.sort()
        //en deuxieme on cherche
        //retourne l'index du premier trouvé sinon -1
        pos = Collections.binarySearch(list, "clé")
        assertEquals(0, pos)
        val list1 = mutableListOf(1, 2, 3, 4, 5)
        val list2 = mutableListOf(0, 0, 0, 0, 0)

        //d'autres méthodes intéressantes concernant Collections

        //copie list1 dans list2, 2e parametre dans 1er parametre
        Collections.copy(list2, list1)
        //comparaison de la copy avec filter
        assertTrue(list1.filterIndexed { i: Int, it: Int -> it != list2[i] }.isEmpty())
        //comparaison de la copy avec map
        list1.mapIndexed { index: Int, it: Int -> assertEquals(it, list2[index]) }

        //rempli avec des 0
        Collections.fill(list2, 0)
        assertTrue(list2.none { it != 0 })

        //le maximum
        assertEquals(5, Collections.max(list1))

        //le minimum
        assertEquals(1, Collections.min(list1))

        //renverse
        Collections.reverse(list)
        listOf("items3", "items2", "items1", "clé").mapIndexed { i: Int, it: String -> assertEquals(it, list[i]) }

        //mélange la list
        Collections.shuffle(list)

        //retourne un ensemble immuable possédant un seul élément 0
        Collections.singleton(0)
        //renvoi un emballage immuable autour d'une liste
        Collections.unmodifiableList(list)
        //renvoi un emballage synchronisé autour d'une map, ensemble clé valeur
        Collections.synchronizedMap(m)
    }
}