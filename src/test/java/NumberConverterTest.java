import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class NumberConverterTest {

    @Test
    public void testConvertToNumberWithPositiveValue() {
        String words = "Two Thousand Five Hundred";
        String result = NumberConverter.convertToNumber(words);
        assertEquals("2500", result);
    }

    @Test
    public void testConvertToNumberWithNegativeValue() {
        String words = "Minus Two Hundred Fifty";
        String result = NumberConverter.convertToNumber(words);
        assertEquals("-250", result);
    }

    @Test
    public void testConvertToNumberWithFractionalValue() {
        String words = "Twenty Five Point Twenty";
        String result = NumberConverter.convertToNumber(words);
        assertEquals("25.2", result);
    }

    @Test
    public void testConvertToNumberWithLargeValue() {
        String words = "One Hundred Twenty Three Million Four Hundred Fifty Six Thousand Seven Hundred Eighty Nine Point Seventy Eight";
        String result = NumberConverter.convertToNumber(words);
        assertEquals("123456789.78", result);
    }

    @Test
    public void testConvertToNumberWithInvalidInput() {
        // Test with an invalid input
        String words = "InvalidInput";
        String result = NumberConverter.convertToNumber(words);
        assertEquals("Invalid Input (Misspelling or out of Range)", result); // Expected behavior when the input is not a valid number in words.
    }

    @Test
    public void testInvalidInput() {
        // Test with invalid input (not a number)
        assertEquals("Invalid Input (Misspelling or out of Range)", NumberConverter.convertNumber("InvalidInput"));
    }

    @Test
    public void testValueOverflow() {
        // Test with a value that exceeds the maximum allowed value
        assertEquals("Value is too large to convert", NumberConverter.convertNumber("1000000000000000.0"));
    }

    @Test
    public void testNegativeValueOverflow() {
        // Test with a negative value that exceeds the maximum allowed value
        assertEquals("Value is too large to convert", NumberConverter.convertNumber("-1000000000000.0"));
    }

    @Test
    public void testConversionToWords() {
        // Test numeric to word conversion for a specific value
        assertEquals("Two Thousand Five Hundred", NumberConverter.convertNumber("2500.0"));
    }

    @Test
    public void testConversionToNumber() {
        // Test word to numeric conversion for a specific value
        assertEquals("2500", NumberConverter.convertToNumber("Two Thousand Five Hundred"));
    }

    @Test
    public void testFractionalConversionToWords() {
        // Test numeric to word conversion for a specific fractional value
        assertEquals("Twenty Five Point Twenty", NumberConverter.convertNumber("25.20"));
    }

    @Test
    public void testFractionalConversionToNumber() {
        // Test word to numeric conversion for a specific fractional value
        assertEquals("25.2", NumberConverter.convertToNumber("Twenty Five Point Twenty"));
    }

    @Test
    public void testNegativeConversionToWords() {
        // Test numeric to word conversion for a negative value
        assertEquals("Minus Two Hundred Fifty", NumberConverter.convertNumber("-250"));
    }

    @Test
    public void testNegativeConversionToNumber() {
        // Test word to numeric conversion for a negative value
        assertEquals("-250", NumberConverter.convertToNumber("Minus Two Hundred Fifty"));
    }

    @Test
    public void testDecimalNumber(){
        assertEquals("801002067011.03" , NumberConverter.convertToNumber("Eight Hundred One Billion Two Million Sixty Seven Thousand Eleven Point Zero Three"));
        assertEquals("801002067011.3" , NumberConverter.convertToNumber("Eight Hundred One Billion Two Million Sixty Seven Thousand Eleven Point Three"));
        assertEquals("801002067011.3" , NumberConverter.convertToNumber("Eight Hundred One Billion Two Million Sixty Seven Thousand Eleven Point Thirty"));
        assertNotEquals("801002067011.3", NumberConverter.convertToNumber("Eight Hundred One Billion Two Million Sixty Seven Thousand Eleven Point Zero Three"));
        assertNotEquals("801002067011.03", NumberConverter.convertToNumber("Eight Hundred One Billion Two Million Sixty Seven Thousand Eleven Point Three"));
    }

    @Test
    public void testConvertNumber(){
        assertEquals("801002067011.03" , NumberConverter.convertNumber("Eight Hundred One Billion Two Million Sixty Seven Thousand Eleven Point Zero Three"));
        assertEquals("Two Thousand Five Hundred", NumberConverter.convertNumber("2500.0"));
    }
}

