package com.example.progettowoc.programs.data

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.readCsv
import org.jetbrains.kotlinx.dataframe.io.readExcel


interface ProgramParserInterface {
    fun toListWeek(context: Context, programSheet: Uri): List<Week>
    fun toProgramSpreadSheet(context: Context, program: ProgramSheet, uri: Uri)
}


object ProgramParser: ProgramParserInterface {

    override fun toListWeek(context: Context, programSheet: Uri): List<Week> {
        // qui prendiamo il formato mime type
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(programSheet) ?: ""
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)

        val df = contentResolver.openInputStream(programSheet)?.use { inputStream ->
            when (extension) {
                "csv" -> DataFrame.readCsv(inputStream)

                "xls", "xlsx" -> DataFrame.readExcel(inputStream)

                else -> error("Formato non supportato")
            }
        }


        // qui creo la lista
        val weeks = mutableListOf<Week>()
        for (weekCol in df?.columns().orEmpty()) {
            val colName = weekCol.name().trim()

            if (!colName.startsWith("week", ignoreCase = true)) continue
            val weekNumber = colName.filter { it.isDigit() }.toIntOrNull() ?: continue

            val currentWeekDays = mutableListOf<Day>()
            var currentDayNumber = 0
            var currentExercises = mutableListOf<Exercise>()

            for (cell in weekCol.values()) {
                val value = cell?.toString()?.trim().orEmpty()

                when {
                    value.startsWith("day", ignoreCase = true) -> {
                        if (currentDayNumber != 0) {
                            currentWeekDays.add(
                                Day(
                                    number = currentDayNumber,
                                    exercises = currentExercises.toList(),
                                    isCompleted = false
                                )
                            )
                        }
                        currentDayNumber = value.filter { it.isDigit() }.toIntOrNull() ?: continue
                        currentExercises = mutableListOf()
                    }

                    value.isNotBlank() -> {
                        currentExercises.add(parseExercise(value))
                    }
                }
            }

            if (currentDayNumber != 0) {
                currentWeekDays.add(
                    Day(number = currentDayNumber, isCompleted = false, exercises = currentExercises)
                )
            }

            weeks.add(Week(weekNumber, currentWeekDays))
        }

        return weeks
    }


    private fun parseExercise(value: String): Exercise {

        val clienteComment = if (value.contains("[") && value.contains("]")) {
            value.substringAfter("[").substringBefore("]").trim()
        } else {
            ""
        }

        val coachComment = if (value.contains("(") && value.contains(")")) {
            value.substringAfter("(").substringBefore(")").trim()
        } else {
            ""
        }

        val valueNoClienteComment = if (clienteComment.isNotBlank()) {
            val prima = value.substringBefore("[")
            val dopo = value.substringAfter("]")
            "$prima $dopo"
        } else {
            value
        }

        val valueNoComments = if (coachComment.isNotBlank()) {
            val prima = valueNoClienteComment.substringBefore("(")
            val dopo = valueNoClienteComment.substringAfter(")")
            "$prima $dopo"
        } else {
            valueNoClienteComment
        }

        val parts = valueNoComments.trim().split(Regex("\\s+")).filter { it.isNotBlank() }

        val restPart = parts.firstOrNull { it.startsWith("rest:", ignoreCase = true) }
        val kgPart = parts.firstOrNull { it.startsWith("kg:", ignoreCase = true) }

        val rest = restPart?.substringAfter(":")?.toIntOrNull() ?: 0
        val weight = kgPart?.substringAfter(":")?.toFloatOrNull() ?: 0f

        val remainingParts = parts.filter { it != restPart && it != kgPart }
        val setsRepsPart = remainingParts.firstOrNull { it.contains("x", ignoreCase = true) }
        val name = remainingParts.filter { it != setsRepsPart }.joinToString(" ")

        val setsRepsParts = setsRepsPart?.split("x", ignoreCase = true)
        val sets = setsRepsParts?.getOrNull(0)?.toIntOrNull() ?: 0
        val reps = setsRepsParts?.getOrNull(1)?.toIntOrNull() ?: 0

        return Exercise(
            name = name,
            sets = sets,
            reps = reps,
            rest = rest,
            weight = weight,
            coachComment = coachComment,
            clienteComment = clienteComment
        )
    }


    override fun toProgramSpreadSheet(context: Context, program: ProgramSheet, uri: Uri) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("programma")

        val maxRows = program.weeks.maxOf { week ->
            week.days.sumOf { it.exercises.size + 1 } + week.days.size
        }

        val rows = (0..maxRows).map { sheet.createRow(it) }

        program.weeks.forEachIndexed { colIndex, week ->
            var rowIndex = 0
            rows[rowIndex++].createCell(colIndex).setCellValue("week ${week.number}")

            week.days.forEach { day ->
                rows[rowIndex++].createCell(colIndex).setCellValue("day ${day.number}")
                day.exercises.forEach { exercise ->
                    val exerciseStr = buildString {
                        append("${exercise.name} ${exercise.sets}x${exercise.reps} rest:${exercise.rest}")
                        if (exercise.weight > 0f) append(" kg:${exercise.weight}")
                        if (exercise.coachComment.isNotBlank()) append(" (${exercise.coachComment})")
                        if (exercise.clienteComment.isNotBlank()) append(" [${exercise.clienteComment}]")
                    }
                    rows[rowIndex++].createCell(colIndex).setCellValue(exerciseStr)
                }
            }
        }

        context.contentResolver.openOutputStream(uri)?.use { workbook.write(it) }
        workbook.close()
    }
}