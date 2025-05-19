package com.skillcraft.calculator.utils

import kotlin.math.*

class ExpressionEvaluator {
    
    fun evaluate(expression: String): Double {
        // Replace mathematical symbols with their Java equivalents
        val processedExpression = expression
            .replace("×", "*")
            .replace("÷", "/")
            .replace("π", Math.PI.toString())
            .replace("e", Math.E.toString())
            .replace("%", "/100")
        
        return evaluateExpression(processedExpression)
    }
    
    private fun evaluateExpression(expression: String): Double {
        val tokens = tokenize(expression)
        return evaluateTokens(tokens)
    }
    
    private fun tokenize(expression: String): List<String> {
        val result = mutableListOf<String>()
        var i = 0
        
        while (i < expression.length) {
            val c = expression[i]
            
            when {
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < expression.length && (expression[i].isDigit() || expression[i] == '.')) {
                        sb.append(expression[i])
                        i++
                    }
                    result.add(sb.toString())
                    continue
                }
                c.isLetter() -> {
                    val sb = StringBuilder()
                    while (i < expression.length && expression[i].isLetter()) {
                        sb.append(expression[i])
                        i++
                    }
                    result.add(sb.toString())
                    continue
                }
                c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == '(' || c == ')' -> {
                    result.add(c.toString())
                }
            }
            i++
        }
        
        return result
    }
    
    private fun evaluateTokens(tokens: List<String>): Double {
        if (tokens.isEmpty()) return 0.0
        
        val values = mutableListOf<Double>()
        val operators = mutableListOf<String>()
        
        var i = 0
        while (i < tokens.size) {
            val token = tokens[i]
            
            when {
                token == "(" -> {
                    // Find the matching closing parenthesis
                    val subExpr = findSubExpression(tokens, i)
                    values.add(evaluateTokens(subExpr))
                    i += subExpr.size + 2 // Skip the sub-expression and parentheses
                    continue
                }
                token == "sin" || token == "cos" || token == "tan" || token == "log" || token == "ln" || token == "√" -> {
                    // Handle functions
                    if (i + 1 < tokens.size && tokens[i + 1] == "(") {
                        val subExpr = findSubExpression(tokens, i + 1)
                        val arg = evaluateTokens(subExpr)
                        values.add(applyFunction(token, arg))
                        i += subExpr.size + 3 // Skip the function name, sub-expression, and parentheses
                        continue
                    }
                }
                token.toDoubleOrNull() != null -> {
                    values.add(token.toDouble())
                }
                token == "+" || token == "-" || token == "*" || token == "/" || token == "^" -> {
                    while (operators.isNotEmpty() && hasPrecedence(token, operators.last())) {
                        values.add(applyOperator(operators.removeAt(operators.lastIndex), values))
                    }
                    operators.add(token)
                }
            }
            i++
        }
        
        while (operators.isNotEmpty()) {
            values.add(applyOperator(operators.removeAt(operators.lastIndex), values))
        }
        
        return values.last()
    }
    
    private fun findSubExpression(tokens: List<String>, start: Int): List<String> {
        var count = 1
        var end = start + 1
        
        while (end < tokens.size && count > 0) {
            when (tokens[end]) {
                "(" -> count++
                ")" -> count--
            }
            end++
        }
        
        return tokens.subList(start + 1, end - 1)
    }
    
    private fun hasPrecedence(op1: String, op2: String): Boolean {
        if (op2 == "(" || op2 == ")") return false
        if ((op1 == "*" || op1 == "/") && (op2 == "+" || op2 == "-")) return false
        if (op1 == "^" && (op2 == "+" || op2 == "-" || op2 == "*" || op2 == "/")) return false
        return true
    }
    
    private fun applyOperator(operator: String, values: MutableList<Double>): Double {
        if (values.size < 2) return 0.0
        
        val b = values.removeAt(values.lastIndex)
        val a = values.removeAt(values.lastIndex)
        
        return when (operator) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> a / b
            "^" -> a.pow(b)
            else -> 0.0
        }
    }
    
    private fun applyFunction(function: String, arg: Double): Double {
        return when (function) {
            "sin" -> sin(arg)
            "cos" -> cos(arg)
            "tan" -> tan(arg)
            "log" -> log10(arg)
            "ln" -> ln(arg)
            "√" -> sqrt(arg)
            else -> arg
        }
    }
}
