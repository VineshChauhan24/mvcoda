package media.xuggle.types;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import media.types.AudioSamples;
import media.types.MediaWriter;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec.ID;
import com.xuggle.xuggler.IRational;

/**
 * Types for the decoupling adapter for the media framework of MV-CoDA
 * @author tony
 *
 */
public class MediaWriterXuggle extends MediaWriter {

	/**
	 * Holds a reference to the xuggle writer
	 */
	private IMediaWriter writer;
	
	/**
	 * returns the IMediaWriter from xuggle
	 * @param writer the IMediaWriter
	 */
	public MediaWriterXuggle(IMediaWriter writer) {	this.writer = writer; }

	/**
	 * Closes the media writer
	 */
	@Override public void close() {	writer.close();	}

	/**
	 * encodes audio to a stream using the xuggle media writer
	 */
	@Override public void encodeAudio(int audioStreamIndex, AudioSamples audioSamples) {
		//In the below call it was necessary to cast this and put this method in the decoder interface because we have to call a method in xuggle that needs IAudioSamples 
		writer.encodeAudio(audioStreamIndex, (IAudioSamples)audioSamples.getInternalAudioSamples());
	}

	/**
	 * Encodes video to a stream using the xuggle media writer
	 */
	@Override public void encodeVideo(int i, BufferedImage videoFrame, long newVideoTimecode, TimeUnit microseconds) {
		writer.encodeVideo(i, videoFrame, newVideoTimecode, microseconds);
	}

	/**
	 * Adds a video stream to a container using the xuggle media writer
	 * 
	 */
	@Override public void addVideoStream(int videoStreamIndex, int videoStreamID, ID videoCodecID, double frameRateAsDouble, int outputWidth, int outputHeight) {
		IRational frameRate = IRational.make(frameRateAsDouble); //we need to keep hold of the rational or avi gets choppy. So call changed to instantiating the rational from IRational Interface here
		writer.addVideoStream(videoStreamIndex, videoStreamID, videoCodecID, frameRate, outputWidth, outputHeight);
		
	}

	/**
	 * Adds an audio stream to a container using the xuggle media writer
	 */
	@Override public void addAudioStream(int audioStreamIndex, int audioStreamID, ID codecId, int numAudioChannels, int audioSampleRate) {
		writer.addAudioStream(audioStreamIndex, audioStreamID, codecId, numAudioChannels, audioSampleRate);	
	}
	
	
	
}
