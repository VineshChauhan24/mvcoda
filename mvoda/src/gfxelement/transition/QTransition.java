package gfxelement.transition;

import lombok.Getter;
import theme.Theme;

public class QTransition extends Transition {
	
	@Getter public int lastInFrame = 32;
	@Getter public int FirstOutFrame = 32;
	@Getter public int numberOfFrames = 32;
	
	@Getter public int xOffsetSD = 450;
	@Getter public int yOffsetSD = -85;
	
	
	public QTransition(Theme theme) {
		super(theme);
	}
	
	@Getter private String directory =  super.getDirectory() +  "QTransition";
}