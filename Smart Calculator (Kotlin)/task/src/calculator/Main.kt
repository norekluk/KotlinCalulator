package calculator

import java.math.BigInteger
import java.util.Stack
import kotlin.math.pow

const val MINUS = '-'
const val PLUS = '+'
const val ONE = "1"
val MINUSES = "-+".toRegex()
const val COMMAND_START = "/"
const val UNKNOWN_VARIABLE = "Unknown variable"
const val INVALID_ASSIGNMENT = "Invalid assignment"
const val INVALID_IDENTIFIER = "Invalid identifier"
const val INVALID_EXPRESSION = "Invalid expression"
const val STORE_FORMAT_RAW = """\s*[a-zA-Z0-9]+(\s*=\s*[-+]*[a-zA-Z0-9]+)+\s*"""
val STORE_FORMAT = STORE_FORMAT_RAW.toRegex()
const val OPERATIONS_FORMAT_RAW =
    """[\s(]*[+-]*[a-zA-Z0-9]+(?:[\s()]*([+-]+|[\/\*\^])[\s()]*[+-]*[a-zA-Z0-9]+[\s()]*)*[\s()]*"""
val OPERATIONS_FORMAT = OPERATIONS_FORMAT_RAW.toRegex()
val EXPRESSION_FORMAT = "$OPERATIONS_FORMAT_RAW|$STORE_FORMAT_RAW".toRegex()
val IDENTIFIER_FORMAT = """[a-zA-Z]+""".toRegex()
val VALUE_FORMAT = """[-+]*([a-zA-Z]+|[0-9]+)""".toRegex()
val OPERATIONS = """[+-]+|[/*^]""".toRegex()
val SPLIT_ON_OPERANDS = "(?<=[+-/*^()])|(?=[+-/*^()])".toRegex()
const val PARENTHESIS_LEFT = "("
const val PARENTHESIS_RIGHT = ")"
val PARENTHESIS = """[$PARENTHESIS_LEFT$PARENTHESIS_RIGHT]""".toRegex()
val STORED_VALUES = mutableMapOf<String, String>()
val operationPrecedence = mapOf("+" to 1, "-" to 1, "*" to 2, "/" to 2, "^" to 3)

fun BigInteger.pow(b: BigInteger): BigInteger {
    return this.pow(b)
}

fun String.toMathematicalOperator(): BigInteger.(BigInteger) -> BigInteger = when (this) {
    "+" -> BigInteger::plus
    "-" -> BigInteger::minus
    "*" -> BigInteger::times
    "/" -> BigInteger::div
    "^" -> BigInteger::pow
    else -> error("Unknown operator $this")
}

fun main() {
    calculator()
}

fun String.replaceConsecutiveMinusesWithTheirValue(): String {
    var line = this
    val indices = MINUSES.findAll(line).map {
        it.range
    }.toList()
    var offset = 0
    for (item in indices) {
        var replacement = MINUS
        if (item.count() % 2 == 0) replacement = PLUS
        line = line.replaceRange(item.first - offset..item.last - offset, replacement.toString())
        offset += item.count() - 1
    }
    return line
}

fun calculator() {
    while (true) {
        val input = readln()
        if (input.isEmpty()) {
            continue
        } else if (input.startsWith(COMMAND_START)) {
            when (input) {
                "/exit" -> {
                    println("Bye!")
                    return
                }

                "/help" -> {
                    println(
                        "The program calculates the sum of numbers. You can use standard mathematical " +
                                "operators for addition '-' and subtraction '-'. You can also chain these operation" +
                                " such as '----', which will evaluate to '+' or '---', which will evaluate to '-'." +
                                "You can also use division '/', multiplication '*', power '^' and enclose  expressions in " +
                                "Parenthesis '()' to separate them."
                    )
                }

                else -> {
                    println("Unknown command")
                }
            }
        } else {
            if (!EXPRESSION_FORMAT.matches(input)) {
                println(INVALID_EXPRESSION)
                continue
            } else if (STORE_FORMAT.matches(input)) {
                val line = input.trim().replace("\\s+".toRegex(), "").split("=")
                storeValue(line)
            } else if (OPERATIONS_FORMAT.matches(input)) {
                if (VALUE_FORMAT.matches(input)) {
                    evaluateSingleExpression(input)
                } else {
                    val line = input.trim().replace("\\s+".toRegex(), "")
                        .replace("\\++".toRegex(), "+")
                        .replaceConsecutiveMinusesWithTheirValue()
                        .split(SPLIT_ON_OPERANDS)
                    println(evaluateExpression(line))
                }
            }

        }
    }
}

fun storeValue(line: List<String>) {
    if (line.size > 2) {
        println(INVALID_ASSIGNMENT)
        return
    }
    val identifier = line[0]
    val value = line[1]
    if (!IDENTIFIER_FORMAT.matches(identifier)) {
        println(INVALID_IDENTIFIER)
        return
    }
    if (!VALUE_FORMAT.matches(value)) {
        println(INVALID_ASSIGNMENT)
        return
    }
    // right side is variable
    if (IDENTIFIER_FORMAT.matches(value)) {
        if (STORED_VALUES.contains(value)) {
            STORED_VALUES[identifier] = STORED_VALUES[value]!!
        } else {
            println(UNKNOWN_VARIABLE)
            return
        }
    } else {
        STORED_VALUES[identifier] = value
    }

}

fun evaluateSingleExpression(expression: String) {
    val soleNumberOrVariable = expression.toIntOrNull()
    if (soleNumberOrVariable != null) {
        println(soleNumberOrVariable)
    } else {
        if (!IDENTIFIER_FORMAT.matches(expression)) {
            println(INVALID_IDENTIFIER)
            return
        }
        println(STORED_VALUES.getOrDefault(expression, UNKNOWN_VARIABLE))
    }
}

fun evaluateExpression(line: List<String>): String {
    try {
        val postFixExpression = createPosFixNotationExpression(line)
        val resultStack = Stack<BigInteger>()
        for (element in postFixExpression) {
            val elemOrNull = element.toBigIntegerOrNull()
            if (elemOrNull != null) {
                resultStack.push(elemOrNull)
            } else if (IDENTIFIER_FORMAT.matches(element)) {
                if (STORED_VALUES.contains(element)) {
                    resultStack.push(STORED_VALUES[element]!!.toBigInteger())
                } else {
                    return UNKNOWN_VARIABLE
                }
            } else if (OPERATIONS.matches(element)) {
                val b = resultStack.pop()
                val a = resultStack.pop()
                val result = element.toMathematicalOperator()(a, b)
                resultStack.push(result)
            }
        }
        return resultStack.peek().toString()
    } catch (e: IllegalArgumentException) {
        return INVALID_EXPRESSION
    }
}

fun createPosFixNotationExpression(expression: List<String>): List<String> {
    val resultStack = Stack<String>()
    val operatorStack = Stack<String>()
    for (word in expression) {
        if (OPERATIONS.matches(word)) {
            if (operatorStack.isEmpty() || operatorStack.peek() == PARENTHESIS_LEFT) {
                operatorStack.push(word)
                continue
            }
            if (operationPrecedence[word]!! > operationPrecedence[operatorStack.peek()]!!) {
                operatorStack.push(word)
            } else {
                while (!operatorStack.isEmpty() && operatorStack.peek() != PARENTHESIS_LEFT && operationPrecedence[operatorStack.peek()]!! >= operationPrecedence[word]!!) {
                    resultStack.push(operatorStack.pop())
                }
                operatorStack.push(word)
            }

        } else if (PARENTHESIS.matches(word)) {
            when (word) {
                PARENTHESIS_LEFT -> {
                    operatorStack.push(word)
                }

                PARENTHESIS_RIGHT -> {
                    if (!operatorStack.contains(PARENTHESIS_LEFT)) {
                        throw IllegalArgumentException("Missing ( in expression")
                    }
                    while (operatorStack.peek() != PARENTHESIS_LEFT) {
                        resultStack.push(operatorStack.pop())
                    }
                    operatorStack.pop()
                }
            }
        } else if (VALUE_FORMAT.matches(word)) {
            resultStack.push(word)
        }
    }
    if (operatorStack.contains(PARENTHESIS_LEFT)) {
        throw IllegalArgumentException("Missing ) in expression")
    }
    resultStack.addAll(operatorStack.reversed())
    return resultStack.toList()
}