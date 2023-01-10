package data

import org.apache.commons.lang3.StringUtils.stripAccents
import java.text.Normalizer.Form.NFD
import java.text.Normalizer.normalize


/**
 *
 */
fun Array<String>.nameToLoginNormalizer(): Array<String> = map {
    it.lowercase().replace(' ', '.').unaccent()
}.toTypedArray()

/**
 *
 */
@Suppress("unused")
fun Array<String>.nameToLogin(): Array<String> = map {
    stripAccents(it.lowercase().replace(' ', '.'))
}.toTypedArray()

/**
 *
 */
@Suppress("MemberVisibilityCanBePrivate", "SpellCheckingInspection")
fun CharSequence.unaccent(): String = "\\p{InCombiningDiacriticalMarks}+"
    .toRegex()
    .replace(normalize(this, NFD), "")