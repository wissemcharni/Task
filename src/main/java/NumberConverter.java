import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.Scanner;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;



public class NumberConverter {
        // Units, TEENS, TENS, and THOUNDS store all the words that can create a number.
        private static final String[] UNITS = { "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine" };
        private static final String[] TEENS = { "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen" };
        private static final String[] TENS = { "", "Ten", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety" };
        private static final String[] THOUSANDS = { "", "Thousand", "Million", "Billion", "Trillion", "Quadrillion", "Quintillion", "Sextillion" };

        // The limit can be removed or changed to fulfill the needs
        private static final int MAX_SCALE = THOUSANDS.length - 1; // Set a limit for big numbers (in words) for memory Consumption and computing time.
        private static final BigDecimal MAX_VALUE = new BigDecimal("999999999999.99"); //  Set a limit for big numbers (numeric) for memory Consumption and computing time.

        // Maps to store the Number and the corresponding word representation of the number.
        // numberToWord stores the number as a key.
        // wordToNumber stores the word as a key
        private static Map<String, Integer> wordToNumber = new HashMap<>();
        private static Map<Integer, String> numberToWord = new HashMap<>();

        // Fill the two maps with number and word representation.
        static {
                for (int i = 1; i < 10; i++) {
                        wordToNumber.put(UNITS[i], i);
                        numberToWord.put(i, UNITS[i]);
                }
                for (int i = 10; i <= 19; i++) {
                        wordToNumber.put(TEENS[i - 10], i);
                        numberToWord.put(i, TEENS[i - 10]);
                }
                for (int i = 10; i <= 90; i += 10) {
                        wordToNumber.put(TENS[i / 10], i);
                        numberToWord.put(i, TENS[i / 10]);
                }
        }
        /** Function that checks the input and decide which conversion to execute word to number or number to word.
         * Checks the number if outside the defined range.
         * Checks if the number is positive or negative.
         * Checks if the number have a fraction part or no.
         *
         * @param input the number to convert as digits or word.
         * @return the conversion of the input to digits or words depending on the input
         */
        public static String convertNumber(String input) {
                try {
                        BigDecimal value = new BigDecimal(input);
                        if (value.abs().compareTo(MAX_VALUE) > 0) {
                                throw new IllegalArgumentException("Value is too large to convert");
                        }

                        BigDecimal integerPart = value.setScale(0, BigDecimal.ROUND_DOWN);
                        BigDecimal fractionalPart = value.subtract(integerPart);
                        String worded = "";
                        if (value.signum() == -1) {
                                worded = worded + "Minus ";
                                value = value.multiply(new BigDecimal(-1));
                                worded = worded + convertToWords(value);
                        }else {
                                worded = convertToWords(value);
                        }if (fractionalPart.signum() == -1) {
                                fractionalPart = fractionalPart.multiply(new BigDecimal("-100"));
                        }else {
                                fractionalPart = fractionalPart.multiply(new BigDecimal("100"));
                        }
                        String fraction = convertToWords(fractionalPart);
                        if (!fraction.equals("Zero")) {
                                worded = worded + " Point " + fraction;
                        }
                        //if (value.compareTo(BigDecimal.ZERO) < 0) {
                        //        worded = "Minus " + worded;
                        //}
                        return worded.isEmpty() ? "Zero" : worded;
                } catch (NumberFormatException e) {
                        // Handle the input format error specifically
                        return convertToNumber(input);
                } catch (IllegalArgumentException e) {
                        // Handle the case where the value is too large
                        return e.getMessage();
                }
        }

        /** Function that converts the number from digits to words.
         * Reads the number as a group of three digits since thousands, Millions, Billions etc. only contains 3 digits.
         * Convert the number to a word using the numberToWord map.
         *
         * @param value the number to convert from digits to words.
         * @return the word representation of the number after conversion.
         */

        public static String convertToWords(BigDecimal value) {
                if (value.compareTo(BigDecimal.ZERO) == 0) {
                        return "Zero";
                }
                StringBuilder result = new StringBuilder();

                int scale = 0;
                boolean hasAppendedScale = false; // Track whether a scale name has been appended

                while (scale <= MAX_SCALE && value.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal chunk = value.remainder(BigDecimal.valueOf(1_000));
                        int chunkValue = chunk.intValue();

                        if (chunkValue != 0 && chunkValue > 9) {
                                if (scale == 0){
                                        if (chunkValue >= 100) {
                                                result.append(UNITS[chunkValue / 100] + " Hundred ");
                                                chunkValue %= 100;
                                        }
                                        if (chunkValue >= 10 && chunkValue <= 19) {
                                                result.append(TEENS[chunkValue - 10] + " ");
                                        } else {
                                                result.append(TENS[chunkValue / 10] + " ");
                                                chunkValue %= 10;
                                                if (chunkValue > 0) {
                                                        result.append(UNITS[chunkValue] + " ");
                                                }
                                        }
                                        if (hasAppendedScale) {
                                                result.append(THOUSANDS[scale] + " ");
                                        }
                                }else{
                                        if (scale > 0) {
                                                result.insert(0,THOUSANDS[scale] + " ");
                                        }
                                        int offset = 0;
                                        if (chunkValue >= 100) {
                                                result.insert(0,UNITS[chunkValue / 100] + " Hundred ");
                                                offset =  UNITS[chunkValue / 100].length() + 9;
                                                chunkValue %= 100;
                                        }
                                        if (chunkValue >= 10 && chunkValue <= 19) {
                                                result.insert(offset,TEENS[chunkValue - 10] + " ");
                                                offset += TEENS[chunkValue - 10].length() + 1 ;
                                        } else {
                                                result.insert(offset,TENS[chunkValue / 10] + " ");
                                                offset += TENS[chunkValue / 10].length() + 1;
                                                chunkValue %= 10;
                                                if (chunkValue > 0) {
                                                        result.insert(offset,UNITS[chunkValue] + " ");
                                                }
                                        }
                                }
                        }else {
                                result.insert(0, UNITS[chunkValue] + " " + THOUSANDS[scale] + " ");
                        }

                        value = value.divideToIntegralValue(BigDecimal.valueOf(1_000));
                        scale++;
                }

                return result.toString().trim();
        }

        /** Function that converts the number from digits to words.
         * Split the number to array using whitespace as delimiter and reads it word by word.
         * Convert the number to a word using the wordToNumber map.
         * if "Hundred" is encountred multiply the chunk with 100.
         * if one of the words defined in THOUSANDS is encountred multiply the chunck by 1000 power the index of the word in THOUSANDS
         * to set the scale of the chunk.
         * if "Point" is encountred Handle the words that comes after it and divide it with 10 or 100 depending on the case.
         *
         * @param words the word representation of the number to be converted.
         * @return the digit representation of the number after conversion.
         */
        public static String convertToNumber(String words) {
                String[] wordArray = words.split(" ");
                BigDecimal result = BigDecimal.ZERO;
                BigDecimal chunk = BigDecimal.ZERO;


                for (String word : wordArray) {
                        if (wordToNumber.containsKey(word)){
                                chunk = chunk.add(new BigDecimal(wordToNumber.get(word)));
                        }else if (word.equals("Hundred")){
                                chunk =  chunk.multiply(BigDecimal.valueOf(100));
                        }else if (Arrays.asList(THOUSANDS).contains(word)){
                                int index = Arrays.asList(THOUSANDS).indexOf(word);
                                BigDecimal value = chunk.multiply(BigDecimal.valueOf(1_000).pow(index));
                                result = result.add(value);
                                chunk = BigDecimal.ZERO;
                        } else if (word.equals("Point")) {
                                // Handle the decimal part of the number
                                BigDecimal decimalValue = BigDecimal.ZERO;
                                BigDecimal decimalMultiplier = BigDecimal.ONE;
                                int pointIndex = Arrays.asList(wordArray).indexOf("Point") + 1; // Find the position of "Point" in wordArray

                                for (int i = pointIndex; i < wordArray.length; i++) {
                                        String decimalWord = wordArray[i];
                                        if (wordToNumber.containsKey(decimalWord)) {
                                                decimalValue = decimalValue.add(BigDecimal.valueOf(wordToNumber.get(decimalWord)));
                                                decimalMultiplier = decimalMultiplier.multiply(BigDecimal.TEN);
                                        }
                                }if (decimalValue.compareTo(new BigDecimal("10")) < 0){
                                        if (wordArray.length-pointIndex == 1){
                                                result = result.add(decimalValue.divide(BigDecimal.valueOf(10)));
                                        }else if (wordArray.length-pointIndex == 2){
                                                result = result.add(decimalValue.divide(BigDecimal.valueOf(100)));
                                        }

                                }else{
                                        result = result.add(decimalValue.divide(BigDecimal.valueOf(100)));
                                }

                                break;
                        }else if (wordArray[0].equals("Minus")){
                                result = result.multiply(BigDecimal.valueOf(-1));
                        }else {
                                return "Invalid Input (Misspelling or out of Range)";
                        }

                }
                result = result.add(chunk);
                if (wordArray[0].equals("Minus")){
                        result = result.multiply(BigDecimal.valueOf(-1));
                }
                return result.toPlainString();
        }

        /** Main function of the program.
         * Create a database if not already existing and save every input with its corresponding output.
         * If database already exists write in the database every input with its corresponding output.
         * Uses Scanner to read the user input from the console.
         */
        public static void main(String[] args) {
                Scanner scanner = new Scanner(System.in);
                // Create a MongoDB client
                MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");

                // Access a specific database (e.g., NumberConversion)
                MongoDatabase database = mongoClient.getDatabase("NumberConversion");

                // Create a collection (table) named ProgramHistory
                MongoCollection<Document> collection = database.getCollection("ProgramHistory");


                while (true) {
                        System.out.println("Enter 'q' to quit or provide a number or words for conversion:");
                        String input = scanner.nextLine().trim();

                        if (input.equalsIgnoreCase("q")) {
                                System.out.println("Exiting the program. Goodbye!");
                                break;
                        }

                        if (input.isEmpty()) {
                                System.out.println("Input is empty. Please enter a valid number or words.");
                                continue;
                        }

                        String result;
                        if (Character.isDigit(input.charAt(0)) || input.charAt(0) == '-') {
                                // If the input starts with a digit or a sign, assume it's a number
                                result = convertNumber(input);
                        } else {
                                // Otherwise, assume it's words
                                result = convertToNumber(input);
                        }
                        // Create a document (record) with user input and program output
                        Document document = new Document("user_input", input)
                                .append("program_output", result);
                        // Insert the document into the collection
                        collection.insertOne(document);
                        System.out.println("Conversion result: " + result);
                }
                scanner.close();
                // Close the MongoDB client when done
                mongoClient.close();
        }
//                String input = "1000000000000000";
//                String input1 = "123456789.5675";
//                String input2 = "-123456789.56";
//                String input3 = "Minus Two Hundred Fifty";
//                String input3Bis = "Twenty Five Point Twenty";
//                String input4 = "-1234.56";
//                String input5 = "Minus One Hundred Twenty Three Million Four Hundred Fifty Six Thousand Seven Hundred Eighty Nine Point Seventy Eight";
//                String input6 = "-123456789.123456789";
//                String input7 = "-123456789.12";
//                String input8 = "Nine Hundred Eighty Seven Billion Six Hundred Fifty Four Thousand Three Hundred Twenty One Point Ninety Eight";
//                String input9 = "Eight Hundred One Billion Two Million Sixty Seven Thousand Eleven Point Three";
//                String input10 = "Eight Hundred One Billion Two Million Sixty Seven Thousand Eleven Point Thirty";
//                String input11 = "Eight Hundred One Billion Two Million Sixty Seven Thousand Eleven Point Zero Three";
//                System.out.println(convertNumber(input));
//                System.out.println(convertNumber(input1));
//                System.out.println(convertNumber(input2));
//                System.out.println(convertNumber(input3));
//                System.out.println(convertNumber(input3Bis));
//                System.out.println(convertNumber(input4));
//                System.out.println(convertNumber(input5));
//                System.out.println(convertNumber(input6));
//                System.out.println(convertNumber(input7));
//                System.out.println(convertNumber(input8));
//                System.out.println(convertNumber(input9));
//                System.out.println(convertNumber(input10));
//                System.out.println(convertNumber(input11));

}
