package media.xuggle;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import media.Decoder;
import media.Encoder;
import media.MusicVideo;
import theme.Theme;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStreamCoder;

import drawing.ImageCompositor;
import drawing.TextCompositor;
/**
 * Basic methods to deal with buffered images we get from music video packets so that we can manipulate them and recode a video. Its basically "do something then encode"
 * @author Tony
 *
 */
public class EncoderXuggle implements Encoder {

	private MusicVideo video;
	private String outFilename;
	private BufferedImage composite;
	private Decoder decoder;
	private Theme theme;

	/**
	 * By passing two UNCpaths to the constructor we specify and input and an output filename
	 * @param filename
	 * @param outFilename
	 */
	public EncoderXuggle(MusicVideo video, Theme theme,String outFilename) {
		this.video = video;
		this.outFilename = outFilename;
		this.theme = theme;
		render();
	}

	/**
	 * Creates a new music video with input filename and a new writer that will write to output filename, iterates through the packets of the music video
	 * encoding both, but allowing something to happen to the buffered images before the encode
	 * @param filename
	 */
	@Override
	public void render() {

		IMediaWriter writer = null;
		try {
			decoder = video.getDecoder();
			writer = getWriter(outFilename);
			long frame = 0;
			long lastFrame = video.getNumVidFrames();
			ImageCompositor strapCompositor = new ImageCompositor(theme.getStrap());
			ImageCompositor strapCompositor2 = new ImageCompositor(theme.getStrap());
			ImageCompositor logoCompositor = new ImageCompositor(theme.getLogo());
			ImageCompositor chartCompositor = new ImageCompositor(theme.getChart());
			ImageCompositor numbersCompositor = new ImageCompositor(theme.getNumbers());
			TextCompositor textCompositor = new TextCompositor();
			textCompositor.setNumber("5");
			textCompositor.setText1("This is the track");
			textCompositor.setText1Pos(390, 460);
			textCompositor.setText2("This is the artist");
			textCompositor.setText2Pos(380, 500);
			TextCompositor chartTextCompositor = new TextCompositor();
			chartTextCompositor.setTrackArtistFont(new Font("Arial Narrow",1,18));
			chartTextCompositor.setText1("THIS WEEK'S FRESH MUSIC");
			chartTextCompositor.setText1Pos(120, 75);
			
			
			
			while (decoder.hasNextPacket()) {
				if (decoder.getVideoFrame() != null) {frame++;} // don't increase counter if not a video frame

				IAudioSamples audioSamples = decoder.getAudioSamples();
				if (audioSamples != null) {
					writer.encodeAudio(video.getAudioStreamIndex(), audioSamples);
				}
				
				BufferedImage videoFrame = decoder.getVideoFrame(); //TODO: here they are they need to be somewhere else!!!!								
				if (videoFrame != null) {
					//System.out.println("Duration of logo: " + theme.getLogo().getDuration(video.getFrameRateDivisor()));
					System.out.println("at video timestamp: " + decoder.getTimeStamp() + " Which in human time is: "+ decoder.getFormattedTimestamp());
					
					composite = logoCompositor.overlayNextImage(decoder.getTimeStamp(),0,video.getVidStreamDuration(), videoFrame);
					
					//theme.getLogo().setInDuration(video.getVidStreamDuration() + 2000);
					//theme.getLogo().setOutDuration(video.getVidStreamDuration() - 3000);
					composite = strapCompositor.overlayNextImage(decoder.getTimeStamp(),3000, 5000, composite);
					composite = strapCompositor2.overlayNextImage(decoder.getTimeStamp(),15000, 2000, composite);//composite);
					composite = chartCompositor.overlayNextImage(decoder.getTimeStamp(),2000, 0, composite);
					composite = numbersCompositor.overlayNextImageAtCoord(decoder.getTimeStamp(),9000, 5000, composite, -680, 0);
					composite = textCompositor.overlayNextFontFrame(strapCompositor.isImOut(), composite);
					composite = textCompositor.overlayNextFontFrame(strapCompositor2.isImOut(), composite);
					composite = chartTextCompositor.overlayNextFontFrame(false, composite);
					
					writer.encodeVideo(0, composite, decoder.getTimeStamp(), TimeUnit.MILLISECONDS);
				}
				if ((frame +1) >= lastFrame) { break; }
			}


		} catch (Exception ex) { //TODO: what ANY exception? Why aren't we saying we throw any then?
			ex.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (RuntimeException ex) { //TODO: only a runtime?!?!
					ex.printStackTrace();
				}
			}
			if (video != null) video.close();

		}
	}
	

	/**
	 * This is called by render(). It makes a new writer from the tool factory, adds a video and audio stream to it, and returns it
	 * @param filename
	 */
	@Override
	public IMediaWriter getWriter(String filename) {
		IMediaWriter writer = ToolFactory.makeWriter(filename);
		addVideoStreamTo(writer);
		IStreamCoder audioCodec = video.getAudioCoder();
		if (audioCodec != null) {addAudioStreamTo(writer, audioCodec);}
		return writer;
	}

	/**
	 * This is called by getWriter(). It adds the video stream to the MediWriter you pass in i.e.: so its ready for writing out 
	 * At the time rate and using the codec the class specifies
	 * @param writer
	 */
	@Override
	public void addVideoStreamTo(IMediaWriter writer) {
		IRational frameRate = IRational.make(video.getFramesPerSecondAsDouble());
		int outputWidth = video.getWidth();
		int outputHeight = video.getHeight();
		writer.addVideoStream(video.getVideoStreamIndex(),video.getVideoStreamID(),video.getVideoCodecID(),frameRate,outputWidth,outputHeight);
	}

	/**
	 * This is called by getWriter(). It adds the audio stream to the MediWriter you pass in i.e.: so its ready for writing out 
	 * using the codec that get's passed to it. At the time rate and using the codec the class specifies
	 * @param writer
	 * @param audioCodec
	 */
	@Override
	public void addAudioStreamTo(IMediaWriter writer, IStreamCoder audioCodec) {//TODO: what's the point of passing the codec in but having the other things fields?
		int numAudioChannels = audioCodec.getChannels();
		int audioSampleRate = audioCodec.getSampleRate();
		ICodec.ID codecId = audioCodec.getCodecID();
		writer.addAudioStream(video.getAudioStreamIndex(),video.getAudioStreamID(),codecId,numAudioChannels,audioSampleRate);
	}
}

