/*
 * The reason I created this part (the sound Manager) was because when I first tried to do the morse code sound playing
 * which encompasses two sound files dot.wav and dash.wav, loading the wav and then playing it for every letter was 
 * too intensive. and caused the sound to not play well, since the dot was too short, and barely played, and the dash was the only
 * thing that I barely heard. I switched them to dot2.wav and dash2.wav which fixed it a bit for having a better sound. 
 * however I looked up the problem and found out that I can pre-load the wav files into memmory and just use them much quicker 
 * using some of the capabilities of the javax.sound library. 
 * please for better quality listen to the morse play out without bluetooth headphones, I find the quality is much better with the
 * Laptop's native speaker. atleast in my case on the Macbook Air M2.
 */

//created a package based on the nomenclature of how they are supposed to be named!
package com.Qahtan.morsecodemachine;

import javax.sound.sampled.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private Map<Character, Clip> soundClips;

    public SoundManager() {
        soundClips = new HashMap<>();
        // preloading the sounds in this application is essential for playing sound clips that are very short (less than a second) and 
        // having many of the letter transitions, the sound clips must be preloaded, otherwise it would sound horrible or not play at all
        // defeating the purpose of a morse machine simulation.
        preloadSounds();
    }
    	//preloading dot2.wav and dash2.wav
    private void preloadSounds() {
        loadSound('.', "./dot2.wav");  // I had two sound files for dot, the second one sounded better
        loadSound('-', "./dash2.wav");  // I had two sound files for dash, i picked the better sounding one in dot and matched it with the right dash one
    }

    private void loadSound(char key, String filename) {
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filename));
            clip.open(audioInputStream);
            soundClips.put(key, clip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSound(char soundChar) {
        Clip clip = soundClips.get(soundChar);
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
            while (clip.isRunning()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Clip not found for: " + soundChar); // so many issues happened so i used this to debug the output, for a while the clips wouldn't load appropriately but it finally worked!
        }
    }
}
