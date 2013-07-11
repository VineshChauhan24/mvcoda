package media;

import java.awt.image.BufferedImage;

import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IPacket;

/**
 * Interface for media decoder. The decoder should open a music video container to read packets and timestamps
 * @author Tony
 *
 */
public interface Decoder {

	public abstract IAudioSamples getAudioSamples();

	public abstract BufferedImage getVideoFrame();

	public abstract long getTimeStamp();

	public abstract String getFormattedTimestamp();

	public abstract void makeResampler(int outputWidth, int outputHeight);
	
	public abstract void readVideo(IPacket packet) throws RuntimeException;
	
	public abstract void readAudio(IPacket packet) throws RuntimeException;
	
	public abstract boolean hasNextPacket() throws RuntimeException;

}