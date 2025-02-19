package com.example.demo

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.PostMapping

// PDF
import org.apache.pdfbox.pdmodel.PDDocument
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

// Converter

@Controller
class HelloController {

    @GetMapping("/") // "/"へのGETリクエストをindex.htmlにマッピング
    fun index(): String {
        return "index" // index.htmlを返す
    }

    @PostMapping("/hello")
    fun hello(
        @RequestParam("pdfFile") pdfFile: MultipartFile,
        @RequestParam("pageNumbers") pageNumbers: String,
        @RequestParam("replacements") replacements: String,
        @RequestParam("delimiterRegex") delimiterRegex: String, // 追加
        @RequestParam("newDelimiter") newDelimiter: String, // 追加
        @RequestParam("divisor") divisor: Int, // 追加
        model: Model
    ): String {
        val extractedText = try {
            extractTextFromPdf(pdfFile, pageNumbers)
        } catch (e: IOException) {
            "Error extracting text: ${e.message}"
        }

        val converter = StringConverter()

        if (replacements.isNotBlank()) { // replacementsが空でない場合に処理
            replacements.split(";").forEach { replacement ->
                val parts = replacement.split("=")
                converter.addReplacement(parts[0], parts[1])
            }
        }
        var convertedText = converter.convert(extractedText)

        // 数字を割る
        if (divisor != 1) {
            convertedText = converter.divideNumbers(convertedText, divisor)
        }

        // CSV形式に変換
        val csvText = converter.convertToCsv(convertedText, delimiterRegex, newDelimiter) // 新しい変数に代入

        //model.addAttribute("extractedText", extractedText)
        //model.addAttribute("extractedText", convertedText)
        model.addAttribute("extractedText", csvText)
        return "result"
    }

    private fun extractTextFromPdf(pdfFile: MultipartFile, pageNumbers: String): String {
        val document = PDDocument.load(pdfFile.inputStream)
        val pages = pageNumbers.split(",").map { it.trim().toInt() - 1 }
        val extractedText = StringBuilder()
        for (page in pages) {
            if (page in 0 until document.numberOfPages) {
                val stripper = org.apache.pdfbox.text.PDFTextStripper()
                stripper.startPage = page + 1 // startPageを設定
                stripper.endPage = page + 1 // endPageを設定
                extractedText.append(stripper.getText(document).trim())
                extractedText.append("\n\n") 
            } else {
                extractedText.append("Invalid page number: ${page + 1}\n\n")
            }
        }
        document.close()
        return extractedText.toString()
    }

}