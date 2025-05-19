// DOM Elements
const calculatorTab = document.getElementById('calculator');
const converterTab = document.getElementById('converter');
const tabButtons = document.querySelectorAll('.tab-button');

const expressionDisplay = document.getElementById('expression');
const resultDisplay = document.getElementById('result');

const basicModeBtn = document.getElementById('basicModeBtn');
const scientificModeBtn = document.getElementById('scientificModeBtn');
const scientificKeypad = document.getElementById('scientificKeypad');

const historyBtn = document.getElementById('historyBtn');
const historyPanel = document.getElementById('historyPanel');
const closeHistoryBtn = document.getElementById('closeHistoryBtn');
const historyList = document.getElementById('historyList');
const exportHistoryBtn = document.getElementById('exportHistoryBtn');
const clearHistoryBtn = document.getElementById('clearHistoryBtn');

const themeToggleBtn = document.getElementById('themeToggleBtn');

const voiceInputBtn = document.getElementById('voiceInputBtn');
const voiceModal = document.getElementById('voiceModal');
const closeVoiceModalBtn = document.getElementById('closeVoiceModalBtn');
const startVoiceBtn = document.getElementById('startVoiceBtn');
const voiceStatus = document.getElementById('voiceStatus');
const voiceResult = document.getElementById('voiceResult');

const converterType = document.getElementById('converterType');
const converterValue = document.getElementById('converterValue');
const fromUnit = document.getElementById('fromUnit');
const toUnit = document.getElementById('toUnit');
const convertBtn = document.getElementById('convertBtn');
const conversionResult = document.getElementById('conversionResult');

// State
let currentExpression = '';
let lastResult = null;
let isScientificMode = false;
let isDarkTheme = false;
let calculationHistory = [];

// Initialize the app
function init() {
    loadHistory();
    setupEventListeners();
    updateConverterUnits();
    checkThemePreference();
}

// Event Listeners
function setupEventListeners() {
    // Tab switching
    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            const tabName = button.getAttribute('data-tab');
            switchTab(tabName);
        });
    });

    // Calculator mode toggle
    basicModeBtn.addEventListener('click', () => toggleCalculatorMode(false));
    scientificModeBtn.addEventListener('click', () => toggleCalculatorMode(true));

    // Calculator buttons
    document.querySelectorAll('.calc-button').forEach(button => {
        button.addEventListener('click', () => handleCalculatorButtonClick(button.getAttribute('data-value')));
    });

    // History panel
    historyBtn.addEventListener('click', toggleHistoryPanel);
    closeHistoryBtn.addEventListener('click', toggleHistoryPanel);
    exportHistoryBtn.addEventListener('click', exportHistory);
    clearHistoryBtn.addEventListener('click', clearHistory);

    // Theme toggle
    themeToggleBtn.addEventListener('click', toggleTheme);

    // Voice input
    voiceInputBtn.addEventListener('click', toggleVoiceModal);
    closeVoiceModalBtn.addEventListener('click', toggleVoiceModal);
    startVoiceBtn.addEventListener('click', startVoiceRecognition);

    // Converter
    converterType.addEventListener('change', updateConverterUnits);
    convertBtn.addEventListener('click', performConversion);
}

// Tab Switching
function switchTab(tabName) {
    // Hide all tab content
    document.querySelectorAll('.tab-content').forEach(tab => {
        tab.classList.remove('active');
    });
    
    // Deactivate all tab buttons
    tabButtons.forEach(button => {
        button.classList.remove('active');
    });
    
    // Activate selected tab and button
    document.getElementById(tabName).classList.add('active');
    document.querySelector(`[data-tab="${tabName}"]`).classList.add('active');
}

// Calculator Mode Toggle
function toggleCalculatorMode(scientific) {
    isScientificMode = scientific;
    
    if (scientific) {
        basicModeBtn.classList.remove('active');
        scientificModeBtn.classList.add('active');
        scientificKeypad.classList.add('active');
    } else {
        basicModeBtn.classList.add('active');
        scientificModeBtn.classList.remove('active');
        scientificKeypad.classList.remove('active');
    }
}

// Calculator Button Click Handler
function handleCalculatorButtonClick(value) {
    switch (value) {
        case 'C':
            // Clear last character
            currentExpression = currentExpression.slice(0, -1);
            break;
        case 'AC':
            // Clear all
            currentExpression = '';
            resultDisplay.textContent = '0';
            break;
        case '=':
            // Calculate result
            calculateResult();
            break;
        case 'sin':
        case 'cos':
        case 'tan':
        case 'log':
        case 'ln':
        case 'sqrt':
            // Add function with opening parenthesis
            currentExpression += `${value}(`;
            break;
        default:
            // Add value to expression
            currentExpression += value;
    }
    
    // Update display
    expressionDisplay.textContent = currentExpression;
}

// Calculate Result
function calculateResult() {
    if (!currentExpression) return;
    
    try {
        // Process the expression
        let processedExpression = currentExpression
            .replace(/×/g, '*')
            .replace(/÷/g, '/')
            .replace(/π/g, 'Math.PI')
            .replace(/e/g, 'Math.E')
            .replace(/sin\(/g, 'Math.sin(')
            .replace(/cos\(/g, 'Math.cos(')
            .replace(/tan\(/g, 'Math.tan(')
            .replace(/log\(/g, 'Math.log10(')
            .replace(/ln\(/g, 'Math.log(')
            .replace(/sqrt\(/g, 'Math.sqrt(')
            .replace(/\^/g, '**');
        
        // Evaluate the expression
        const result = eval(processedExpression);
        
        // Display the result
        resultDisplay.textContent = formatResult(result);
        lastResult = result;
        
        // Add to history
        addToHistory(currentExpression, result);
        
    } catch (error) {
        resultDisplay.textContent = 'Error';
    }
}

// Format Result
function formatResult(result) {
    if (Number.isInteger(result)) {
        return result.toString();
    } else {
        return parseFloat(result.toFixed(8)).toString();
    }
}

// History Functions
function addToHistory(expression, result) {
    const timestamp = new Date().toLocaleString();
    const historyItem = {
        expression,
        result: formatResult(result),
        timestamp
    };
    
    calculationHistory.unshift(historyItem);
    saveHistory();
    updateHistoryDisplay();
}

function updateHistoryDisplay() {
    if (calculationHistory.length === 0) {
        historyList.innerHTML = '<div class="empty-history">No calculations yet</div>';
        return;
    }
    
    historyList.innerHTML = '';
    calculationHistory.forEach(item => {
        const historyItemElement = document.createElement('div');
        historyItemElement.className = 'history-item';
        historyItemElement.innerHTML = `
            <div class="history-expression">${item.expression}</div>
            <div class="history-result">${item.result}</div>
            <div class="history-timestamp">${item.timestamp}</div>
        `;
        
        historyItemElement.addEventListener('click', () => {
            currentExpression = item.expression;
            expressionDisplay.textContent = currentExpression;
            resultDisplay.textContent = item.result;
            toggleHistoryPanel();
        });
        
        historyList.appendChild(historyItemElement);
    });
}

function toggleHistoryPanel() {
    historyPanel.classList.toggle('active');
}

function saveHistory() {
    localStorage.setItem('calculatorHistory', JSON.stringify(calculationHistory));
}

function loadHistory() {
    const savedHistory = localStorage.getItem('calculatorHistory');
    if (savedHistory) {
        calculationHistory = JSON.parse(savedHistory);
        updateHistoryDisplay();
    }
}

function clearHistory() {
    calculationHistory = [];
    saveHistory();
    updateHistoryDisplay();
}

function exportHistory() {
    if (calculationHistory.length === 0) {
        alert('No history to export');
        return;
    }
    
    let csvContent = 'Expression,Result,Timestamp\n';
    calculationHistory.forEach(item => {
        csvContent += `"${item.expression}","${item.result}","${item.timestamp}"\n`;
    });
    
    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `calculator_history_${new Date().toISOString().slice(0, 10)}.csv`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
}

// Theme Toggle
function toggleTheme() {
    isDarkTheme = !isDarkTheme;
    document.body.classList.toggle('dark-theme', isDarkTheme);
    themeToggleBtn.querySelector('.material-icons').textContent = isDarkTheme ? 'light_mode' : 'dark_mode';
    localStorage.setItem('darkTheme', isDarkTheme);
}

function checkThemePreference() {
    const savedTheme = localStorage.getItem('darkTheme');
    if (savedTheme === 'true') {
        isDarkTheme = true;
        document.body.classList.add('dark-theme');
        themeToggleBtn.querySelector('.material-icons').textContent = 'light_mode';
    }
}

// Voice Input
function toggleVoiceModal() {
    voiceModal.classList.toggle('active');
    if (voiceModal.classList.contains('active')) {
        voiceResult.textContent = '';
        voiceStatus.textContent = 'Click "Start Listening" to begin';
    }
}

function startVoiceRecognition() {
    if (!('webkitSpeechRecognition' in window) && !('SpeechRecognition' in window)) {
        voiceStatus.textContent = 'Speech recognition not supported in this browser';
        return;
    }
    
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    const recognition = new SpeechRecognition();
    
    recognition.lang = 'en-US';
    recognition.continuous = false;
    recognition.interimResults = false;
    
    recognition.onstart = () => {
        voiceStatus.textContent = 'Listening...';
        startVoiceBtn.disabled = true;
    };
    
    recognition.onresult = (event) => {
        const transcript = event.results[0][0].transcript;
        voiceResult.textContent = transcript;
        
        // Process voice input
        processVoiceInput(transcript);
    };
    
    recognition.onerror = (event) => {
        voiceStatus.textContent = `Error: ${event.error}`;
        startVoiceBtn.disabled = false;
    };
    
    recognition.onend = () => {
        voiceStatus.textContent = 'Finished listening';
        startVoiceBtn.disabled = false;
    };
    
    recognition.start();
}

function processVoiceInput(input) {
    // Clean up the input
    const cleanInput = input.toLowerCase()
        .replace("what's", "")
        .replace("what is", "")
        .replace("calculate", "")
        .replace("equals", "")
        .replace("times", "*")
        .replace("multiplied by", "*")
        .replace("plus", "+")
        .replace("minus", "-")
        .replace("divided by", "/")
        .replace("x", "*")
        .trim();
    
    // Set the expression
    currentExpression = cleanInput;
    expressionDisplay.textContent = currentExpression;
    
    // Calculate the result
    calculateResult();
    
    // Close the modal after a delay
    setTimeout(() => {
        toggleVoiceModal();
    }, 2000);
}

// Converter Functions
function updateConverterUnits() {
    const type = converterType.value;
    let units = [];
    
    switch (type) {
        case 'currency':
            units = ['USD', 'EUR', 'GBP', 'JPY', 'CAD', 'AUD', 'CNY', 'INR'];
            break;
        case 'length':
            units = ['Meter', 'Kilometer', 'Centimeter', 'Millimeter', 'Mile', 'Yard', 'Foot', 'Inch'];
            break;
        case 'weight':
            units = ['Kilogram', 'Gram', 'Milligram', 'Metric Ton', 'Pound', 'Ounce', 'Stone'];
            break;
        case 'temperature':
            units = ['Celsius', 'Fahrenheit', 'Kelvin'];
            break;
    }
    
    // Update from unit dropdown
    fromUnit.innerHTML = '';
    units.forEach(unit => {
        const option = document.createElement('option');
        option.value = unit;
        option.textContent = unit;
        fromUnit.appendChild(option);
    });
    
    // Update to unit dropdown
    toUnit.innerHTML = '';
    units.forEach(unit => {
        const option = document.createElement('option');
        option.value = unit;
        option.textContent = unit;
        toUnit.appendChild(option);
    });
    
    // Set default "to" unit to the second option if available
    if (units.length > 1) {
        toUnit.value = units[1];
    }
}

function performConversion() {
    const value = parseFloat(converterValue.value);
    if (isNaN(value)) {
        conversionResult.textContent = 'Please enter a valid number';
        return;
    }
    
    const from = fromUnit.value;
    const to = toUnit.value;
    const type = converterType.value;
    
    let result;
    switch (type) {
        case 'currency':
            result = convertCurrency(value, from, to);
            break;
        case 'length':
            result = convertLength(value, from, to);
            break;
        case 'weight':
            result = convertWeight(value, from, to);
            break;
        case 'temperature':
            result = convertTemperature(value, from, to);
            break;
    }
    
    conversionResult.textContent = `${value} ${from} = ${result} ${to}`;
}

// Conversion Functions
function convertCurrency(value, from, to) {
    // Simple conversion rates (in a real app, these would come from an API)
    const rates = {
        'USD': 1.0,
        'EUR': 0.85,
        'GBP': 0.73,
        'JPY': 110.0,
        'CAD': 1.25,
        'AUD': 1.35,
        'CNY': 6.45,
        'INR': 74.5
    };
    
    // Convert to USD first, then to target currency
    const inUSD = value / rates[from];
    const result = inUSD * rates[to];
    
    return parseFloat(result.toFixed(2));
}

function convertLength(value, from, to) {
    // Conversion factors to meters
    const factors = {
        'Meter': 1,
        'Kilometer': 1000,
        'Centimeter': 0.01,
        'Millimeter': 0.001,
        'Mile': 1609.34,
        'Yard': 0.9144,
        'Foot': 0.3048,
        'Inch': 0.0254
    };
    
    // Convert to meters first, then to target unit
    const inMeters = value * factors[from];
    const result = inMeters / factors[to];
    
    return parseFloat(result.toFixed(4));
}

function convertWeight(value, from, to) {
    // Conversion factors to kilograms
    const factors = {
        'Kilogram': 1,
        'Gram': 0.001,
        'Milligram': 0.000001,
        'Metric Ton': 1000,
        'Pound': 0.453592,
        'Ounce': 0.0283495,
        'Stone': 6.35029
    };
    
    // Convert to kilograms first, then to target unit
    const inKilograms = value * factors[from];
    const result = inKilograms / factors[to];
    
    return parseFloat(result.toFixed(4));
}

function convertTemperature(value, from, to) {
    let result;
    
    // Convert to Celsius first
    let celsius;
    switch (from) {
        case 'Celsius':
            celsius = value;
            break;
        case 'Fahrenheit':
            celsius = (value - 32) * 5/9;
            break;
        case 'Kelvin':
            celsius = value - 273.15;
            break;
    }
    
    // Convert from Celsius to target unit
    switch (to) {
        case 'Celsius':
            result = celsius;
            break;
        case 'Fahrenheit':
            result = celsius * 9/5 + 32;
            break;
        case 'Kelvin':
            result = celsius + 273.15;
            break;
    }
    
    return parseFloat(result.toFixed(2));
}

// Initialize the app
document.addEventListener('DOMContentLoaded', init);
