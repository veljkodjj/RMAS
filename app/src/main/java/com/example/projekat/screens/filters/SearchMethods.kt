package com.example.projekat.screens.filters

import com.example.projekat.model.Kladionice

fun searchKladioniceByDescription(
    kladionice: MutableList<Kladionice>,
    query: String
):List<Kladionice>{
    val regex = query.split(" ").joinToString(".*"){
        Regex.escape(it)
    }.toRegex(RegexOption.IGNORE_CASE)
    return kladionice.filter { kladionica ->
        regex.containsMatchIn(kladionica.description)
    }
}