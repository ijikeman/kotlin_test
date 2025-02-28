package com.example.demo

class StringConverter {
    private val replacements = mutableMapOf<String, String>()
    
    // 文字列の置換を追加する
    fun addReplacement(oldString: String, newString: String) {
        replacements[oldString] = newString
    }

    // 文字列を置換する
    fun convert(text: String): String {
        var result = text
        for ((oldString, newSTring) in replacements) {
            result = result.replace(oldString, newSTring)
        }
        return result
    }

    // テキストをCSV形式に変換する
    fun convertToCsv(text: String, delimiterRegex: String, newDelimiter: String): String {
        val lines = text.split("\n")
        val convertedLines = lines.map { line ->
            line.split(Regex(delimiterRegex)).joinToString(newDelimiter)
        }
        return convertedLines.joinToString("\n")
    }

    // 数字を指定した除数で割る
    fun divideNumbers(text: String, divisor: Int): String {
        val pattern = Regex("^(.+)(\\s+(-?\\d+)\\s+(-?\\d+))$") // グループ分けを修正
        return text.split("\n").joinToString("\n") { line ->
            pattern.find(line)?.let { matchResult ->
                val prefix = matchResult.groupValues[1] // 1番目のグループ（prefix）を取得
                val num1 = matchResult.groupValues[3] // 3番目のグループ（num1）を取得
                val num2 = matchResult.groupValues[4] // 4番目のグループ（num2）を取得
                val dividedNum1 = num1.toIntOrNull()?.div(divisor) ?: num1
                val dividedNum2 = num2.toIntOrNull()?.div(divisor) ?: num2
                "$prefix $dividedNum1 $dividedNum2" // prefix を含めて出力
            } ?: line
        }
    }

    // 特定の文字列で始まる行を削除する
    fun removeLinesStartingWith(text: String, targets: String): String {
        val targetList = targets.split(",")
        return text.split("\n")
            .filterNot { line -> targetList.any { target -> line.contains(target) } }
            .joinToString("\n")
    }
}
