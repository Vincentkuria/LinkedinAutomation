package com.vinteque.automations;

import org.openqa.selenium.WebElement;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/// Utility class for human-like delays and randomization to avoid bot detection.
/// This class provides various methods to add randomness and natural timing
/// to automated actions, making them appear more human-like.
public class HumanBehaviorSimulator {

    private  final Random random = new Random();

    /**
     * Sleep for a random duration between min and max milliseconds
     *
     * @param minMs Minimum sleep time in milliseconds
     * @param maxMs Maximum sleep time in milliseconds
     * @throws InterruptedException if sleep is interrupted
     */
    public  void randomDelay(int minMs, int maxMs) throws InterruptedException {
        if (minMs > maxMs) {
            throw new IllegalArgumentException("minMs cannot be greater than maxMs");
        }

        int delay = ThreadLocalRandom.current().nextInt(minMs, maxMs + 1);
        Thread.sleep(delay);
    }

    /**
     * Sleep for a random duration around a base time with variance
     *
     * @param baseMs Base time in milliseconds
     * @param variancePercent Percentage variance (e.g., 20 for ±20%)
     * @throws InterruptedException if sleep is interrupted
     */
    public  void randomDelayWithVariance(int baseMs, int variancePercent) throws InterruptedException {
        int variance = (int) (baseMs * (variancePercent / 100.0));
        int minMs = baseMs - variance;
        int maxMs = baseMs + variance;

        // Ensure minimum is at least 0
        minMs = Math.max(0, minMs);

        randomDelay(minMs, maxMs);
    }

    /**
     * Simulate human-like reading/processing time based on text length
     * Humans typically read 200-300 words per minute
     *
     * @param text Text being "read"
     * @param minWordsPerMinute Minimum reading speed
     * @param maxWordsPerMinute Maximum reading speed
     * @throws InterruptedException if sleep is interrupted
     */
    public  void simulateReadingTime(String text, int minWordsPerMinute, int maxWordsPerMinute)
            throws InterruptedException {
        if (text == null || text.isEmpty()) {
            randomDelay(500, 1500); // Short glance
            return;
        }

        int wordCount = text.split("\\s+").length;

        // Random reading speed between min and max
        int wordsPerMinute = ThreadLocalRandom.current().nextInt(minWordsPerMinute, maxWordsPerMinute + 1);

        // Calculate reading time in milliseconds
        double minutesToRead = (double) wordCount / wordsPerMinute;
        int readingTimeMs = (int) (minutesToRead * 60 * 1000);

        // Add some randomness to the reading time (±30%)
        randomDelayWithVariance(readingTimeMs, 30);
    }

    /**
     * Simulate human-like reading time with default speeds (200-300 WPM)
     *
     * @param text Text being "read"
     * @throws InterruptedException if sleep is interrupted
     */
    public  void simulateReadingTime(String text) throws InterruptedException {
        simulateReadingTime(text, 200, 300);
    }


    /**
     * Random delay between actions with occasional longer pauses
     * Simulates human distraction or multitasking
     *
     * @param normalMinMs Normal minimum delay
     * @param normalMaxMs Normal maximum delay
     * @param longPauseMinMs Long pause minimum
     * @param longPauseMaxMs Long pause maximum
     * @param longPauseProbability Probability of long pause (0.0 to 1.0, e.g., 0.1 = 10%)
     * @throws InterruptedException if sleep is interrupted
     */
    public  void randomDelayWithOccasionalPause(int normalMinMs, int normalMaxMs,
                                                      int longPauseMinMs, int longPauseMaxMs,
                                                      double longPauseProbability)
            throws InterruptedException {
        if (random.nextDouble() < longPauseProbability) {
            // Take a long pause (simulating distraction)
            System.out.println("  [Human-like pause: taking a brief break...]");
            randomDelay(longPauseMinMs, longPauseMaxMs);
        } else {
            // Normal delay
            randomDelay(normalMinMs, normalMaxMs);
        }
    }

    /**
     * Random typing speed simulation
     * Returns delay per character to simulate human typing
     *
     * @param avgWPM Average typing speed in words per minute (40-80 is typical)
     * @return Delay per character in milliseconds
     */
    public  int getTypingDelayPerChar(int avgWPM) {
        // Average word length is 5 characters
        // Convert WPM to characters per minute, then to ms per character
        double charsPerMinute = avgWPM * 5.0;
        double msPerChar = 60000.0 / charsPerMinute;

        // Add randomness (±40%)
        int variance = (int) (msPerChar * 0.4);
        int minDelay = (int) (msPerChar - variance);
        int maxDelay = (int) (msPerChar + variance);

        return ThreadLocalRandom.current().nextInt(Math.max(10, minDelay), maxDelay + 1);
    }


    /**
     * Simulate human decision-making time
     * Short pause before clicking/acting on something
     *
     * @throws InterruptedException if sleep is interrupted
     */
    public  void simulateDecisionTime() throws InterruptedException {
        // Humans take 300-1500ms to make simple decisions
        randomDelay(300, 1500);
    }

    /**
     * Get random boolean with specified probability
     *
     * @param probability Probability of true (0.0 to 1.0)
     * @return true with given probability, false otherwise
     */
    public  boolean randomBoolean(double probability) {
        return random.nextDouble() < probability;
    }

    /**
     * simuletes human like typing
     *
     * @param element text input to type text
     * @param text the text to write
     * @throws InterruptedException if typing is interrupted
     */
    public void typeLikeHuman(WebElement element, String text) throws InterruptedException {

        int avgWPM = ThreadLocalRandom.current().nextInt(45, 75);

        for (char c : text.toCharArray()) {

            element.sendKeys(String.valueOf(c));

            int delay = getTypingDelayPerChar(avgWPM);
            Thread.sleep(delay);

            if (randomBoolean(0.05)) {
               randomDelay(300, 900);
            }
        }
    }

}