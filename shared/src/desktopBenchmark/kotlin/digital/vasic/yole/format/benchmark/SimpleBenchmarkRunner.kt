/*#######################################################
 *
 * SPDX-FileCopyrightText: 2025 Milos Vasic
 * SPDX-License-Identifier: Apache-2.0
 *
 * Simple Benchmark Runner - Workaround for KMP JMH issues
 *
 *########################################################*/
package digital.vasic.yole.format.benchmark

import kotlin.system.measureTimeMillis

/**
 * Simple benchmark runner that provides basic performance metrics
 * without requiring JMH integration.
 *
 * This is a workaround for kotlinx.benchmark KMP compatibility issues.
 */
object SimpleBenchmarkRunner {

    private const val WARMUP_ITERATIONS = 3
    private const val MEASUREMENT_ITERATIONS = 10

    data class BenchmarkResult(
        val name: String,
        val averageTimeMs: Double,
        val minTimeMs: Long,
        val maxTimeMs: Long
    )

    private fun runBenchmark(name: String, block: () -> Unit): BenchmarkResult {
        // Warmup
        repeat(WARMUP_ITERATIONS) {
            block()
        }

        // Measure
        val times = (1..MEASUREMENT_ITERATIONS).map {
            measureTimeMillis { block() }
        }

        return BenchmarkResult(
            name = name,
            averageTimeMs = times.average(),
            minTimeMs = times.minOrNull() ?: 0,
            maxTimeMs = times.maxOrNull() ?: 0
        )
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println("============================================")
        println("Yole Parser Performance Benchmarks")
        println("============================================")
        println()
        println("Warmup iterations: $WARMUP_ITERATIONS")
        println("Measurement iterations: $MEASUREMENT_ITERATIONS")
        println()

        val results = mutableListOf<BenchmarkResult>()

        // Markdown Parser Benchmarks
        println("Running Markdown Parser Benchmarks...")
        val mdBench = MarkdownParserBenchmark()
        mdBench.setup()

        results.add(runBenchmark("Markdown: Small document (~1KB)") {
            mdBench.parseSmallDocument()
        })

        results.add(runBenchmark("Markdown: Medium document (~10KB)") {
            mdBench.parseMediumDocument()
        })

        results.add(runBenchmark("Markdown: Large document (~100KB)") {
            mdBench.parseLargeDocument()
        })

        results.add(runBenchmark("Markdown: Complex document") {
            mdBench.parseComplexDocument()
        })

        results.add(runBenchmark("Markdown: HTML conversion") {
            mdBench.convertToHtml()
        })

        println("  ✓ Complete")
        println()

        // Todo.txt Parser Benchmarks
        println("Running Todo.txt Parser Benchmarks...")
        val todoBench = TodoTxtParserBenchmark()
        todoBench.setup()

        results.add(runBenchmark("TodoTxt: Small list (10 tasks)") {
            todoBench.parseSmallList()
        })

        results.add(runBenchmark("TodoTxt: Medium list (100 tasks)") {
            todoBench.parseMediumList()
        })

        results.add(runBenchmark("TodoTxt: Large list (1000 tasks)") {
            todoBench.parseLargeList()
        })

        results.add(runBenchmark("TodoTxt: Complex list") {
            todoBench.parseComplexList()
        })

        println("  ✓ Complete")
        println()

        // CSV Parser Benchmarks
        println("Running CSV Parser Benchmarks...")
        val csvBench = CsvParserBenchmark()
        csvBench.setup()

        results.add(runBenchmark("CSV: Small table (10x5)") {
            csvBench.parseSmallTable()
        })

        results.add(runBenchmark("CSV: Medium table (100x10)") {
            csvBench.parseMediumTable()
        })

        results.add(runBenchmark("CSV: Large table (1000x20)") {
            csvBench.parseLargeTable()
        })

        results.add(runBenchmark("CSV: Complex table") {
            csvBench.parseComplexTable()
        })

        println("  ✓ Complete")
        println()

        // LaTeX Parser Benchmarks
        println("Running LaTeX Parser Benchmarks...")
        val latexBench = LatexParserBenchmark()
        latexBench.setup()

        results.add(runBenchmark("LaTeX: Small document (~2KB)") {
            latexBench.parseSmallDocument()
        })

        results.add(runBenchmark("LaTeX: Medium document (~20KB)") {
            latexBench.parseMediumDocument()
        })

        results.add(runBenchmark("LaTeX: Large document (~200KB)") {
            latexBench.parseLargeDocument()
        })

        results.add(runBenchmark("LaTeX: Complex document") {
            latexBench.parseComplexDocument()
        })

        println("  ✓ Complete")
        println()

        // Print results
        println("============================================")
        println("Benchmark Results")
        println("============================================")
        println()

        results.forEach { result ->
            println("${result.name}")
            println("  Average: ${String.format("%.2f", result.averageTimeMs)} ms")
            println("  Min: ${result.minTimeMs} ms")
            println("  Max: ${result.maxTimeMs} ms")
            println()
        }

        // Check against targets
        println("============================================")
        println("Target Analysis")
        println("============================================")
        println()

        val targets = mapOf(
            "Markdown: Small document (~1KB)" to 10.0,
            "Markdown: Medium document (~10KB)" to 50.0,
            "Markdown: Large document (~100KB)" to 500.0,
            "TodoTxt: Small list (10 tasks)" to 5.0,
            "TodoTxt: Medium list (100 tasks)" to 20.0,
            "TodoTxt: Large list (1000 tasks)" to 150.0,
            "CSV: Small table (10x5)" to 5.0,
            "CSV: Medium table (100x10)" to 30.0,
            "CSV: Large table (1000x20)" to 300.0,
            "LaTeX: Small document (~2KB)" to 40.0,
            "LaTeX: Medium document (~20KB)" to 200.0,
            "LaTeX: Large document (~200KB)" to 2000.0
        )

        var passedCount = 0
        var totalCount = 0

        results.forEach { result ->
            val target = targets[result.name]
            if (target != null) {
                totalCount++
                val passed = result.averageTimeMs <= target
                if (passed) passedCount++

                val status = if (passed) "✓ PASS" else "✗ SLOW"
                val percent = (result.averageTimeMs / target * 100).toInt()
                println("$status ${result.name}: ${String.format("%.2f", result.averageTimeMs)} ms (target: ${target.toInt()} ms, $percent%)")
            }
        }

        println()
        println("Performance Summary: $passedCount/$totalCount benchmarks met targets")
        println()
        println("Baseline metrics established successfully!")
    }
}
