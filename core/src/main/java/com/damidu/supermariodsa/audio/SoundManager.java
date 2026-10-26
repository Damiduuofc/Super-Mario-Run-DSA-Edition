package com.damidu.supermariodsa.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.damidu.supermariodsa.persistence.SaveManager;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Procedural retro sound synthesizer for 8-bit platformer audio.
 * Synthesizes classic sound waveforms directly in memory (zero external asset dependency).
 * Supports global mute and volume controls.
 */
public class SoundManager {

    private Sound jumpSound;
    private Sound coinSound;
    private Sound stompSound;
    private Sound powerUpSound;
    private Sound hurtSound;
    private Sound gameOverSound;
    private Sound victorySound;

    private boolean muted;
    private float volume = 0.8f;

    public SoundManager() {
        this.muted = SaveManager.loadMuteSetting();
        initializeProceduralSounds();
    }

    private void initializeProceduralSounds() {
        try {
            jumpSound = generateSound(generateTone(44100, 0.15f, 220f, 660f, WaveType.SQUARE));
            coinSound = generateSound(generateArpeggio(44100, 0.22f, new float[]{987f, 1318f}, WaveType.SQUARE));
            stompSound = generateSound(generateTone(44100, 0.12f, 350f, 120f, WaveType.TRIANGLE));
            powerUpSound = generateSound(generateArpeggio(44100, 0.40f, new float[]{330f, 392f, 659f, 523f, 587f, 784f}, WaveType.SQUARE));
            hurtSound = generateSound(generateNoise(44100, 0.25f, 240f, 80f));
            gameOverSound = generateSound(generateArpeggio(44100, 0.60f, new float[]{440f, 415f, 392f, 349f}, WaveType.TRIANGLE));
            victorySound = generateSound(generateArpeggio(44100, 0.80f, new float[]{523f, 659f, 784f, 1046f, 784f, 1046f}, WaveType.SQUARE));
        } catch (Exception e) {
            Gdx.app.log("SoundManager", "Audio initialization fallback: " + e.getMessage());
        }
    }

    public void playJump() {
        playSound(jumpSound, 0.7f);
    }

    public void playCoin() {
        playSound(coinSound, 0.9f);
    }

    public void playStomp() {
        playSound(stompSound, 0.85f);
    }

    public void playPowerUp() {
        playSound(powerUpSound, 1.0f);
    }

    public void playHurt() {
        playSound(hurtSound, 1.0f);
    }

    public void playGameOver() {
        playSound(gameOverSound, 1.0f);
    }

    public void playVictory() {
        playSound(victorySound, 1.0f);
    }

    private void playSound(Sound sound, float relativeVolume) {
        if (muted || sound == null) return;
        try {
            sound.play(volume * relativeVolume);
        } catch (Exception e) {
            // Ignore audio device exceptions
        }
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
        SaveManager.saveSettings(muted, volume);
    }

    public void toggleMute() {
        setMuted(!muted);
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0f, Math.min(1f, volume));
        SaveManager.saveSettings(muted, this.volume);
    }

    // --- PROCEDURAL 16-BIT PCM WAVEFORM SYNTHESIS ---

    private enum WaveType { SQUARE, TRIANGLE, SINE }

    private byte[] generateTone(int sampleRate, float durationSec, float startFreq, float endFreq, WaveType wave) {
        int totalSamples = (int) (sampleRate * durationSec);
        short[] buffer = new short[totalSamples];
        double phase = 0;

        for (int i = 0; i < totalSamples; i++) {
            float t = (float) i / totalSamples;
            double freq = startFreq + (endFreq - startFreq) * t;
            phase += 2.0 * Math.PI * freq / sampleRate;

            double sample;
            switch (wave) {
                case SQUARE:
                    sample = Math.sin(phase) >= 0 ? 0.6 : -0.6;
                    break;
                case TRIANGLE:
                    sample = (2.0 / Math.PI) * Math.asin(Math.sin(phase));
                    break;
                default:
                    sample = Math.sin(phase);
            }

            // Exponential decay envelope
            double envelope = Math.exp(-3.0 * t);
            buffer[i] = (short) (sample * envelope * 24000);
        }
        return pcmToWav(buffer, sampleRate);
    }

    private byte[] generateArpeggio(int sampleRate, float durationSec, float[] frequencies, WaveType wave) {
        int totalSamples = (int) (sampleRate * durationSec);
        short[] buffer = new short[totalSamples];
        int noteSamples = totalSamples / frequencies.length;
        double phase = 0;

        for (int i = 0; i < totalSamples; i++) {
            int noteIndex = Math.min(i / noteSamples, frequencies.length - 1);
            double freq = frequencies[noteIndex];
            phase += 2.0 * Math.PI * freq / sampleRate;

            double sample = (wave == WaveType.SQUARE) ? (Math.sin(phase) >= 0 ? 0.5 : -0.5) : Math.sin(phase);
            float noteProgress = (float) (i % noteSamples) / noteSamples;
            double env = 1.0 - noteProgress * 0.4;
            buffer[i] = (short) (sample * env * 22000);
        }
        return pcmToWav(buffer, sampleRate);
    }

    private byte[] generateNoise(int sampleRate, float durationSec, float startFreq, float endFreq) {
        int totalSamples = (int) (sampleRate * durationSec);
        short[] buffer = new short[totalSamples];
        for (int i = 0; i < totalSamples; i++) {
            float t = (float) i / totalSamples;
            double whiteNoise = (Math.random() * 2.0 - 1.0);
            double envelope = 1.0 - t;
            buffer[i] = (short) (whiteNoise * envelope * 26000);
        }
        return pcmToWav(buffer, sampleRate);
    }

    private byte[] pcmToWav(short[] pcmData, int sampleRate) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            int subChunk2Size = pcmData.length * 2;
            int chunkSize = 36 + subChunk2Size;

            dos.writeBytes("RIFF");
            dos.writeInt(Integer.reverseBytes(chunkSize));
            dos.writeBytes("WAVE");
            dos.writeBytes("fmt ");
            dos.writeInt(Integer.reverseBytes(16)); // Subchunk1Size
            dos.writeShort(Short.reverseBytes((short) 1)); // AudioFormat (PCM)
            dos.writeShort(Short.reverseBytes((short) 1)); // NumChannels (Mono)
            dos.writeInt(Integer.reverseBytes(sampleRate)); // SampleRate
            dos.writeInt(Integer.reverseBytes(sampleRate * 2)); // ByteRate
            dos.writeShort(Short.reverseBytes((short) 2)); // BlockAlign
            dos.writeShort(Short.reverseBytes((short) 16)); // BitsPerSample
            dos.writeBytes("data");
            dos.writeInt(Integer.reverseBytes(subChunk2Size));

            for (short sample : pcmData) {
                dos.writeShort(Short.reverseBytes(sample));
            }
            dos.flush();
        } catch (IOException e) {
            return new byte[0];
        }
        return baos.toByteArray();
    }

    private Sound generateSound(byte[] wavBytes) {
        try {
            FileHandle tempFile = FileHandle.tempFile("sfx_");
            tempFile.writeBytes(wavBytes, false);
            Sound sound = Gdx.audio.newSound(tempFile);
            tempFile.delete();
            return sound;
        } catch (Exception e) {
            return null;
        }
    }

    public void dispose() {
        if (jumpSound != null) jumpSound.dispose();
        if (coinSound != null) coinSound.dispose();
        if (stompSound != null) stompSound.dispose();
        if (powerUpSound != null) powerUpSound.dispose();
        if (hurtSound != null) hurtSound.dispose();
        if (gameOverSound != null) gameOverSound.dispose();
        if (victorySound != null) victorySound.dispose();
    }
}
